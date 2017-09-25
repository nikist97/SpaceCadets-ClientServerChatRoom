package serverSide;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * a Thread used to listed for incoming messages from a client
 */
public class ServerMessageListeningThread extends Thread{

    /**
     * the client socket object
     */
    private Socket client;

    /**
     * an array list of all client sockets
     */
    private ArrayList<Socket> clients;

    /**
     * a constructor for the server message listening thread
     * @param client the socket object of the client
     * @param clients the list of all client sockets
     */
    ServerMessageListeningThread(Socket client, ArrayList<Socket> clients){
        this.client = client;
        this.clients = clients;
    }

    /**
     * the run method of the thread, listens to client's messages and sends them to all other clients
     */
    public void run(){
        DataInputStream in;
        OutputStream outToClient;
        DataOutputStream out;
        while (true){
            try {
                in = new DataInputStream(client.getInputStream());
                String message = in.readUTF();
                System.out.println(message);
                for (Socket socket : this.clients){
                    try {
                        outToClient = socket.getOutputStream();
                        out = new DataOutputStream(outToClient);
                        out.writeUTF("Client: " + message);
                        out.flush();
                        outToClient.flush();
                    }
                    catch (SocketException se){
                        continue;
                    }
                }
            }
            catch (IOException ioe){
                ioe.printStackTrace();
                break;
            }
        }
    }

}
