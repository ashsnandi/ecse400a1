package ca.mcgill.ecse420.a1;

public class DiningPhilosophersv1 {

  public static void main(String[] args) {
    int numberOfPhilosophers = 5;
    Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
    Object[] chopsticks = new Object[numberOfPhilosophers];

    // Create the chopsticks shared by the philosophers
    for (int i = 0; i < numberOfPhilosophers; i++) {
      chopsticks[i] = new Object();
    }

    // Create each philosopher and start a thread for them
    for (int i = 0; i < numberOfPhilosophers; i++) {
      philosophers[i] =
          new Philosopher(
              i, chopsticks[i], chopsticks[(i + 1) % numberOfPhilosophers]);

      new Thread(philosophers[i]).start();
    }
  }

  // Represents one philosopher at the table
  public static class Philosopher implements Runnable {

    private final int id;
    private final Object leftChopstick;
    private final Object rightChopstick;

    // Creates a philosopher with a left and right chopstick
    public Philosopher(int id, Object leftChopstick, Object rightChopstick) {
      this.id = id;
      this.leftChopstick = leftChopstick;
      this.rightChopstick = rightChopstick;
    }

    @Override
    public void run() {
      while (true) {
        System.out.println("Philosopher " + id + " is thinking.");

        // Only one philosopher can use this chopstick at a time
        synchronized (leftChopstick) {
          System.out.println("Philosopher " + id + " picked up left chopstick.");

          try {
            // Give the other philosophers time to pick up their left chopstick
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }

          // Wait until the right chopstick is available
          synchronized (rightChopstick) {
            System.out.println("Philosopher " + id + " picked up right chopstick.");
            System.out.println("Philosopher " + id + " is eating.");
          }
        }
      }
    }
  }
}