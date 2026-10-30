package com.arley.arlynx;

public class QuotaExhaustedException extends RuntimeException {

    public QuotaExhaustedException() {
        super("The monthly token quota is exhausted, upgrade the plan to continue");
    }
}
