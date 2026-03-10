import java.util.*;

class TokenBucket {

    int tokens;
    int maxTokens;
    double refillRate;
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens based on elapsed time
    private void refill() {

        long now = System.currentTimeMillis();

        double tokensToAdd =
                (now - lastRefillTime) / 1000.0 * refillRate;

        tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);

        lastRefillTime = now;
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }
}

public class RateLimiter {

    private HashMap<String, TokenBucket> clients = new HashMap<>();

    private final int LIMIT = 1000;

    private final double REFILL_RATE = 1000.0 / 3600.0;

    // Check rate limit
    public synchronized String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.getRemainingTokens()
                    + " requests remaining)";
        }

        return "Denied (0 requests remaining)";
    }

    // Status API
    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "No usage yet";
        }

        int used = LIMIT - bucket.getRemainingTokens();

        return "{used: " + used +
                ", limit: " + LIMIT + "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        System.out.println(
                limiter.checkRateLimit("abc123"));

        System.out.println(
                limiter.checkRateLimit("abc123"));

        System.out.println(
                limiter.getRateLimitStatus("abc123"));
    }
}