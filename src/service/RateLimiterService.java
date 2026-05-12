package service;

import config.RateLimiterConfig;
import factory.RateLimiterFactory;
import limiters.RateLimiter;
import model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private final ConcurrentHashMap<String, RateLimiter> profiles;

    public RateLimiterService() {
        profiles = new ConcurrentHashMap<>();
    }

    public void registerProfile(String profileName, RateLimiterConfig config) {
        if (profileName == null || profileName.isBlank())
            throw new IllegalArgumentException("Profile name must not be null or blank.");
        RateLimiter limiter = RateLimiterFactory.create(config);
        profiles.put(profileName, limiter);
        System.out.printf("Registered profile [%-10s] -> %s%n", profileName, config);
    }

    public RateLimitResult allowRequest(String profile, String key) {
        RateLimiter limiter = profiles.get(profile);
        if (limiter == null) {
            throw new IllegalArgumentException(
                    "No rate limiter profile registered for: '" + profile + "'. "
                            + "Call registerProfile() before using it.");
        }
        return limiter.tryAcquire(key);
    }

    public String getAlgorithmName(String profile) {
        RateLimiter limiter = profiles.get(profile);
        if (limiter == null) {
            throw new IllegalArgumentException(
                    "No rate limiter profile registered for: '" + profile + "'. "
                            + "Call registerProfile() before using it.");
        }
        return limiter.getAlgorithmName();
    }
}
