package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import dto.AuctionRequest;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import server.NetworkUtil;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlayerDisplayController implements Initializable {

    @FXML
    public ListView<String> playerListView;

    @FXML
    public Button testLoad, auctionButton;

    @FXML
    public Label nameLabel, clubLabel, countryLabel, ageLabel, heightLabel, numberLabel, positionLabel, salaryLabel, auctionLabel;

    @FXML
    public ImageView playerImage, clubImage, countryImage;

    @FXML
    public HBox playerData;

    @FXML
    public VBox auctionBox;

    @FXML
    public TextField priceText;
    //controllers gonna show stuffs and do operations, pass some dtos to the server. so it should have the access of playerDatabase and the network util
    NetworkUtil networkUtil = Client.getSockt();
    List<Player> players = Client.getPlayers();
    //we need to do certain operations on the player list, so mere lise is not enough. we gotta build a database. a new constructor add kora lagbe which i did
    PlayerDatabase playerDatabase = new PlayerDatabase(players);
    @FXML public void showPlayerInfo(Player p) {
        nameLabel.setText(p.getName());
        clubLabel.setText(p.getClub());
        countryLabel.setText(p.getCountry());
        ageLabel.setText(String.valueOf(p.getAge()));
        heightLabel.setText(String.valueOf(p.getHeight()));
        numberLabel.setText(String.valueOf(p.getNumber()));
        positionLabel.setText(p.getPosition());
        //we have to show it in correct format
        salaryLabel.setText("%.0f".formatted(p.getWeeklySalary())+" INR");
        //all labels are in enabled form
        ageLabel.setDisable(false);
        heightLabel.setDisable(false);
        numberLabel.setDisable(false);
        positionLabel.setDisable(false);
        salaryLabel.setDisable(false);

        Image playerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/playerImages/" + p.getName() + ".png")));
        this.playerImage.setImage(playerImage);
        Image cflag = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/" + p.getCountry() + ".png")));
        countryImage.setImage(cflag);
        Image clublogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/" + p.getClub() + ".png")));
        clubImage.setImage(clublogo);
        if (Client.getUserMode() == UserMode.CLUB) {
            if(p.isAuctioned()){
                auctionButton.setDisable(true);
                priceText.setDisable(true);
                priceText.setText(String.valueOf(p.getPrice()));
                auctionLabel.setText("Auctioned");

            }
            else{
                auctionButton.setDisable(false);
                priceText.setDisable(false);
                auctionLabel.setText("Enter price:");
            }

        }
    }
   @FXML public void showPlayerInfo(){
        String name = playerListView.getSelectionModel().getSelectedItem();
        Player p = playerDatabase.searchByName(name);
        //two things can happen, if the client is a club we gotta check if the player is in the club or not, for the guest we will always show the player
        if(Client.getUserMode()==UserMode.GUEST){
            showPlayerInfo(p);
        }
        else{
            if(p.getClub().equals(Client.getClientClubName())){
                showPlayerInfo(p);
            }
            else{
                //the player is not in the club, so we will show a generic image and disable all the labels
                resetPlayerInfo();
            }
        }
    }


   @FXML public void loadPlayers() {
        ObservableList<String> names = FXCollections.observableArrayList();
        for (Player player : players) {
            names.add(player.getName());
        }
        if(Client.getUserMode()== UserMode.CLUB){
            //then the initial state of auction button should be disabled, price spinner should be disabled too
            auctionButton.setDisable(true);
            priceText.setDisable(true);
            auctionLabel.setText("Set Price");

        }
        playerListView.setItems(names);

    }
  @FXML  public void resetPlayerInfo(){
        nameLabel.setText("No Player is Selected");
        clubLabel.setText("");
        countryLabel.setText("");
        ageLabel.setText("");
        heightLabel.setText("");
        numberLabel.setText("");
        positionLabel.setText("");
        salaryLabel.setText("");
        //all labels are in disabled form
        ageLabel.setDisable(true);
        heightLabel.setDisable(true);
        numberLabel.setDisable(true);
        positionLabel.setDisable(true);
        salaryLabel.setDisable(true);
        //as no player is selected we will choose a generic image to show in the profile pic viewer
        Image playerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/playerImages/generic-player.png")));
        this.playerImage.setImage(playerImage);
        Image cflag = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/generic-country.png")));

        countryImage.setImage(cflag);
        Image clublogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/generic-logo.png")));
        clubImage.setImage(clublogo);
        if (Client.getUserMode() == UserMode.CLUB) {
            auctionButton.setDisable(true);
            priceText.setDisable(true);
            auctionLabel.setText("Enter price:");
        }

    }

   @FXML public void auctionPlayer() {
        //a club owner trying to auction a player, we'll fetch the player
        Player p = playerDatabase.searchByName(playerListView.getSelectionModel().getSelectedItem());
        //show an alert for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Auction Confirmation");
        alert.setHeaderText("Are you sure you want to auction "+p.getName()+"?");
        alert.setContentText("Please confirm");
        alert.showAndWait();
        if(alert.getResult()== ButtonType.OK){
            //player ta ke garite tol, imean create a new auction request dto and send it to the server, the server will take care of the rest
            //server will write the player in the auction list text file and send the info to other clients so that they can see that palyer available in the auciton market
            p.setAuctionState(true);
            p.setPrice(Double.parseDouble(priceText.getText()));
            //dto baniye send him to the server
            try {
                networkUtil.write(new AuctionRequest(p.getName(),p.getClub(),p.getPrice()));
                loadPlayers();
                resetPlayerInfo();
            } catch (Exception e) {
                System.out.println("auction request write e jhamela");
            }


        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
