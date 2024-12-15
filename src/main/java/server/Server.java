package server;
//import database class
import dataModels.PlayerDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private int port = 12345;
    private ServerSocket serverSocket;
    private static PlayerDatabase playerDatabase; //static so that all threads can access it without creating an instance
    public HashMap<String, NetworkUtil> clientMap;
    //key - club name and value- networkUtil

    public Server(){
        System.out.println("Server started successfully");
        clientMap = new HashMap<>();
        playerDatabase = new PlayerDatabase("src/main/resources/data/players.txt");
        System.out.println("Player loading successful");
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
