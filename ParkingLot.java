import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        this.occupied = false;
    }
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int size;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        size++;

        System.out.println("Assigned spot #" + index +
                " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index].occupied) {

            if (licensePlate.equals(table[index].licensePlate)) {

                long duration =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5;

                table[index].occupied = false;
                table[index].licensePlate = null;

                size--;

                System.out.println("Spot #" + index +
                        " freed, Duration: " +
                        String.format("%.2f", hours) +
                        "h, Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    // Statistics
    public void getStatistics() {

        double occupancy =
                (size * 100.0) / capacity;

        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}