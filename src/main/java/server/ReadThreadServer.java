package server;

import dataModels.Player;
import dataModels.PlayerDatabase;
import dto.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ReadThreadServer implements Runnable {
    private Thread thread;
    private NetworkUtil networkUtil;
    public HashMap<String, NetworkUtil> clientMap;
    //key - club name and value- networkUtil
    //we will have another hashmap with club name and password that will be fetched from data/credentials.txt file
    public HashMap<String, String> clubCredentialMap;
    public PlayerDatabase playerDatabase;
    //server load the credentials from the file
    public void readCredentials() {
        clubCredentialMap = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("src/main/resources/data/credentials.txt"));
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] credential = line.split(",");
                clubCredentialMap.put(credential[0], credential[1]);
            }
            System.out.println("Credential loaded successfully");
            //test the first one
//            System.out.println(clubCredentialMap.get("Pubjab Kings"));
        } catch (IOException e) {
            System.out.println("credential file read e jhamela");
            e.printStackTrace();
        }
    }

    public ReadThreadServer(HashMap<String, NetworkUtil> clientMap, NetworkUtil networkUtil){
//        System.out.println("Read thread server er bhitor");
        readCredentials();
        playerDatabase = Server.getPlayerDatabase();
//        System.out.println("Testing if the read thread server got the player database");
//        playerDatabase.printFirstPlayer();
        this.clientMap = clientMap;
        this.networkUtil = networkUtil;
        this.thread = new Thread(this);
        thread.start();
    }
    public void run(){
        //server e asha info read korbe
        try {
            while(true){
                Object o = networkUtil.read();
                if(o instanceof String && ((String) o).equalsIgnoreCase("Guest_login")){
                    //organizer clicked to login, so the client will send a string message "Organizer_login" and the server will send
                    //the whole player list to the client.
                    System.out.println("Guest login request received by the server");
                    networkUtil.write(playerDatabase.getAllPlayers());
                    System.out.println("A player list with size "+playerDatabase.getAllPlayers().size()+" sent to the guest");
                }
                else if(o instanceof LoginCredential){
                    System.out.println("Login credential received by the server");
                    //some club clicked to login. we will fetch the dto->find the club name and see if the club is already connected with the server
                    // by checking in the clientMap. if it is already in the map, we will send a message to the client that the club is already connected
                    //if not, then we will need to check if the credential matches with the database. if it does, we will add the club to the clientMap
                    if(clientMap.containsKey(((LoginCredential) o).getClubName())){
                        System.out.println("Server checked the credential and it is already connected");
                        networkUtil.write("ALREADY_CONNECTED");

                    }
                    else{
                        if(clubCredentialMap.containsKey(((LoginCredential) o).getClubName()) && clubCredentialMap.get(((LoginCredential) o).getClubName()).equals(((LoginCredential) o).getPassword())){
                            clientMap.put(((LoginCredential) o).getClubName(), networkUtil);
                            System.out.println("The server received a valid credential and added the club to the clientMap");
                            System.out.println("Name of the club : "+((LoginCredential) o).getClubName()+" Password: "+((LoginCredential) o).getPassword());
                            //the club is in. now we need to send the player list according to the clubs need using the searching funcitnos in database
                            List<Player> clubPlayers = playerDatabase.searchByClub(((LoginCredential) o).getClubName());
                            System.out.println("Server sending playerlist with size "+clubPlayers.size());
                            networkUtil.write(clubPlayers);
                            //the club should also get the currently auctioned players list that the server contains.
                            networkUtil.write(new AuctionedPlayerList(Server.auctionedPlayerList));
                        }
                        else{
                            System.out.println("The credential does not matches with the database the server has");

                            networkUtil.write("WRONG_CREDENTIAL");
                        }
                    }

                }
                else if(o instanceof LogoutRequest logoutRequest){
                    //a club wants to logout. we will get the club name and remove the club from the clientMap

                    clientMap.remove(logoutRequest.getClubName());
                    System.out.println("Club "+logoutRequest.getClubName()+" logged out");

                }
                else if(o instanceof BuyRequest buyRequest){
                    //a club wants to buy a player. we will get the player name and the club name and the price. we will search the player in the database
                    //and if the player is found, we will update the player's club name and price and send a message to the client that the player is bought

                    Player player = playerDatabase.searchByName(buyRequest.getPlayer().getName());
                    System.out.println("Buy request of player "+buyRequest.getPlayer().getName()+" by club "+buyRequest.getDestinationClub()+" received by the server");
                    if(player != null){


                        Server.auctionedPlayerList.remove(player);
                        //we will send the news that the player is already bought, so the other client will update their ui and remove the player form the auction list and if the destination club is = to the client club name then the client will add the palyer to its list and update the ui
                        for(String clubName: clientMap.keySet()){
                            clientMap.get(clubName).write(new BuyConfirmation(player,buyRequest.getDestinationClub()));
                        }
                        player.setClub(buyRequest.getDestinationClub());
                        player.setAuctionState(false);
//                        System.out.println("after buying the player ");
//                        System.out.println(player);
                        //the player should be removed from the auction list
                        Server.auctionedPlayerList.remove(player);
                        System.out.println("number of player in the auction player list "+Server.auctionedPlayerList.size());
                    }

                }
                else if(o instanceof AuctionRequest auctionRequest){
                    //a club wants to sell one of its player-> get the dto-> extract the player-> set the auction state and price -> and send the player info as auction message to all the clubs other than the seller club, they will update their ends ui
                    Player player = playerDatabase.searchByName(auctionRequest.getPlayerName());
                    System.out.println("Auction request from club "+auctionRequest.getClubName()+" for player "+auctionRequest.getPlayerName()+" received by the server");

                    if(player != null){
                        player.setAuctionState(true);
                        player.setPrice(auctionRequest.getPrice());
                        Server.auctionedPlayerList.add(player);
                        for(String clubName: clientMap.keySet()){
                            if(!clubName.equals(auctionRequest.getClubName())){
                                clientMap.get(clubName).write(new AuctionUpdate(player));
                            }
                        }
                    }

                }
                else if(o instanceof PlayerAddRequest playerAddRequest){
                    //a club wants to add a player to the database. we will get the dto-> extract the player-> check if there is any player with the same name-> if the player is unique, then we will add the player to the main database and then send the dto back with the message being approved, and if there is any player already in the database with the same name, then we will send the dto back with the msg that rejected
                    Player player = playerAddRequest.getPlayer();
                    if(playerDatabase.searchByName(player.getName()) == null){
                        playerDatabase.addPlayer(player);
                        networkUtil.write(new PlayerAddRequest(player, "APPROVED"));
                        System.out.println("Player "+player.getName()+" added to the database");
                    }
                    else{
                        networkUtil.write(new PlayerAddRequest(player, "REJECTED"));
                        System.out.println("Player "+player.getName()+" already in the database");
                    }
                }



                else{
                    //if the object is not a string or a dto, then it is a problem
                    System.out.println("Read thread server e jhamela");
                }

            }
        } catch (Exception e) {
            System.out.println("Read thread server er run e jhamela");
        }
    }

    
}
