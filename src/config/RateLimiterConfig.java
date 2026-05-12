package config;

import enums.RateLimiterType;

public class RateLimiterConfig {
    private RateLimiterType type;
    private int capacity;
    private int rateOrWindowSeconds;


    public RateLimiterConfig(RateLimiterType type, int capacity, int rateOrWindowSeconds) {
        this.capacity = capacity;
        this.rateOrWindowSeconds = rateOrWindowSeconds;
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRateOrWindowSeconds() {
        return rateOrWindowSeconds;
    }

    public RateLimiterType getType() {
        return type;
    }


    public String toString() {
        return String.format(
                "RateLimiterConfig{type=%s, capacity=%d, rateOrWindowSeconds=%d/s}",
                type, capacity, rateOrWindowSeconds);
    }
}
