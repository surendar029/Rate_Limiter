package limiters;

import model.RateLimitResult;

public interface RateLimiter {
    RateLimitResult tryAcquire(String key);
    String getAlgorithmName();
}
