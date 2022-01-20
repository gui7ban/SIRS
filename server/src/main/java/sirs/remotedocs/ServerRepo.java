package sirs.remotedocs;

import org.apache.ibatis.jdbc.ScriptRunner;
import sirs.remotedocs.domain.Permissions;
import sirs.remotedocs.domain.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;

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

    public boolean loginUser(String username, String hashedPassword) {
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
            this.logger.log(e.getMessage());
            return false;
        }
    }

    public User getUser(String username) {
        String query = "SELECT * FROM remotedocs_users WHERE username=?";

        try {
            Connection connection = this.newConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("salt")
                );
            }
        } catch (SQLException e) {
            this.logger.log(e.getMessage());
        }

        return null;
    }

    public void registerUser(String username, String hashedPassword, String salt) {
        String query = "INSERT INTO remotedocs_users (username, password, salt) VALUES (?, ?, ?)";

        try {
            Connection connection = this.newConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, salt);
            statement.execute();
        } catch (SQLException e) {
            this.logger.log(e.getMessage());
        }
    }

    public void createFile(String id, String name, String username) {
        String query = "INSERT INTO remotedocs_files (id, name, digest) VALUES (?, ?, ?)";
        String query2 = "INSERT INTO remotedocs_permissions (userId, fileId, permission, sharedKey) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = this.newConnection();
            PreparedStatement createFile = connection.prepareStatement(query);
            PreparedStatement addFilePermissions = connection.prepareStatement(query2);
            createFile.setString(1, id);
            createFile.setString(2, name);
            createFile.setString(3, "");
            createFile.execute();

            addFilePermissions.setString(1, username);
            addFilePermissions.setString(2, id);
            addFilePermissions.setInt(3, Permissions.OWNER.getValue());
            addFilePermissions.setString(4, ""); // TODO: Add shared key.
            addFilePermissions.execute();
        } catch (SQLException e) {
            this.logger.log(e.getMessage());
        }
    }
}
