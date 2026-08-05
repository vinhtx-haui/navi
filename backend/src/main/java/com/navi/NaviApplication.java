package com.navi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Navi Platform backend — a modular monolith.
 *
 * <p>The application is split into modules by bounded context ({@code identity}, {@code academic},
 * {@code progress}, {@code goal}, {@code skill}, {@code knowledge}). Modules talk to each other
 * only through their published {@code *ModuleApi} interface; see {@code docs/architecture.md} and
 * {@code docs/adr/0001-modular-monolith.md}.
 */
@SpringBootApplication
public class NaviApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaviApplication.class, args);
    }
}
