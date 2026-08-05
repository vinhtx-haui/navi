package com.navi.shared.error;

/**
 * A requested resource does not exist, or does not belong to the caller. Maps to HTTP 404.
 *
 * <p>Note the second case: when a user asks for a resource owned by someone else, Navi answers 404
 * rather than 403. A 403 would confirm that the resource exists, which leaks information about
 * other students.
 */
public class ResourceNotFound extends DomainException {

    public ResourceNotFound(String resourceType, Object id) {
        super("resource.not_found", resourceType + " not found: " + id);
    }
}
