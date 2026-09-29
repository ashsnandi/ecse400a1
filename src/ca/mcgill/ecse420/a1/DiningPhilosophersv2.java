package mcgill.ecse420.a1;

public class DiningPhilosophersv2 {

  public static void main(String[] args) {
    int numberOfPhilosophers = 5;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];

    // Create the chopsticks shared by the philosophers.
    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Object();
    }

    // Create each philosopher and start a thread for them
    for (int i = 0; i < numberOfPhilosophers; i++) {
      int rightChopstickId = (i + 1) % numberOfPhilosophers;

      philosophers[i] =
          new Philosopher(
              i,
              chopsticks[i],
              chopsticks[rightChopstickId],
              i,
              rightChopstickId);

      new Thread(philosophers[i]).start();
    }
  }

  // Represents one philosopher at the table
  public static class Philosopher implements Runnable {

    private final int id;
    private final Object leftChopstick;
    private final Object rightChopstick;
    private final int leftChopstickId;
    private final int rightChopstickId;

    // Creates a philosopher with a left and right chopstick
    public Philosopher(
        int id,
        Object leftChopstick,
        Object rightChopstick,
        int leftChopstickId,
        int rightChopstickId) {
      this.id = id;
      this.leftChopstick = leftChopstick;
      this.rightChopstick = rightChopstick;
      this.leftChopstickId = leftChopstickId;
      this.rightChopstickId = rightChopstickId;
    }

    @Override
    public void run() {
      while (true) {
        System.out.println("Philosopher " + id + " is thinking.");

        Object firstChopstick;
        Object secondChopstick;

        // Always pick up the lower-numbered chopstick first.
        if (leftChopstickId < rightChopstickId) {
          firstChopstick = leftChopstick;
          secondChopstick = rightChopstick;
        } else {
          firstChopstick = rightChopstick;
          secondChopstick = leftChopstick;
        }

        synchronized (firstChopstick) {
          System.out.println("Philosopher " + id + " picked up first chopstick.");

          synchronized (secondChopstick) {
            System.out.println("Philosopher " + id + " picked up second chopstick.");
            System.out.println("Philosopher " + id + " is eating.");
          }
        }
      }
    }
  }
}