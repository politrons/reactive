package vavr;

import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.Random;

import static io.vavr.API.*;
import static io.vavr.Patterns.*;
import static io.vavr.Predicates.instanceOf;

/**
 * Vavr it's a toolkit of Monads type to control effects to do Functional programing and also has
 * lazy evaluation to be referential transparency and being pure functional programing.
 * It contains the same functional monads name like in Scala.
 */
public class VavrEffects {


    /**
     * Using Vavr allow us control effects of the monad Option to have or not have a value.
     * We can make transformation with [map] and composition with [flatMap]
     * The Option it will return a [Some(T)] in case it contains a value, just like in Scala.
     *
     * Using the Vavr implementation of Pattern matching we can check if the Option is Some or None
     * and extract the value.(Option, Try, Either, Future)
     */
    @Test
    public void optionFeature() {
        var maybeString = Option.of(getMaybeString())
                .filter(value -> value.length() >= 4)
                .map(String::toUpperCase)
                .flatMap(value -> Option.of(value + "!!"));

        String response = Match(maybeString).of(
                Case($Some($()), value -> value + " defined"),
                Case($None(), "empty"));

        System.out.println(maybeString);
        System.out.println(response);

    }


    /**
     * Using Vavr allow us control effects of the monad Try to have a value or receive a throwable.
     * We can make transformation with [map] and composition with [flatMap]
     * Vavr provide a copy of patter matching quite less powerful and much more verbose.
     * Try monad it will return a Success(T) or Failure(T) just like in Scala.
     */
    @Test
    public void tryFeature() {
        var tryValue = Try.of(this::getStringOrError)
                .filter(value -> value.length() >= 4)
                .map(String::toUpperCase)
                .recover(throwable -> Match(throwable).of(
                        Case($(instanceOf(CustomException.class)), t -> "Business error caused by:" + t.getMessage()),
                        Case($(instanceOf(Exception.class)), t -> "Unknown error caused by:" + t.getMessage())
                )).flatMap(value -> Try.of(() -> value + "!!!"));
        System.out.println(tryValue);
    }

    /**
     * Using Vavr allow us control effects of the monad Either to have a Left value(normally business error) or Right of T.
     * We can make transformation with [map] and composition with [flatMap]
     * Either monad it will return a Left(T) or Right(T) just like in Scala.
     * <p>
     * Once that you get the either in your pipeline, you can use [right()] operator and use right value of the either
     * to make filter, transformation, compositions in case we go through that path, and in case is a Left, all that path
     * it will be omitted.
     * In this example we also use pattern matching, that even not being native in Java it behave pretty good with success and failure
     */
    @Test
    public void eitherFeature() {
        Either<Throwable, String> eitherString =
                getEitherString()
                        .right()
                        .map(String::toUpperCase)
                        .map(s -> s + "!!!")
                        .toEither();

        String response = Match(eitherString).of(
                Case($Right($()), value -> "Right value " + value),
                Case($Left($()), x -> "Left value " + x));

        System.out.println(response);
        System.out.println(eitherString);
    }

    /**
     * Lazy is a monad that allow do Pure functional programing since is referential transparency, it lazy evaluated
     * which means that it does not matter how many times you evaluated it always return the same value.
     * Also since is referential transparency is memoizing, so it does not evaluate the result more than one.
     */
    @Test
    public void lazyFeature() {
        Lazy<Long> lazy = Lazy.of(System::nanoTime);
        System.out.println(lazy.isEvaluated()); // = false
        System.out.println(lazy.get());  // = bla (random generated)
        System.out.println(lazy.isEvaluated());   // = true
        System.out.println(lazy.get());  // = bla (memoized)(

        Lazy<Either<Throwable, String>> notEvaluatedEither =
                Lazy.of(() -> getEitherString()
                        .right()
                        .map(String::toUpperCase)
                        .map(s -> s + "!!!")
                        .toEither());

        System.out.println(notEvaluatedEither.isEvaluated());
        System.out.println(notEvaluatedEither.get());
        System.out.println(notEvaluatedEither.isEvaluated());
        System.out.println(notEvaluatedEither.get());
    }

    /**
     * Future monad to execute your program in another thread and control if the program return the expected output or
     * a throwable.
     * It contains all operator of Scala Future API.
     * <p>
     * In this example we also use pattern matching, that even not being native in Java it behave pretty good with success and failure
     */
    @Test
    public void futureFeature() {
        Future<String> future = Future.of(() -> "This it will happen in the future")
                .peek(value -> println("Thread " + Thread.currentThread().getName()))
                .map(String::toUpperCase)
                .flatMap(value -> Future.of(() -> value + "!!!!"));
        future.onComplete(tryResponse -> {
            String response = Match(tryResponse).of(
                    Case($Success($()), value -> "Success response:" + value),
                    Case($Failure($()), x -> "Error response"));
            System.out.println(response);
        });

        System.out.println(future.get());
    }

    private String getMaybeString() {
        if (new Random().nextBoolean()) {
            return "hello Vavr world";
        } else {
            return null;
        }
    }

    private String getStringOrError() {
        if (new Random().nextBoolean()) {
            return "hello Vavr world";
        } else {
            throw new CustomException();
        }
    }

    private Either<Throwable, String> getEitherString() {
        if (new Random().nextBoolean()) {
            return Right("hello Vavr world " + System.nanoTime());
        } else {
            return Left(new CustomException());
        }
    }

    private class CustomException extends RuntimeException {
    }
}
