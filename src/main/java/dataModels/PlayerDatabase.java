package dataModels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//player database contain the list of players,and functions that returns player lists based on search criteria
public class PlayerDatabase
{
    private List<Player> players;
    private List<Player> auctionList;
    private String filename;
    FileManager fileManager;
    public void loadAuctionList() {
        //there is a text file in the resource folder that contains playername,price data of auctioned players, read the names then search for the player with name, and add the player in the aucitoned players list one by one
        //the file is in the format of playername,price
        //read the file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("@../../resources/data/auctionedPlayers.txt"));
        } catch (Exception e) {
            System.out.println("aucitoned players list file read e jhamela:");
            e.printStackTrace();
        }
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                String[] playerData = line.split(",");
                String name = playerData[0];
                double price = Double.parseDouble(playerData[1]);
                Player player = searchByName(name);
                if (player != null) {
                    player.setPrice(price);
                    auctionList.add(player);
                }
            }
        } catch (Exception e) {
            System.out.println("aucitoned players list file read e jhamela:");
            e.printStackTrace();
        }

    }
    public PlayerDatabase(String filename)
    {
        this.filename = filename;

        this.fileManager = new FileManager(filename);
        this.players = fileManager.loadPlayers(filename);
    }
    public PlayerDatabase(List<Player> players)
    {
        this.players = players;
    }

    public List<Player> getAuctionList() {

        return auctionList;
    }

    public void setAuctionList(List<Player> auctionList) {
        this.auctionList = auctionList;
    }

    //newly added to return all the players
    public List<Player> getAllPlayers(){
        return players;
    }
    public void savePlayers()
    {
        fileManager.savePlayers(players);
    }
    public Player searchByName(String name)
    {
        for(Player player: players)
        {
            if(player.getName().equalsIgnoreCase(name))
            {
                return player;
            }
        }
        return null;
    }
    //print the first player of the playerlist  to test if database is loaded
    public void printFirstPlayer()
    {
        System.out.println(players.get(0));
    }

    public List<Player> searchByClubAndCountry(String club, String country)
    {
        List<Player> playersByClubAndCountry = new ArrayList<Player>();
        for(Player player: players)
        {
            if(((player.getClub().equalsIgnoreCase(club)||club.equalsIgnoreCase("ANY"))) && player.getCountry().equalsIgnoreCase(country))
            {
                playersByClubAndCountry.add(player);
            }
        }
        return playersByClubAndCountry;
    }
    public List<Player> searchByClub(String club)
    {
        List<Player> playersByClub = new ArrayList<Player>();
        for(Player player: players)
        {
            if(player.getClub().equalsIgnoreCase(club))
            {
                playersByClub.add(player);
            }
        }
        return playersByClub;
    }
    public List<Player> searchByPosition(String position)
    {
        List<Player> playersByPosition = new ArrayList<Player>();
        for(Player player: players)
        {
            if(player.getPosition().equalsIgnoreCase(position))
            {
                playersByPosition.add(player);
            }
        }
        return playersByPosition;
    }
    public List<Player> searchByWeeklySalary(double minSalary, double maxSalary)
    {
        List<Player> playersByWeeklySalary = new ArrayList<Player>();
        for(Player player: players)
        {
            if(player.getWeeklySalary() >= minSalary && player.getWeeklySalary() <= maxSalary)
            {
                playersByWeeklySalary.add(player);
            }
        }
        return playersByWeeklySalary;
    }
    public HashMap<String, Integer> countryWiseCOunt(){
        HashMap<String, Integer> countryWisePlayers = new HashMap<String, Integer>(); //gotta use the wrapper class
        for(Player player:players){
            if(countryWisePlayers.containsKey(player.getCountry())){
                countryWisePlayers.put(player.getCountry(),countryWisePlayers.get(player.getCountry())+1);
                //if the country is already present in the hashmap, then we increment the count by 1.we gotta check the condition
                //cause hash map is initializdd with null. so get() will result null pointer exception

            }
            else{
                //so it is a new country
                countryWisePlayers.put(player.getCountry(),1);

            }
        }
        return countryWisePlayers;

    }

    //searching by club name
    public List<Player> searchMaxSalaryPlayers(String club){
        List<Player> maxSalaryPlayers = new ArrayList<Player>();
        double maxSalary = 0;
        for(Player player:players){
            if(player.getClub().equalsIgnoreCase(club)){
                if(player.getWeeklySalary()>maxSalary){
                    maxSalary = player.getWeeklySalary();
                    maxSalaryPlayers.clear();
                    maxSalaryPlayers.add(player);

                }
                else if(player.getWeeklySalary()==maxSalary){
                    maxSalaryPlayers.add(player);
                }
            }

        }
        return maxSalaryPlayers;
    }
    public List<Player> searchMaxAgedPlayers(String club){
        List<Player> maxAgedPlayers = new ArrayList<Player>();
        int maxAge = 0;
        for(Player player:players){
            if(player.getClub().equalsIgnoreCase(club)){
                if(player.getAge()>maxAge){
                    maxAge = player.getAge();
                    maxAgedPlayers.clear();
                    maxAgedPlayers.add(player);

                }
                else if(player.getAge()==maxAge){
                    maxAgedPlayers.add(player);
                }
            }

        }
        return maxAgedPlayers;
    }
    public List<Player> searchMaxHeightPlayers(String club){
        List<Player> maxHeightPlayers = new ArrayList<Player>();
        double maxHeight = 0;
        for(Player player:players){
            if(player.getClub().equalsIgnoreCase(club)){
                if(player.getHeight()>maxHeight){
                    maxHeight = player.getHeight();
                    maxHeightPlayers.clear();
                    maxHeightPlayers.add(player);

                }
                else if(player.getHeight()==maxHeight){
                    maxHeightPlayers.add(player);
                }
            }

        }
        return maxHeightPlayers;
    }
    public double totalSalary(String club){
        //we have to find yaerly salary
        double totalSalary = 0;
        for(Player player:players){
            if(player.getClub().equalsIgnoreCase(club)){
                totalSalary+=player.getWeeklySalary()*52.00;
            }
        }
        return totalSalary;
    }
    public boolean addPlayer(Player newPlayer){
        //name should be uniqye
        boolean isPresent = false;
        for(Player player:players){
            if(player.getName().equalsIgnoreCase(newPlayer.getName())){
                isPresent = true;
                break;
            }
        }
        if(!isPresent){
            players.add(newPlayer);
            return true;
        }
        else{
            return false;
        }
    }
    //new addition. a funciton that will return the name of all the club names
    public List<String> getClubNames(){
        List<String> clubNames = new ArrayList<String>();
        for(Player player:players){
            if(!clubNames.contains(player.getClub())){
                clubNames.add(player.getClub());
            }
        }
        return clubNames;
    }
    //return the list of all countries
    public List<String> getCountryNames(){
        List<String> countryNames = new ArrayList<String>();
        for(Player player:players){
            if(!countryNames.contains(player.getCountry())){
                countryNames.add(player.getCountry());
            }
        }
        return countryNames;
    }

    /////might delete later
//    public Player getFirstPlayer(){
//        if(!players.isEmpty()){
//            return players.getFirst();
//        }
//        else{
//            return null;
//        }
//
//    }

}
