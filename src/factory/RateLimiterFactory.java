package factory;

import config.RateLimiterConfig;
import enums.RateLimiterType;
import limiters.*;

public class RateLimiterFactory {

    public static RateLimiter create(RateLimiterConfig config){
        RateLimiterType type=config.getType();
        int capacity=config.getCapacity();
        int rateOrWindowSeconds=config.getRateOrWindowSeconds();

        return switch (type){
            case TOKEN_BUCKET -> new TokenBucket(capacity,rateOrWindowSeconds);
            case LEAKY_BUCKET -> new LeakyBucket(capacity,rateOrWindowSeconds);
            case FIXED_WINDOW -> new FixedWindow(capacity,rateOrWindowSeconds);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLog(capacity,rateOrWindowSeconds);
            case SLIDING_WINDOW_COUNTER -> new SlidingWindowCounter(capacity,rateOrWindowSeconds);
            default -> throw new IllegalArgumentException("Unsupported rate limiter type: " + type);
        };
    }
}
