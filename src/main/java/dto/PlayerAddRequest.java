package dto;

import dataModels.Player;

import java.io.Serializable;

public class PlayerAddRequest implements Serializable {
    Player player;

    public PlayerAddRequest(Player player, String status) {
        this.player = player;
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

}
