
package dataModels;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Player implements Serializable {
    private String name;
    private String country;
    private int age;
    private double height;
    private String club;
    private String position;
    private int number;
    private double weeklySalary;
    private byte[] pfpBytes;
    private boolean auctioned;
    private double price;
    public Player(String name, String country, int age, double height, String club, String position, int number, double weeklySalary) {
        this.name = name;
        this.country = country;
        this.age = age;
        this.height = height;
        this.club = club;
        this.position = position;
        this.number = number;
        this.weeklySalary = weeklySalary;
        this.auctioned = false;
        this.setpfp();

    }
    public void setpfp()  {
        try {
            pfpBytes = Files.readAllBytes(Paths.get("src/main/resources/playerImages/"+name+".png"));
        } catch (IOException e) {
            System.out.println("Error in loading player image");
        }
    }
    public Image getPfp() {
        InputStream is = new ByteArrayInputStream(pfpBytes);
        return new Image(is);
    }
    public byte[] getPfpBytes() {
        return pfpBytes;
    }

    public boolean isAuctioned() {
        return this.auctioned;
    }

    public void setAuctionState (boolean state) {
        this.auctioned = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getWeeklySalary() {
        return weeklySalary;
    }

    public void setWeeklySalary(double weeklySalary) {
        this.weeklySalary = weeklySalary;
    }


    @Override
    public String toString() {
        return "Player name: "+name+"\n"+
                "Country: "+country+"\n"+
                "Age: "+age+"\n"+
                "Height: "+height+" m\n"+
                "Club: "+club+"\n"+
                "Position: "+position+"\n"+
                "Jersey Number: "+(number==0 ? "N/A":number)+"\n"+
                "Weekly Salary: "+String.format("%.0f", weeklySalary)+" INR\n";
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
