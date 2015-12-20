import org.junit.Test;
import rx.Observable;


public class ObservableZip {

    @Test
    public void testZip() {
        Observable.zip(obString(), obString1(), obString2(), (s, s2, s3) -> s.concat(s2)
                                                                         .concat(s3))
                  .subscribe(System.out::println);
    }

    public Observable<String> obString() {
        return Observable.just("hello");
    }

    public Observable<String> obString1() {
        return Observable.just(" world");
    }

    public Observable<String> obString2() {
        return Observable.just("!");
    }
}
