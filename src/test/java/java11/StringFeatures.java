package java11;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    }

    /**
     * Write and read files now in Java is really simple with the upgrade of the API of the [File] class.
     * With the operators [writeString] and [readString] adding the [Path] we can easily write and read in files.
     */
    @Test
    public void filesFeatures() throws IOException {
        var words = "Write now in Java \n is really easy";
        Files.writeString(Path.of("sentence.txt"), words);

        Files.readString(Path.of("sentence.txt"))
                .lines()
                .forEach(System.out::println);
    }

}
