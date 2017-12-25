package rx.observables.creating;

import org.junit.Test;
import rx.*;
import rx.internal.util.ActionSubscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 * Normally when we create an observable this one is consume by a observer(Subscription).
 * which is created by subscribe method providing an Action function for onNext, onError and onComplete.
 * Those functions are just like a Java 8 consumer functions
 * Once that we subscribe and we create an observer the main thread is block unitl the observer is unsubscribe from the observable.
 * This happens once the last item of the observable has been process through the pipeline.
 */
public class ObservableSubscription {

    private String foo = "empty";

    int total = 0;

    /**
     * In this test we prove how when we subscribe a observable, this one block the thread until emit all items
     */
    @Test
    public void testObservableSubscriptionBlockMainThread() {
        Integer[] numbers = {0, 1, 2, 3, 4};

        Observable.from(numbers)
                .flatMap(Observable::just)
                .doOnNext(number -> {
                    sleep();
                })
                .subscribe(number -> total += number);
        System.out.println("I finish after all items are emitted:" + total);
    }

    /**
     * Here since we use delay, that makes the pipeline asynchronous,
     * we can check how only when the observable has emit all items the observer is unsubscribed
     */
    @Test
    public void testObservableWaitForUnsubscribed() {
        Subscription subscription = Observable.just(1)
                .delay(5, TimeUnit.MILLISECONDS)
                .subscribe(number -> foo = "Subscription finish");
        while (!subscription.isUnsubscribed()) {
            System.out.println("wait for subscription to finish");
        }
        System.out.println(foo);
    }


    /**
     * In this example we create another observable through the subscription, and we subscribe to be informed when the previous observer was unsubscribed
     * Since we dont want to block our program, we will run in another thread,
     * then and once the pipeline continue we return the result event to the main thread(immediate)
     */
    @Test
    public void testObservableWaitForUnsubscribedListener() {
        Subscription subscription = Observable.just(1)
                .delay(1, TimeUnit.SECONDS)
                .subscribe(number -> foo = "Subscription finish");
        Scheduler mainThread = Schedulers.immediate();
        Observable.just(subscription)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(s -> {
                    while (!s.isUnsubscribed()) {
                        sleep();
                    }
                }).observeOn(mainThread)
                .subscribe(u -> System.out.println("Observer unsubscribed:" + u.toString()));

        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(2, TimeUnit.SECONDS);
    }

    /**
     * The doOnUnsubscribe will be invoked just before the subscriber unsubscribe from the observable
     */
    @Test
    public void testDoOnUnsubscribe() {
        Integer[] numbers = {0, 1, 2, 3, 4};
        Observable.from(numbers)
                .doOnUnsubscribe(() -> System.out.println("Last action must be done here"))
                .subscribe(number -> System.out.println("number:" + number),
                        System.out::println,
                        () -> System.out.println("End of pipeline"));
    }


    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * In every moment we have the possibility to create our own subscriber, which you have to implement ActionSubscriber
     *  with onNext, onError and onComplete functions.
     *  Once that you do that you can attach that subscriber into a subscription.
     *
     *  You can also can add the subscription into a subscriptionList that a subscriber has to know the state of the
     *  subscriptions where he is part of
     */
    @Test
    public void subscriberAndSubscription() {
        Integer[] numbers = {0, 1, 2};

        Subscriber subscriber = new ActionSubscriber(number -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Subscriber number:" + number);
        },
                System.out::println,
                () -> System.out.println("Subscriber End of pipeline"));

        Subscription subscription = Observable.from(numbers).subscribeOn(Schedulers.newThread()).subscribe(subscriber);
        Subscription subscription1 = Observable.from(numbers).subscribeOn(Schedulers.newThread()).subscribe(subscriber);

        subscriber.add(subscription);
        subscriber.add(subscription1);
        System.out.println("Is Unsubscribed??:" + subscriber.isUnsubscribed());


        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(5, TimeUnit.SECONDS);

        System.out.println("Is Unsubscribed??:" + subscriber.isUnsubscribed());

    }


}
