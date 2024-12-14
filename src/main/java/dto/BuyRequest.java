package dto;

import dataModels.Player;

public class BuyRequest {
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
