import java.util.concurrent.Semaphore;
import java.util.Random;

public class ClothingStore {
    private static int num_customers;
    private static int simulation_time_seconds;

    private Semaphore fittingRooms;
    private Semaphore waitingChairs;
    private Random random;

    public ClothingStore(int numFittingRooms, int numWaitingChairs, int simulationTimeSeconds) {
        num_customers = numFittingRooms + numWaitingChairs;
        simulation_time_seconds = simulationTimeSeconds;

        fittingRooms = new Semaphore(numFittingRooms, true);
        waitingChairs = new Semaphore(numWaitingChairs, true);
        new Semaphore(1, true);
        random = new Random();
    }

    public void runSimulation() {
        Thread[] customers = new Thread[num_customers];

        for (int i = 0; i < num_customers; i++) {
            customers[i] = new Thread(new Customer(i));
            customers[i].start();
        }

        try {
            Thread.sleep(simulation_time_seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread customer : customers) {
            customer.interrupt();
        }
    }

    class Customer implements Runnable {
        private final int id;

        public Customer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(random.nextInt(1000));

                    if (waitingChairs.tryAcquire()) {
                        System.out.println("Customer # " + id + " enters the system");

                        System.out.println("    Customer # " + id + " enters the waiting area and has a seat. We have " +
                                (num_customers - waitingChairs.availablePermits()) + " waiting");

                        fittingRooms.acquire();
                        waitingChairs.release();
                        System.out.println("        Customer # " + id + " enters the fitting room. We have " +
                                fittingRooms.availablePermits() + " changing and " + waitingChairs.availablePermits() + " waiting.");
                        

                        System.out.println("            Customer # " + id + " leaves the fitting room.");

                        fittingRooms.release();
                      


                    } else {
                        System.out.println("                Customer # " + id + " left in frustration.");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        final int numFittingRooms = Integer.parseInt(args[1]);
        final int simulationTime = Integer.parseInt(args[0]);
        final int numWaitingChairs = numFittingRooms * 2;
        ClothingStore store = new ClothingStore(numFittingRooms, numWaitingChairs, simulationTime);
        store.runSimulation();
    }
}
