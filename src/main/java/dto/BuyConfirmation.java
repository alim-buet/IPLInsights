package dto;

import dataModels.Player;

import java.io.Serializable;

public class BuyConfirmation implements Serializable {
    private Player player;
    private String destinationClub;
    private String prevClub;

    public String getPrevClub() {
        return prevClub;
    }

    public void setPrevClub(String prevClub) {
        this.prevClub = prevClub;
    }

    public BuyConfirmation(Player player, String destinationClub, String prevClub) {
        this.player = player;
        this.destinationClub = destinationClub;
        this.prevClub = prevClub;
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
