import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.Function;

public class GenericsFeature {


    @Test
    public void genericWithFunctions(){
        applyFunction(s -> 1981);
        applyFunction(s -> "1981");
    }


    public <T> T applyFunction(Function<String, T> function) {
        return function.apply("hello world");
    }

    public <T> Try<T> applyTryFunction(Function<String, T> function) {
        return Try.of(()->function.apply("hello world"));
    }
}
