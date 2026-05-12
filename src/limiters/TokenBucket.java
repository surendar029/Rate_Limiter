package limiters;

import model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucket implements RateLimiter {
    private final int capacity;
    private final int refillRatePerSecond;
    private final ConcurrentHashMap<String, Bucket> buckets;

    public TokenBucket(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, refillRatePerSecond));
        return bucket.tryConsume();
    }

    @Override
    public String getAlgorithmName() {
        return "Token Bucket";
    }


    private static class Bucket {
        private final int capacity;
        private final int refillRatePerSecond;
        private int tokens;
        private long lastRillTimeStamp;

        Bucket(int capacity, int refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens = capacity;
            this.lastRillTimeStamp = System.currentTimeMillis();
        }

        synchronized RateLimitResult tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return RateLimitResult.allowed(tokens);
            }
            long msPerToken = 1000L / refillRatePerSecond;
            return RateLimitResult.rejected(msPerToken, "Token bucket exhausted. Next token in ~" + msPerToken + "ms.");
        }

        private void refill() {
            long now = System.currentTimeMillis();
            double elapsedSeconds = (double) (now - lastRillTimeStamp) / 1000.0;
            int tokensToAdd = (int) (elapsedSeconds * refillRatePerSecond);
            if (tokensToAdd > 0) {
                tokens = Math.min(capacity, tokensToAdd + tokens);
                lastRillTimeStamp = now;
            }
        }
    }
}
