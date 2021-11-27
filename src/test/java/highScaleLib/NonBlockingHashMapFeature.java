package highScaleLib;

import io.vavr.concurrent.Future;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class NonBlockingHashMapFeature {

    static Random random = new Random();


    public static void main(String[] args) {
        var executor = Executors.newFixedThreadPool(100);
        var nonBlockingMap = new HashMap<Integer, String>();
//        var nonBlockingMap = new NonBlockingHashMap<Integer, String>();
        nonBlockingMap.put(1, "dddd");
        nonBlockingMap.put(2, "dddd");
        nonBlockingMap.put(3, "dddd");
        nonBlockingMap.put(4, "dddd");


//        nonBlockingMap.forEach((key, value) -> {
//            System.out.printf("Thread %s and value %s \n", Thread.currentThread().getName(), value);
//            process(nonBlockingMap);
//        });

        var timer = System.currentTimeMillis() + 10000;
        while (timer > System.currentTimeMillis()) {
            Future.successful(executor, process(nonBlockingMap))
                    .forEach(num -> nonBlockingMap.forEach((key, value) -> System.out.printf("Thread %s and value %s \n", Thread.currentThread().getName(), value)));
        }
    }

    public static int process(HashMap<Integer, String> map) {
        var num = random.nextInt(4) + 1;
        System.out.println(num);
        map.put(num, UUID.randomUUID().toString());
        return num;
    }

}



