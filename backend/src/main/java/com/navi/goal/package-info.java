/**
 * Goal module — what the student is trying to achieve, broken into steps they can finish.
 *
 * <p><b>Bounded context:</b> goals and subgoals.
 *
 * <p><b>Owns tables:</b> {@code goal.goals}, {@code goal.subgoals}.
 *
 * <p><b>Core rule:</b> goal progress is <em>computed</em> from completed subgoals. Users do not type
 * in a percentage — a self-reported number measures optimism, not progress.
 */
package com.navi.goal;
