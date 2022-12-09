package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

/**
 * This is the chat server class. Press Ctrl + C to terminate the program.
 *
 */
public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private static String url;
    private static String database;

    private static String username;

    private static String password;


    private ArrayList<String> loadcreds = new ArrayList<>();

    private LoadConfigs loadConfigs = new LoadConfigs();
    private static ChatDB chatDB;


    public ChatServer(int port) throws Exception {
        loadcreds = loadConfigs.credentials();
        this.url = loadcreds.get(0);
        this.database = loadcreds.get(1);
        this.username = loadcreds.get(2);
        this.password = loadcreds.get(3);
        this.port = port;
        System.out.println();
    }

   // private ChatDB chatDB = new ChatDB(this.url+database,this.username, this.password);


    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                UserThread newUser = new UserThread(socket, this, this.chatDB);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        try{
            ChatServer server = new ChatServer(port);
            Class.forName("com.mysql.cj.jdbc.Driver");
            chatDB = new ChatDB(url+database,username,password);

            server.execute();
        }catch (SQLException sqlException){
            System.out.println("FAILED TO ESTABLISH DATABASE CONNECTION!");
        }
        catch (Exception classNotFoundException){
            System.out.println("PROVIDE THE DATABASE DRIVER CLASS!");
        }
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    public synchronized void broadcast(String message, UserThread excludeUser) {
        if(!message.equals("bye")) {
            if (this.chatDB.saveMessages(message)) {
                for (UserThread aUser : userThreads) {
                    if (aUser != excludeUser) {
                        aUser.sendMessage(message);
                    }
                }
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconnected, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }


}
