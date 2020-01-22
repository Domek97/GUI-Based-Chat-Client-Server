package cis296_project4client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Dominic
 */
public class FXMLDocumentController implements Initializable {
   
    @FXML
    private TextArea outputBox;
    @FXML
    private TextField inputBox, ipField, portField;
    @FXML
    private Button sendButton, connectButton;
    @FXML
    private Label ipLabel, portLabel;
    
    Socket socket;
    static DataInputStream in;
    static DataOutputStream out;
    String ip;
    int port;
    public FXMLDocumentController() {

    }

    @FXML
    private void hitSend(ActionEvent event) {          
        String userInput = inputBox.getText();
        if(userInput.equals("")) {}
        else {
            inputBox.setText("");
            try {
                out.writeUTF(userInput);
            } catch (IOException ex) {
                System.out.println("Message did not send correctly.");
            }
        }
    }

    @FXML
    private void hitConnect(ActionEvent event) {      
        ip = ipField.getText();
        if(portField.getText().matches("[0-9]+") && (portField.getText().length() <= 5)) {
            port = Integer.parseInt(portField.getText());
        }
        try {                     
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(new ChatHandler(socket)).start();
            
        } 
        catch (IOException ex) { //server isn't found
            System.out.println("Failed to establish a connection.");
            outputBox.appendText("Failed to establish a connection. \n");
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        outputBox.appendText("If server is on lan, use IPv4 address. If not, use IPv6 address. \nBlank field enters localhost. \n");
    }    
    class ChatHandler implements Runnable { //receives messages here
        final private Socket clientSocket;
        ChatHandler(Socket socket) {
            clientSocket = socket;
        }
        @Override
        public void run() {
        DataInputStream in;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            int result = in.readInt();
            if(result == 1){
                outputBox.appendText("The server is full! Please try again later. \n");
            }
            else {
                outputBox.setText("Welcome. Type \"QUIT\" or close the chat window to disconnect. \n \n");
                connectButton.setDisable(true); connectButton.setVisible(false);
                ipLabel.setDisable(true); ipLabel.setVisible(false);
                ipField.setDisable(true); ipField.setVisible(false);
                portLabel.setDisable(true); portLabel.setVisible(false);
                portField.setDisable(true); portField.setVisible(false);
                inputBox.setDisable(false); inputBox.setVisible(true);
                sendButton.setDisable(false); sendButton.setVisible(true);
                inputBox.setText("");
                while(true) {
                    String message = in.readUTF();
                    outputBox.appendText(message + "\n");
                    if(message.equals("You have been disconnected.")) break; //this text will only be received if the user types QUIT
            }
            }

        } 
        catch (IOException ex) { //When the server fails to communicate to the client
            System.out.println("Lost connection to the server.");
            outputBox.appendText("Lost connection to the server. \n");
            sendButton.setDisable(true);sendButton.setVisible(false);
            inputBox.setDisable(true); inputBox.setVisible(false);
            connectButton.setDisable(false); connectButton.setVisible(true);
            ipLabel.setDisable(false); ipLabel.setVisible(true);
            ipField.setDisable(false); ipField.setVisible(true);
            portLabel.setDisable(false); portLabel.setVisible(true);
            portField.setDisable(false); portField.setVisible(true);
        }
    }

    
    }
}
