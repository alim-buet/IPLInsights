package controllers;

import client.Client;
import dataModels.UserMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import network.ReadThreadClient;
import server.NetworkUtil;
import dataModels.Player;
import dto.LoginCredential;

import java.io.IOException;
import java.util.List;


public class LoginController {
        //as this guy is running in the backend of the client we should have the clients network util and list of players here
        NetworkUtil networkUtil = Client.getSockt();
        List<Player> players;
         public Button guest_login_button;
        public Button login_button;


        public PasswordField password_field;
        public TextField clubname_field;
        @FXML
        public void initialize() throws Exception{
            //unknown client mdoe
            Client.setUserMode(UserMode.NONE);
            //might add an animated logo here
        }


        public void login_button_clicked(ActionEvent actionEvent) {
            //extract the club name and password, make a dto-> send it to the server and receive its reply and then decide to show a wrong password alert or move to the next page
            //if the password is correct, then the server will send the list of players to the client
            String clubname = clubname_field.getText();
            String password = password_field.getText();
            //send the dto to the server
            LoginCredential loginCredential = new LoginCredential(clubname, password);
            try {
                networkUtil.write(loginCredential);
            } catch (IOException e) {
                System.out.println("login dto write e jhamela");
            }
            //based on the credential the server will give me a reply
            Object o = null;
            try {
                 o = networkUtil.read();
            }catch (Exception e){
                System.out.println("Server theke login page e response read e jhamela");
            }
            //there can be three possible repsones : 1) ALREADY_CONNECTED 2) WRONG_CREDENTIAL 3) a list of players meaning successful login
            //if the response is a list of players, then we will move to the next page,for other cases show suitable alert
            if(o instanceof String){
                if(((String) o).equalsIgnoreCase("ALREADY_CONNECTED")){
                    System.out.println("ALREADY_CONNECTED club trynna connect again");
                    //show an alert
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Club already signed in");
                    alert.setHeaderText("You are already signed in");
                    alert.setContentText("You are already signed in. Please sign out first to sign in again");
                    alert.showAndWait();

                }
                else if(((String) o).equalsIgnoreCase("WRONG_CREDENTIAL")){
                    //show alert
                    System.out.println("WRONG_CREDENTIAL");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Wrong Credential");
                    alert.setHeaderText("Wrong Credential");
                    alert.setContentText("The club name or password is incorrect. Please try again");
                    alert.showAndWait();
                }
            }
            else if (o instanceof List) {
                // Move to the next page
                players = (List<Player>) o;
                Client.setPlayers(players);
                Client.observablePlayerList.setAll(players);
                Client.setUserMode(UserMode.CLUB);
//                System.out.println("Size of the player list send by the server is "+players.size());
                System.out.println("Everything is set in the login controller for a new club login");
                Client.setClientClubName(clubname);
                // Change the scene to signed-in.fxml view
                try {
                    FXMLLoader loader = new FXMLLoader();

                    loader.setLocation(getClass().getResource("/views/signed-in.fxml"));

                    Scene newScene = new Scene(loader.load());
                    System.out.println("ei line execute");
                    Client.getMainStage().setScene(newScene);
                    Client.getMainStage().show();
                    System.out.println("ei line execute hoise");

                } catch (IOException e) {

                    System.out.println("Club login er jonno fxml loading failed");
                }
                //now that the clien is a club we will create a read thread for the client
                Client.setRtc(new ReadThreadClient(networkUtil));

            }


        }


    public void guest_button_clicked(ActionEvent actionEvent) {
            //server ke inform kore or send kora player list accept korbo
        try {
            networkUtil.write("Guest_login");
        } catch (Exception e) {
            System.out.println("Guest login dto write e problem");
        }
        Object o = null;
        try {
            o = networkUtil.read();
        } catch (Exception e) {
            System.out.println("Guest login e response read e jhamela");
        }
        if(o instanceof List){
            // Move to the next page
            players = (List<Player>) o;
            Client.setPlayers(players);
            Client.observablePlayerList.setAll(players);
            Client.setUserMode(UserMode.GUEST);
            System.out.println("Everything is set in the login controller for a new guest login");

            try {

                FXMLLoader loader = new FXMLLoader();

                loader.setLocation(getClass().getResource("/views/guest.fxml"));


                Scene newScene = new Scene(loader.load());
                System.out.println("guest login fxml loading ... done");
//                System.out.println("ei line execute");
                Client.getMainStage().setScene(newScene);
                Client.getMainStage().show();
//                System.out.println("ei line execute hoise");

            } catch (Exception e) {

                System.out.println("Guest er jonno fxml loading failed");
                //show the exception
                e.printStackTrace();

            }

            //now that the client is a club we will create a read thread for the client
            Client.setRtc(new ReadThreadClient(networkUtil));
        }

    }
}


