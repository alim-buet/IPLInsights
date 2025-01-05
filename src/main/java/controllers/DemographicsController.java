package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;

public class DemographicsController implements Initializable {
   @FXML public Button secondaryButton;
   @FXML public TableView<CountryData> countryTable;
   @FXML public TableColumn<CountryData,ImageView> countryFlagColumn;
  @ FXML
    public TableColumn<CountryData,String> countryColumn;
   @FXML public TableColumn<CountryData,Integer> countryNumColumn;
   @FXML public ImageView clubLogo_demographics;
   List<Player> players = Client.getPlayers();
   PlayerDatabase playerDatabase =Client.getPlayerDatabase();



   @FXML public void switchToPrimary() {
        FXMLLoader loader = new FXMLLoader();
        if(Client.getUserMode() == UserMode.CLUB) loader.setLocation(getClass().getResource("/views/signed-in.fxml"));
        else loader.setLocation(getClass().getResource("/views/guest.fxml"));

        try {
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Demographics view theke back korte problem");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up columns
        countryFlagColumn.setCellValueFactory(new PropertyValueFactory<>("flag"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));

        countryNumColumn.setCellValueFactory(new PropertyValueFactory<>("playerCount"));

        countryTable.setItems(getCountryData());
        if(Client.getUserMode()==UserMode.CLUB){
            clubLogo_demographics.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(String.format("/clubLogos/%s.png", Client.getClientClubName())))));
        }
        else if(Client.getUserMode()==UserMode.GUEST){
            clubLogo_demographics.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/iplLogo.png"))));
        }
    }

    private ObservableList<CountryData> getCountryData() {
        ObservableList<CountryData> countryDataList = FXCollections.observableArrayList();

        HashMap<String, Integer> countryPlayerCounts = playerDatabase.countryWiseCOunt();

        for (Map.Entry<String, Integer> entry : countryPlayerCounts.entrySet()) {
            String countryName = entry.getKey();
            int playerCount = entry.getValue();
            ImageView flagImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(String.format("/flags/%s.png", countryName)))));
            flagImageView.setFitHeight(60);
            flagImageView.setFitWidth(100);

            countryDataList.add(new CountryData(flagImageView, countryName, playerCount));
        }

        return countryDataList;
    }

    public static class CountryData {
        private ImageView flag;
        private String countryName;
        private int playerCount;

        public CountryData(ImageView flag, String countryName, int playerCount) {
            this.flag = flag;
            this.countryName = countryName;
            this.playerCount = playerCount;
        }

        public ImageView getFlag() {
            return flag;
        }

        public void setFlag(ImageView flag) {
            this.flag = flag;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }


        public int getPlayerCount() {
            return playerCount;
        }

        public void setPlayerCount(int playerCount) {
            this.playerCount = playerCount;
        }
    }
}
