package com.arley.arlynx;

public interface UserAccountRepository {

    UserAccount findOrCreate(String userId);

    void resetAllUsage();
}
