import java.util.*;

public class UsernameChecker {

    // username -> userId
    private HashMap<String, Integer> usernames;

    // username -> attempt frequency
    private HashMap<String, Integer> attempts;

    public UsernameChecker() {
        usernames = new HashMap<>();
        attempts = new HashMap<>();
    }

    // Register a username
    public void register(String username, int userId) {
        usernames.put(username, userId);
    }

    // Check username availability
    public boolean checkAvailability(String username) {

        // track attempts
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !usernames.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernames.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // modify characters
        String modified = username.replace("_", ".");
        if (!usernames.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }

    // Test program
    public static void main(String[] args) {

        UsernameChecker system = new UsernameChecker();

        system.register("john_doe", 1);
        system.register("admin", 2);

        System.out.println(system.checkAvailability("john_doe"));   
        // false

        System.out.println(system.checkAvailability("jane_smith")); 
        // true

        System.out.println(system.suggestAlternatives("john_doe")); 

        System.out.println(system.getMostAttempted());
    }
}