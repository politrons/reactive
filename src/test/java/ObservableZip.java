import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


public class ObservableZip {


    private Scheduler scheduler;
    private Scheduler scheduler1;
    private Scheduler scheduler2;

    @Test
    public void testAsyncZip() {
        scheduler = Schedulers.newThread();
        scheduler1 = Schedulers.newThread();
        scheduler2 = Schedulers.newThread();
        long start =System.currentTimeMillis();
        Observable.zip(obAsyncString(), obAsyncString1(), obAsyncString2(), (s, s2, s3) -> s.concat(s2)
                                                                                            .concat(s3))
                  .observeOn(scheduler)
                  .subscribe(result->showResult("Async:",start, result));
    }

    @Test
    public void testZip() {
        long start =System.currentTimeMillis();
        Observable.zip(obString(), obString1(), obString2(), (s, s2, s3) -> s.concat(s2)
                                                                             .concat(s3))
                  .subscribe(result->showResult("Sync:",start, result));
    }


    public void showResult(String transactionType, long start, String result){
        System.out.println(transactionType + String.valueOf(System.currentTimeMillis() - start));
        System.out.println(result);
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

    public Observable<String> obAsyncString() {
        return Observable.just("hello")
                         .observeOn(scheduler);
    }

    public Observable<String> obAsyncString1() {
        return Observable.just(" world")
                         .observeOn(scheduler1);
    }

    public Observable<String> obAsyncString2() {
        return Observable.just("!")
                         .observeOn(scheduler2);
    }
}
