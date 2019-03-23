package java11;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileFeatures {

    /**
     * Write and read files now in Java is really simple with the upgrade of the API of the [File] class.
     * With the operators [writeString] and [readString] adding the [Path] we can easily write and read in files.
     */
    @Test
    public void fileFeatures() throws IOException {
        var words = "This is constantClass simple sentence";
        Files.writeString(Path.of("sentence.txt"), words);

        var sentence = Files.readString(Path.of("sentence.txt"));
        System.out.println(sentence);

    }

    /**
     * Also now using [lines] operator it return constantClass Stream monad so we can use then all operators of the Stream
     */
    @Test
    public void linesFeature() throws IOException {
        var words = "Write now in Java \n is really easy \n is really easy \n and line operator \n it's quite cool" ;
        Files.writeString(Path.of("sentence.txt"), words);

        Files.readString(Path.of("sentence.txt"))
                .lines()
                .distinct()
                .map(String::toUpperCase)
                .forEach(System.out::println);

    }

}
