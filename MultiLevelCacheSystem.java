import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    // L1 Cache (Memory)
    private LinkedHashMap<String, VideoData> L1;

    // L2 Cache (SSD simulation)
    private LinkedHashMap<String, VideoData> L2;

    // L3 Database
    private HashMap<String, VideoData> database;

    // Access counter
    private HashMap<String, Integer> accessCount;

    private int L1_SIZE = 10000;
    private int L2_SIZE = 100000;

    // Statistics
    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    public MultiLevelCacheSystem() {

        L1 = new LinkedHashMap<>(L1_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L1_SIZE;
            }
        };

        L2 = new LinkedHashMap<>(L2_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L2_SIZE;
            }
        };

        database = new HashMap<>();
        accessCount = new HashMap<>();

        // preload some database data
        database.put("video_123", new VideoData("video_123", "Movie A"));
        database.put("video_999", new VideoData("video_999", "Movie B"));
    }

    public VideoData getVideo(String videoId) {

        // L1 Cache
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 Cache
        if (L2.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData data = L2.get(videoId);

            promoteToL1(videoId, data);

            return data;
        }

        System.out.println("L2 Cache MISS");

        // L3 Database
        if (database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData data = database.get(videoId);

            L2.put(videoId, data);

            accessCount.put(videoId,
                    accessCount.getOrDefault(videoId, 0) + 1);

            return data;
        }

        System.out.println("Video not found.");
        return null;
    }

    private void promoteToL1(String videoId, VideoData data) {

        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);

        if (count > 2) {
            L1.put(videoId, data);
            System.out.println("Promoted to L1");
        }
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double l1Rate = (L1Hits * 100.0) / total;
        double l2Rate = (L2Hits * 100.0) / total;
        double l3Rate = (L3Hits * 100.0) / total;

        System.out.println("\nCache Statistics:");
        System.out.println("L1 Hit Rate: " + String.format("%.2f", l1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", l2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", l3Rate) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}