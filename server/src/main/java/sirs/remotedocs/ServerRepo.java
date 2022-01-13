package sirs.remotedocs;

import org.apache.ibatis.jdbc.ScriptRunner;
import sirs.remotedocs.domain.User;
import sirs.remotedocs.domain.exception.ErrorMessage;
import sirs.remotedocs.domain.exception.RemoteDocsException;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.logging.ErrorManager;

public class ServerRepo {

    private final Logger logger;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String dbDir;

    public ServerRepo() {
        this.logger = new Logger("Server", "DB");
        this.dbUrl = System.getenv("DB_URL");
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        this.dbDir = System.getenv("DB_DIR");

        try {
            Connection connection = this.newConnection();
            this.logger.log("Connected to database successfully!");

            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(new BufferedReader(new FileReader(this.dbDir)));
            this.logger.log("Database structure created successfully!");
        } catch (SQLException | FileNotFoundException e) {
            this.logger.log(e.getMessage());
        }
    }

    private Connection newConnection() throws SQLException {
        return DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
    }

    public boolean loginUser(String username, String hashedPassword) throws RemoteDocsException {
        String query = "SELECT COUNT(username) AS users FROM remotedocs_users WHERE username=? AND password=?";

        try {
            Connection connection = this.newConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.execute();

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int numberOfUsers = resultSet.getInt("users");
            return numberOfUsers > 0;
        } catch (SQLException e) {
            throw new RemoteDocsException(ErrorMessage.USER_NOT_FOUND);
        }
    }

    public void registerUser(String username, String hashedPassword, int salt) throws RemoteDocsException {
        String query = "INSERT INTO remotedocs_users (username, password, salt) VALUES (?, ?, ?)";

        try {
            Connection connection = this.newConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setInt(3, salt);
            statement.execute();
        } catch (SQLException e) {
            throw new RemoteDocsException(ErrorMessage.USER_ALREADY_EXISTS);
        }
    }
}
