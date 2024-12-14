package controllers;

import client.Client;
import dataModels.PlayerDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SalaryController implements Initializable {
    public Label title;
    public TableView<ClubData> salaryTable;
    public TableColumn<ClubData, ImageView> logoColumn;
    public TableColumn<ClubData, String> clubColumn;
    public TableColumn<ClubData, String> salaryColumn;
    public Button secondaryButton;

    private PlayerDatabase playerDatabase = Client.getPlayerDatabase();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up columns
        logoColumn.setCellValueFactory(new PropertyValueFactory<>("logo"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("annualSalary"));

        salaryTable.setItems(getClubData());
    }

    private ObservableList<ClubData> getClubData() {
        ObservableList<ClubData> clubDataList = FXCollections.observableArrayList();
        List<String> clubNames = playerDatabase.getClubNames();
        for (String clubName : clubNames) {
            String logoUrl = "@../resources/clubLogos/" + clubName + ".png";

            double annualSalary = playerDatabase.totalSalary(clubName);

            ImageView logoImageView = new ImageView(new Image(logoUrl));
            logoImageView.setFitHeight(40);
            logoImageView.setFitWidth(40);

            clubDataList.add(new ClubData(logoImageView, clubName, String.format("$%.2f", annualSalary)));
        }
        return clubDataList;
    }

    public void switchToPrimary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("@../../resources/views/guest.fxml"));
            Client.getMainStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("salary view theke back korar shomoy problem");
        }
    }


    public static class ClubData {
        private ImageView logo;
        private String clubName;
        private String annualSalary;

        public ClubData(ImageView logo, String clubName, String annualSalary) {
            this.logo = logo;
            this.clubName = clubName;
            this.annualSalary = annualSalary;
        }

        public ImageView getLogo() {
            return logo;
        }

        public void setLogo(ImageView logo) {
            this.logo = logo;
        }

        public String getClubName() {
            return clubName;
        }

        public void setClubName(String clubName) {
            this.clubName = clubName;
        }

        public String getAnnualSalary() {
            return annualSalary;
        }

        public void setAnnualSalary(String annualSalary) {
            this.annualSalary = annualSalary;
        }
    }
}
