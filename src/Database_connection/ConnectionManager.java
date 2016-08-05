package Database_connection;

import java.sql.*;

public class ConnectionManager {
	// double-checked locking
    private volatile static ConnectionManager connectionManager;
    private Connection connection;

    private ConnectionManager() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dominion", "root", "");
            //connection = DriverManager.getConnection("jdbc:mysql://www.db4free.net:3306/projectdominion?verifyServerCertificate=false&useSSL=true&requireSSL", "groep10", "dominion");
        } catch (Exception e) {
            //e.printStackTrace();
        	try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dominion", "root", "");
        	} catch (Exception ee) {
        		ee.printStackTrace();
        	}
        }
    }

    public static ConnectionManager getInstance() {
        if(connectionManager == null) {
            synchronized (ConnectionManager.class) {
                if(connectionManager == null) {
                    connectionManager = new ConnectionManager();
                }
            }
        }

        return connectionManager;
    }

    public Connection getConnection() {
        return connection;
    }
}
