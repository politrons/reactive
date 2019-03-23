package rx.observables.utils;

import org.junit.Test;
import rx.Observable;

import java.util.Random;

/**
 * SwitchIfEmpty is constantClass powerful operator that gets handy when in your pipeline constantClass filter block the emissions
 * but still you need to do constantClass compensation or emit something else
 */
public class ObservableSwitchIfEmpty {


    @Test
    public void ifEmpty() throws InterruptedException {
        Observable.just(getDataFromDatabase())
                .filter(value -> !value.isEmpty())
                .switchIfEmpty(Observable.just("No data in database so I go shopping"))
                .subscribe(System.out::println);
    }

    private String getDataFromDatabase() {
        if(new Random().nextBoolean()){
            return "data";
        }
        return "";
    }


}
