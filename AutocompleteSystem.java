import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query into trie
    public void insertQuery(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
    }

    // Update frequency
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);

        insertQuery(query);
    }

    // Search suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();

        collectQueries(node, prefix, results);

        // Top 10 by frequency
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(b) - frequencyMap.get(a));

        pq.addAll(results);

        List<String> suggestions = new ArrayList<>();

        for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
            suggestions.add(pq.poll());
        }

        return suggestions;
    }

    // DFS to collect queries
    private void collectQueries(TrieNode node,
                                String prefix,
                                List<String> results) {

        if (node.isEnd) {
            results.add(prefix);
        }

        for (char c : node.children.keySet()) {

            collectQueries(node.children.get(c),
                    prefix + c,
                    results);
        }
    }

    public static void main(String[] args) {

        AutocompleteSystem system =
                new AutocompleteSystem();

        system.updateFrequency("java tutorial");
        system.updateFrequency("javascript");
        system.updateFrequency("java download");
        system.updateFrequency("java 21 features");

        System.out.println(system.search("jav"));
    }
}