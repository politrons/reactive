package effects;

import java22.FeaturesJava22;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.*;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class JIOFeature {


    public static class JIO<X, T> {
        private Optional<T> value = Optional.empty();
        private CompletableFuture<T> futureValue;
        private Throwable error;

        private JIO(T value) {
            this.value = Optional.of(value);
        }

        private JIO(Throwable error) {
            this.error = error;
        }

        private JIO() {
            this.value = Optional.empty();
        }

        public JIO(CompletableFuture<T> futureValue) {
            this.futureValue = futureValue;
        }

        public static <T> JIO<Throwable, T> fromEffect(Supplier<T> action) {
            try {
                return new JIO<>(action.get());
            } catch (Exception e) {
                return new JIO<>(e);
            }
        }

        public static <T> JIO<Throwable, T> fromOptional(Optional<T> value) {
            return value.<JIO<Throwable, T>>map(v -> new JIO<>(v))
                    .orElseGet(JIO::new);
        }

        public static <T> JIO<Throwable, T> fromFuture( Supplier<T> action) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> action.get(), newVirtualThreadPerTaskExecutor());
            return new JIO<>(future);

        }

        public JIO<X, T> map(Function<T, T> func) {
            if (value.isPresent() && error == null) {
                try {
                    return new JIO<>(func.apply(value.get()));
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = e;
                    return this;
                }
            }
            return this;
        }

        public JIO<Throwable, T> flatMap(Function<T, JIO<Throwable, T>> func) {
            if (value.isPresent() && error == null) {
                try {
                    return func.apply(value.get());
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = e;
                    return (JIO<Throwable, T>) this;
                }
            }
            return (JIO<Throwable, T>) this;
        }


        public JIO<Throwable, T> mapAsync(Function<T, T> func) {
            if (value.isPresent() && error == null) {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> func.apply(value.get()), newVirtualThreadPerTaskExecutor());
                return new JIO<>(future);
            }
            try {
                final T value = this.get();
                return new JIO<>(CompletableFuture.supplyAsync(() -> value, newVirtualThreadPerTaskExecutor()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }



//        public JIO<Throwable, CompletableFuture<T>> flatMapAsync(Function<T, JIO<Throwable, CompletableFuture<T>>> func) {
//            if (value.isPresent() && error == null) {
//                return func.apply(value.get());
//            }
//            try {
//                final T value = this.get();
//                return new JIO<>(CompletableFuture.supplyAsync(() -> value));
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }


        public JIO<Throwable, T> filter(Function<T, Boolean> func) {
            if (value.isPresent() && error == null) {
                try {
                    if (!func.apply(value.get())) {
                        this.value = Optional.empty();
                    }
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = e;
                    return (JIO<Throwable, T>) this;
                }
            }
            return (JIO<Throwable, T>) this;
        }


        public T get() throws Throwable {
            if (isEmpty()) {
                throw new IllegalStateException("No value present");
            } else if (error != null) {
                throw error;
            }
            return value.get();
        }

        public T getAsync() throws Throwable {
            if (futureValue == null) {
                throw new IllegalStateException("No value present");
            } else if (error != null) {
                throw error;
            }
            return futureValue.get();
        }

        public boolean isPresent() {
            return value.isPresent();
        }

        public boolean isEmpty() {
            return value.isEmpty();
        }

        public boolean isSucceed() {
            return error == null;
        }

        public boolean isFailure() {
            return error != null;
        }


        public T getOrElse(T defaultValue) throws Throwable {
            if (isEmpty() || error != null) {
                return defaultValue;
            }
            return value.get();
        }

        @Override
        public String toString() {
            if (isEmpty()) {
                return "Empty";
            } else if (isFailure()) {
                return STR."Side-effect detected:\{error.toString()}";
            } else {
                return STR."Value(\{value})";
            }
        }
    }

    @Test
    public void effectSystem() throws Throwable {
        Optional<String> optionalWithValue = Optional.of("Hello");
        JIO<Throwable, String> optionalEffect = JIO.fromOptional(optionalWithValue)
                .map(effect -> STR."\{effect} world");
        System.out.println(optionalEffect);

        JIO<Throwable, String> errorEffect = JIO.fromEffect(() -> "Hello world without side-effects")
                        .map(effect -> STR."\{effect}!");
        System.out.println(errorEffect);

        JIO<Throwable, String> nullEffect = JIO.fromOptional(Optional.empty());
        System.out.println(nullEffect.getOrElse("Let's use a backup"));

    }

    @Test
    public void sideEffects() {
        Optional<String> optionalWithoutValue = Optional.empty();
        JIO<Throwable,String> optionalEffect = JIO.fromOptional(optionalWithoutValue);
        System.out.println(optionalEffect);
        System.out.println(optionalEffect.isEmpty());
        System.out.println(optionalEffect.isPresent());

        JIO<Throwable,String> errorEffect = JIO.fromEffect(() -> {
            throw new NullPointerException();
        });
        System.out.println(errorEffect);
        System.out.println(errorEffect.isSucceed());
        System.out.println(errorEffect.isFailure());
    }

    @Test
    public void transform() throws Throwable {
        JIO<Throwable, String> map = JIO.fromOptional(Optional.of("Let's transform this value"))
                .map(String::toUpperCase)
                .map(value -> STR."[\{value}]");
        System.out.println(map);
    }

    @Test
    public void composition() {
        JIO<Throwable, String> composition = JIO.fromEffect(() -> "Hello JIO")
                .map(value -> STR."\{value}!")
                .flatMap(value -> JIO.fromOptional(Optional.of(STR."\{value} together is better")));
        System.out.println(composition);
    }

    @Test
    public void async() throws Throwable {
        JIO<Throwable, String> asyncProgram = JIO.fromEffect(() -> "Hello JIO")
                .mapAsync(value -> STR."\{value}");
//                        .flatMapAsync(future -> future.thenCompose(value -> STR.""))
        System.out.println(asyncProgram.getAsync());

    }
}



