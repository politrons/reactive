package utils;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.meanbean.test.BeanTester;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by pabloperezgarcia on 02/04/2017.
 */
public class PojoTest {

    @Test
    public void main() {
        Reflections reflections = new Reflections("rx.observables");
        Set<Class<? extends Cloneable>> classes = reflections.getSubTypesOf(Cloneable.class);
        for (Class<?> aClass : classes) {
            EqualsVerifier.forClass(aClass).suppress(Warning.STRICT_INHERITANCE,
                    Warning.INHERITED_DIRECTLY_FROM_OBJECT,
                    Warning.NONFINAL_FIELDS)
                    .verify();
            new BeanTester().testBean(aClass);
        }
    }
}
