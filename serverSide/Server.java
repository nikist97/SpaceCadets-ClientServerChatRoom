package serverSide;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * a class, which represents the Server, to which all clients connect
 */
public class Server {

    /**
     * a constructor for the Server class
     * @param port the port number the server is running on
     * @throws IOException in case of a used port, etc.
     */
    private Server(int port) throws IOException{
        // initialise the server socket and the timeout for clients to connect
        final ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000000);

        // printing the host address and the host name for testing purposes
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Host address is: " + ip.getHostAddress());
        System.out.println("Host name is: " + ip.getHostName());

        // initialise an array list for clients sockets
        final ArrayList<Socket> clients = new ArrayList<>();

        Socket client;
        while(true) {
            try{
                client = serverSocket.accept();
                System.out.println("Connected to " + client.getRemoteSocketAddress());
                clients.add(client);
                new ClientThread(client, clients, "Server").start();
            }
            catch (SocketTimeoutException soe){
                System.out.println("Timeout exception occurred.");
                // close all sockets
                serverSocket.close();
                for(Socket clientToClose : clients){
                    clientToClose.close();
                }
                break;
            }
        }
    }

    /**
     * main method - gets the port number and starts the server
     * @param args command line arguments, port number should be the first argument
     */
    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);

        try {
            new Server(port);
        } catch (IOException ioe) {
            System.out.println("Something went wrong while initialising the server: " + ioe.getLocalizedMessage());
        }
    }
}
