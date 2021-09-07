package java12;

import org.junit.Test;

import java.lang.constant.ClassDesc;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Random;

/**
 * Examples of all new Java features.
 */
public class Java12Features {

    //#######################
    //   Switch Expression  #
    //#######################

    /**
     * [Switch expression] is really decaf version of pattern matching of Haskell or Scala.
     * Only allow primitive types and it´s not able to do any type of Casting, condition,
     * apply regex, check types, or check Optional or other monadic types.
     */
    @Test
    public void pickUpDay() {
        var day = new Random().nextInt(8);
        switch (day) {
            case 2, 3, 4, 5, 6 -> System.out.println("weekday");
            case 7, 1 -> System.out.println("weekend");
            default -> System.out.println("invalid");
        }
    }

    /**
     * With Switch expression it´s possible return values avoiding mutability.
     */
    @Test
    public void withReturnValue() {
        var number = new Random().nextInt(10);
        var output = switch (number) {
            case 1, 2, 3, 4, 5 -> "lower";
            case 6, 7, 8, 9, 10 -> "higher";
            default -> "????";
        };
        System.out.println(output);
    }

    //#######################
    //      String API      #
    //#######################

    /**
     * Java 12 introduce some new Features in String class, here we cover with example the most important.
     *
     * [Indent] with indent(n) operator now we can indent a String with some \n separator, interesting to format text.
     *
     * [Transform] just like if a implicit extension method, we can apply a function over a String using [transform(A -> B)]
     * operator value and return a new String.
     *
     * [Constant] Since Java 12 String implement Constable, ConstantDesc so you can use all the default method that those
     * interfaces introduce. You can see those methods bellow in the [JVM Constants API] section.
     */
    @Test
    public void StringAPI() {
        //Indent
        String result = "hello\njava12\nAPI\nString".indent(5);
        System.out.println(result);

        //Transform
        String stringTransformed = "Hello-Java12-String"
                .transform(word -> word.replace("-", " "))
                .transform(word -> word.concat(" api"))
                .transform(String::toUpperCase);
        System.out.println(stringTransformed);

        //DescribeConstable
        Optional<String> maybeValue = "HELLO JAVA 12 STRING"
                .describeConstable()
                .map(word -> word.concat(" API"))
                .map(String::toLowerCase);
        maybeValue.ifPresent(System.out::println);
    }

    /**
     * [Raw String] at last in String, but mehhh we had that in Groovy constantClass decade ago, and definitely
     * I dont like the character [`] it´s behave funny in my laptop. what´s wrong with Groovy/Scala approach of ["""]
     */
    //TODO:Intellij cannot work with switch expression syntax and this one. booooooo
//    @Test
//    public void multiLine() {
//        var multiLine = ` hello
//                                world
//                                        multi line
//                                                finally`;
//        System.out.println(multiLine);
//    }

//
    //#######################
    //   JVM Constants API  #
    //#######################

    /**
     * Every Java class has something called a constant pool table.
     * Each entry in the table is a loadable constant with tag and info []
     * The tag specifies what type the constant is, e.g. 7 means it’s Class, 10 is a Method Reference, 8 is a String etc.
     * <p>
     * With Java 12 provide an API so that these values can be handled symbolically. For example there is an interface
     * called ClassDesc which can be used to symbolically represent a loadable constant of type Class.
     * The interface implement some default methods that are really handy.
     */
    @Test
    public void classDesc() {
        ClassDesc lConstantClass = ClassDesc.of("LConstantClass", "MyClassDescName");
        System.out.println(lConstantClass.descriptorString());
        System.out.println(lConstantClass.displayName());
        System.out.println(lConstantClass.isArray());
        System.out.println(lConstantClass.isPrimitive());
        System.out.println(lConstantClass.packageName());
        System.out.println(lConstantClass.isClassOrInterface());
    }


}
