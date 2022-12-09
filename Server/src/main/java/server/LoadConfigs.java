package server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class LoadConfigs {
    public static Properties properties = new Properties();
    public static String database;
    public static String username;
    public static String password;
    public static String url;


    public ArrayList<String> credentials() throws Exception {
        ArrayList<String> credentials = new ArrayList<>();

       // File file = new File(this.getClass().getResource("/config.properties").toURI());
        try (InputStream fs = this.getClass().getResourceAsStream("/configs/config.properties")) {
            properties.load(fs);
            url = properties.getProperty("URL");
            database = properties.getProperty("DATABASE");
            username = properties.getProperty("USERNAME");
            password = properties.getProperty("PASSWORD");

            credentials.add(url);
            credentials.add(database);
            credentials.add(username);
            credentials.add(password);

        } catch (Exception e) {
            System.out.println(e);
        }
        return credentials;
    }

}
