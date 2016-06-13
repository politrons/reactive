package rx.utils;

import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;

import java.util.Iterator;
import java.util.List;

/**
 * Here we have a comparative between iterator pattern and observer pattern.
 * We can see how iterator pattern is much faster than observer pattern.
 */
public class ObserverVsIterator {

    /**
     * Using iterator pattern we get an iterator from the collection,
     * and we ask in every iteration to the iterator if there´s more dats
     * @param start
     */
    private void pullExample(long start) {
        final List<Integer> list = Lists.newArrayList(1,2,3);
        final Iterator<Integer> iterator = list.iterator();
        System.out.println("Get iterator took:" +(System.currentTimeMillis() - start));
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("pull took:" +(System.currentTimeMillis() - start));
    }

    /**
     * Using observer patter we create our own structure where we push our data,
     * so we control when we finish to iterate over the data.
     * @param start
     */
    private void pushExample(long start) {
        final List<Integer> list = Lists.newArrayList(4,5,6);
        final Observable<Integer> observable = Observable.from(list);
        System.out.println("Create observable took:" +(System.currentTimeMillis() - start));
        observable.subscribe(System.out::println, System.out::println,
                             () -> System.out.println("push took:" +(System.currentTimeMillis() - start)));
    }

    @Test
    public void run() {
        System.out.println("******+Iterator pattern*******");
        pullExample(System.currentTimeMillis());
        System.out.println("******+Observer pattern*******");
        pushExample(System.currentTimeMillis());
    }
}