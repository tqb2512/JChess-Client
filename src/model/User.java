package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String username;
    private transient String password;
    private int totalGames;
    private int wins;
    private int losses;
}