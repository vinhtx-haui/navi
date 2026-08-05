package com.navi.architecture;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Enforces the architectural rules that {@code docs/adr/0001-modular-monolith.md} depends on.
 *
 * <p>These rules exist before the modules they constrain. A modular monolith only stays modular if
 * something mechanical says no: nothing at the network level stops one module from reaching into
 * another's entities or tables, so this test is the boundary. Added after the violations appear, it
 * would be an expensive cleanup instead of a cheap guard.
 *
 * <p>Written against ArchUnit's core API with plain JUnit {@code @Test} methods rather than
 * {@code @ArchTest} fields. The field form needs ArchUnit's own test engine, which this build's
 * runner did not discover — silently reporting zero tests, which is the worst possible failure mode
 * for a guard rule. Explicit {@code check(CLASSES)} calls cannot pass by not running.
 *
 * <p>Several rules currently match no classes because the modules are still empty. Those allow an
 * empty match so the build stays green until the first module lands, and begin enforcing the moment
 * it does.
 */
class ModuleBoundaryTest {

    private static final String ROOT = "com.navi";

    /** The bounded contexts from docs/architecture.md §3.1. {@code shared} is not a module. */
    private static final Set<String> MODULES =
            Set.of("identity", "academic", "progress", "goal", "skill", "knowledge");

    /** Layers that are a module's own business — never another module's to touch. */
    private static final Set<String> INTERNAL_LAYERS =
            Set.of("domain", "application", "infrastructure", "api");

    private static JavaClasses classesUnderTest;

    @BeforeAll
    static void importProductionClasses() {
        classesUnderTest = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT);
    }

    // ─── Rule 1: domain logic stays free of the framework ────────────────────

    @Test
    @DisplayName("domain classes do not depend on Spring, JPA, Hibernate or the servlet API")
    void domain_is_free_of_framework_dependencies() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "jakarta.servlet..",
                        "org.hibernate..")
                .because("domain logic must be testable without a Spring context or a database "
                        + "(ADR-0001); framework types belong in the outer layers");

        rule.check(classesUnderTest);
    }

    @Test
    @DisplayName("dependencies point inward: domain knows nothing of api or infrastructure")
    void domain_does_not_depend_on_outer_layers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..api..", "..infrastructure..")
                .because("the domain must not know how it is exposed or persisted");

        rule.check(classesUnderTest);
    }

    // ─── Rule 2: modules talk only through published APIs ────────────────────

    @Test
    @DisplayName("no module reaches into another module's internal layers")
    void modules_communicate_only_through_published_apis() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(ROOT + "..")
                .should(reachIntoAnotherModulesInternals())
                .because("a module is reachable only through its published *ModuleApi; importing "
                        + "another module's domain or querying its tables is what turns a modular "
                        + "monolith back into a tangle");

        rule.check(classesUnderTest);
    }

    // ─── Rule 3: no cycles between modules ──────────────────────────────────

    @Test
    @DisplayName("modules are free of dependency cycles")
    void modules_are_free_of_cycles() {
        ArchRule rule = slices()
                .matching(ROOT + ".(*)..")
                .should().beFreeOfCycles()
                .because("a cycle means two modules are really one, and neither can be extracted "
                        + "or reasoned about alone");

        rule.check(classesUnderTest);
    }

    // ─── Rule 4: layer conventions ──────────────────────────────────────────

    @Test
    @DisplayName("controllers live in api packages")
    void controllers_live_in_api_packages() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAPackage("..api..")
                .because("the api package is the module's edge; controllers elsewhere blur it");

        rule.check(classesUnderTest);
    }

    @Test
    @DisplayName("transactions are opened in the application layer")
    void transactions_are_opened_in_the_application_layer() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Transactional.class)
                .should().resideInAPackage("..application..")
                .allowEmptyShould(true) // no use cases exist yet
                .because("the transaction boundary belongs to the use case (docs/architecture.md "
                        + "§4.1) — not to a controller, and not to the domain");

        rule.check(classesUnderTest);
    }

    @Test
    @DisplayName("dependencies are injected through constructors, not into fields")
    void no_field_injection() {
        ArchRule rule = noFields()
                .should().beAnnotatedWith(Autowired.class)
                .because("constructor injection makes dependencies explicit and keeps classes "
                        + "instantiable in a plain unit test");

        rule.check(classesUnderTest);
    }

    // ─── Custom condition ───────────────────────────────────────────────────

    /**
     * Flags any dependency that crosses from one module into another module's internal layers.
     *
     * <p>Used with {@code noClasses()}, so a satisfied event is reported as a violation.
     */
    private static ArchCondition<JavaClass> reachIntoAnotherModulesInternals() {
        return new ArchCondition<>("reach into another module's internal layers") {
            @Override
            public void check(JavaClass origin, ConditionEvents events) {
                String originModule = moduleOf(origin.getPackageName());
                if (originModule == null) {
                    return; // the shared kernel and the application root are allowed everywhere
                }

                for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                    String targetPackage = dependency.getTargetClass().getPackageName();
                    String targetModule = moduleOf(targetPackage);

                    boolean crossesModules = targetModule != null && !targetModule.equals(originModule);
                    if (crossesModules && isInternalLayer(targetPackage, targetModule)) {
                        events.add(SimpleConditionEvent.satisfied(origin, dependency.getDescription()));
                    }
                }
            }
        };
    }

    /** The module a package belongs to, or null for {@code shared} and the application root. */
    private static String moduleOf(String packageName) {
        if (!packageName.startsWith(ROOT + ".")) {
            return null;
        }
        String candidate = firstSegment(packageName.substring(ROOT.length() + 1));
        return MODULES.contains(candidate) ? candidate : null;
    }

    private static boolean isInternalLayer(String packageName, String module) {
        String prefix = ROOT + "." + module + ".";
        if (!packageName.startsWith(prefix)) {
            return false;
        }
        return INTERNAL_LAYERS.contains(firstSegment(packageName.substring(prefix.length())));
    }

    private static String firstSegment(String packagePath) {
        int dot = packagePath.indexOf('.');
        return dot < 0 ? packagePath : packagePath.substring(0, dot);
    }
}
