package com.navi.shared.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reports what this build of the API is.
 *
 * <p>Useful once more than one environment exists: it answers "which version am I actually talking
 * to?" without needing access to the deployment. Contains no user data, so it needs no
 * authentication.
 */
@RestController
@RequestMapping("/api/v1/meta")
class MetaController {

    private final String version;

    MetaController(@Value("${info.app.version:0.1.0-SNAPSHOT}") String version) {
        this.version = version;
    }

    @GetMapping
    ApiMeta meta() {
        return new ApiMeta("Navi Platform API", version, "v1", "1 — Foundation");
    }

    record ApiMeta(String name, String version, String apiVersion, String phase) {
    }
}
