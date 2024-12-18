package dto;

import dataModels.Player;

import java.io.Serializable;
import java.util.List;

public class AuctionedPlayerList implements Serializable {
    List<Player> auctionedPlayerList;

    public AuctionedPlayerList(List<Player> auctionedPlayerList) {
        this.auctionedPlayerList = auctionedPlayerList;
    }

    public void setAuctionedPlayerList(List<Player> auctionedPlayerList) {
        this.auctionedPlayerList = auctionedPlayerList;
    }


    public List<Player> getAuctionedPlayerList() {
        return auctionedPlayerList;
    }


}
