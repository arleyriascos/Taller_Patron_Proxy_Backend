package com.arley.arlynx;

public interface RequestWindowTracker {

    boolean tryAcquire(String userId, int limit);

    int used(String userId);

    void clear();
}
