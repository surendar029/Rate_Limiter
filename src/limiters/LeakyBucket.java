package limiters;

import model.RateLimitResult;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucket implements RateLimiter {
    private final int capacity;
    private final int leakRatePerSecond;
    private final ConcurrentHashMap<String, Bucket> buckets;

    public LeakyBucket(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, leakRatePerSecond));
        return bucket.tryConsume();
    }

    @Override
    public String getAlgorithmName() {
        return "Leaky Bucket";
    }

    private static final class Bucket {
        private final int capacity;
        private final int leakRatePerSecond;
        private final Deque<Long> queue;
        private long lastLeakTimeStamp;

        Bucket(int capacity, int leakRatePerSecond) {
            this.capacity = capacity;
            this.leakRatePerSecond = leakRatePerSecond;
            this.queue = new ArrayDeque<>(capacity);
            this.lastLeakTimeStamp = System.currentTimeMillis();
        }

        synchronized RateLimitResult tryConsume() {
            leak();
            if (queue.size() < capacity) {
                queue.addLast(System.currentTimeMillis());
                return RateLimitResult.allowed(capacity - queue.size());
            }
            long msPerLeak = 1000L / leakRatePerSecond;
            return RateLimitResult.rejected(msPerLeak, "Leaky bucket full (capacity=" + capacity + "). Retry after ~" + msPerLeak + "ms.");
        }

        private void leak() {
            long now = System.currentTimeMillis();
            double elapsedSeconds = (double) (now - lastLeakTimeStamp) / 1000.0;
            int requestsToLeak = (int) (elapsedSeconds * leakRatePerSecond);
            for (int i = 0; i < requestsToLeak && !queue.isEmpty(); i++) {
                queue.pollFirst();
            }
            if (requestsToLeak > 0) lastLeakTimeStamp = now;
        }

    }
}
