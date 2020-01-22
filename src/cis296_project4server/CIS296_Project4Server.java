package cis296_project4server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominic
 */
public class CIS296_Project4Server {
    static public int clientCount;
    static Socket[] sockets;
     
    public static void main(String[] args) {
        try {
            Socket socket;
            ServerSocket server;
            sockets = new Socket[3];
            clientCount = 0;
            server = new ServerSocket(1226);
            while(true) {
                socket = server.accept();               
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                InetAddress inetAddress = socket.getInetAddress();
                if(clientCount == 2) {
                    System.out.println(inetAddress.getCanonicalHostName() + " tried to connect, but the server is full.");
                    out.writeInt(1);
                }
                else {
                    out.writeInt(0);
                    new Thread(new ChatHandler(socket)).start();
                    clientCount++;
                }
            }
        } //end main
        catch (IOException ex) {
            Logger.getLogger(CIS296_Project4Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 static class ChatHandler implements Runnable {
    final private Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;
    //ServerSocket server;
    InetAddress inetAddress;
    ChatHandler(Socket socket) {
        clientSocket = socket;
        sockets[clientCount] = clientSocket;
        inetAddress = clientSocket.getInetAddress();
    }
    @Override
    public void run() {
            try {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                for(int i = 0; i < clientCount; i++) {
                    if (sockets[i] == clientSocket) {}
                    else {
                        out = new DataOutputStream(sockets[i].getOutputStream());
                        out.writeUTF(inetAddress.getCanonicalHostName() + " has connected to the server."); 
                        System.out.println(inetAddress.getCanonicalHostName() + " has connected to the server.");
                    }    
                }
                while(true) {
                    String message;
                    message = in.readUTF();
                    if(message.equals("QUIT")) {
                        userDisconnect(); //calls function userDisconnect for cleanliness
                        break;
                    }
                    else {
                        System.out.println(inetAddress.getCanonicalHostName() + " : " + message); //outputs message to clients if not quit command
                        for(int i = 0; i < clientCount; i++) { //looping through all sockets
                            out = new DataOutputStream(sockets[i].getOutputStream());
                            out.writeUTF(inetAddress.getCanonicalHostName() + " : " + message);
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("The connection was forcibly closed.");
                clientCount--;
            }
    }
    void userDisconnect() {
        int i;
        DataOutputStream outLeave;
        System.out.println(inetAddress.getCanonicalHostName() + " disconnected from the server.");
        for(i = 0;i < clientCount; i++) {
            if(sockets[i] == clientSocket) {
                try {
                    outLeave = new DataOutputStream(sockets[i].getOutputStream());
                    outLeave.writeUTF("You have been disconnected. \n");
                    sockets[i].close();
                    clientCount--;
                    for(int j = i; j < clientCount; j++) {
                        sockets[j] = sockets[j+1];
                    }
                    break;
                } catch (IOException ex) {
                    System.out.println("OOF! Something went wrong while a user disconnected.");
                    clientCount--;
                }
            }
        }
            for(i = 0; i < clientCount; i++) {
                if(sockets[i] == clientSocket) {}
                else {
                    try {
                        out = new DataOutputStream(sockets[i].getOutputStream());
                        out.writeUTF(inetAddress.getCanonicalHostName() + " disconnected from the server.");
                    }   
                    catch (IOException ex) {
                        System.out.println("OOF! Something went wrong while a user was disconnecting.");
                    }
                }
            }
        }   
    }
}



