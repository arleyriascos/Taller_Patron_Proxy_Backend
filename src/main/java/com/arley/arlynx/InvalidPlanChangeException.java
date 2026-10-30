package com.arley.arlynx;

public class InvalidPlanChangeException extends RuntimeException {

    public InvalidPlanChangeException(SubscriptionPlan current) {
        super("Plan " + current + " cannot be upgraded any further");
    }
}
