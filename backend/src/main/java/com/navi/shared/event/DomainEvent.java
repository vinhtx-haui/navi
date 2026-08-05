package com.navi.shared.event;

import java.time.Instant;

/**
 * Something that happened in a module's domain, which other modules may care about.
 *
 * <p>Events let the owning module stay unaware of its subscribers: {@code academic} publishes
 * {@code CourseCompleted} without knowing that {@code progress}, {@code goal} and {@code skill}
 * react to it.
 *
 * <p>Subscribers listen with
 * {@code @TransactionalEventListener(phase = AFTER_COMMIT)} so that a failing side effect cannot
 * roll back the originating transaction. When a module is later extracted into its own service, the
 * in-process publisher is replaced by a message broker and the domain code does not change.
 */
public interface DomainEvent {

    /** When the event occurred. Set by the publishing module, not by the listener. */
    Instant occurredAt();
}
