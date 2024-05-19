package effects;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class JIOFeature {


    public static class JIO<X, T> {
        private Optional<T> value = Optional.empty();
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

        public static <T> JIO<Throwable, T> fromEffect(Supplier<T> action) {
            try {
                return new JIO<>(action.get());
            } catch (Exception e) {
                return new JIO<>(e);
            }
        }

        public static <T> JIO<Throwable, T> fromOptional(Optional<T> value) {
            return value.<JIO<Throwable, T>>map(JIO::new)
                    .orElseGet(JIO::new);
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

        public boolean isError() {
            return error != null;
        }

        public boolean isEmpty() {
            return value.isEmpty();
        }

        @Override
        public String toString() {
            if (isEmpty()) {
                return "Empty";
            } else if (isError()) {
                return STR."Side-effect detected:\{error.toString()}";
            } else {
                return STR."Value(\{value})";
            }
        }
    }

    @Test
    public void happyPath() {
        //Happy
        Optional<String> optionalWithValue = Optional.of("Hello");
        JIO<Throwable, String> mio = JIO.fromOptional(optionalWithValue);
        System.out.println(mio);

        JIO<Throwable, String> eio = JIO.fromEffect(() -> "Hello world without side-effects");
        System.out.println(eio);

        //Side effects
        Optional<String> optionalWithoutValue = Optional.empty();
        mio = JIO.fromOptional(optionalWithoutValue);
        System.out.println(mio);

        eio = JIO.fromEffect(() -> {
            throw new NullPointerException();
        });
        System.out.println(eio);

        //Transform
        JIO<Throwable, String> map = JIO.fromOptional(Optional.of("Let's transform this value"))
                .map(String::toUpperCase)
                .map(value -> STR."[\{value}]");
        System.out.println(map);

        //Composition
        JIO<Throwable, String> composition = JIO.fromEffect(() -> "Hello JIO")
                .map(value -> STR."\{value}!")
                .flatMap(value -> JIO.fromOptional(Optional.of(STR."\{value} together is better")));
        System.out.println(composition);
    }
}



