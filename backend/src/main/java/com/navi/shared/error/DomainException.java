package com.navi.shared.error;

/**
 * Base class for business-rule violations raised by the domain layer.
 *
 * <p>Deliberately not a Spring or HTTP type: the domain layer does not know that HTTP exists.
 * Translation into a status code happens once, at the edge, in {@code ApiExceptionHandler}.
 *
 * @see com.navi.shared.error.ApiExceptionHandler
 */
public abstract class DomainException extends RuntimeException {

    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * A stable, machine-readable code (for example {@code course.prerequisite_not_met}).
     *
     * <p>Clients branch on this rather than on the message text, so messages stay free to change
     * and to be translated.
     */
    public String code() {
        return code;
    }
}
