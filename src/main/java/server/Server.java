package server;
//import database class
import dataModels.Player;
import dataModels.PlayerDatabase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server {
    private int port = 12345;
    private ServerSocket serverSocket;
    private static PlayerDatabase playerDatabase; //static so that all threads can access it without creating an instance
    public HashMap<String, NetworkUtil> clientMap;
    //key - club name and value- networkUtil
    public static List<Player> auctionedPlayerList;


    public Server(){
        System.out.println("Server started successfully");
        clientMap = new HashMap<>();
        playerDatabase = new PlayerDatabase("src/main/resources/data/players.txt");
        playerDatabase.loadAuctionList();
        auctionedPlayerList = playerDatabase.getAuctionList();
        System.out.println("Number of auctioned players: " + auctionedPlayerList.size());
        System.out.println("Player loading successful");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutting down");
            playerDatabase.saveAuctionList(auctionedPlayerList);

            // Save the player list too
            playerDatabase.savePlayers();
            System.out.println("Player list saved successfully");
        }));
        //
        try {
            serverSocket = new ServerSocket(port);

            while(true){
                Socket clientSocket = serverSocket.accept();
                NetworkUtil nu = new NetworkUtil(clientSocket);
                //dont add already. wait for the loginDOT and if the login is successful, then add to the map
                //the server will create a read thread for that client and wait for any data. It will have the clientMap that will help to send data to other clients
                new ReadThreadServer(clientMap, nu);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static PlayerDatabase getPlayerDatabase(){
        return playerDatabase;
    }

    public static void main(String[] args) {
        new Server();

    }

}
