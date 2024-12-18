package dto;

import dataModels.Player;

import java.io.Serializable;

public class AuctionUpdate implements Serializable {
    Player player;

    public AuctionUpdate(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
