package server;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This thread handles connection for each connected client, so the server can
 * handle multiple clients at the same time.
 */
public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private ChatDB chatDB;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server, ChatDB chatDB) {
        this.socket = socket;
        this.server = server;
        this.chatDB = chatDB;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers(); /** Inform the new user of all the connected users */


            String userName = reader.readLine();
            server.addUserName(userName);
            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            /** Read previous messages. Send to the newly connected user */
            ResultSet resultSet = this.chatDB.readMessage();
            try{
                while(resultSet.next()){
                    String message = resultSet.getString("TEXT");
                    this.sendMessage(message);
                }
            }catch(SQLException sqlException){
                System.out.println(sqlException.getMessage());
            }

            /** Read and send message from the connected user */
            String clientMessage;
            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();
            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
