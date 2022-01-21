package vavr;

import io.vavr.Predicates;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import static io.vavr.API.*;
import static io.vavr.Patterns.*;
import static io.vavr.Predicates.*;

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

    @Test
    public void eitherPattern() {
        var output = Match(Either.right("hello world")).of(
                Case($Right($()), value -> {
                    return "success";
                }),
                Case($Left($(Predicates.instanceOf(NullPointerException.class))), e -> "error" + e)
        );
        println(output);
    }


    @Test
    public void typeClassesPattern() {
        Object obj = new A("Hello A");
        String of = Match(obj).of(
                Case($(instanceOf(A.class)), a -> a.a),
                Case($(instanceOf(B.class)), b -> b.b)
        );
        System.out.println(of);
    }

    @Test
    public void booleanPattern() {
        var output = Match(false).of(
                Case(($(true)), value -> "found"),
                Case(($(false)), value -> "not found")
        );
        println(output);
    }

    static class A {
        public A(String a) {
            this.a = a;
        }

        public String a;
    }
    static class B {
        public B(String b) {
            this.b = b;
        }

        public String b;
    }

}
