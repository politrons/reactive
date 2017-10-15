package workspace;

import org.junit.Test;

public class Client {

    @Test
    public void testFutureString() throws Exception {
        FutureDao dao = new FutureDao();
        dao.searchString();
    }

    @Test
    public void testCompletableFuture() throws Exception {
        CompletableFutureDao dao = new CompletableFutureDao();
        dao.searchString();
    }

    @Test
    public void testFutureInteger() throws Exception {
        FutureDao dao = new FutureDao();
        dao.searchInt();
    }

    @Test
    public void testCompletableInteger() throws Exception {
        CompletableFutureDao dao = new CompletableFutureDao();
        dao.searchInt();
    }



}
