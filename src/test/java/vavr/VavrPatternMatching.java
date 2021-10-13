package vavr;

import io.vavr.Predicates;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import static io.vavr.API.*;
import static io.vavr.Patterns.*;

public class VavrPatternMatching {

    @Test
    public void optionPattern() {
        var option = Option.of("hello world");
        var output = Match(option).of(
                Case($Some($()), value -> "defined"),
                Case($None(), "empty")
        );
        println(output);
    }

       @Test
    public void tryPattern() {

        var output = Match(Try.of(() ->"hello world")).of(
                Case($Success($()), value -> {
                    return "success";
                }),
                Case($Failure($(Predicates.instanceOf(NullPointerException.class))), e -> "error" + e)
        );
        println(output);
    }


}
