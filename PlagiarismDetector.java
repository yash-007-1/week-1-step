import java.util.*;

public class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> index;

    // document -> its n-grams
    private HashMap<String, List<String>> documentNgrams;

    private int N = 5; // 5-gram

    public PlagiarismDetector() {
        index = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String existingDoc : index.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        // Calculate similarity
        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);
            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");

            System.out.println("Similarity: " +
                    String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "machine learning is a field of artificial intelligence that uses data";
        String essay2 = "machine learning is a field of artificial intelligence used for data analysis";

        detector.addDocument("essay_092.txt", essay1);

        detector.analyzeDocument("essay_123.txt", essay2);
    }
}