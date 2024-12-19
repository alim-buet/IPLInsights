package server;
//this class works as socket wrapper. two kinds of socket:

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkUtil {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetworkUtil() {

    }
    

    public NetworkUtil(String s, int port) throws IOException {
        //for client side socket
        this.socket = new Socket(s, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public NetworkUtil(Socket s) throws IOException {
        //for server side socket
        this.socket = s;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public Object read() throws IOException, ClassNotFoundException {
        return ois.readUnshared();
    }

    public void write(Object o) throws IOException {
        oos.writeUnshared(o);
    }

    public void closeConnection() throws IOException {
        ois.close();
        oos.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
