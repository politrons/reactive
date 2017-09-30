package java9;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

/**
 * Java 9 introduce Flow, an API to finally do reactive programing with Java.
 * It´s based like other Reactive stream libraries in the Observer pattern([Publisher] -> [Subscriber])
 * Here Flow it´s just an extension to Stream, where instead of just return a value in the stream, we have
 * the possibility to pass the item to a [Publisher] which it will have or not a [Subscriber] associated.
 * <p>
 * By default the subscription in Java 9 is asynchronous. We need to create
 **/
public class FlowFeatures {


    /**
     * Just like the subscriber in RxJava, where we define the 4 callbacks:
     * <p>
     * onSubscribe:Invoked when we subscribe to the publisher.
     * onNext:Invoked when an item is emitted by the publisher.
     * onError:Invoked when an error happens in the pipeline.
     * onComplete: Invoked when the publisher finish emitting items.
     *
     * @param <T>
     */
    public class CustomSubscriber<T> implements Flow.Subscriber<T> {

        protected Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            System.out.println("Subscription done:");
            subscription.request(1);
        }

        @Override
        public void onNext(T item) {
            System.out.println("Got : " + item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Done");
        }
    }

    class CancelSubscriptionSubscriber extends CustomSubscriber<Integer> {
        @Override
        public void onNext(Integer item) {
            System.out.println("Got : " + item);
            if (item > 30) {
                subscription.cancel();
            } else {
                subscription.request(1);
            }
        }
    }

    class CancelSubscriber extends CustomSubscriber<String> {
        int i = 0;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            if (i == 0) {
                i++;
                subscription.cancel();
            } else {
                System.out.println("Subscription done:");
                subscription.request(1);
            }
        }
    }

    /**
     * A simple publisher which will receive items form the stream to be passed to the subscriber.
     * We can use all the commons operator from Stream before submit the items to the publisher.
     */
    @Test
    public void testPublisher() throws InterruptedException {
        //Create Publisher for expected items Strings
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        //Register Subscriber
        publisher.subscribe(new CustomSubscriber<>());

        //Publish items
        System.out.println("Publishing Items...");
        String[] items = {"1", "A", "2", "B", "3", "C"};
        Arrays.stream(items)
                .filter(item -> item.chars().allMatch(Character::isAlphabetic))
                .map(String::toUpperCase)
                .forEach(publisher::submit);
        Thread.sleep(500);
        publisher.close();
    }

    /**
     * In Flow you have the Flow.subscription which we use as mechanism to say the publisher
     * to continue the emission of items, it could be consider a short of backPressure mechanism, but it´s not
     * as good as RxJava operator such as buffer/window, so just in general Java 9 Flow is just a started to introduce
     * in the reactive programing.
     * <p>
     * We can stop the emission by using [cancel] operator or just passing in the request a 0 < value
     */
    @Test
    public void testCancelSubscription() throws InterruptedException {
        //Create Publisher for expected types Integer
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        //Register Subscriber
        publisher.subscribe(new CancelSubscriptionSubscriber());

        //Publish items
        System.out.println("Publishing Items...");
        Integer[] items = {1, 2, 3, 4, 5, 6};
        Arrays.stream(items)
                .filter(item -> item < 10)
                .map(item -> item * 10)
                .forEach(publisher::submit);
        Thread.sleep(500);
        publisher.close();
    }

    /**
     * Offer operator pass an item to the publisher and in case this one is rejected because for instance the buffer is
     * full, it will execute the predicate function, and in case it return true, it will retry the emission of the item.
     */
    @Test
    public void offer() throws InterruptedException {
        SubmissionPublisher<String> publisher =
                new SubmissionPublisher<>(ForkJoinPool.commonPool(), 2);
        // Register Subscriber
        for (int i = 0; i < 5; i++) {
            publisher.subscribe(new CustomSubscriber<>());
        }
        // publish 3 items for each subscriber
        for (int i = 0; i < 3; i++) {
            int result = publisher.offer("item" + i, (subscriber, value) -> {
                // sleep for a small period before deciding whether to retry or not
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;  // you can switch to true to see that drops are reduced
            });
            // show the number of dropped items
            if (result < 0) {
                System.err.println("dropped: " + result);
            }
        }
        Thread.sleep(5000);
        publisher.close();


    }

    /**
     * TransformerProcessor it´s just like Transformer in RxJava, a component that acts as both a Subscriber and Publisher.
     * The processor sits between the Publisher and Subscriber.
     * <p>
     * The TransformerProcessor will be created specifying the input/output type [[TransformerProcessor<String, Integer>]]
     */
    public class TransformerProcessor<T, R> extends SubmissionPublisher<R> implements Flow.Processor<T, R> {

        private Function function;
        private Flow.Subscription subscription;

        public TransformerProcessor(Function<? super T, ? extends R> function) {
            super();
            this.function = function;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(T item) {
            submit((R) function.apply(item));
            subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onComplete() {
            close();
        }
    }


    @Test
    public void testTransformer() throws InterruptedException {
        //Create Publisher
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        //Create Processor
        TransformerProcessor<String, Integer> transformProcessor = new TransformerProcessor<>(Integer::parseInt);

        //Chain Processor and Subscriber
        transformProcessor.subscribe(new CustomSubscriber<>());
        publisher.subscribe(transformProcessor);

        System.out.println("Publishing Items...");
        String[] items = {"1", "2", "3", "4"};
        Arrays.stream(items).forEach(publisher::submit);
        Thread.sleep(500);
        publisher.close();
    }


}
