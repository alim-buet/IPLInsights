package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import dto.BuyRequest;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import server.NetworkUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AuctionController implements Initializable {

    @FXML public TableColumn<Player, String> positionColumn;
    @FXML public TableColumn<Player, Integer> numberColumn;
    @FXML public TableColumn<Player, String> weeklySalaryColumn;
    @FXML private TableView<Player> playerTable;
    @FXML private TableColumn<Player, String> nameColumn;
    @FXML private TableColumn<Player, String> clubColumn;
    @FXML private TableColumn<Player, Integer> ageColumn;
    @FXML private TableColumn<Player, Double> heightColumn;
    @FXML private TableColumn<Player, String> priceColumn;
    @FXML private TableColumn<Player, Void> buyColumn;
    @FXML private Button secondaryButton;
    @FXML private Label title;
    @FXML private VBox vBox;

    private final PlayerDatabase playerDatabase = Client.getPlayerDatabase();
    private final NetworkUtil networkUtil = Client.getSockt();

    // ObservableList for UI binding
    private final ObservableList<Player> observableAuctionedPlayerList = Client.getObservableAuctionedPlayerList();

    @FXML
    private void switchToPrimary(ActionEvent event) {
        try {
            if (Client.getUserMode() == UserMode.CLUB) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signed-in.fxml"));
                Client.getMainStage().getScene().setRoot(loader.load());
            }
        } catch (IOException e) {
            System.err.println("Error switching view from auction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind table columns to player properties
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        clubColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClub()));
        ageColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAge()));
        heightColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHeight()));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>("%.0f".formatted(cellData.getValue().getPrice())));
        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPosition()));
        numberColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNumber()));
        weeklySalaryColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>("%.0f".formatted(cellData.getValue().getWeeklySalary())));

        buyColumn.setCellFactory(tc -> new TableCell<>() {
            private final Button buyButton = new Button("Buy");

            {
                buyButton.setOnAction(e -> {
                    Player player = getTableView().getItems().get(getIndex());
                    if (player != null) {
                        if (player.getClub().equals(Client.clientClubName)) {
                            showAlert(Alert.AlertType.ERROR, "Error", "You can't buy your own player");
                        } else {
                            Alert buyConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                            buyConfirmation.setTitle("Buy Player");
                            buyConfirmation.setHeaderText("Are you sure you want to buy " + player.getName() + " for " + player.getPrice() + " INR?");
                            buyConfirmation.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    handleBuyAction(player);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buyButton);
            }
        });

        List<Player> playerList = Client.auctionedPlayerList;
        observableAuctionedPlayerList.setAll(playerList);
        //list ta bind kora lagbe
        playerTable.setItems(observableAuctionedPlayerList);
    }

    private void handleBuyAction(Player player) {
        if (player != null) {
            try {
                networkUtil.write(new BuyRequest(player, Client.getClientClubName()));
                System.out.println(player.getName() + "'s buy request sent to the server.");
                observableAuctionedPlayerList.remove(player);

            } catch (Exception e) {
                System.err.println("Error sending purchase request for player: " + player.getName());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
