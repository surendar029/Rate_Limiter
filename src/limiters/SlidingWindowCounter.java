package limiters;

import model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Sliding Window Counter Rate Limiter.
 *
 * Algorithm:
 * - Keeps:
 *      1. current window count
 *      2. previous window count
 * - Uses weighted calculation based on elapsed time.
 *
 * Advantage:
 * - More accurate than Fixed Window.
 * - Much lower memory than Sliding Window Log.
 *
 * Trade-off:
 * - Slight approximation.
 *
 * Thread Safety:
 * - Per-user synchronization.
 */
public class SlidingWindowCounter implements RateLimiter {

    private final int capacity;
    private final long windowSizeMillis;

    private final ConcurrentHashMap<String, Window> windows;

    public SlidingWindowCounter(int capacity, int windowSizeSeconds) {

        if (capacity <= 0 || windowSizeSeconds <= 0) {
            throw new IllegalArgumentException(
                    "capacity and windowSizeSeconds must be > 0"
            );
        }

        this.capacity = capacity;
        this.windowSizeMillis = (long) windowSizeSeconds * 1000;

        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimitResult tryAcquire(String key) {

        Window window = windows.computeIfAbsent(
                key,
                k -> new Window(capacity, windowSizeMillis)
        );

        return window.tryConsume();
    }

    @Override
    public String getAlgorithmName() {
        return "Sliding Window Counter";
    }

    private static class Window {

        private final int capacity;
        private final long windowSizeMillis;

        // current window
        private long currentWindowStart;
        private int currentCount;

        // previous window
        private int previousCount;

        Window(int capacity, long windowSizeMillis) {
            this.capacity = capacity;
            this.windowSizeMillis = windowSizeMillis;

            this.currentWindowStart =
                    System.currentTimeMillis();

            this.currentCount = 0;
            this.previousCount = 0;
        }

        synchronized RateLimitResult tryConsume() {

            long now = System.currentTimeMillis();

            long elapsed = now - currentWindowStart;

            // Move to next window
            if (elapsed >= windowSizeMillis) {

                // if more than 1 full window passed
                if (elapsed >= windowSizeMillis * 2) {
                    previousCount = 0;
                } else {
                    previousCount = currentCount;
                }

                currentCount = 0;

                currentWindowStart = now;
                elapsed = 0;
            }

            // weight of previous window
            double previousWeight =
                    (double) (windowSizeMillis - elapsed)
                            / windowSizeMillis;

            // estimated total requests
            double estimatedCount =
                    (previousCount * previousWeight)
                            + currentCount;

            if (estimatedCount < capacity) {

                currentCount++;

                return RateLimitResult.allowed(
                        Math.max(0,
                                capacity - (int) estimatedCount - 1)
                );
            }

            long retryAfterMillis =
                    windowSizeMillis - elapsed;

            return RateLimitResult.rejected(
                    retryAfterMillis,
                    "Sliding window counter limit exceeded. Try again in "
                            + retryAfterMillis + " ms"
            );
        }
    }
}