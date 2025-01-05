package client;
import controllers.PlayerDisplayController;
import dataModels.Player;
import dataModels.PlayerDatabase;
import dataModels.UserMode;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import network.ReadThreadClient;
import server.NetworkUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

//app is basically a client that will have a player list, a network util, read, write thread and a stage
public class Client extends Application {

    private static Stage mainStage;
    private static Scene scene;
    public static List<Player> players; //every client will have a list of players. if the client is organizer the server will provide him the whole list. and filtered list otherwise
    //to see the real tiem change in the list of players, we'll use observable list
    public static ObservableList<Player> observablePlayerList = FXCollections.observableArrayList();
    public static String clientClubName;




    private static NetworkUtil sockt = new NetworkUtil();
    //the app we will run is a client which will read data from server
    private static ReadThreadClient rtc;

    private static UserMode userMode;
    //0- none, so we'll land on login page, 1-club 2-organizer
    private static PlayerDisplayController playerDisplayC,playerDisplayP; //playerDisplayC shows the info when searched by club, playerDisplayP shows the info when searched by player
    private static Label balanceLabel;
    private static String serverAddress = "127.0.0.1";
    private static final int PORT = 12345;
    public static PlayerDatabase playerDatabase;
    public static List<Player> auctionedPlayerList;
    //initialize the observable list with auctioned players
    public static ObservableList<Player> observableAuctionedPlayerList = FXCollections.observableArrayList();


    public static void setPlayerDatabase(PlayerDatabase playerDatabase) {
        Client.playerDatabase = playerDatabase;
    }

    public static List<Player> getAuctionedPlayerList() {
        return auctionedPlayerList;
    }

    public static void setAuctionedPlayerList(List<Player> auctionedPlayerList) {
        Client.auctionedPlayerList = auctionedPlayerList;
    }

    public static ObservableList<Player> getObservableAuctionedPlayerList() {
        return observableAuctionedPlayerList;
    }

    public static void setObservableAuctionedPlayerList(ObservableList<Player> observableAuctionedPlayerList) {
        Client.observableAuctionedPlayerList = observableAuctionedPlayerList;
    }
    public static ObservableList<Player> getObservablePlayerList() {
        return observablePlayerList;
    }

    public static void setObservablePlayerList(ObservableList<Player> observablePlayerList) {
        Client.observablePlayerList = observablePlayerList;
    }

    public static UserMode getUserMode() {
        return userMode;
    }
    public static String getClientClubName() {
        return clientClubName;
    }
    public static void setClientClubName(String clientClubName) {
        Client.clientClubName = clientClubName;
    }

    public static void setUserMode(UserMode userMode) {
        Client.userMode = userMode;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage mainStage) {
        Client.mainStage = mainStage;
    }

    public static Scene getScene() {
        return scene;
    }

    public static PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }



    public static void setScene(Scene scene) {
        Client.scene = scene;
    }

    public static NetworkUtil getSockt() {
        return sockt;
    }
    public static List<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(List<Player> players) {
        playerDatabase = new PlayerDatabase(players);
        Client.players = players;
    }

    public static void setSockt(NetworkUtil sockt) {
        Client.sockt = sockt;
    }

    public static ReadThreadClient getRtc() {
        return rtc;
    }

    public static void setRtc(ReadThreadClient rtc) {
        Client.rtc = rtc;
    }

    public static PlayerDisplayController getPlayerDisplayC() {
        return playerDisplayC;
    }

    public static void setPlayerDisplayC(PlayerDisplayController playerDisplayC) {
        Client.playerDisplayC = playerDisplayC;
    }

    public static PlayerDisplayController getPlayerDisplayP() {
        return playerDisplayP;
    }

    public static void setPlayerDisplayP(PlayerDisplayController playerDisplayP) {
        Client.playerDisplayP = playerDisplayP;
    }

    public static Label getBalanceLabel() {
        return balanceLabel;
    }

    public static void setBalanceLabel(Label balanceLabel) {
        Client.balanceLabel = balanceLabel;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        Client.serverAddress = serverAddress;
    }

    public int getPORT() {
        return PORT;
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("IPL Insights");
        stage.setResizable(false);
        //loading the login fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));

        scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/guest.css")).toExternalForm());
        stage.setScene(scene);
        mainStage = stage;
        stage.show();
    }
    public static void main(String[] args)   {
        //we'll create a network socket for that client
        try {
            sockt = new NetworkUtil(Client.serverAddress, Client.PORT);
            launch(args);
        } catch (Exception e) {
            System.out.println("Cannot connect to the server");
        }

        //no thread for reading and writing is created yet


    }
}
