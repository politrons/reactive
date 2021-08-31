package eclipse_collection;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.junit.Test;

import java.sql.Time;
import java.util.List;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://www.eclipse.org/collections/
 */
public class EclipseCollectionFeature {

    /**
     * Eclipse collection can enrich the collection as we know in Java 8 with so much more operators to empower us.
     */
    @Test
    public void listFeature() {
        /**
         * Constructor to create an empty [ImmutableList] of Eclipse collection.
         * Once we have it we can create a new collection adding new element using [newWith]
         * Also Eclipse collection provide some operators to get first and last element like in Scala with
         * [getFirstOptional] and [getLastOptional]
         */
        ImmutableList<String> immutableListEmpty = Lists.immutable.empty();
        var newList = immutableListEmpty.newWith("New element");
        var newList1 = newList.newWith("last element");
        System.out.println(newList1.getFirstOptional());
        System.out.println(newList1.getLastOptional());

        /**
         * We can transform the Eclipse collection into Java stream just using  [stream] operator.
         * Once we do that we can just use the stream API of Java as usual.
         */
        final List<String> strings = Lists.immutable.of("hello", "Eclipse", "functional")
                .stream()
                .map(String::toUpperCase)
                .toList();

        System.out.println(strings);

        /**
         Using [select] operator we can filter the collection values using a predicate function.
         */
        final MutableList<String> mutableList = Lists.immutable.of("hello", "Eclipse", "functional")
                .select(f -> f.length() > 5)
                .toList();

        System.out.println(mutableList);
    }

    /**
     * [IntLists, DoubleList, BooleanList] Allow us to create collection for a very specific type
     */
    @Test
    public void primitiveListFeature() {
        final OptionalInt first = IntLists.immutable.of(1981)
                .primitiveStream()
                .map(value -> value * 100)
                .findFirst();
        System.out.println(first.orElse(0));
    }

    /**
     * With Map, it works exactly the same, it provides a functional API without have to use [Stream]
     * In this example we create an immutable map which means once is created you cannot add any new entry in it.
     * Here in order to add a new entry in the map it would be using [newWithKeyValue] which allow you to add a new key, value
     * and return a new Map with previous entry and new one.
     */
    @Test
    public void mapFeature() {

        final ImmutableMap<String, Integer> map = Maps.immutable.of("key1", 1981, "key2", 666, "key3", 1000);
        final ImmutableMap<String, Integer> newMap = map.newWithKeyValue("newKey", 3000);
        System.out.println(map);
        System.out.println(newMap);

        final ImmutableBag<Integer> select = Maps.immutable.of("key1", 1981, "key2", 666, "key3", 1000)
                .select(value -> value >= 1000);
        System.out.println(select);

    }

    /**
     * [TypeInterval] with fromTo arguments, provide an enrich Iterable of one particular type,
     * which implement useful features like:
     * * [getFirst] get the first element of the iterable
     * * [getLast] get the last element of the iterable
     * * [allSatisfy] passing a predicate function check that all elements pass the condition and return true or false.
     * We can also can do some maths calc like [min, max, average, median] of the elements in the iterator.
     * * [select] to create a new [ImmutableIntList] with the elements that poss the predicate function.
     */
    @Test
    public void intervalFeature() {
        final IntInterval intInterval = IntInterval.fromTo(1, 10);
        final int i = intInterval.get(0);
        System.out.println(i);
        final int first = intInterval.getFirst();
        System.out.println(first);
        final int last = intInterval.getLast();
        System.out.println(last);
        final boolean result = intInterval.allSatisfy(number -> number < 100);
        System.out.println(result);
        System.out.println("Median:" + intInterval.median());
        System.out.println("Average:" + intInterval.average());
        final ImmutableIntList select = intInterval.select(number -> number <= 5);
        System.out.println(select);
    }

    /**
     * One of the greatest feature of Eclipse collection is the memory consumption, and the performance.
     * Here for instance we use the operator [binarySearch] to use the binarySearch algorithm to find an element
     * in the collection.
     * Compare with the [filter] of Stream we see that is so much worst in terms of perforance.
     * Result:
     * BigO time:3677 result:999999
     * BigO time:6401460 result:1000000
     */
    @Test
    public void performanceFeature() {
        final IntInterval intInterval = IntInterval.fromTo(1, 1000000000);

        var start = System.nanoTime();
        final int result = intInterval.binarySearch(1000000);
        System.out.println("BigO time:" + (System.nanoTime() - start) + " result:" + result);

        final IntStream range = IntStream.range(1, 1000000000);

        var start2 = System.nanoTime();
        var result1 = range.filter(number -> number == 1000000).findFirst().getAsInt();
        System.out.println("BigO time:" + (System.nanoTime() - start2) + " result:" + result1);


    }


}
