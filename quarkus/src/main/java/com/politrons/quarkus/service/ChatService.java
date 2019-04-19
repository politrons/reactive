package com.politrons.quarkus.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

/**
 * WebSocket service which it will open a connection to ws://localhost.port/politrons/chat/
 * In order to have a websocket endpoint where to connect from html client, we use the annotation [ServerEndpoint]
 * Where we specify the endpoint.
 * <p>
 * Thanks to the different annotations we can control all the phases of the Websocket communication.
 */
@ServerEndpoint("/politrons/chat/{username}")
@ApplicationScoped
public class ChatService {

    private Map<String, Session> sessions = new ConcurrentHashMap<>(); //

    /**
     * Once we have a conection open we receive a session which we persist to have a broadcast once we
     * want to write to them
     *
     * @param session  of the connection
     * @param username of the new connection
     */
    @OnOpen
    public void onOpenConnection(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        broadcast("Human " + username + " joined the room");
    }

    /**
     * When the websocket it's close we receive a call, so we can remove from the map the session
     * to send any broadcast communication
     *
     * @param session  to be removed
     * @param username to be used to remove from the key/map
     */
    @OnClose
    public void onCloseConnection(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast("Human " + username + " left the room");
    }

    /**
     * In case the connection  it's close by unhandled error on client, we detect that and we pass
     * to the callback same arguments as in onClose, plus the Throwable.
     *
     * @param session   to be removed
     * @param username  to be used to remove from the key/map
     * @param throwable reason of the onError
     */
    @OnError
    public void onErrorConnection(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast("Human " + username + " left the room because: " + throwable);
    }

    @OnMessage
    public void onMessageIncoming(String message, @PathParam("username") String username) {
        broadcast(">> " + username + ": " + message);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> s.getAsyncRemote()
                .sendObject(message, result -> {
                    Optional.of(result.getException())
                            .ifPresent(t -> System.out.println("Unable to send message: " + t));
                }));
    }

}