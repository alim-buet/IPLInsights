package server;

import dataModels.Player;
import dataModels.PlayerDatabase;
import dto.AuctionRequest;
import dto.AuctionUpdate;
import dto.BuyRequest;
import dto.LoginCredential;

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
                        }
                        else{
                            System.out.println("The credential does not matches with the database the server has");

                            networkUtil.write("WRONG_CREDENTIAL");
                        }
                    }

                }
                else if(o instanceof BuyRequest){
                    //a club wants to buy a player. we will get the player name and the club name and the price. we will search the player in the database
                    //and if the player is found, we will update the player's club name and price and send a message to the client that the player is bought

                    BuyRequest buyRequest = (BuyRequest) o;
                    Player player = playerDatabase.searchByName(buyRequest.getPlayer().getName());
                    if(player != null){
                        player.setClub(buyRequest.getDestinationClub());
                        player.setAuctionState(false);
                        networkUtil.write("BOUGHT");
                    }
                    else{
                        networkUtil.write("NOT_FOUND");
                    }

                }
                else if(o instanceof AuctionRequest auctionRequest){
                    //a club wants to sell one of its player-> get the dto-> extract the player-> set the auction state and price -> and send the player info as auction message to all the clubs other than the seller club, they will update their ends ui
                    Player player = playerDatabase.searchByName(auctionRequest.getPlayerName());
                    if(player != null){
                        player.setAuctionState(true);
                        player.setPrice(auctionRequest.getPrice());
                        networkUtil.write(player);
                        for(String clubName: clientMap.keySet()){
                            if(!clubName.equals(auctionRequest.getClubName())){
                                clientMap.get(clubName).write(new AuctionUpdate(player));
                            }
                        }
                    }

                }
                else if(o instanceof Player){
                    //a club has created a new player and want to update its info everywhere.
                    Player player = (Player) o;
                    playerDatabase.addPlayer(player);


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
