import java.util.*;

class PageStats {
    String url;
    int visits;

    PageStats(String url, int visits) {
        this.url = url;
        this.visits = visits;
    }
}

public class RealTimeAnalytics {

    // page -> visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // page -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();


    // Process incoming page event
    public void processEvent(String url, String userId, String source) {

        // Count page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // Count traffic source
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    // Get Top 10 pages
    public List<PageStats> getTopPages() {

        PriorityQueue<PageStats> pq =
                new PriorityQueue<>((a, b) -> b.visits - a.visits);

        for (String page : pageViews.keySet()) {
            pq.add(new PageStats(page, pageViews.get(page)));
        }

        List<PageStats> result = new ArrayList<>();

        for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }

        return result;
    }

    // Dashboard output
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<PageStats> top = getTopPages();

        int rank = 1;

        for (PageStats p : top) {

            int unique = uniqueVisitors.getOrDefault(p.url,
                    new HashSet<>()).size();

            System.out.println(rank + ". " + p.url +
                    " - " + p.visits +
                    " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;
        for (int count : trafficSources.values()) {
            total += count;
        }

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = (count * 100.0) / total;

            System.out.println(source + ": " +
                    String.format("%.1f", percent) + "%");
        }
    }


    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_456", "facebook");
        analytics.processEvent("/sports/championship", "user_111", "google");
        analytics.processEvent("/sports/championship", "user_222", "direct");
        analytics.processEvent("/sports/championship", "user_333", "google");

        analytics.getDashboard();
    }
}