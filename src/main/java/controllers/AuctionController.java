package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import dto.BuyRequest;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import server.NetworkUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuctionController implements Initializable {

    // Reference to the FXML components
    @FXML private TableView<Player> playerTable;
    @FXML private TableColumn<Player, String> nameColumn;
    @FXML private TableColumn<Player, String> clubColumn;
    @FXML private TableColumn<Player, Integer> ageColumn;
    @FXML private TableColumn<Player, Double> heightColumn;
    @FXML private TableColumn<Player, Double> priceColumn;
    @FXML private TableColumn<Player, Player> buyColumn;
    @FXML private Button secondaryButton;
    @FXML private Label title;
    @FXML private VBox vBox;
    // Access to client-side data and network utilities
    private final PlayerDatabase playerDatabase = Client.getPlayerDatabase();
    private final NetworkUtil networkUtil = Client.getSockt();

    @FXML
    private void switchToPrimary(ActionEvent event) {
        try {
            if (Client.getUserMode() == UserMode.CLUB) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signed-in.fxml"));
                Client.getMainStage().getScene().setRoot(loader.load());
            }
        } catch (IOException e) {
            System.err.println("auction view theke back korte problem");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        clubColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClub()));
        ageColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAge()));
        heightColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHeight()));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getPrice()));

        // Add Buy button dynamically to the Buy column
        buyColumn.setCellFactory(tc -> new TableCell<>() {
            private final Button buyButton = new Button("Buy");

            {
                buyButton.setOnAction(e -> {
                    Player player = getTableView().getItems().get(getIndex());
                    if (player.getClub().equals(Client.clientClubName)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("You can't buy your own player");
                        alert.showAndWait();
                    } else {
                        Alert buyConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        buyConfirmation.setTitle("Buy Player");
                        buyConfirmation.setHeaderText("Are you sure you want to buy " +
                                player.getName() + " for " + player.getPrice() + " INR?");
                        //if yes clicked then handle the player buying
                        buyConfirmation.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                handleBuyAction(player);
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setGraphic(null);
                } else {
                    setGraphic(buyButton);
                }
            }
        });

        // Load data into the table
        playerTable.setItems(FXCollections.observableArrayList(playerDatabase.getAuctionList()));
    }


    private void handleBuyAction(Player player) {
        if (player != null) {
            try {
                networkUtil.write(new BuyRequest(player, Client.getClientClubName()));

                System.out.println( player.getName()+"'s dto for buy request is sent to the server..server will take care of the rest");

            } catch (Exception e) {
                System.err.println("Error sending purchase request for player: "+player.getName());
            }
        }
    }
}
