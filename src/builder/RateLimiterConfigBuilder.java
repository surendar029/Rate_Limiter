package builder;

import config.RateLimiterConfig;
import enums.RateLimiterType;

public class RateLimiterConfigBuilder {
    private RateLimiterType type;
    private int capacity;
    private int rateOrWindowSeconds;

    public RateLimiterConfigBuilder type(RateLimiterType type) {
        if (type == null) throw new IllegalArgumentException("RateLimiterType must not be null.");
        this.type = type;
        return this;
    }

    public RateLimiterConfigBuilder capacity(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive. Got: " + capacity);
        this.capacity = capacity;
        return this;
    }

    public RateLimiterConfigBuilder rateOrWindowSeconds(int rateOrWindowSeconds) {
        if (rateOrWindowSeconds < 0) throw new IllegalArgumentException("Refill rate must be non-negative. Got: " + rateOrWindowSeconds);
        this.rateOrWindowSeconds = rateOrWindowSeconds;
        return this;
    }


    public RateLimiterConfig build(){
        return new RateLimiterConfig(type,capacity,rateOrWindowSeconds);
    }

}
