package network;

import client.Client;
import dataModels.Player;
import dto.AuctionedPlayerList;
import dto.BuyConfirmation;
import javafx.application.Platform;
import server.NetworkUtil;

import java.io.IOException;
import java.util.List;

public class ReadThreadClient implements Runnable {
    NetworkUtil networkUtil;
    Thread thread;
    List<Player> players;
    public ReadThreadClient(NetworkUtil networkUtil){
        this.networkUtil = networkUtil;
        this.thread = new Thread(this);
        this.players = Client.getPlayers();
        thread.start();
    }

    @Override
    public void run() {
        //this thread will be running in the client side and read the dtos coming from the server

        try {
            while(true){
                Object o = networkUtil.read();
                //the client club can get auction update , buy request etc from the server, we'll handle them later
                //but at the very beginning , the client will get the list of auctioned players
                if (o instanceof AuctionedPlayerList) {
                    AuctionedPlayerList apl = (AuctionedPlayerList) o;
                    List<Player> auctionedPlayerList = apl.getAuctionedPlayerList();

                    auctionedPlayerList.removeIf(player -> player.getClub().equals(Client.getClientClubName()));

                    Client.auctionedPlayerList = auctionedPlayerList;

//                    Platform.runLater(() -> Client.observableAuctionedPlayerList.setAll(Client.auctionedPlayerList));
                }

                if (o instanceof BuyConfirmation) {
                    BuyConfirmation bc = (BuyConfirmation) o;
                    Player player = bc.getPlayer();

                    Platform.runLater(() -> {
                        // Remove the player from the regular auctioned player list
                        for (int i = 0; i < Client.auctionedPlayerList.size(); i++) {
                            if (Client.auctionedPlayerList.get(i).getName().equals(player.getName())) {
                                Client.auctionedPlayerList.remove(i);
                                break;
                            }
                        }
                        // Also update the observable auctioned player list
                        Client.observableAuctionedPlayerList.setAll(Client.auctionedPlayerList);

                        // If the client is the buyer, add the player to their team
                        if (Client.getClientClubName().equals(bc.getDestinationClub())) {
                            Client.getPlayers().add(player);
                            //clear the observable list and add all the players again
                            Client.getObservablePlayerList().clear();
                            Client.getObservablePlayerList().addAll(Client.getPlayers());
                        }
                        // If the client is the seller, remove the player from their team
                        else if (Client.getClientClubName().equals(player.getClub())) {
                            Client.getPlayers().remove(player);

                            //clear the observable list and add all the players again
                            Client.getObservablePlayerList().clear();
                            Client.getObservablePlayerList().addAll(Client.getPlayers());
                        }
                    });
                }




            }
        } catch (Exception e) {
            System.out.println("ReadThreadClient e jhamela");
        }
    }
}
