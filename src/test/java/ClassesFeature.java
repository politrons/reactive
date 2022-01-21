import io.vavr.control.Try;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Function;

public class ClassesFeature {

    @Test
    public void compareTypeClasses(){
        A hello = new A("hello");
        A world = new A("world");
        A hello1 = new A("hello");
        A world1 = new A("world");

        System.out.println(hello.equals(world));
        System.out.println(hello.equals(hello1));
        System.out.println(world.equals(world1));
    }

    static class A{

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        final String value;

        A(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            A a = (A) o;
            return Objects.equals(value, a.value);
        }


    }

}
