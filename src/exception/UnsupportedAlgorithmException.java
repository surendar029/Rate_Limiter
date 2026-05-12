package exception;

import enums.RateLimiterType;

public class UnsupportedAlgorithmException extends RateLimiterException{
    public UnsupportedAlgorithmException(RateLimiterType type) {
        super("No implementation registered for rate limiter type: " + type.name()
                + ". Supported types: TOKEN_BUCKET, LEAKY_BUCKET, FIXED_WINDOW, "
                + "SLIDING_WINDOW_LOG, SLIDING_WINDOW_COUNTER");
    }
}
