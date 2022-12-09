package server;

import java.sql.*;

public class ChatDB {
    private String schemaName;
    private String userName;
    private String password;
    private Connection connection;


    /**
     * EEstablish connection to the database
     * @param schemaName
     * @param userName
     * @param password
     * @throws ClassNotFoundException on com.mysql.cj.jdbc.Driver
     * @throws SQLException on db connection failure
     */
    public ChatDB(String schemaName, String userName, String password) throws  ClassNotFoundException, SQLException {
        this.schemaName = schemaName;
        this.userName = userName;
        this.password = password;
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(this.schemaName, this.userName, this.password);
    }


    /**
     * Insert the chat message in the db.
     * @param message
     * @return TRUE if message saved successfully. Otherwise FALSE.
     */
    public boolean saveMessages(String message){
        boolean messageSaved = false;
        try {
            String insertQuery = "INSERT INTO messages (TEXT) VALUES (?)";
            PreparedStatement statement = this.connection.prepareStatement(insertQuery);
            this.connection.setAutoCommit(false);
            this.connection.commit();
            statement.setString(1, message);
            int rows = statement.executeUpdate();
            if(rows > 0)
                messageSaved = true;
            this.connection.setAutoCommit(true);
        }catch (SQLException sqlException){
            messageSaved = false;
        }
        return messageSaved;
    }


    /**
     * Retrieve all messages from the db.
     * @return new ResultSet of messages. Else null on failure to query table.
     */
    public ResultSet readMessage(){
        try {
            String query = "SELECT * FROM messages";
            PreparedStatement statement = this.connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            return resultSet;
        }catch (SQLException sqlException){
            System.out.println("ERROR FAILED TO READ messages TABLE!");
            return null;
        }
    }
}
