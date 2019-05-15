package vavr;

import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.List;
import org.junit.Test;

import java.util.ArrayList;

public class VavrFunctions {

    @Test
    public void funcFeatures() {
        Function2<String, Integer, Boolean> func1 = (str, intValue) -> str.equals("hello") && intValue == 1981;
        System.out.println(func1);//Function
        Function1<Integer, Boolean> partialFunc = func1.apply("hello");
        System.out.println(partialFunc); //Partial application
        Function1<Integer, Boolean> funcCurried = func1.curried().apply("hello");
        System.out.println(funcCurried); //Curried
        System.out.println(func1.apply("hello", 1981));//Full
    }

    @Test
    public void funcCompositionFeatures() {
        Function2<String, Integer, Boolean> func1 = (str, intValue) -> str.equals("hello") && intValue == 1981;
        Function1<Boolean , String> func2 = (bool) -> bool ? "True" : "False";

        Function2<String, Integer, String> stringIntegerStringFunction2 = func1.andThen(func2);

        String responseTrue = stringIntegerStringFunction2.apply("hello", 1981);
        System.out.println(responseTrue);

        String responseFalse = stringIntegerStringFunction2.apply("hello", 1111);
        System.out.println(responseFalse);//Function
    }

    @Test
    public void funcMemorization() {
        Function0<Long> funMemorized = System::nanoTime;
        Function0<Long> memoized = funMemorized.memoized();
        System.out.println(memoized.apply());
        System.out.println(memoized.apply());
        System.out.println(memoized.apply());
    }
}

