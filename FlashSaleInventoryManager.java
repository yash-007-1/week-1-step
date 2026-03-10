import java.util.*;

public class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> inventory;

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Boolean>> waitingList;

    public FlashSaleInventoryManager() {
        inventory = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!inventory.containsKey(productId)) {
            return "Product not found";
        }

        int stock = inventory.get(productId);

        if (stock > 0) {
            inventory.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        }

        // Add to waiting list
        LinkedHashMap<Integer, Boolean> queue = waitingList.get(productId);
        queue.put(userId, true);

        int position = queue.size();

        return "Added to waiting list, position #" + position;
    }

    // View waiting list
    public List<Integer> getWaitingList(String productId) {

        if (!waitingList.containsKey(productId)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(waitingList.get(productId).keySet());
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Simulate stock finished
        manager.inventory.put("IPHONE15_256GB", 0);

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}