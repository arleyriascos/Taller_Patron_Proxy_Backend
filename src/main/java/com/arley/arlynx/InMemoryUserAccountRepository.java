package com.arley.arlynx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserAccountRepository implements UserAccountRepository {

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    @Override
    public UserAccount findOrCreate(String userId) {
        return accounts.computeIfAbsent(userId, UserAccount::new);
    }

    @Override
    public void resetAllUsage() {
        accounts.values().forEach(UserAccount::resetUsage);
    }
}
