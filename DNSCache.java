import java.util.*;

class DNSEntry {
    String ipAddress;
    long expiryTime;

    public DNSEntry(String ipAddress, int ttlSeconds) {
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private final int MAX_SIZE;

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public DNSCache(int maxSize) {
        this.MAX_SIZE = maxSize;

        cache = new LinkedHashMap<String, DNSEntry>(maxSize, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_SIZE; // LRU eviction
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                cacheHits++;
                System.out.println("Cache HIT");
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        cacheMisses++;

        // Simulate upstream DNS lookup
        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(ip, 300));

        System.out.println("Cache MISS → Queried upstream");

        return ip;
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + r.nextInt(255);
    }

    // Remove expired entries (background cleanup)
    public synchronized void cleanupExpired() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    // Cache statistics
    public void getCacheStats() {

        int total = cacheHits + cacheMisses;
        double hitRate = total == 0 ? 0 : ((double) cacheHits / total) * 100;

        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache(5);

        System.out.println("IP: " + dns.resolve("google.com"));
        System.out.println("IP: " + dns.resolve("google.com"));

        dns.getCacheStats();
    }
}