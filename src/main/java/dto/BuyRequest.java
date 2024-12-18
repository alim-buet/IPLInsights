package dto;

import dataModels.Player;

import java.io.Serializable;

public class BuyRequest implements Serializable {
    private Player player;
    private String destinationClub; //club that is gonna buy that player
    public BuyRequest(Player player, String destinationClub){
        this.player = player;
        this.destinationClub = destinationClub;
    }
    public Player getPlayer() {
        return player;
    }
    public String getDestinationClub() {
        return destinationClub;
    }
}
