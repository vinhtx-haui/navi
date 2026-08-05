/**
 * Skill module — skills, roadmaps, and how far the student has got with each.
 *
 * <p><b>Bounded context:</b> skills, roadmaps, roadmap steps, proficiency.
 *
 * <p><b>Owns tables:</b> {@code skill.skills}, {@code skill.roadmaps}, {@code skill.roadmap_steps},
 * {@code skill.user_skills}.
 *
 * <p><b>Design constraint:</b> the skill model must not hard-code assumptions about software
 * engineering. Phase 1 targets IT students, but expanding to other fields later must not require
 * rewriting this module — so a skill is "a capability that can be learned and assessed", nothing
 * more specific.
 *
 * <p>Proficiency uses the fixed 0–5 scale defined in {@code docs/glossary.md}, whose levels are
 * described by what the student can <em>do</em>, not by how confident they feel.
 */
package com.navi.skill;
