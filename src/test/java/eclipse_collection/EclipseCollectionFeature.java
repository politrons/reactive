package eclipse_collection;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.junit.Test;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
         */
        ImmutableList<String> immutableListEmpty = Lists.immutable.empty();

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

    @Test
    public void mapFeature() {

        final ImmutableMap<String, Integer> map = Maps.immutable.of("key1", 1981, "key2", 666, "key3", 1000);
        System.out.println(map);

        final ImmutableBag<Integer> select = Maps.immutable.of("key1", 1981, "key2", 666, "key3", 1000)
                .select(value -> value >= 1000);
        System.out.println(select);

    }

}
