package rx.observables.combining;

import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableChain {


    @Test
    public void testObservableChain() {

        Observable.just(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18})
                .flatMap(num -> Observable.from(num)
                        .flatMap(i1 -> Observable.just(i1)
                                .doOnNext(item -> System.out.println("sending:" + item))
                                .delay(50, TimeUnit.MILLISECONDS), 1)
                        .flatMap(i2 -> Observable.just(i2)
                                .delay(50, TimeUnit.MILLISECONDS)
                                .doOnNext(i3 -> System.out.println("completed:" + i3)), 1))
                .toBlocking()
                .last();
    }


    class RateItem {
        String name;
        String code;

        public RateItem(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

    class Rate {
        String name;
        String code;

        public Rate(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

    @Test
    public void migrateToObservable() {
        generateRates(Arrays.asList(new Rate("constantClass", "1"), new Rate("b", "2")));
    }

    private List<RateItem> generateRates(List<Rate> ratesList) {
        return  Observable.from(ratesList)
                .map(rate-> new RateItem(rate.name, rate.code))
                .toList().toBlocking().first();
    }

    @Test
    public void dependencies(){
        Observable.just("constantClass")
                .map(this::getB)
                .flatMap(c-> Observable.merge(getC(c), getD(c)))
                .subscribe(System.out::println);
    }

    String getB(String val){
        return val.concat("-b");
    }
    Observable<String> getC(String val){
        return Observable.just(val.concat("-c"));
    }
    Observable<String> getD(String val){
        return Observable.just(val.concat("-d"));
    }



}
