package effects;

import io.vavr.control.Option;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class PolEffectSystem {

    @Test
    public void polMonadEffects() {
        new PolMonad<String, String>().pure("hello Pol Monad")
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

    @Test
    public void polMonadFromOption() {
        new PolMonad<String, String>().fromOption(Option.of("hello option world"))
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

    interface PolEffect<A,B> {

        boolean isDefined();

        PolMonad<A, B> pure(A input);

        PolMonad<A,B> map(Function<A, B> function);

        PolMonad<A,B> flatMap(Function<A, PolMonad<A, B>> function);

        void forEach(Consumer<A> consumer);

        PolMonad<A, B> fromOption(Option<A> input);

    }

    /**
     * Monad implementation to control side-effects and allow to do
     * [map] transformation
     * [flatMap] composition
     */
    static class PolMonad<A,B> implements PolEffect<A,B>{

        public A value;

        public PolMonad(){}

        public PolMonad(A input){
            this.value =input;
        }

        @Override
        public boolean isDefined(){
            return Option.of(this.value).isDefined();
        }

        @Override
        public PolMonad<A, B> pure(A a) {
            return new PolMonad<>(a);
        }

        @Override
        public PolMonad<A,B> map(Function<A, B> function){
            if (this.isDefined()) {
                return new PolMonad(function.apply(this.value));
            } else {
                return this;
            }
        }

        @Override
        public PolMonad<A,B> flatMap(Function<A, PolMonad<A, B>> function){
            if (this.isDefined()) {
                return function.apply(this.value);
            } else {
                return this;
            }
        }

        @Override
        public void forEach(Consumer<A> consumer){
            consumer.accept(value);
        }

        @Override
        public PolMonad<A, B> fromOption(Option<A> input) {
            if (input.isDefined()) {
                return new PolMonad<>(input.get());
            } else {
                this.value=null;
                return this;
            }
        }
    }

}

