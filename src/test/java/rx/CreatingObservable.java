package rx;

import org.junit.Test;


/**
 * @author Pablo Perez
 */
public class CreatingObservable {


    /**
     * We create an observable through a subscriber where we define the item emitted in every onNext, and once we finish to emmit we set onComplete,
     * Which make the subscriber to finish.
     */
    @Test
    public void testObservable() {

        Observable.create(subscriber -> {
            for (int n : getNumbers()) {
                System.out.println("generating");
                subscriber.onNext(n);
            }
            subscriber.onCompleted();
        })
                  .subscribe(n -> System.out.println("item: " + n), (error) -> System.out.println("Something went wrong" + error.getMessage()),
                             () -> System.out.println("This observable has finished"));
    }

    public int[] getNumbers() {
        return new int[]{0, 1, 2, 3, 4, 5};
    }


}
