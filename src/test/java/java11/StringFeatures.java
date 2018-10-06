package java11;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StringFeatures {

    /**
     * Some new features in the String class
     */
    @Test
    public void features() {

        //Check if value is empty
        var empty = "";
        System.out.println(empty.isBlank());

        //The operator lines allow you separate every element in the string that has new line operator
        var numbers =
                "1 \n" +
                        "2 \n" +
                        "3 \n" +
                        "4 \n" +
                        "5 \n";
        numbers.lines().forEach(System.out::println);

        //Repeat text
        var sentence = "Repeat text is handy sometimes \n";
        System.out.println(sentence.repeat(5));

        //Evolution of trim for unicode
        var name = "    Paul    ";
        System.out.println(name.strip());
        // Remove the whitespace in the beginning of the string
        var name1 = "    Paul";
        System.out.println(name1.stripLeading());
        // Remove the whitespace from the end of the string
        var name2 = "Paul    ";
        System.out.println(name2.stripTrailing());

    }


}
