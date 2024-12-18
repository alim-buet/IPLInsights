package dto;

import dataModels.Player;

import java.io.Serializable;

public class BuyConfirmation implements Serializable {
    private Player player;
    private String destinationClub;


    public BuyConfirmation(Player player, String destinationClub) {
        this.player = player;
        this.destinationClub = destinationClub;
    }

    public String getDestinationClub() {
        return destinationClub;
    }

    public void setDestinationClub(String destinationClub) {
        this.destinationClub = destinationClub;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
