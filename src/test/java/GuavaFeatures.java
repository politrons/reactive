import com.google.common.collect.EvictingQueue;
import org.junit.Test;

public class GuavaFeatures {

    @Test
    public void evictingQueue(){
        var ringBuffer = EvictingQueue.<Integer>create(4);
        if(ringBuffer.size()<4){
            ringBuffer.add(1);
            ringBuffer.add(2);
            ringBuffer.add(3);
            ringBuffer.add(4);
        }

        for(int i=0; i<20;i++){
            Integer poll = ringBuffer.poll();
            System.out.println(poll);
            ringBuffer.add(poll);
        }
    }
}
