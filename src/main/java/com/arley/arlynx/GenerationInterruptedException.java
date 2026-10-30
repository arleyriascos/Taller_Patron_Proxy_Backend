package com.arley.arlynx;

public class GenerationInterruptedException extends RuntimeException {

    public GenerationInterruptedException() {
        super("The generation was interrupted");
    }
}
