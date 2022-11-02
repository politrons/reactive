package vavr;

import io.vavr.API;
import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
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
     * <p>
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
     * Operator to map error with a Case condition
     */
    @Test
    public void tryMapFailure() {
        Try<String> tryValue =
                Try.of(this::getStringOrError)
                        .mapFailure(API.Case($(instanceOf(CustomException.class)), io -> new IllegalAccessError("I just map the error channel")));

        System.out.println(tryValue.failed().get());
    }

    @Test
    public void tryAddFinally() {
        Try<Integer> ryWithFinally =
                Try.of(()->"Hello world")
                        .map(s -> Integer.parseInt(s))
                                .andFinally(()-> System.out.println("We are always polite, even under errors"));
        System.out.println(ryWithFinally.get());
    }

    /**
     * RecoverWith operator allow us to specify what type of error we can recover
     */
    @Test
    public void tryRecoverWithClassType() {
        Try<String> recoverProgram = Try.of(() -> getNullString().toUpperCase())
                .recoverWith(NullPointerException.class, Try.success("recover from error"));

        Try<String> failedProgram = Try.of(() -> getNullString().toUpperCase())
                .recoverWith(IllegalStateException.class, t -> Try.success("recover from error"));

        System.out.println(recoverProgram);
        System.out.println(failedProgram);
    }

    @Test
    public void tryRecoverWithFailure() {
        Try<String> failedProgram = Try.of(() -> getNullString().toUpperCase())
                .recoverWith(t ->  Try.failure(t));

        System.out.println(failedProgram);
    }

    /**
     * We can extract the info of the pipeline and check the value is passing using the next operators.
     */
    @Test
    public void loggingInfo() {
        Try<String> program = Try.of(() -> "hello world")
                .andThen(info -> System.out.println("Looking value using [andThen] " + info))
                .peek(info -> System.out.println("Peeking what is passed in the pipeline " + info));
        System.out.println(program);
    }

    @Test
    public void transform() {
        String output = Try.of(() -> "hello world")
                .transform(value -> value.get() + "!!!!");
        System.out.println(output);

    }

    @Test
    public void peekError() {
        Try.of(() -> "hello world")
                .peek(t -> {
                    throw new RuntimeException();
                })
                .onFailure(t -> {
                    System.out.println("Not in here");
                    System.out.println(t);
                });

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
                        .map(String::toUpperCase)
                        .map(s -> s + "!!!"));

        System.out.println(notEvaluatedEither.isEvaluated());
        System.out.println(notEvaluatedEither.get());
        System.out.println(notEvaluatedEither.isEvaluated());
        System.out.println(notEvaluatedEither.get());
    }

    /**
     * Validate allow us to encapsulate if an element is valid or not, and them make combination of them.
     */
    @Test
    public void validateEffect() {
        Validation<Object, String> good = Validation.valid("Very");
        Validation<Object, String> bad = Validation.valid("Good");

        String result = Validation.combine(good, bad)
                .ap((a, b) -> a + " " + b)
                .map(String::toUpperCase)
                .get();
        System.out.println(result);

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

    @Test
    public void tryError() {
        Try<Object> errorProgram = Try.of(() -> {
            System.out.println("I'm not lazy and I'm gonna blow if you get me!");
            throw new NullPointerException();
        });

        System.out.println(errorProgram.isSuccess());
    }

    @Test
    public void tryToEither() {
        Either<Throwable, String> eitherProgram =
                Try.of(() -> "hello world").toEither();
        System.out.println(eitherProgram.isRight());
        System.out.println(eitherProgram.get());

        Either<Integer, String> emptyProgram =
                Try.of(() -> "hello")
                        .filter(m -> m.equals("hello world"))
                        .toEither(1981);
        System.out.println(emptyProgram.isRight());
        System.out.println(emptyProgram.getLeft());


        Either<String, String> nullProgram =
                Try.of(this::getNullString)
                        .map(String::toUpperCase)
                        .toEither("Left value");
        System.out.println(nullProgram.isRight());
        System.out.println(nullProgram.getLeft());
    }

    @Test
    public void tryAndResources() {
        Try.of(() -> "hello world")
                .flatMap(text -> Try.withResources((() -> new ByteArrayInputStream(text.getBytes())))
                        .of(bais -> "back"));
    }

    @Test
    public void optionToTry() {
        Try<String> maybeNullProgram = Option.of(getMaybeString())
                .toTry()
                .map(String::toUpperCase);
        System.out.println(maybeNullProgram);
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

    private String getNullString() {
        return null;
    }

    private Either<Throwable, String> getEitherString() {
        if (new Random().nextBoolean()) {
            return Right("hello Vavr world " + System.nanoTime());
        } else {
            return Left(new CustomException());
        }
    }

    private String getEitherStringOrError() {
        if (new Random().nextBoolean()) {
            return "hello Vavr world " + System.nanoTime();
        } else {
            throw new IllegalArgumentException("Error in Either monad");
        }
    }

    private class CustomException extends RuntimeException {
    }
}
