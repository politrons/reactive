package effects;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class JIOFeature {


    public static class JIO<T> {
        private Optional<T> value = Optional.empty();
        private Optional<CompletableFuture<T>> futureValue = Optional.empty();
        private Optional<Throwable> error = Optional.empty();

        private JIO(T value) {
            this.value = Optional.of(value);
        }

        private JIO(Throwable error) {
            this.error = Optional.of(error);
        }

        private JIO() {
            this.value = Optional.empty();
        }

        public JIO(CompletableFuture<T> futureValue) {
            this.futureValue = Optional.of(futureValue);
        }

        public static <T> JIO<T> from(T value) {
            return new JIO<>(value);
        }

        public static <T> JIO<T> fromEffect(Supplier<T> action) {
            try {
                return new JIO<>(action.get());
            } catch (Exception e) {
                return new JIO<>(e);
            }
        }

        public static <T> JIO<T> fromOptional(Optional<T> value) {
            return value.map(JIO::new)
                    .orElseGet(JIO::new);
        }

        public static <T> JIO< T> fromFuture(Supplier<T> action) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(action, newVirtualThreadPerTaskExecutor());
            return new JIO<>(future);
        }

        public JIO<T> map(Function<T, T> func) {
            if (value.isPresent() && error.isEmpty()) {
                try {
                    return new JIO<>(func.apply(value.get()));
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = Optional.of(e);
                    return this;
                }
            }
            return this;
        }

        public JIO<T> flatMap(Function<T, JIO<T>> func) {
            if (value.isPresent() && error.isEmpty()) {
                try {
                    return func.apply(value.get());
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = Optional.of(e);
                    return this;
                }
            }
            return this;
        }

        public JIO< T> mapAsync(Function<T, T> func) {
            if (value.isPresent() && error.isEmpty()) {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> func.apply(value.get()), newVirtualThreadPerTaskExecutor());
                return new JIO<>(future);
            }
            if (futureValue.isPresent() && error.isEmpty()) {
                return new JIO<>(futureValue.get().thenApply(func));
            }
            try {
                final T value = this.get();
                return new JIO<>(CompletableFuture.supplyAsync(() -> value, newVirtualThreadPerTaskExecutor()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public JIO<T> parallelAsync(Function<T, T> func1, Function<T, T> func2, BiFunction<T, T, T> mergeFunc) throws InterruptedException {
            if (value.isPresent() && error.isEmpty()) {
                try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                    StructuredTaskScope.Subtask<T> task1 = scope.fork(() -> func1.apply(value.get()));
                    StructuredTaskScope.Subtask<T> task2 = scope.fork(() -> func2.apply(value.get()));
                    var maybeSideEffect = scope.join().exception();
                    if (maybeSideEffect.isPresent()) {
                        error = maybeSideEffect;
                    } else {
                        this.value = Optional.of(mergeFunc.apply(task1.get(), task2.get()));
                    }
                    return this;
                }
            } else {
                return this;
            }
        }

        public JIO<T> filter(Function<T, Boolean> func) {
            if (value.isPresent() && error.isEmpty()) {
                try {
                    if (!func.apply(value.get())) {
                        this.value = Optional.empty();
                    }
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = Optional.of(e);
                    return this;
                }
            }
            return this;
        }

        public JIO<T> onSuccess(Consumer<T> func) {
            if (value.isPresent() && error.isEmpty()) {
                try {
                    func.accept(value.get());
                } catch (Exception e) {
                    this.value = Optional.empty();
                    this.error = Optional.of(e);
                    return this;
                }
            }
            return this;
        }

        public <T> JIO< T> onFailure(Consumer<Throwable> func) {
            if (error.isPresent()) {
                try {
                    func.accept(error.get());
                } catch (Exception e) {
                    this.error = Optional.of(e);
                    return (JIO<T>) this;
                }
            }
            return (JIO<T>) this;
        }

        public boolean isPresent() {
            return value.isPresent();
        }

        public boolean isEmpty() {
            return value.isEmpty();
        }

        public boolean isSucceed() {
            return error.isEmpty();
        }

        public boolean isFailure() {
            return error.isPresent();
        }

        public T get() throws Throwable {
            if (value.isEmpty()) {
                throw new IllegalStateException("No value present");
            } else if (error.isPresent()) {
                throw error.get();
            }
            return value.get();
        }

        public T getAsync() throws Throwable {
            if (futureValue.isEmpty()) {
                throw new IllegalStateException("No value present");
            } else if (error.isPresent()) {
                throw error.get();
            }
            return futureValue.get().get();
        }


        public T getOrElse(T defaultValue)  {
            if (value.isEmpty() || error.isPresent()) {
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
        JIO<String> optionalEffect = JIO.fromOptional(optionalWithValue)
                .map(effect -> STR."\{effect} world")
                .onSuccess(v -> System.out.println(STR."We found a value \{v}"));

        System.out.println(optionalEffect);

        JIO<String> errorEffect = JIO.fromEffect(() -> "Hello world without side-effects")
                .map(effect -> STR."\{effect}!");
        System.out.println(errorEffect);

        JIO<String> nullEffect = JIO.fromOptional(Optional.empty());
        System.out.println(nullEffect.getOrElse("Let's use a backup"));

    }

    @Test
    public void sideEffects() {
        Optional<String> optionalWithoutValue = Optional.empty();
        JIO<String> optionalEffect = JIO.fromOptional(optionalWithoutValue);
        System.out.println(optionalEffect);
        System.out.println(optionalEffect.isEmpty());
        System.out.println(optionalEffect.isPresent());

        JIO<String> errorEffect = JIO.fromEffect(() -> {
                    throw new NullPointerException();
                })
                .onFailure(t -> System.out.println(STR."We found a side effect. Caused by \{t.getMessage()}"));
        System.out.println(errorEffect);
        System.out.println(errorEffect.isSucceed());
        System.out.println(errorEffect.isFailure());
    }

    @Test
    public void transform() throws Throwable {
        JIO<String> map = JIO.fromOptional(Optional.of("Let's transform this value"))
                .map(String::toUpperCase)
                .map(value -> STR."[\{value}]");
        System.out.println(map);
    }

    @Test
    public void composition() {
        JIO<String> composition = JIO.fromEffect(() -> "Hello JIO")
                .map(value -> STR."\{value}!")
                .flatMap(value -> JIO.fromOptional(Optional.of(STR."\{value} together is better")));
        System.out.println(composition);
    }

    @Test
    public void async() throws Throwable {
        JIO<String> futureProgram = JIO.fromFuture(() -> "Hello from")
                .mapAsync(d -> STR."\{d}  the future");
        System.out.println(futureProgram.getAsync());

        JIO<String> asyncProgram = JIO.fromEffect(() -> "Hello JIO")
                .mapAsync(value -> STR."\{value}!")
                .mapAsync(value -> STR."\{value}!!");
        System.out.println(asyncProgram.getAsync());
    }

    @Test
    public void parallel() throws Throwable {
        JIO<String> parallelProgram = JIO.from("")
                .parallelAsync(_ -> "Hello", _ -> "world", (hello, world) -> STR."\{hello} \{world}")
                .map(String::toUpperCase);
        System.out.println(parallelProgram.get());
    }
}



