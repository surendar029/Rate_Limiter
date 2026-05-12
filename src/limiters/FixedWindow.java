package limiters;

import model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindow implements RateLimiter {
    private final int capacity;
    private final int windowSize;
    private final ConcurrentHashMap<String, Window> windows;

    public FixedWindow(int capacity, int windowSize) {
        this.capacity = capacity;
        this.windowSize = windowSize;
        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        Window window = windows.computeIfAbsent(key, k -> new Window(capacity, windowSize));
        return window.tryConsume();
    }

    @Override
    public String getAlgorithmName() {
        return "Fixed Window";
    }

    private static class Window {
        private final int capacity;
        private final long windowSizeMillis;
        private long windowStartTime;
        private int counter;

        Window(int capacity, int windowSizeSeconds) {
            this.capacity = capacity;
            this.windowSizeMillis = (long) windowSizeSeconds * 1000;
            this.counter = 0;
            this.windowStartTime = System.currentTimeMillis();
        }

        synchronized RateLimitResult tryConsume() {
            long now = System.currentTimeMillis();

            if (now - windowStartTime >= windowSizeMillis) {
                counter = 0;
                windowStartTime = now;
            }
            int currentCount = ++counter;
            if (currentCount <= capacity) {
                return RateLimitResult.allowed(capacity - currentCount);
            }

            long retryAfterMillis=windowSizeMillis-(now-windowStartTime);
            return RateLimitResult.rejected(
                    retryAfterMillis,
                    "Rate limit exceeded. Try again in "
                            + retryAfterMillis + " ms"
            );
        }
    }
}
