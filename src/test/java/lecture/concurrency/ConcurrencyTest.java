package lecture.concurrency;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyTest {
    @Test
    public void thread() throws InterruptedException {
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " running...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Thread thread = new Thread(task);
        thread.run();
        thread.start();
//        Thread.sleep(3000);
//        thread.start();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newFixedThreadPool(12);
        executor.submit(task);
        executor.submit(task);
        executor2.submit(task);
        executor2.submit(task);

        Thread.sleep(10000);
    }

    @Test
    public void reentrantLockTryLock() throws InterruptedException {
        final ReentrantLock lockA = new ReentrantLock();
        final ReentrantLock lockB = new ReentrantLock();
        Thread thread1 = new Thread(() -> {
            if (lockA.tryLock()) { // Acquires Lock A
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired Lock A");
                    try {
                        System.out.println(Thread.currentThread().getName() + " is working...");
                        Thread.sleep(4000); // Simulate work
                    } catch (InterruptedException ignored) {
                    }

                    System.out.println(Thread.currentThread().getName() + " is acquiring Lock B...");
                    if (lockB.tryLock()) { // Tries to acquire Lock B
                        try {
                            System.out.println(Thread.currentThread().getName() + " acquired Lock B");
                        } finally {
                            lockB.unlock();
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " cannot acquire Lock B");
                    }
                } finally {
                    lockA.unlock();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            if (lockB.tryLock()) { // Acquires Lock B
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired Lock B");
                    try {
                        System.out.println(Thread.currentThread().getName() + " is working...");
                        Thread.sleep(3000); // Simulate work
                    } catch (InterruptedException ignored) {
                    }

                    System.out.println(Thread.currentThread().getName() + " is acquiring Lock A...");
                    if (lockA.tryLock()) { // Tries to acquire Lock A
                        try {
                            System.out.println(Thread.currentThread().getName() + " acquired Lock A");
                        } finally {
                            lockA.unlock();
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " cannot acquire Lock A");
                    }
                } finally {
                    lockB.unlock();
                }
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(10000);
    }

    @Test
    public void reentrantLockFairness() throws InterruptedException {
        int threads = 10;
        final ReentrantLock lock = new ReentrantLock(true);

        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(threadName + " is acquiring lock...");
            lock.lock();
            try {
                System.out.println(threadName + " acquired the lock.");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        Thread.sleep(35000);
    }

    @Test
    public void reentrantLockInterruptible() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();

        Runnable task = () -> {
            try {
                lock.lockInterruptibly(); // Allows thread interruption
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired lock.");
                    Thread.sleep(3000);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted.");
            }
        };


        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        Thread.sleep(1000);
        t2.interrupt(); // Interrupt t2 while it's waiting
        Thread.sleep(5000);
    }

    @Test
    public void condition() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        Person dataAvailable = new Person();

        Thread producer = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Producer: Generating data...");
                Thread.sleep(2000); // Simulate data production
                dataAvailable.name = "Bob";
                condition.signal(); // Notify the consumer
                System.out.println("Producer: Data ready, notified consumer!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread consumer = new Thread(() -> {
            lock.lock();
            try {
                while (dataAvailable.name == null) {
                    System.out.println("Consumer: Waiting for data...");
                    condition.await(); // Releases the lock temporarily and waits until another thread signals it
                }
                System.out.println("Consumer: Data received, processing...");
                System.out.println("Consumer: Consuming data... " + dataAvailable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        consumer.start();
        producer.start();

        Thread.sleep(4000);
    }

    @Test
    public void countdownLatch() throws InterruptedException {
        int runners = 5;
        CountDownLatch startSignal = new CountDownLatch(1); // To start the race
        CountDownLatch doneSignal = new CountDownLatch(runners); // To wait for all runners to finish

        Runnable runner = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " is ready at the starting line.");
                startSignal.await(); // Wait for the start signal
                System.out.println(Thread.currentThread().getName() + " started running!");
                Thread.sleep((long) (Math.random() * 3000)); // Simulate running time
                System.out.println(Thread.currentThread().getName() + " finished the race!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                doneSignal.countDown(); // Notify that the runner has finished
            }
        };

        for (int i = 0; i < runners; i++) {
            new Thread(runner).start();
        }

        System.out.println("Referee: Get ready...");
        Thread.sleep(2000); // Simulate preparation time
        System.out.println("Referee: Go!");
        startSignal.countDown(); // Signal all runners to start

        doneSignal.await(); // Wait for all runners to finish
        System.out.println("Race Finished! 🏁");
    }

    @Test
    public void semaphore() throws InterruptedException {
        int parkingSpots = 3;
        Semaphore semaphore = new Semaphore(parkingSpots); // 3 parking spaces available
        CountDownLatch latch = new CountDownLatch(10);

        Runnable car = () -> {
            String name = "Car-" + Thread.currentThread().getName();
            try {
                System.out.println(name + " is trying to enter the parking lot.");

                // Acquire a parking spot (blocking if no spot is available)
                semaphore.acquire();

                System.out.println(name + " has parked. 🚗");
                Thread.sleep((long) (Math.random() * 5000)); // Simulate parking time

                System.out.println(name + " is leaving the parking lot. 🏁");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); // Release the spot for other cars
                latch.countDown();
            }
        };

        for (int i = 1; i <= 10; i++) {
            new Thread(car).start();
        }

        latch.await();
    }

    @Test
    public void atomicVars() {
        final AtomicLong counter = new AtomicLong(1);

        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                long id = counter.getAndIncrement();
                System.out.println(Thread.currentThread().getName() + " generated ID: " + id);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final counter value: " + counter.get());
    }

    static class Person {
        String name;

        Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
