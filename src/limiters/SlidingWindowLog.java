package limiters;

import model.RateLimitResult;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowLog implements RateLimiter {
    private final int capacity;
    private final int windowSize;

    private final ConcurrentHashMap<String, Window> windows;

    public SlidingWindowLog(int capacity, int windowSize) {
        this.capacity = capacity;
        this.windowSize = windowSize;
        this.windows = new ConcurrentHashMap<>();
    }

    public RateLimitResult tryAcquire(String key) {
        Window window=windows.computeIfAbsent(key,k->new Window(capacity,windowSize));
        return window.tryConsume();
    }

    @Override
    public String getAlgorithmName() {
        return "";
    }

    private final class Window {
        private final int capacity;
        private final long windowSizeMillis;
        private final Deque<Long> timestamps ;

        public Window(int capacity,int windowSizeMillis){
            this.capacity=capacity;
            this.windowSizeMillis=(long) windowSizeMillis*1000;
            this.timestamps =new ArrayDeque<>(capacity);
        }

        synchronized RateLimitResult tryConsume() {
            long now=System.currentTimeMillis();

            while (!timestamps.isEmpty() && (now-timestamps.peek()>=windowSizeMillis)){
                timestamps.poll();
            }

            if(timestamps.size()<capacity){
                timestamps.addLast(now);
                return RateLimitResult.allowed(capacity-timestamps.size());
            }

            long oldestRequest = timestamps.peekFirst();

            long retryAfterMillis =
                    windowSizeMillis - (now - oldestRequest);
            return RateLimitResult.rejected(
                    retryAfterMillis,
                    "Sliding window log limit exceeded. Try again in "
                            + retryAfterMillis + " ms"
            );
        }
    }
}
