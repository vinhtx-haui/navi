/**
 * Progress module — turning academic records into an honest picture of where the student stands.
 *
 * <p><b>Bounded context:</b> credit progress, GPA, trends, progress snapshots.
 *
 * <p><b>Owns tables:</b> {@code progress.progress_snapshots}.
 *
 * <p><b>Reads from other modules through their published APIs only.</b> This module never queries
 * {@code academic.enrollments} directly, even though that would be shorter — see
 * {@code docs/adr/0001-modular-monolith.md}.
 *
 * <p><b>Core rule:</b> every figure this module produces is either computed from data it can point
 * to, or returned as {@link com.navi.shared.domain.Answer.Unknown} with a reason. It never
 * substitutes {@code 0} for "not enough data": a student may act on a number, so a misleading number
 * is worse than a visible gap.
 */
package com.navi.progress;
