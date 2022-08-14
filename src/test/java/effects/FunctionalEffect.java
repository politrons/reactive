package effects;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.Function;

public class FunctionalEffect {

    @Test
    public void main() {

        Option<String> hello_world = optionPolEffect.pure("hello world");
        System.out.println(hello_world);
        Option<String> map = optionPolEffect.map(hello_world, input -> input + "!!!!");
        System.out.println(map);

        Try<String> tryWorld = tryEffect.pure("hello world");
        System.out.println(tryWorld);
        Try<String> mapTry = tryEffect.map(tryWorld, input -> input + "!!!!");
        System.out.println(mapTry);

    }

    PolEffect<Option> optionPolEffect = new PolEffect<>() {
        @Override
        public <A> Option<A> pure(A a) {
            return Option.of(a);
        }

        @Override
        public <A, B> Option<B> map(Option input, Function<A, B> function) {
            if (input.isDefined()) {
                return Option.of(function.apply((A) input.get()));
            } else {
                return Option.none();
            }
        }
    };

    PolEffect<Try> tryEffect = new PolEffect<>() {

        @Override
        public <A> Try pure(A a) {
            return Try.of(() -> a);
        }

        @Override
        public <A, B> Try map(Try input, Function<A, B> function) {
            if (input.isSuccess()) {
                return input.map(value -> function.apply((A) value));
            } else {
                return Try.failure(input.getCause());
            }
        }
    };

    interface PolEffect<M> {

        <A> M pure(A a);

        <A, B> M map(M input, Function<A, B> function);

    }

}

