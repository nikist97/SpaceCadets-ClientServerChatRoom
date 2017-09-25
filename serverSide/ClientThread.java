package serverSide;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * a Thread opened for each individual client socket connected to the server
 */
public class ClientThread extends Thread {

    /**
     * the socket object for the client
     */
    private Socket socket;

    /**
     * the array list holding all client sockets
     */
    private ArrayList<Socket> clients;

    /**
     * the name which represents the server in the messages
     */
    private String socketName;

    /**
     * a constructor for the ClientThread class
     * @param socket the client socket
     * @param clients the array list with all client sockets
     */
    ClientThread(Socket socket, ArrayList<Socket> clients, String socketName){
        this.socket = socket;
        this.clients = clients;
        this.socketName = socketName;
    }

    /**
     * the run method for the thread,
     * starts a ServerMessageListeningThread for the client so that the server listens to incoming messages,
     * waits for an input from server and then sends it to all clients
     */
    public void run(){

        Scanner scanner = new Scanner(System.in);
        String line;
        OutputStream outToClient;
        DataOutputStream out;
        ServerMessageListeningThread listenMessageThread = new ServerMessageListeningThread(this.socket, this.clients);
        listenMessageThread.start();

        try {
            while (true) {
                line = scanner.nextLine();
                for (Socket socket : this.clients){
                    try {
                        outToClient = socket.getOutputStream();
                        out = new DataOutputStream(outToClient);
                        out.writeUTF(socketName + ">>> " + line);
                        out.flush();
                        outToClient.flush();
                    }
                    catch (SocketException se){
                        continue;
                    }
                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

}
