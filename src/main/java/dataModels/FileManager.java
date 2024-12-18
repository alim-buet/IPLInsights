package dataModels;

import java.io.*;
import java.util.List;
import java.util.ArrayList;


public class FileManager {
    String filename;
    public FileManager(String filename) {
        this.filename = filename;
    }

    public List<Player> loadPlayers(String fileName) {
        List<Player> players = new ArrayList<Player>();
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(fileName));

        }
        catch (FileNotFoundException e) {
            System.out.println("Could not read Player data from the file: " + fileName);
            e.printStackTrace();
        }
        String line = "";
        try {
            while((line = br.readLine()) != null){
                if(line.equals("")){
                    continue;
                }
                String[] playerData = line.split(",");
                String name = playerData[0];
                String country = playerData[1];
                int age = Integer.parseInt(playerData[2]);
                double height = Double.parseDouble(playerData[3]);
                String club = playerData[4];
                String position = playerData[5];
                int number = playerData[6].equals("") ? 0 : Integer.parseInt(playerData[6]);
                double weeklySalary = Double.parseDouble(playerData[7]);
                Player player = new Player(name, country, age, height, club, position, number, weeklySalary);
//                System.out.println("player contructed: " + player.getName());
                players.add(player);

            }
        } catch (IOException e) {
            System.out.println("Could not read Player data from the file: " + fileName);
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            System.out.println("Could not close the file: " + fileName);
            e.printStackTrace();
        }

        return players;

    }
    public void saveAuctionList(List<Player> auctionedList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/data/auction-list.txt"))) {
            for (Player player : auctionedList) {
                bw.write(player.getName() + "," + player.getPrice() + "\n");
            }
            System.out.println("Auction list saved successfully");
        } catch (IOException e) {
            System.out.println("Could not write Auctioned Player data to the file: src/main/resources/data/auction-list.txt");
            e.printStackTrace();
        }
    }

    //writing the player data to the file after completion of the program
    public void savePlayers(List<Player> players){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(this.filename));
            for(Player player: players){

                bw.write(player.getName() + "," + player.getCountry() + "," + player.getAge() + "," + player.getHeight() + "," + player.getClub() + "," + player.getPosition() + "," + (player.getNumber()==0 ? "":player.getNumber()) + "," + String.format("%.0f",player.getWeeklySalary()) + "\n");

            }

        } catch (IOException e) {

            System.out.println("Could not write Player data to the file: " + this.filename);
            e.printStackTrace();
        }
        finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close the file: " + this.filename);
            }
        }
    }
}
