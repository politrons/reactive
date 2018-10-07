package java11;

import com.esotericsoftware.kryo.NotNull;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * In Java 11 it allow add var in lambdas which allows you to add annotations
 * to the parameters like @Nonnull or @Nullable.
 */
public class LocalVariableFeature {


    @Test
    public void nullAllowed() {
        var numbers = new String[]{"**************", "hello", "local", "variable", null, "in", "lambdas"};
        Arrays.stream(numbers)
                .forEach((@Nullable var out) -> System.out.println(out));
    }

    @Test
    public void notNullAllowed() {
        var numbers = new String[]{"hello", "local", "variable", null, "in", "lambdas"};
        Arrays.stream(numbers)
                .filter((@NonNull var a) -> a.length() < 10)
                .forEach(System.out::println);
    }


}
