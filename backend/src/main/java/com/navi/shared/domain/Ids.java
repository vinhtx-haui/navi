package com.navi.shared.domain;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import java.util.UUID;

/**
 * Identifier generation.
 *
 * <p>Navi uses UUID v7: random enough not to leak how many rows exist or who registered first,
 * but prefixed with a timestamp so that ids sort by creation time. That ordering keeps B-tree index
 * inserts local, which random UUID v4 does not — see
 * {@code docs/adr/0003-postgresql-as-primary-datastore.md}.
 */
public final class Ids {

    private static final NoArgGenerator UUID_V7 = Generators.timeBasedEpochGenerator();

    private Ids() {
    }

    public static UUID newId() {
        return UUID_V7.generate();
    }
}
