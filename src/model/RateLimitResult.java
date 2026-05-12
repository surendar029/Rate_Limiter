package model;

public class RateLimitResult {
    private boolean allowed;
    private int remainingTokens;
    private long retryAfterMillis;
    private String reason;

    private RateLimitResult(boolean allowed,int remainingTokens,long retryAfterMillis,String reason){
        this.allowed=allowed;
        this.remainingTokens=remainingTokens;
        this.retryAfterMillis=retryAfterMillis;
        this.reason=reason;
    }

    public static RateLimitResult allowed(int remainingTokens){
        return new RateLimitResult(true,remainingTokens,0,"Request Allowed");
    }

    public static RateLimitResult rejected(long retryAfterMillis,String reason){
        return new RateLimitResult(false,0,retryAfterMillis,reason);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }

    public int getRemainingTokens() {
        return remainingTokens;
    }

    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }

    public String toString() {
        return allowed
                ? String.format("ALLOWED [remaining=%d]", remainingTokens)
                : String.format("REJECTED [retryAfter=%dms, reason=%s]", retryAfterMillis, reason);
    }
}
