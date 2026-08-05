package com.navi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies the baseline migration against a real PostgreSQL instance.
 *
 * <p>Deliberately not H2: H2's PostgreSQL compatibility mode does not reproduce {@code CHECK}
 * constraint behaviour, triggers, or schema handling faithfully, so a green test there would say
 * nothing about production. See {@code docs/adr/0003-postgresql-as-primary-datastore.md}.
 *
 * <p>Requires a running Docker daemon.
 */
@SpringBootTest
@Testcontainers
class BaselineMigrationTest {

    /** Same image tag as infra/docker/docker-compose.dev.yml, so dev and CI agree. */
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("Flyway applied the baseline migration")
    void baseline_migration_is_recorded_as_applied() {
        List<String> applied = jdbc.queryForList(
                "SELECT version FROM flyway_schema_history WHERE success = true ORDER BY installed_rank",
                String.class);

        assertThat(applied).contains("1");
    }

    @Test
    @DisplayName("every module has its own schema")
    void all_module_schemas_exist() {
        List<String> schemas = jdbc.queryForList(
                "SELECT schema_name FROM information_schema.schemata", String.class);

        assertThat(schemas).contains("identity", "academic", "progress", "goal", "skill", "knowledge");
    }

    @Test
    @DisplayName("knowledge.sources exists with the columns provenance depends on")
    void sources_table_has_the_expected_shape() {
        // Asserting on the schema rather than on row counts: every test in this class shares one
        // container, so a count assertion would depend on execution order.
        List<String> columns = jdbc.queryForList(
                """
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = 'knowledge' AND table_name = 'sources'
                """,
                String.class);

        assertThat(columns).contains(
                "id", "kind", "name", "reference_url", "note", "published_at",
                "created_at", "updated_at", "deleted_at");
    }

    @Test
    @DisplayName("a source must be checkable: either a reference URL or a note")
    void rejects_an_unverifiable_source() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO knowledge.sources (id, kind, name) VALUES (?, ?, ?)",
                UUID.randomUUID(), "UNIVERSITY_OFFICIAL", "Some curriculum with no way to check it"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("sources_verifiable");
    }

    @Test
    @DisplayName("an unrecognised source kind is rejected")
    void rejects_an_unknown_source_kind() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO knowledge.sources (id, kind, name, reference_url) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(), "RUMOUR", "Truyền miệng", "https://example.com"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("sources_kind_valid");
    }

    @Test
    @DisplayName("updated_at is maintained by the database, not by the caller")
    void updated_at_trigger_fires_on_update() {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO knowledge.sources (id, kind, name, reference_url) VALUES (?, ?, ?, ?)",
                id, "UNIVERSITY_OFFICIAL", "Chương trình đào tạo K19 SE", "https://example.edu/k19-se");

        jdbc.update("UPDATE knowledge.sources SET name = ? WHERE id = ?", "Chương trình đào tạo K19 SE (v2)", id);

        Boolean updatedAfterCreated = jdbc.queryForObject(
                "SELECT updated_at > created_at FROM knowledge.sources WHERE id = ?", Boolean.class, id);

        assertThat(updatedAfterCreated)
                .as("the trigger should advance updated_at even though the UPDATE never mentioned it")
                .isTrue();
    }
}
