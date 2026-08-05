/**
 * Academic module — the curriculum, its courses, and what the student has taken.
 *
 * <p><b>Bounded context:</b> curricula, courses, enrollments, semesters.
 *
 * <p><b>Owns tables:</b> {@code academic.curricula}, {@code academic.courses},
 * {@code academic.enrollments}, {@code academic.semesters}.
 *
 * <p><b>Key distinction:</b> a {@code Course} is the definition of a subject (code, name, credits,
 * prerequisites); an {@code Enrollment} is one student taking it (status, semester, grade). Merging
 * the two is a common modelling mistake and expensive to undo — see {@code docs/glossary.md}.
 *
 * <p><b>Publishes events:</b> {@code CourseCompleted}, consumed by {@code progress}, {@code goal}
 * and {@code skill}. This module does not know who its subscribers are.
 */
package com.navi.academic;
