package vavr;

import io.vavr.control.Option;
import org.junit.Test;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

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


}
