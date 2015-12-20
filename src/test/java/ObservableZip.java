import org.junit.Test;
import rx.Observable;


public class ObservableZip {

    @Test
    public void testZip() {
        Observable.zip(obString(), obString1(), String::concat)
                  .subscribe(System.out::println);
    }

    public Observable<String> obString() {
        return Observable.just("hello");
    }

    public Observable<String> obString1() {
        return Observable.just(" world");
    }
}
