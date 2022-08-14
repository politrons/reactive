package effects;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionalEffect {

    @Test
    public void optionEffect() {
        var optionPolEffect = new OptionEffect<String, String>();
        Option<String> optionWorld = optionPolEffect.pure("hello option world");
        optionPolEffect.map(optionWorld, input -> input + "!!!!")
                .forEach(System.out::println);
        optionPolEffect.flatMap(optionWorld, input -> Option.of(input + " with composition"))
                .forEach(System.out::println);

    }

    @Test
    public void tryEffect() {
        var tryEffect = new TryEffect<String,String>();
        Try<String> tryWorld = tryEffect.pure("hello try world");
        System.out.println(tryWorld);
        tryEffect.map(tryWorld, input -> input + "!!!!")
                .forEach(System.out::println);
        tryEffect.flatMap(tryWorld, input -> Try.success(input + " with composition"))
                .forEach(System.out::println);
    }

    @Test
    public void polMonadEffects() {
        new PolMonad<String, String>().pure("hello Pol Monad")
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

    /**
     * Monad implementation to control side-effects and allow to do
     * [map] transformation
     * [flatMap] composition
     */
    static class PolMonad<A,B> {

        public A a;

        public PolMonad(){}

        public PolMonad(A a){
            this.a=a;
        }

        boolean isDefined(){
            return Option.of(this.a).isDefined();
        }

        public PolMonad<A, B> pure(A a) {
            return new PolMonad<>(a);
        }

        public PolMonad<A,B> map(Function<A, B> function){
            if (this.isDefined()) {
                return new PolMonad(function.apply(this.a));
            } else {
                return this;
            }
        }

        public PolMonad<A,B> flatMap(Function<A, PolMonad<A, B>> function){
            if (this.isDefined()) {
                return function.apply(this.a);
            } else {
                return this;
            }
        }

        public void forEach(Consumer<A> consumer){
            consumer.accept(a);
        }
    }

    /**
     * Contract to be used and implemented for the effect system that we want
     */
    interface PolEffect<A, B, M> {

        M pure(A a);

        M map(M input, Function<A, B> function);

        M flatMap(M input, Function<A, M> function);

    }

    /**
     * Implementation of Option effect, using Vavr Option monad
     */
    record OptionEffect<A, B>() implements PolEffect<A, B, Option<?>> {
        @Override
        public Option<A> pure(A a) {
            return Option.of(a);
        }

        @Override
        public Option<B> map(Option<?> input, Function<A, B> function) {
            if (input.isDefined()) {
                return Option.of(function.apply((A) input.get()));
            } else {
                return Option.none();
            }
        }

        @Override
        public Option<B> flatMap(Option<?> input, Function<A, Option<?>> function) {
            if (input.isDefined()) {
                return (Option<B>) function.apply((A) input.get());
            } else {
                return Option.none();
            }
        }
    }

    /**
     * Implementation of Try effect, using Vavr Try monad
     */
    record TryEffect<A,B>() implements PolEffect<A, B, Try<?>> {

        @Override
        public Try<A> pure(A a) {
            return Try.success(a);
        }

        @Override
        public Try<B> map(Try<?> input, Function<A, B> function) {
            if (input.isSuccess()) {
                return Try.success(function.apply((A) input.get()));
            } else {
                return Try.failure(input.getCause());
            }
        }

        @Override
        public Try<B> flatMap(Try<?> input, Function<A, Try<?>> function) {
            if (input.isSuccess()) {
                return (Try<B>) function.apply((A) input.get());
            } else {
                return Try.failure(input.getCause());
            }
        }
    }

}

