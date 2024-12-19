package network;

import client.Client;
import dataModels.Player;
import dto.AuctionUpdate;
import dto.AuctionedPlayerList;
import dto.BuyConfirmation;
import dto.PlayerAddRequest;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
                Object o = null;
                try {
                    o = networkUtil.read();
                } catch (Exception e) {
                    System.out.println("Client disconnected");
                    break;
                }
                //the client club can get auction update , buy request etc from the server, we'll handle them later
                //but at the very beginning , the client will get the list of auctioned players
                if (o instanceof AuctionedPlayerList) {
                    AuctionedPlayerList apl = (AuctionedPlayerList) o;
                    List<Player> auctionedPlayerList = apl.getAuctionedPlayerList();

                    auctionedPlayerList.removeIf(player -> player.getClub().equals(Client.getClientClubName()));

                    Client.auctionedPlayerList = auctionedPlayerList;

//                    Platform.runLater(() -> Client.observableAuctionedPlayerList.setAll(Client.auctionedPlayerList));
                }
                else if(o instanceof PlayerAddRequest){
                    PlayerAddRequest par = (PlayerAddRequest) o;
                    Player player = par.getPlayer();
                    String status = par.getStatus();
                    if(status.equals("REJECTED")){
                        //there is a player with the same name so we will not the player in the list instead show an error alert saying that using platform.runlater
                        Platform.runLater(() -> {
                            //show alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Task Successfully Unsuccessful 😒");
                            alert.setContentText("Please try again with a different name");
                            alert.showAndWait();
                        });

                    }
                    else if(status.equals("APPROVED")){
                        //add the player to the list
                        players.add(player);
                        //tell the club with an alert that the player addition was successful
                        Platform.runLater(() -> {

                            //clear the observable list and add all the players again
                            try {
                                Client.observablePlayerList.add(player);
                            } catch (Exception e) {
                                System.out.println("Observable list error");
                            }
                            //show alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Task Successfully Completed ☺️");
                            alert.setContentText("The player has been added to your team");
                            alert.showAndWait();
                        });

                    }
                }


                else if (o instanceof BuyConfirmation) {
                    BuyConfirmation bc = (BuyConfirmation) o;
                    Player player = bc.getPlayer();
                    //the seller club should remove its player from the list and the result should be seem immediately in the ui
                    System.out.println("Buy confirmation for player "+player.getName()+ "  Player current club: "+player.getClub()+  " buyer club "+bc.getDestinationClub()+" received by the client");
                    //make this block synchronized so that only one thread can access this block at a time

                    Platform.runLater(() -> {
                        System.out.println("Buy confirmation er run later e duklo");
                        // Remove the player from the regular auctioned player list
                        Client.auctionedPlayerList.remove(player);
                        // Also update the observable auctioned player list
                        Client.observableAuctionedPlayerList.clear();
                        Client.observableAuctionedPlayerList.setAll(Client.auctionedPlayerList);

                        // If the client is the buyer, add the player to their team
                        if (Client.getClientClubName().equals(bc.getDestinationClub())) {
                            System.out.println("Buy confirmation er run later e buyer er kaj korlo");
                            Client.getPlayers().add(player);
                            //clear the observable list and add all the players again
                            Client.getObservablePlayerList().clear();
                            Client.getObservablePlayerList().addAll(Client.getPlayers());
//                            System.out.println("Number of player in the club "+Client.getClientClubName()+" is "+Client.getPlayers().size());
                        }
                        // If the client is the seller, remove the player from their team
                        else if (Client.getClientClubName().equals(bc.getPrevClub())) {
                            System.out.println("Buy confirmation er run later e seller er kaj korlo");

                            Client.getPlayers().remove(player);
                            //clear the observable list and add all the players again
                            Client.getObservablePlayerList().clear();
                            Client.getObservablePlayerList().addAll(Client.getPlayers());
                            System.out.println("After removing the player the number of playeri n the list is "+Client.getObservablePlayerList().size());
                        }
                    });


                    player.setClub(bc.getDestinationClub());
                    player.setAuctionState(false);
                }
                else if(o instanceof AuctionUpdate){
                    AuctionUpdate au = (AuctionUpdate) o;
                    Player player = au.getPlayer();

                    System.out.println("Club "+Client.getClientClubName()+" got the auction update of player "+player.getName());
                    System.out.println("Before adding the player in the auction list the size of the list is "+Client.auctionedPlayerList.size());
                    Platform.runLater(() -> {
                        // Add the player to the auctioned player list
                        Client.auctionedPlayerList.add(player);
                        // Also update the observable auctioned player list
                        Client.observableAuctionedPlayerList.clear();
                        Client.observableAuctionedPlayerList.setAll(Client.auctionedPlayerList);
                        //show an information alert that the player is now in auction
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Auction Update");
                        alert.setHeaderText("Task Successfully Completed ☺️");
                        alert.setContentText("Player " + player.getName() + " is now in auction");
                        alert.showAndWait();

                    });
                    System.out.println("After adding the player, Number of player in the auction player list "+Client.auctionedPlayerList.size());
                }
            }
        } catch (Exception e) {
            System.out.println("ReadThreadClient e jhamela");
            e.printStackTrace();

        }
    }
}
