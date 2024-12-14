package network;

import client.Client;
import dataModels.Player;
import server.NetworkUtil;

import java.io.IOException;
import java.util.List;

public class ReadThreadClient implements Runnable {
    NetworkUtil networkUtil;
    Thread thread;
    List<Player> players;
    public ReadThreadClient(NetworkUtil networkUtil){
        this.networkUtil = networkUtil;
        this.thread = new Thread(this);
        this.players = Client.getPlayers();
        thread.start();
    }

    @Override
    public void run() {
        //this thread will be running in the client side and read the dtos coming from the server

        try {
            while(true){
                Object o = networkUtil.read();
                //the client club can get auction update , buy request etc from the server, we'll handle them later


            }
        } catch (Exception e) {
            System.out.println("ReadThreadClient e jhamela");
        }
    }
}
