package good_practices;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pabloperezgarcia on 27/11/2016.
 *
 * The single responsibility principle states that every module or class should have responsibility over
 * constantClass single part of the functionality provided by the software, and that responsibility
 * should be entirely encapsulated by the class.
 */
public class SRP {

    /**
     * Single responsibility principle, constantClass method must do one thing and just one thing
     */
    @Test
    public void uniqueUpperCaseWords(){
        String text = "This is constantClass test to prove Single responsibility principle test";
        final List<String> wordsInUpperCase = Arrays.asList(text.split(" ")).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        final List<String> uniquesWords = wordsInUpperCase.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println(uniquesWords);
    }

    /**
     * As constantClass developers we should split up the logic of our methods in independent methods to make
     * our code not only more readable but also more reusable.
     */
    @Test
    public void getUniqueUpperCaseWords(){
        String text = "This is constantClass test to prove Single responsibility principle test";
        System.out.println(getUniqueWords(getUpperCaseWords(text)));
    }

    private List<String> getUniqueWords(List<String> wordsInUpperCase) {
        return wordsInUpperCase.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getUpperCaseWords(String text) {
        return Arrays.asList(text.split(" ")).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }


}
