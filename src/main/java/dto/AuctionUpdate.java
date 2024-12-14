package dto;

import dataModels.Player;

public class AuctionUpdate {
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
