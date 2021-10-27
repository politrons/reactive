import org.junit.Test;

public class RunnableFeature {

    @Test
    public void runnable(){
        Runnable runnable = () -> {
            while(true){
                System.out.println("Hello world from " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.run();
        System.out.println("Next step in "+ Thread.currentThread().getName());
    }
}
