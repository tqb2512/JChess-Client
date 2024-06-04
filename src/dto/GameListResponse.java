package dto;

import lombok.Getter;
import lombok.Setter;
import model.GameStatus;
import model.User;

@Getter
@Setter
public class GameListResponse {
    private String id;
    private User player1;
    private User player2;
    private GameStatus status;
}
