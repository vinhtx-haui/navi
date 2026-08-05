package com.navi.shared.error;

/** A request that was well-formed but violates a business rule. Maps to HTTP 422. */
public class BusinessRuleViolation extends DomainException {

    public BusinessRuleViolation(String code, String message) {
        super(code, message);
    }
}
