package dto;

import java.io.Serializable;

public class AuctionRequest implements Serializable {
    private String clubName;
    private String playerName;
    private double price;
    public AuctionRequest(String playerName, String clubName, double price) {
        this.clubName = clubName;
        this.playerName = playerName;
        this.price = price;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
