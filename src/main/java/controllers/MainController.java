package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import server.NetworkUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public ImageView clubLogoDisplay;
    public Label clubNameDisplay;
    public TabPane tabPane1;
    public Button nameSearchButton;
    public ComboBox<String> clubSearch;
    public ComboBox<String> countrySearch;
    public Button nameSearchButton1;
    public ComboBox<String> positionSearch;
    public Button nameSearchButton2;
    public Spinner<Double> r1Spinner;
    public Spinner<Double> r2Spinner;
    public Button nameSearchButton11;
    public AnchorPane playerDisplayP;
    public Label clubNameDisplay1;
    public AnchorPane playerDisplayC;
    public TextField addName;
    public ChoiceBox<String> addPosition;
    public TextField addCountry;
    public Spinner<Double> addAge;
    public Spinner<Double> addHeight;
    public Spinner<Double> addSalary;
    public Spinner<Integer> addNumber;
    public Button addPfp1;
    public ImageView showSettings;
    public PlayerDisplayController playerDisplayPController, playerDisplayCController;
    public TextField searchNameTextField;
    public TextField nameSearchTextbox;
    public ComboBox<String> selectedClub1;
    public Button nameSearchButton131;
    public ComboBox<String> selectedClub11;
    public Button nameSearchButton1312;
    public ComboBox<String> selectedClub;
    public Button nameSearchButton1311;
    public ComboBox<String> selectedClubForMaxAge;
    public ComboBox<String> selectedClubForMaxHeight;
    public ComboBox<String> SelectedClubForMaxSalaray;
    public Button nameSearchButton13111;
    public ComboBox<String> selectedClubForMaxAge1;
    public Label clubAnnualSalaryLabel;


    public void logout() {
        //only the club client has that button acsess so no need to check the user mode
        //first ekta confirmation alert show kore dei

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        alert.showAndWait();
        if(alert.getResult()==ButtonType.OK){
            try {
                //network connection close kore dite hobe

                Client.getSockt().closeConnection();
                //notun socket open kore lagbe client er jonno
                Client.setSockt(new NetworkUtil("127.0.0.1",12345));
            } catch (Exception e) {
                System.out.println("Logout er shomoy network close korte jhamela");
            }

            //login page load kora lagbe
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("@../../resources/views/login.fxml"));
            try {
                Client.getMainStage().getScene().setRoot(loader.load());
            } catch (Exception e) {
                System.out.println("logout kore login view Load korte jhamela");
            }
        }
    }

    public void refreshList(Event event) {
        playerDisplayPController.loadPlayers();
        playerDisplayCController.loadPlayers();
        playerDisplayPController.resetPlayerInfo();
        playerDisplayCController.resetPlayerInfo();

    }

    public void searchByName(ActionEvent actionEvent) {
        Player p = Client.playerDatabase.searchByName(searchNameTextField.getText());
        if(p==null){
            //show an alert that no player found
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No player found");
            alert.setHeaderText(null);
            alert.setContentText("No player found with the given name");
            alert.showAndWait();
        }
        else{
            //show the player info in the player display
            playerDisplayPController.playerListView.setItems(FXCollections.observableArrayList(p.getName()));

        }

    }

    public void searchByCountryAndClub(ActionEvent actionEvent) {
        String club = clubSearch.getValue();
        String country = countrySearch.getValue();
        List<Player> result = Client.playerDatabase.searchByClubAndCountry(club, country);
        handleResult(result, playerDisplayPController);

    }

    public void searchByPosition(ActionEvent actionEvent) {
        String position = positionSearch.getValue();
        List<Player> result = Client.playerDatabase.searchByPosition(position);
        handleResult(result, playerDisplayPController);
    }

    public void searchBySalary(ActionEvent actionEvent) {
        List<Player> result = Client.playerDatabase.searchByWeeklySalary(r1Spinner.getValue(), r2Spinner.getValue());
        handleResult(result, playerDisplayPController);
    }

    public void showDemographics(Event event) {
        //user clicks on the countrywise player analysis tab. demographics view load kore client er scene oita set kora lagbe
        //load the demographics view fxml file and set it as the scene of the client
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("@../../resources/views/demographics.fxml"));
        try {
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Demographics view Load korte jhamela");
        }
    }

    public void showAnnualSalary() {
        if(Client.getUserMode()==UserMode.CLUB){
            //show the annual salary of the club
            clubAnnualSalaryLabel.setText("Annual Salary: "+Client.playerDatabase.totalSalary(Client.getClientClubName())+" INR");
        }
        else if(Client.getUserMode()==UserMode.GUEST){
            //view ache. so oita load korbo
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("@../../resources/views/salary.fxml"));
            try {
                Client.getMainStage().getScene().setRoot(loader.load());
            } catch (Exception e) {
                System.out.println("Salary view Load korte jhamela");
            }
        }
    }

    public void showAuction(Event event) {
        //user clicks on the auction tab. auction view load kore client er scene oita set kora lagbe
        //load the auction view fxml file and set it as the scene of the client
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("@../../resources/views/auction.fxml"));
        try {
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Auction view Load korte jhamela");
        }
    }

    public void submitPlayer() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input missing");
        alert.setHeaderText(null);
        //gather all the informations about that player, build the player and give it to the server. server reply dibe kichu ekta, oita onujayi baki kaj kora lagbe
        if (addName.getText().isBlank()) {
            alert.setContentText("Please enter a name for the player.");
            alert.showAndWait();
        }
        else if (addCountry.getText().isBlank()) {
            alert.setContentText("Please enter a country for the player.");
            alert.showAndWait();
        }
        else if (addAge.getValue() == 0) {
            alert.setContentText("Please enter an age for the player.");
            alert.showAndWait();
        }
        else if (addHeight.getValue() == 0) {
            alert.setContentText("Please enter a height for the player.");
            alert.showAndWait();
        }
        else if (addPosition.getValue().isBlank()) {
            alert.setContentText("Please enter a position for the player.");
            alert.showAndWait();
        }
        else if (addNumber.getValue() == 0) {
            alert.setContentText("Please enter a number for the player.");
            alert.showAndWait();
        }
        else if (addSalary.getValue() == 0) {
            alert.setContentText("Please enter a salary for the player.");
            alert.showAndWait();
        }
        else {
            //everything is aight
            //create the player
            Player p = new Player(addName.getText(), addCountry.getText(),  addAge.getValue().intValue(), addHeight.getValue(), Client.getClientClubName(), addPosition.getValue(), addNumber.getValue(), addSalary.getValue());
            //send the guy to the server
            try {
                Client.getSockt().write(p);
                //clear the textfields
                addName.clear();
                addCountry.clear();
                addAge.getValueFactory().setValue(0.0);
                addHeight.getValueFactory().setValue(0.0);
                addPosition.getSelectionModel().clearSelection();
                addNumber.getValueFactory().setValue(0);
                addSalary.getValueFactory().setValue(0.0);
            } catch (Exception e) {
                System.out.println("add player e Player write e jhamela");
            }

        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Client.setPlayerDisplayC(playerDisplayCController);
            Client.setPlayerDisplayP(playerDisplayPController);
            playerDisplayPController.loadPlayers();
            playerDisplayCController.loadPlayers();
        } catch (Exception e) {
            System.out.println("PlayerDisplayController load korte jhamela");
        }
        tabPane1.getSelectionModel().select(0);
        //meaning first tab is selected initially
        PlayerDatabase pdb = new PlayerDatabase(Client.getPlayers());
        List<String> clubNames = new ArrayList<>();

        clubNames = pdb.getClubNames();
        List<String> countryNames = new ArrayList<>();
        countryNames = pdb.getCountryNames();


        if(Client.getUserMode()== UserMode.GUEST){
            //remove the auction box from the player display
            playerDisplayPController.playerData.getChildren().remove(playerDisplayPController.auctionBox);
            playerDisplayCController.playerData.getChildren().remove(playerDisplayCController.auctionBox);
            //in the fxml view there is a selection box for the club, we need to load all the club names in that box
            //there is a function in the database that returns the list of all available club names

            selectedClub.setItems(FXCollections.observableArrayList(clubNames));

            //set the items of the combo box
            selectedClub.getSelectionModel().select(0);
            selectedClub.setEditable(false);



        }
        else if(Client.getUserMode()==UserMode.CLUB){
            //initialize the clubname
            clubNameDisplay.setText(Client.getClientClubName());
            Image clubLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("@../resources/clubLogos/" + Client.getClientClubName() + ".png")));
            clubLogoDisplay.setImage(clubLogo);
            addPosition.getItems().addAll("Batsman","Bowler","Wicketkeeper","Allrounder");


        }
        clubSearch.setItems(FXCollections.observableArrayList(clubNames));
        countrySearch.setItems(FXCollections.observableArrayList(countryNames));
        countrySearch.getItems().addFirst("-------ANY-------");
        positionSearch.setItems(FXCollections.observableArrayList("Batsman","Bowler","Wiicketkeeper","Allrounder"));
        //we dont want a club client to access the search filtering by club name
        if(Client.getUserMode()==UserMode.CLUB){
            clubSearch.setDisable(true);
            clubSearch.getSelectionModel().select(Client.getClientClubName());
        }




        clubSearch.getSelectionModel().select(0);


    }

    public void maxAgeSearch() {
        String clubName;
        if(Client.getUserMode()==UserMode.CLUB){
             clubName = Client.getClientClubName();
        }
        else{
             clubName = selectedClubForMaxAge.getValue();
        }
        List<Player> result = Client.playerDatabase.searchMaxAgedPlayers(clubName);
        handleResult(result, playerDisplayPController);

    }

    public void maxHeightSearch(ActionEvent actionEvent) {
       String clubName;
       if(Client.getUserMode()==UserMode.CLUB){
              clubName = Client.getClientClubName();
         }
         else{
              clubName = selectedClubForMaxHeight.getValue();
       }
        List<Player> result = Client.playerDatabase.searchMaxHeightPlayers(clubName);
        handleResult(result, playerDisplayPController);
    }
    public void maxSalarySearch(ActionEvent actionEvent) {
        String clubName;
        if(Client.getUserMode()==UserMode.CLUB){
            clubName = Client.getClientClubName();
        }
        else{
            clubName = SelectedClubForMaxSalaray.getValue();
        }
        List<Player> result = Client.playerDatabase.searchMaxSalaryPlayers(clubName);
        handleResult(result, playerDisplayPController);
    }

    private static void handleResult(List<Player> result, PlayerDisplayController playerDisplayPController) {
        if(result.isEmpty()){
            //show an alert that no player found
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No player found");
            alert.setHeaderText(null);
            alert.setContentText("No player found with the given criteria");
            alert.showAndWait();
        }
        else{
            //get the name of the players
            List<String> pnames = new ArrayList<>();
            for(Player p: result){
                pnames.add(p.getName());
            }
            playerDisplayPController.playerListView.setItems(FXCollections.observableArrayList(pnames));
        }
    }

}
