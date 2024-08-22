package communication;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class ChannelFeature {

    public static class Channel<T> {

        private final BlockingQueue<T> queue;

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        // Private constructor to initialize the BlockingQueue with the specified capacity.
        private Channel(int capacity) {
            this.queue = new ArrayBlockingQueue<>(capacity);
        }

        /**
         * Static factory method to create a Channel with a default capacity of 1.
         *
         * @param <T> The type of messages this channel will handle.
         * @return A new Channel instance with a capacity of 1.
         */
        public static <T> Channel<T> make() {
            return new Channel<>(1);
        }

        /**
         * Static factory method to create a Channel with a specified capacity.
         *
         * @param <T>      The type of messages this channel will handle.
         * @param capacity The maximum number of messages that can be held in the channel at any one time.
         * @return A new Channel instance with the specified capacity.
         */
        public static <T> Channel<T> make(int capacity) {
            return new Channel<>(capacity);
        }

        /**
         * Sends a message to the channel by placing the result of the supplied function
         * into the BlockingQueue. The send operation is performed in a new virtual thread.
         * If the queue is full, this method will block until space is available.
         *
         * @param func A Supplier function that produces the message to be sent to the channel.
         * @throws RuntimeException if the thread is interrupted while waiting to send the message.
         */
        public void send(Supplier<T> func) {
            appendInQueue(func);
        }

        public void send(T t) {
            appendInQueue(() -> t);
        }

        private void appendInQueue(Supplier<T> func) {
            Thread.ofVirtual().start(() -> {
                try {
                    queue.put(func.get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupt status
                    throw new RuntimeException("Thread was interrupted while sending", e);
                }
            });
        }

        /**
         * Receives a message from the channel by retrieving and removing the head of the BlockingQueue.
         * If the queue is empty, this method will block for the specified timeout duration, waiting for a message.
         *
         * @param timeout The maximum time to wait for a message.
         * @param unit    The time unit of the timeout argument.
         * @return The message received from the channel.
         * @throws RuntimeException if the timeout expires before a message is received or if the thread is interrupted.
         */
        public T receive(long timeout, TimeUnit unit) {
            try {
                T value = queue.poll(timeout, unit);
                if (value == null) {
                    throw new RuntimeException("Timeout waiting for the message");
                }
                return value;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                throw new RuntimeException("Thread was interrupted while receiving", e);
            }
        }

        public T receive() throws InterruptedException {
            return queue.take();
        }

        /**
         * Receives a message from the channel asynchronously by using a CompletableFuture that
         * will complete when a message is available. This method does not block the main thread and
         * returns immediately with a future that can be used to obtain the message once it arrives.
         *
         * @return A CompletableFuture that will be completed with the received message when it becomes available.
         */
        public CompletableFuture<T> receiveAsync() {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return queue.take(); // take() will block the virtual thread but not the platform thread
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupt status
                    throw new RuntimeException("Thread was interrupted while receiving", e);
                }
            }, executor);
        }
    }

    @Test
    void runChannel() throws InterruptedException {
        Channel<String> channel = Channel.make();

        Thread consumer = Thread.ofVirtual().start(() -> {
            String message = null;
            try {
                message = channel.receive();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(STR."Channel received message: \{message}");
        });

        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(1000);
                channel.send(() -> "Hello from producer");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        consumer.join();
    }

    @Test
    void runChannelMultipleThreads() throws InterruptedException {
        Channel<String> channel = Channel.make(2);

        // Sending messages to the channel in virtual threads.
        channel.send(() -> "Message from producer 1");
        channel.send("Message from producer 2");

        // Two consumers receiving messages from the channel.
        Thread consumer1 = Thread.ofVirtual().start(() -> {
            String message = channel.receive(5, TimeUnit.SECONDS);
            System.out.println(STR."Channel Consumer 1 received: \{message}");
        });

        try {
            var message = channel.receiveAsync().get();
            System.out.println(STR."Channel Consumer 2 received: \{message}");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // Wait for both consumers to finish
        consumer1.join();
    }
}
