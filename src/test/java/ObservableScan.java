import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */
public class ObservableScan {


    /**
     * apply this function for every item against the previous emitted item from the source
     */
    @Test
    public void scanObservable(){
        Integer[] numbers = { 0, 1, 2, 3, 4, 5 };

        Observable.from(numbers).scan((x,y)-> (x + y)).subscribe(System.out::println);
    }

}
