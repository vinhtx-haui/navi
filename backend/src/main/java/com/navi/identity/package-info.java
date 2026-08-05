/**
 * Identity module — who the user is and how they prove it.
 *
 * <p><b>Bounded context:</b> registration, authentication, sessions.
 *
 * <p><b>Owns tables:</b> {@code identity.users}, {@code identity.refresh_tokens}.
 *
 * <p><b>Published API:</b> {@code IdentityModuleApi} (to be added with the first consumer).
 *
 * <p><b>Boundary rules:</b> other modules never load a user entity; they receive a {@code userId}
 * and treat it as an opaque reference. Authorisation checks live in each module's
 * {@code application} layer — every query for user-owned data is constrained by the authenticated
 * {@code userId}.
 */
package com.navi.identity;
