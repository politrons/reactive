package workspace;

import org.junit.Test;

public class Client {

    @Test
    public void testFuture() throws Exception {
        FutureDao dao = new FutureDao();
        dao.search();
    }

    @Test
    public void testCompletableFuture() throws Exception {
        CompletableFutureDao dao = new CompletableFutureDao();
        dao.search();
    }


}
