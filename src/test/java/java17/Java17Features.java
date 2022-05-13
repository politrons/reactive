package java17;

import com.google.common.collect.EvictingQueue;
import org.junit.Test;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class Java17Features {

    @Test
    public void switchPatternMatchingFeature() {
        System.out.println(switchPatternMatching("hello"));
        System.out.println(switchPatternMatching(1981));
        System.out.println(switchPatternMatching(1000L));
        System.out.println(switchPatternMatching(null));
    }

    /**
     * Before Java 17 introduce an improvement in if-else-chain  with [instanceof] adding a variable.
     * Now we have the closet thing to Pattern matching. We are able to cast the initial object type in each case.
     * We can even match null values using case null.
     */
    private String switchPatternMatching(Object o) {
        return switch (o) {
            case null -> String.format("Unknown!");
            case Integer i -> String.format("int %d", i);
            case Long l -> String.format("long %d", l);
            case Double d -> String.format("double %f", d);
            case String s -> String.format("String %s", s);
            default -> o.toString();
        };
    }

    @Test
    public void sealedPermit() {
        new ClassA().helloWorld();
        new ClassB().helloWorld();
    }

    /**
     * Now using [sealed] interface we can specify using [permit] after declaration which classes
     * we will allow to implement this interface.
     * In this example we only allow implement ClassA, ClassB, but classC not, so it wont compile if we uncomment code
     */
    public sealed interface HelloWorldClass permits ClassA, ClassB {

        void helloWorld();
    }

    static final class ClassA implements HelloWorldClass {

        @Override
        public void helloWorld() {
            System.out.println("Hello world from A");
        }
    }

    static final class ClassB implements HelloWorldClass {

        @Override
        public void helloWorld() {
            System.out.println("Hello world from B");
        }
    }

    /**
     * Compilation time errot
     * java: class is not allowed to extend sealed class: (as it is not listed in its permits clause)
     */
//    static class ClassC implements HelloWorldClass {
//
//        @Override
//        public void helloWorld() {
//
//        }
//    }

    /**
     * Java 17 introduce [RandomGeneratorFactory] which allow create [RandomGenerator] passing a
     * name of random number generator algorithm.
     * In this example we use the famous [Xoshiro256PlusPlus]
     */
    @Test
    public void randomGenerator() {
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(999);
        // 0-10
        System.out.println(randomGenerator.nextInt(11));
        // 0-20
        System.out.println(randomGenerator.nextInt(21));
        // All available algorithm in Java 17
        RandomGeneratorFactory.all()
                .map(fac -> fac.group()+ " : " +fac.name())
                .sorted()
                .forEach(System.out::println);
    }

}
