package effects;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class PolEffectSystem {

    @Test
    public void polMonadEffects() {
        new PolMonad<String>().pure("hello Pol Monad")
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

    @Test
    public void polMonadFromOption() {
        new PolMonad<String>().fromOption(Option.of("hello option world"))
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

    @Test
    public void polMonadFromTry() {
        new PolMonad<String>().fromTry(Try.of(()-> "hello try world"))
                .map(String::toUpperCase)
                .flatMap(value -> new PolMonad<>(value + " with composition"))
                .forEach(System.out::println);
    }

//    @Test
//    public void polMonadCurried() {
//        new PolMonad<String, String>().pure("hello Pol CurriedMonad")
//                .curried(input -> input2 -> input  + input2)
//                .map(curriedFunc -> curriedFunc.apply(" world"))
//                .flatMap(value -> new PolMonad<>(value + " with composition"))
//                .forEach(System.out::println);
//    }

    /**
     * Contract of all implementation required for Monad [PolEffect]
     *
     * Referring to [Category theory] this is a functor since implement [map]
     * and a monad since implement [flatMap]
     */
    interface PolEffect<T> {

        boolean isDefined();

        boolean isSuccess();

        boolean isFailure();

        PolMonad<T> pure(T input);

        <A> PolMonad<A> map(Function<T, A> function);

        <A> PolMonad<A> flatMap(Function<T, PolMonad<A>> function);

//        PolMonad<Function<A, B>,Function<A, B>> curried(Function<A, Function<A, B>> function);

        void forEach(Consumer<T> consumer);

        PolMonad<T> fromOption(Option<T> input);

        PolMonad<T> fromTry(Try<T> input);

    }

    /**
     * Monad implementation to control side-effects and allow to do
     * [map] transformation
     * [flatMap] composition
     */
    static class PolMonad<T> implements PolEffect<T>{

        public T value;

        public Throwable sideEffect;

        public PolMonad(){}

        public PolMonad(T input){
            this.value =input;
        }

        @Override
        public boolean isDefined(){
            return Option.of(this.value).isDefined();
        }

        @Override
        public boolean isSuccess() {
            return sideEffect==null;
        }

        @Override
        public boolean isFailure() {
            return sideEffect != null;
        }

        @Override
        public PolMonad<T> pure(T a) {
            return new PolMonad<>(a);
        }

        @Override
        public <A> PolMonad<A> map(Function<T,A> function){
            if (this.isDefined()) {
                return new PolMonad<>(function.apply(this.value));
            } else {
                return new PolMonad<>();
            }
        }

        @Override
        public <A> PolMonad<A> flatMap(Function<T, PolMonad<A>> function){
            if (this.isDefined()) {
                return function.apply(this.value);
            } else {
                return new PolMonad<>();
            }
        }

//        @Override
//        public PolMonad<Function<A, B>, Function<A, B>> curried(Function<A, Function<A, B>> function) {
//            return new PolMonad(function.apply(this.value));
//        }

        @Override
        public void forEach(Consumer<T> consumer){
            consumer.accept(value);
        }

        @Override
        public  PolMonad<T> fromOption(Option<T> input) {
            if (input.isDefined()) {
                return new PolMonad<>(input.get());
            } else {
                this.value=null;
                return this;
            }
        }

        @Override
        public PolMonad<T> fromTry(Try<T> input) {
            if (input.isSuccess()) {
                return new PolMonad<>(input.get());
            } else {
                this.sideEffect=input.getCause();
                return this;
            }
        }
    }

}

