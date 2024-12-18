package controllers;

import client.Client;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import dto.LogoutRequest;
import dto.PlayerAddRequest;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    @FXML public TextField salaryRangeFromText;
   @FXML public TextField salaryRangeToText;
    @FXML public TextField addAge;
   @FXML public TextField addHeight;
   @FXML public TextField addSalary;
   @FXML public TextField addNumber;
    @FXML
    private ImageView clubLogoDisplay, showSettings;
    @FXML
    private Label clubNameDisplay, clubAnnualSalaryLabel;
    @FXML
    private TabPane tabPane1;
    @FXML
    private Button nameSearchButton, nameSearchButton1, nameSearchButton2, nameSearchButton11, nameSearchButton131, nameSearchButton1311, nameSearchButton1312, nameSearchButton13111, addPfp1;
    @FXML
    private ComboBox<String> clubSearch, countrySearch, positionSearch, selectedClubForMaxAge, selectedClubForMaxHeight, SelectedClubForMaxSalary;




    @FXML
    private HBox playerDisplayP, playerDisplayC;
    @FXML
    private TextField addName, addCountry, searchNameTextField, nameSearchTextbox;
    @FXML
    private ChoiceBox<String> addPosition;
    @FXML private PlayerDisplayController playerDisplayPController, playerDisplayCController;





    List<Player> players = Client.getPlayers();
    PlayerDatabase pdb = Client.getPlayerDatabase();

    public MainController() throws IOException {
    }

    @FXML
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
                Client.getSockt().write(new LogoutRequest(Client.getClientClubName()));

                Client.getSockt().closeConnection();
                //notun socket open kora lagbe client er jonno
                Client.setSockt(new NetworkUtil("127.0.0.1",12345));
            } catch (Exception e) {
                System.out.println("Logout er shomoy network close korte jhamela");
            }

            //login page load kora lagbe
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login.fxml"));
            try {
                Client.getMainStage().getScene().setRoot(loader.load());
            } catch (Exception e) {
                System.out.println("logout kore login view Load korte jhamela");
            }
        }


    }
    @FXML
    public void refreshList() {
        try {
            if(playerDisplayPController!=null){
                playerDisplayPController.loadPlayers();
            }
            if(playerDisplayCController!=null){
                playerDisplayCController.loadPlayers();
            }
        } catch (Exception e) {
            System.out.println("Refreshlist function e controller diye load player function call e problem");
            e.printStackTrace();
        }
        try {
            if (playerDisplayPController != null) {
                playerDisplayPController.resetPlayerInfo();
            }
            if(playerDisplayCController!=null){
                playerDisplayCController.resetPlayerInfo();
            }
        } catch (Exception e) {
            System.out.println("Refreshlist function e controller diye reset player info function call e problem");
            e.printStackTrace();


        }

    }
    //made some changes here

    @FXML
    public void searchByName(ActionEvent actionEvent) {
        System.out.println("Search by name button clicked");
        Player p = Client.playerDatabase.searchByName(searchNameTextField.getText());
        System.out.println(p);
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
            playerDisplayPController.playerListView.getSelectionModel().select(p.getName());
            playerDisplayPController.playerListView.scrollTo(p.getName());
            playerDisplayPController.showPlayerInfo(p);
        }
    }

    @FXML public void searchByCountryAndClub(ActionEvent actionEvent) {
        String club = clubSearch.getValue();
        String country = countrySearch.getValue();
        List<Player> result = Client.playerDatabase.searchByClubAndCountry(club, country);
        handleResult(result,0); //0-playercontroller 1-clubcontroller

    }

    @FXML public void searchByPosition(ActionEvent actionEvent) {
        String position = positionSearch.getValue();
        List<Player> result = Client.playerDatabase.searchByPosition(position);
        handleResult(result,0);
    }

    @FXML  public void searchBySalary(ActionEvent actionEvent) {
        List<Player> result = Client.playerDatabase.searchByWeeklySalary(Double.parseDouble(salaryRangeFromText.getText()), Double.parseDouble(salaryRangeToText.getText()));
        handleResult(result, 0);
    }

    @FXML  public void showDemographics(Event event) {
        //user clicks on the countrywise player analysis tab. demographics view load kore client er scene oita set kora lagbe
        //load the demographics view fxml file and set it as the scene of the client
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/demographics.fxml"));
        try {
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Demographics view Load korte jhamela");
        }
    }

    @FXML   public void showAnnualSalary() {
        if(Client.getUserMode()==UserMode.CLUB){
            //show the annual salary of the club in a formatted way to show the full text using %.2f
            clubAnnualSalaryLabel.setText("Total Annual Salary: "+String.format("%.0f", Client.playerDatabase.totalSalary(Client.getClientClubName())));
        }
        else if(Client.getUserMode()==UserMode.GUEST){
            //view ache. so oita load korbo
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/salary.fxml"));
            try {
                Client.getMainStage().getScene().setRoot(loader.load());
            } catch (Exception e) {
                System.out.println("annual salary button e click korar por Salary view Load korte jhamela");
            }
        }
    }

    @FXML  public void showAuction() {
        //user clicks on the auction tab. auction view load kore client er scene oita set kora lagbe
        //load the auction view fxml file and set it as the scene of the client
        System.out.println("Auction button clicked");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/auction.fxml"));
        try {
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Auction view Load korte jhamela");
            e.printStackTrace();
        }
    }

    @FXML  public void submitPlayer() {
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
        else if (addAge.getText().isBlank()) {
            alert.setContentText("Please enter an age for the player.");
            alert.showAndWait();
        }
        else if (addHeight.getText().isBlank()) {
            alert.setContentText("Please enter a height for the player.");
            alert.showAndWait();
        }
        else if (addPosition.getValue().isBlank()) {
            alert.setContentText("Please enter a position for the player.");
            alert.showAndWait();
        }
        else if (addNumber.getText().isBlank()) {
            alert.setContentText("Please enter a number for the player.");
            alert.showAndWait();
        }
        else if (addSalary.getText().isBlank()) {
            alert.setContentText("Please enter a salary for the player.");
            alert.showAndWait();
        }
        else {
            //everything is aight

            Player p = new Player(addName.getText(), addCountry.getText(), Integer.parseInt(addAge.getText()), Double.parseDouble(addHeight.getText()), Client.getClientClubName(), addPosition.getValue(), Integer.parseInt(addNumber.getText()), Double.parseDouble(addSalary.getText()));
            //send the guy to the server
            try {
                Client.getSockt().write(new PlayerAddRequest(p,"Sending"));
                //clear the textfields
                addName.clear();
                addCountry.clear();
                addAge.clear();
                addHeight.clear();
                addNumber.clear();
                addSalary.clear();
                addPosition.getSelectionModel().select(0);

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
            //show what kind of error it is
            e.printStackTrace();
        }
        tabPane1.getSelectionModel().select(0);
//        System.out.println("First tab selected");
        //meaning first tab is selected initially
        List<String> clubNames = pdb.getClubNames();
        List<String> countryNames = pdb.getCountryNames();



        if(Client.getUserMode()== UserMode.GUEST){
            //remove the auction box from the player display
            playerDisplayPController.playerData.getChildren().remove(playerDisplayPController.auctionBox);
            playerDisplayCController.playerData.getChildren().remove(playerDisplayCController.auctionBox);
            //in the fxml view there is a selection box for the club, we need to load all the club names in that box
            //there is a function in the database that returns the list of all available club names

            selectedClubForMaxAge.setItems(FXCollections.observableArrayList(clubNames));
            selectedClubForMaxHeight.setItems(FXCollections.observableArrayList(clubNames));
            SelectedClubForMaxSalary.setItems(FXCollections.observableArrayList(clubNames));
            selectedClubForMaxHeight.getSelectionModel().select(0);
            selectedClubForMaxAge.getSelectionModel().select(0);
            SelectedClubForMaxSalary.getSelectionModel().select(0);





        }
        else if(Client.getUserMode()==UserMode.CLUB){
            //initialize the clubname
            clubNameDisplay.setText(Client.getClientClubName());

            Image clubLogo = null;
            try {
                clubLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/clubLogos/" + Client.getClientClubName() + ".png")));
                clubLogoDisplay.setImage(clubLogo);

            } catch (Exception e) {
                System.out.println("Club logo load korte problem");
            }
            addPosition.getItems().addAll("Batsman","Bowler","Wicketkeeper","Allrounder");


        }
        clubSearch.setItems(FXCollections.observableArrayList(clubNames));
        countrySearch.setItems(FXCollections.observableArrayList(countryNames));
        clubSearch.getItems().addFirst("ANY");
        positionSearch.setItems(FXCollections.observableArrayList("Batsman","Bowler","Wicketkeeper","Allrounder"));
        //we dont want a club client to access the search filtering by club name
        if(Client.getUserMode()==UserMode.CLUB){
            clubSearch.setDisable(true);
            clubSearch.getSelectionModel().select(Client.getClientClubName());
        }

        clubSearch.getSelectionModel().select(0);
        countrySearch.getSelectionModel().select(0);


    }

    @FXML  public void maxAgeSearch() {
        System.out.println("Max age search button clicked");
        String clubName;
        if(Client.getUserMode()==UserMode.CLUB){
             clubName = Client.getClientClubName();
        }
        else{
             clubName = selectedClubForMaxAge.getValue();
        }
        List<Player> result = Client.playerDatabase.searchMaxAgedPlayers(clubName);
        System.out.println("Result size: "+result.size());
        handleResult(result, 1);

    }

    @FXML   public void maxHeightSearch(ActionEvent actionEvent) {
       String clubName;
       if(Client.getUserMode()==UserMode.CLUB){
              clubName = Client.getClientClubName();
         }
         else{
              clubName = selectedClubForMaxHeight.getValue();
       }
        List<Player> result = Client.playerDatabase.searchMaxHeightPlayers(clubName);
        handleResult(result, 1);
    }
    @FXML  public void maxSalarySearch(ActionEvent actionEvent) {
        String clubName;
        if(Client.getUserMode()==UserMode.CLUB){
            clubName = Client.getClientClubName();
        }
        else{
            clubName = SelectedClubForMaxSalary.getValue();
        }
        List<Player> result = Client.playerDatabase.searchMaxSalaryPlayers(clubName);
        handleResult(result, 1);
    }


    @FXML public static void handleResult(List<Player> result, int id)
    { {
        //id - 0= playercontroller, 1=clubcontroller
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
            if(id==0){
                //playercontroller
                Client.getPlayerDisplayP().playerListView.setItems(FXCollections.observableArrayList(pnames));
                Client.getPlayerDisplayP().resetPlayerInfo();
                Client.getPlayerDisplayP().showPlayerInfo(result.getFirst());
            }
            else if(id==1){
                //clubcontroller
                Client.getPlayerDisplayC().playerListView.setItems(FXCollections.observableArrayList(pnames));
                Client.getPlayerDisplayC().resetPlayerInfo();
                Client.getPlayerDisplayC().showPlayerInfo(result.getFirst());
            }
        }

    }

}}
