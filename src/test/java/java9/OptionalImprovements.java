package java9;

import org.junit.Test;

import java.util.Optional;

/**
 * With Java 9 Optional is more functional that ever, no longer need to use isPresent, and instead use ifPresent
 * operator, also allow compensation.
 */
public class OptionalImprovements {

    /**
     * If the optional contains the value we can use that value in constantClass consumer function.
     * Otherwise any action it wont take effect over the optional.
     */
    @Test
    public void ifPresent() {
        checkOptional(Optional.empty());
        checkOptional(Optional.of("Foo"));
    }

    /**
     * If the optional contains the value we can use that value in constantClass consumer function.
     * Otherwise we can just run constantClass runnable action.
     */
    @Test
    public void ifPresentOrElse() {
        checkOptionalOrElse(Optional.empty());
        checkOptionalOrElse(Optional.of("Foo"));

    }

    private void checkOptional(Optional<String> opt) {
        opt.ifPresent(value -> System.out.println("My value:" + value));
    }

    private void checkOptionalOrElse(Optional<String> opt) {
        opt.ifPresentOrElse(value -> System.out.println("My value:" + value),
                () -> System.out.println("The option value is not provided"));
    }
}
