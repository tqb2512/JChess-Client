import adapter.PieceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Game;
import model.piece.Piece;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GameSocketConnection {
    private StompSession stompSession;
    public Game game;
    public GameForm gameForm;

    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        try {
            stompSession = stompClient.connect("http://localhost:8080/gameplay", sessionHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (stompSession != null) {
            stompSession.disconnect();
        }
    }

    public void updateLabels() {
        if (gameForm != null) {
            gameForm.player1Label.setText(game.getPlayer1().getUsername());
            gameForm.player2Label.setText(game.getPlayer2() != null ? game.getPlayer2().getUsername() : "Waiting for player 2");
            gameForm.turnLabel.setText(game.getCurrentPlayer() != null ? game.getCurrentPlayer().getUsername() + "'s turn" : "Game has not started yet");
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
            session.subscribe("/topic/game/" + game.getId(), new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Map.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Map message = (Map) payload;
                    System.out.println("Message: " + message);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                            .create();
                    game = gson.fromJson(message.toString(), Game.class);
                    GameSocketConnection.this.updateLabels();
                }
            });
            System.out.println("Subscribed to /topic/game/" + game.getId());
            System.out.println("Connected: " + connectedHeaders.toString());
        }
    }
}