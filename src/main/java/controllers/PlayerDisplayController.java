package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import dto.AuctionRequest;
import javafx.collections.ListChangeListener;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    // Get the NetworkUtil and PlayerDatabase from Client class
    NetworkUtil networkUtil = Client.getSockt();
    List<Player> players = Client.getPlayers();
    ObservableList<Player> observablePlayerList = Client.observablePlayerList;
    // Database for searching players
    PlayerDatabase playerDatabase = new PlayerDatabase(Client.getPlayers());

    @FXML
    public void showPlayerInfo(Player p) {
        System.out.println("Showing player info for " + p.getName());
        if (p == null) {
            return;
        }
        nameLabel.setText(p.getName());
        clubLabel.setText(p.getClub());
        countryLabel.setText(p.getCountry());
        ageLabel.setText(String.valueOf(p.getAge()));
        heightLabel.setText(String.valueOf(p.getHeight()));
        numberLabel.setText(String.valueOf(p.getNumber()));
        positionLabel.setText(p.getPosition());
        salaryLabel.setText("%.0f".formatted(p.getWeeklySalary()) + " INR");

        // Enable all the labels once data is set
        ageLabel.setDisable(false);
        heightLabel.setDisable(false);
        numberLabel.setDisable(false);
        positionLabel.setDisable(false);
        salaryLabel.setDisable(false);

        // Set player image
        Image playerImage;
        try {
            playerImage= new Image(Objects.requireNonNull(getClass().getResourceAsStream("/playerImages/" + p.getName() + ".png")));
        }
        catch (Exception e) {
            System.out.println("Player image not found");
            playerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/playerImages/generic-player.png")));
        }
        this.playerImage.setImage(playerImage);

        // Set country flag
        Image cflag;
        try {
            cflag = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/" + p.getCountry() + ".png")));
        }
        catch (Exception e) {
            System.out.println("Country flag not found");
            cflag = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/generic-country.png")));
        }
        countryImage.setImage(cflag);

        // Set club logo
        Image clublogo;
        try {
            clublogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/" + p.getClub() + ".png")));
        }
        catch (Exception e) {
            System.out.println("Club logo not found");
            clublogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/generic-logo.png")));
        }
        clubImage.setImage(clublogo);

        // If the user is in CLUB mode
        if (Client.getUserMode() == UserMode.CLUB) {
            if (p.isAuctioned()) {
                auctionButton.setDisable(true);
                priceText.setDisable(true);
                priceText.setText(String.valueOf(p.getPrice()));
                auctionLabel.setText("Auctioned");
            } else {
                auctionButton.setDisable(false);
                priceText.setDisable(false);
                auctionLabel.setText("Enter price:");
            }
        }
    }

    @FXML
    public void showPlayerInfo() {
        System.out.println("Now the player list view contains number of players: " + playerListView.getItems().size());
        String name = playerListView.getSelectionModel().getSelectedItem();

        Player p = playerDatabase.searchByName(name);
//        System.out.println("Selected player name: " + name+" and club: "+p.getClub());

        // Show player info based on user mode
        if (Client.getUserMode() == UserMode.GUEST) {
            try {
                showPlayerInfo(p);
            } catch (Exception e) {
                System.out.println("No player found!");
            }
        } else {
            if (p.getClub().equals(Client.getClientClubName())) {
                showPlayerInfo(p);
            } else {
                // If the player is not in the club, reset the player info
                resetPlayerInfo();
            }
        }
    }

    @FXML
    public void loadPlayers() {
        playerListView.setItems(
                FXCollections.observableArrayList(
                        observablePlayerList.stream()
                                .map(Player::getName)
                                .collect(Collectors.toList())
                )
        );

        if (Client.getUserMode() == UserMode.CLUB) {
            auctionButton.setDisable(true);
            priceText.setDisable(true);
            auctionLabel.setText("Set Price");
        }
    }



    @FXML
    public void resetPlayerInfo() {
        nameLabel.setText("No Player is Selected");
        clubLabel.setText("");
        countryLabel.setText("");
        ageLabel.setText("");
        heightLabel.setText("");
        numberLabel.setText("");
        positionLabel.setText("");
        salaryLabel.setText("");

        // Disable all labels when no player is selected
        ageLabel.setDisable(true);
        heightLabel.setDisable(true);
        numberLabel.setDisable(true);
        positionLabel.setDisable(true);
        salaryLabel.setDisable(true);

        // Set generic images for the profile
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

    @FXML
    public void auctionPlayer() {
        // Fetch the player to auction
        Player p = playerDatabase.searchByName(playerListView.getSelectionModel().getSelectedItem());

        // Show confirmation dialog before auctioning
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Auction Confirmation");
        alert.setHeaderText("Are you sure you want to auction " + p.getName() + "?");
        alert.setContentText("Please confirm");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            // Mark player as auctioned and set price
            p.setAuctionState(true);
            try {
                p.setPrice(Double.parseDouble(priceText.getText()));
            } catch (NumberFormatException e) {
                //show an alert
                Alert priceAlert = new Alert(Alert.AlertType.ERROR);
                priceAlert.setTitle("Invalid Price");
                priceAlert.setHeaderText("Please enter a valid price");
                priceAlert.setContentText("Please enter a valid price for the player");
                priceAlert.showAndWait();
            }

            // Create AuctionRequest DTO and send to server
            try {
                networkUtil.write(new AuctionRequest(p.getName(), p.getClub(), p.getPrice()));
                System.out.println("Auction request sent from playercontroller class for " + p.getName());
                loadPlayers(); // Reload players with updated observable list
                resetPlayerInfo(); // Reset player info
            } catch (Exception e) {
                System.out.println("Auction request write error");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Directly bind the observablePlayerList to the ListView
        observablePlayerList.addListener((ListChangeListener<Player>) change -> {
            playerListView.setItems(
                    FXCollections.observableArrayList(
                            observablePlayerList.stream()
                                    .map(Player::getName)
                                    .collect(Collectors.toList())
                    )
            );
        });

        loadPlayers();
    }

}
