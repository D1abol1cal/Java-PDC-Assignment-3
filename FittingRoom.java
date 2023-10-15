import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FittingRoom {
    
    private static Semaphore fittingRooms;
    private static Semaphore waitingArea;
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java FittingRoom <time_seconds> <num_fitting_rooms>");
            return;
        }

        int simulationTime = Integer.parseInt(args[0]);
        int numFittingRooms = Integer.parseInt(args[1]);
        int numWaitingChairs = numFittingRooms * 2;
        int numCustomers = numFittingRooms + numWaitingChairs;

        fittingRooms = new Semaphore(numFittingRooms, true);
        waitingArea = new Semaphore(numWaitingChairs, true);

        System.out.println("Sleep Time : " + simulationTime + "\nFitting rooms : " + numFittingRooms + "\nNumber of chairs in waiting area : " + numWaitingChairs + "\nNumber of customers : " + numCustomers );
        

        Thread systemThread = new Thread(new SystemThread(simulationTime));
        systemThread.start();

        for (int i = 1; i <= numCustomers; i++) {
            Thread customerThread = new Thread(new CustomerThread(i));
            customerThread.start();
        }
    }

    static class SystemThread implements Runnable {
        private int simulationTime;

        public SystemThread(int simulationTime) {
            this.simulationTime = simulationTime;
        }

        @Override
        public void run() {
            // System logic
            try {
                Thread.sleep(simulationTime * 1000);
                System.out.println("System: Simulation ended. Closing the store.");
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CustomerThread implements Runnable {
        private int customerNumber;

        public CustomerThread(int customerNumber) {
            this.customerNumber = customerNumber;
        }

        @Override
        public void run() {
            System.out.println("Customer # " + customerNumber + " enters the system");
            try {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("     Customer # " + customerNumber + " enters the waiting area and has a seat. We have "
                        + waitingArea.availablePermits() + " waiting");

                waitingArea.acquire();
                System.out.println("          Customer # " + customerNumber + " enters fitting room. We have "
                        + fittingRooms.availablePermits() + " changing " + waitingArea.availablePermits() + " waiting");

                fittingRooms.acquire();
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("               Customer # " + customerNumber + " leaves fitting room");

                fittingRooms.release();
                waitingArea.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
