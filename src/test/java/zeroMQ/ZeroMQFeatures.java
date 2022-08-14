package zeroMQ;

import io.vavr.control.Try;
import org.junit.Test;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.CompletableFuture;

public class ZeroMQFeatures {

    @Test
    public void runClientServer() throws Exception {
        CompletableFuture.runAsync(() -> Try.run(() -> new ZeroMQServer().start()));
        Thread.sleep(2000);
        CompletableFuture.runAsync(() -> Try.run(() -> new ZeroMQClient().start()));
        Thread.sleep(10000);
    }

    /**
     * Implementation of ZeroMQ Server.
     * We initially create the [ZContext] the core class that we will use to create the Socket.
     * We create a [Socket] using the previous created context, and we specify the socket type of REP(Response)
     * once we have the socket we bind to TCP protocol with wildcard IP and a port.
     * We keep them subscribe to the socket in a loop until someone kills/interrupt the current thread.
     * To subscribe to the socket we use [recv] where we can set a timeout.
     */
    public static class ZeroMQServer {
        public void start() throws Exception {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket socket = context.createSocket(SocketType.REP);
                socket.bind("tcp://*:5555");
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] reply = socket.recv(0);
                    System.out.println("Message received:" + ": [" + new String(reply, ZMQ.CHARSET) + "]");
                    String response = "ZeroMQ looks promising";
                    socket.send(response.getBytes(ZMQ.CHARSET), 0);
                }
            }
        }
    }

    public static class ZeroMQClient {
        public void start() {
            try (ZContext context = new ZContext()) {
                System.out.println("Opening a socket connection with Server");
                ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                socket.connect("tcp://localhost:5555");
                String request = "Hey Politrons, do you like ZeroMQ";
                socket.send(request.getBytes(ZMQ.CHARSET), 0);
                byte[] reply = socket.recv(0);
                System.out.println("Message response: " + new String(reply, ZMQ.CHARSET));
            }
        }
    }
}
