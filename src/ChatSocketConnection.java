import adapter.PieceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ChatMessage;
import model.Game;
import model.piece.Piece;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChatSocketConnection {
    private StompSession stompSession;
    public Game game;
    public GameForm gameForm;
    private final String serverUrl = "https://jchess.onrender.com";

    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));

        stompClient.setMessageConverter(new StringMessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        try {
            stompSession = stompClient.connect(serverUrl + "/chat", sessionHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (stompSession != null) {
            stompSession.disconnect();
        }
    }

    public void updateChatPane(ChatMessage message) {
        if (gameForm != null) {
            gameForm.chatPane.setText(gameForm.chatPane.getText() + "\n" + message.getUsername() + ": " + message.getMessage());
        }
    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            System.out.println("Exception: " + exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            System.out.println("Transport Error: " + exception.getMessage());
        }
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe("/topic/chat/" + game.getId(), new StompFrameHandler() {

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    ChatMessage message = new Gson().fromJson((String) payload, ChatMessage.class);
                    updateChatPane(message);
                }
            });
            System.out.println("Subscribed to /topic/chat/" + game.getId());
            System.out.println("Connected: " + connectedHeaders.toString());
        }
    }
}
