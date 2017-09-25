/* links for emoticons
http://assets.nydailynews.com/polopoly_fs/1.1163260.1348105449!/img/httpImage/image.jpg_gen/derivatives/article_750/emoticon20n-1-web.jpg
http://clipartsign.com/upload/2016/02/24/winking-face-clip-art-clipart.jpeg
http://content.photojojo.com/wordpress/wp-content/uploads/2015/08/iphone-emoji-faces.png
http://pix.iemoji.com/images/emoji/apple/ios-9/256/heavy-black-heart.png
https://s-media-cache-ak0.pinimg.com/736x/dd/26/79/dd2679d14390fd1436941eeb2484c9de.jpg
*/
package clientSide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class ClientListenMessageThread extends Thread{

    private Socket client;
    private ClientGUI clientGUI;

    ClientListenMessageThread(Socket client, ClientGUI clientGUI){
        this.client = client;
        this.clientGUI = clientGUI;
    }

    public void run(){
        DataInputStream in;
        while (true){
            try {
                in = new DataInputStream(client.getInputStream());
                String message = in.readUTF();
                this.clientGUI.chatAddMessage(message);
            }
            catch (IOException ioe){
                break;
            }
        }
    }

}

public class ClientGUI extends JFrame implements MouseListener, KeyListener {

    private JTextField chatTextField;
    private JLabel chatLabel;
    private String chat;
    private String socketName;
    private Socket client;

    private ClientGUI(String serverName,int port,String socketName){
        // setting up the GUI
        this.socketName = socketName;
        System.out.println(System.getProperty("user.dir"));
        this.chat = "<html>Chat Room<br>";
        this.init();
        this.chatTextField = this.createTextField();
        this.getContentPane().add(this.chatTextField);
        this.chatLabel = this.createLabel();
        JScrollPane jScrollPane = new JScrollPane(this.chatLabel);
        jScrollPane.setBounds(15, 60, this.getWidth() - 50, this.getHeight() - 110);
        jScrollPane.setOpaque(true);
        jScrollPane.setVisible(true);
        this.getContentPane().add(jScrollPane);
        this.getContentPane().add(this.nameLabel());
        this.setVisible(true);

        // connecting the client socket to the server socket
        try {
            this.chat += "Connecting to " + serverName + " on port " + port + "<br>";
            //SocketAddress sockaddr = new InetSocketAddress(serverName, port);
            this.client = new Socket(serverName,port);
            //this.client.connect(sockaddr,1000000000);
            this.chat += "Successful connection! Welcome " + this.socketName + "!<br><br>";
        }
        catch(IOException ioe){
            //ioe.printStackTrace();
            this.chat += "Unsuccessful connection";
        }
        this.chatLabel.setText(this.chat);
    }

    private void init(){
        this.setSize(400,500);
        this.setTitle("ClientChat");
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JTextField createTextField(){
        JTextField textField = new JTextField("Enter your message...");
        textField.setBounds(10, 10, this.getWidth()-40, 20);
        textField.setVisible(true);
        textField.setOpaque(true);
        textField.addMouseListener(this);
        textField.addKeyListener(this);
        return textField;
    }

    private JLabel nameLabel(){
        JLabel namelabel = new JLabel("You are logged in as: " + this.socketName + ".");
        namelabel.setOpaque(true);
        namelabel.setVerticalAlignment(SwingConstants.TOP);
        namelabel.setBounds(this.getWidth()/2 - 90, 35, 180, 20);
        //namelabel.setBackground(Color.white);
        namelabel.setVisible(true);
        return namelabel;

    }

    private JLabel createLabel(){
        JLabel chatLabel = new JLabel(this.chat);
        chatLabel.setBounds(15, 60, this.getWidth() - 50, this.getHeight() - 110);
        chatLabel.setVisible(true);
        chatLabel.setVerticalAlignment(SwingConstants.TOP);
        chatLabel.setOpaque(true);
        chatLabel.setBackground(Color.white);
        return chatLabel;
    }

    void chatAddMessage(String message){
        this.chat += message + "<br>";
        this.chatLabel.setText(this.chat);
    }

    private Socket getClient(){
        return this.client;
    }

    // mouse events
    public void mouseClicked(MouseEvent e){
        //this.chatTextField.setText("");
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){
        if(this.chatTextField.getText().equals("Enter your message...")){
            this.chatTextField.setText("");
        }
    }
    public void mouseExited(MouseEvent e){
        if(this.chatTextField.getText().equals("")){
            this.chatTextField.setText("Enter your message...");
        }
    }

    // keyboard events
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER ){
            String message = this.chatTextField.getText();
            message = message.replace(":)","<img src=\"http://assets.nydailynews.com/polopoly_fs/1.1163260.1348105449!/img/httpImage/image.jpg_gen/derivatives/article_750/emoticon20n-1-web.jpg\" height = 16 width = '27'>");
            message = message.replace(";)","<img src = \"http://clipartsign.com/upload/2016/02/24/winking-face-clip-art-clipart.jpeg\" height = 16 width = '20'>");
            message = message.replace(":*","<img src = \"http://content.photojojo.com/wordpress/wp-content/uploads/2015/08/iphone-emoji-faces.png\" height = 16 width = 23>");
            message = message.replace("<3","<img src = \"http://pix.iemoji.com/images/emoji/apple/ios-9/256/heavy-black-heart.png\" height = '16' width = '22'>");
            message = message.replace("(y)","<img src = \"https://s-media-cache-ak0.pinimg.com/736x/dd/26/79/dd2679d14390fd1436941eeb2484c9de.jpg\" height = 21 width = 26>");
            OutputStream outToServer;
            DataOutputStream out;
            try {
                outToServer = this.client.getOutputStream();
                out = new DataOutputStream(outToServer);

                // user logging out
                if(message.equals("logout")){
                    out.writeUTF(socketName + " logged out.");
                    out.flush();
                    outToServer.flush();
                    Thread.sleep(1000);
                    this.client.close();
                    System.exit(0);
                }
                // sending a message
                else {
                    out.writeUTF(socketName + ">>> " + message);
                    out.flush();
                    outToServer.flush();
                }

            }
            catch (IOException ioe){
                ioe.printStackTrace();
                System.exit(0);
            }
            catch (InterruptedException ie){
                ie.printStackTrace();
            }

            this.chatTextField.setText("");
        }
    }
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}


    public static void main(String[] args){
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        String socketName = args[2];
        ClientGUI clientGUI = new ClientGUI(serverName,port,socketName);
        new ClientListenMessageThread(clientGUI.getClient(),clientGUI).start();
    }
}
