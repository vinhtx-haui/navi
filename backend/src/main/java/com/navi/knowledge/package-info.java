/**
 * Knowledge module — where every piece of knowledge in Navi records its origin.
 *
 * <p><b>Bounded context:</b> sources and verification. This module exists because of the product's
 * first core value rather than because of a feature request: it is what makes "no unverified
 * information reaches the student unlabelled" enforceable instead of aspirational.
 *
 * <p><b>Owns tables:</b> {@code knowledge.sources}, {@code knowledge.verifications}.
 *
 * <p><b>Published API:</b> {@code KnowledgeModuleApi} (to be added when the first consumer exists).
 *
 * <p><b>Boundary rules</b> — enforced by {@code ModuleBoundaryTest}, not by convention:
 * <ul>
 *   <li>Only this module writes to the tables above.</li>
 *   <li>Other modules reach it through its published API, never by importing this
 *       {@code domain} package or querying its tables.</li>
 * </ul>
 *
 * @see com.navi.shared.domain.Provenance
 */
package com.navi.knowledge;
