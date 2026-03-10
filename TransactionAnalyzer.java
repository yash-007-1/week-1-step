import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long time;
    String account;

    Transaction(int id, int amount, String merchant, long time, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.time = time;
        this.account = account;
    }
}

public class TransactionAnalyzer {

    // Classic Two-Sum
    public static void findTwoSum(List<Transaction> transactions, int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                System.out.println("Two-Sum Pair Found: "
                        + map.get(complement).id + " , " + t.id);
            }

            map.put(t.amount, t);
        }
    }

    // Two-Sum with Time Window (1 hour)
    public static void findTwoSumWithTime(List<Transaction> transactions, int target) {

        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction prev : map.get(complement)) {

                    if (Math.abs(t.time - prev.time) <= 3600) {
                        System.out.println("Time Window Pair: "
                                + prev.id + " , " + t.id);
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }
    }

    // Duplicate Detection
    public static void detectDuplicates(List<Transaction> transactions) {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate Transactions:");

                for (Transaction t : list) {
                    System.out.println("ID:" + t.id +
                            " Account:" + t.account +
                            " Amount:" + t.amount +
                            " Merchant:" + t.merchant);
                }
            }
        }
    }

    // K-Sum
    public static void kSum(List<Integer> nums, int target, int k,
                            int start, List<Integer> path) {

        if (k == 2) {

            HashSet<Integer> set = new HashSet<>();

            for (int i = start; i < nums.size(); i++) {

                int complement = target - nums.get(i);

                if (set.contains(complement)) {

                    List<Integer> result = new ArrayList<>(path);
                    result.add(complement);
                    result.add(nums.get(i));

                    System.out.println("K-Sum Result: " + result);
                }

                set.add(nums.get(i));
            }

            return;
        }

        for (int i = start; i < nums.size(); i++) {

            path.add(nums.get(i));

            kSum(nums, target - nums.get(i), k - 1, i + 1, path);

            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "StoreA", 1000, "acc1"));
        transactions.add(new Transaction(2, 300, "StoreB", 1200, "acc2"));
        transactions.add(new Transaction(3, 200, "StoreC", 1500, "acc3"));
        transactions.add(new Transaction(4, 500, "StoreA", 1600, "acc4"));

        System.out.println("Classic Two Sum:");
        findTwoSum(transactions, 500);

        System.out.println("\nTwo Sum With Time Window:");
        findTwoSumWithTime(transactions, 500);

        System.out.println("\nDuplicate Detection:");
        detectDuplicates(transactions);

        System.out.println("\nK-Sum (3 transactions → target 1000):");

        List<Integer> nums = Arrays.asList(500, 300, 200);
        kSum(nums, 1000, 3, 0, new ArrayList<>());
    }
}