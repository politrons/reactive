package good_practices;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pabloperezgarcia on 27/11/2016.
 *
 * In software engineering, don't repeat yourself (DRY) is a principle of software development,
 * aimed at reducing repetition of information of all kinds,
 */
public class DRY {

    @Test
    public void getDistinctBigWordsWrong() {
        String text = "This is a test to prove Dont repeat yourself test prove";
        final List<String> collect = Arrays.asList(text.split(" ")).stream()
                .map(String::toUpperCase)
                .filter(word -> word.toCharArray().length > 3)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void getBigWordsWrong() {
        String text = "This is a test to prove Dont repeat yourself test prove";
        final List<String> wordsInUpperCase = Arrays.asList(text.split(" ")).stream()
                .map(String::toUpperCase)
                .filter(word -> word.toCharArray().length > 3)
                .collect(Collectors.toList());
        System.out.println(wordsInUpperCase);
    }

    /**
     * As a developers we should never duplicate code unnecessarily, it will cost us time in refactor,
     * And introduce more bugs possibility.
     */
    private String text = "This is a test to prove Dont repeat yourself test prove";

    @Test
    public void getDistinctBigWords() {
        final List<String> collect = getBigWordsStream(text)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void getBigWords() {
        final List<String> wordsInUpperCase = getBigWordsStream(text)
                .collect(Collectors.toList());
        System.out.println(wordsInUpperCase);
    }

    /**
     * This code be externalize to be reused for both methods since it´s doing the same thing
     */
    private Stream<String> getBigWordsStream(String text) {
        return Arrays.asList(text.split(" ")).stream()
                .map(String::toUpperCase)
                .filter(word -> word.toCharArray().length > 3);
    }

}
