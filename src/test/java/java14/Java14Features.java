package java14;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Test;

public class Java14Features {

    /**
     * Java 14 introduce a pseudo patter matching with if/else syntax, where apart to match an element using
     * [instanceOf] we can extract the cast variable to be used or even add extra conditions over it.
     */
    @Test
    public void patternMatching() {
        var element = getElement();
        if (element instanceof String value && value.length() > 5) {
            System.out.println("String:" + value);
        } else if (element instanceof Integer value && value > 100 && value < 200) {
            System.out.println("Integer:" + value);
        } else if (element instanceof Long value) {
            System.out.println("Double:" + value);
        } else if (element instanceof Optional<?> maybe &&
            maybe.isPresent() &&
            maybe.get() instanceof String value) {
            System.out.println("String from Optional:" + value);
        }
    }

    /**
     * Java 14 brings kind multi line string just like Scala does, adding extra features like allow just one line String
     * in new lines adding  [\] at the end of each new line.
     */
    @Test
    public void textBlocks() {
        String textInOneLine = """
            Somewhere in La Mancha,\
             in a place whose name \
             I do not care to remember,
            """;
        System.out.println(textInOneLine);

        String multiLine = """
            "user":{
                "name":"politrons",
                "age":"38
            }
            """;
        System.out.println(multiLine);

    }

    /**
     * [Record type] in Java 14 it's kind of like [case class] of scala. It's allow to create final immutable instances
     * with toString/equals/hashCode and getter for all arguments passed in the constructor. The different
     * between case class and record, is that the first one don't require [new] operator to create the instance, but
     * record still it does.
     */
    @Test
    public void recordType() {
        var userRecord = new UserRecord("politrons", 38, new ProductRecord("coke-cole"));
        System.out.println(userRecord.getUserInfo());
        System.out.println(userRecord.getProducts());
        userRecord.getProducts().forEach(productRecord -> System.out.println(productRecord.product));
    }

    /**
     * User record type that it will bring the getter of the attributes
     */
    record UserRecord(String name, Integer age, ProductRecord product) {

        public String getUserInfo() {
            return "name:" + name + " " + "age:" + age;
        }

        public List<ProductRecord> getProducts(){
            return List.of(product);
        }
    }

    /**
     * Product record type that it will bring the getter of the product
     */
    record ProductRecord(String product) {

    }

    public Object getElement() {
        var number = new Random().nextInt(4);
        if (number == 0) {
            return "hello pattern matching in Java";
        } else if (number == 1) {
            return 1981;
        } else if (number == 2) {
            return 1981L;
        } else {
            return Optional.of("Hello String inside Optional");
        }

    }

}
