package sirs.remotedocs;

import org.apache.ibatis.jdbc.ScriptRunner;
import sirs.remotedocs.domain.FileDetails;
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
            PreparedStatement loginUser = connection.prepareStatement(query);
            loginUser.setString(1, username);
            loginUser.setString(2, hashedPassword);
            loginUser.execute();

            ResultSet resultSet = loginUser.executeQuery();
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
            PreparedStatement registerUser = connection.prepareStatement(query);
            registerUser.setString(1, username);
            registerUser.setString(2, hashedPassword);
            registerUser.setString(3, salt);
            registerUser.executeQuery();
        } catch (SQLException e) {
            this.logger.log(e.getMessage());
        }
    }

    public void createFile(int id, String name, String username) {
        String query = "INSERT INTO remotedocs_files (id, name, digest) VALUES (?, ?, ?)";
        String query2 = "INSERT INTO remotedocs_permissions (userId, fileId, permission, sharedKey) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = this.newConnection();
            PreparedStatement createFile = connection.prepareStatement(query);
            PreparedStatement addFilePermissions = connection.prepareStatement(query2);
            createFile.setInt(1, id);
            createFile.setString(2, name);
            createFile.setString(3, "");
            createFile.executeQuery();

            addFilePermissions.setString(1, username);
            addFilePermissions.setInt(2, id);
            addFilePermissions.setInt(3, Permissions.OWNER.getValue());
            addFilePermissions.setString(4, ""); // TODO: Add encrypted shared key.
            addFilePermissions.executeQuery();
        } catch (SQLException e) {
            this.logger.log(e.getMessage());
        }
    }

    public boolean fileExists(String username, String name) throws SQLException {
        String query = "SELECT COUNT(A.id) AS total FROM remotedocs_files AS A, remotedocs_permissions AS B WHERE " +
                "A.id = B.fileId AND B.userId=? AND A.name=? AND B.permission = 0";

        Connection connection = this.newConnection();
        PreparedStatement getFiles = connection.prepareStatement(query);
        getFiles.setString(1, username);
        getFiles.setString(2, name);
        ResultSet resultSet = getFiles.executeQuery();

        return resultSet.next() && resultSet.getInt("total") > 0;
    }

    public int getMaxFileId() throws SQLException {
        String query = "SELECT MAX(id) AS maxId FROM remotedocs_files";
        Connection connection = this.newConnection();
        PreparedStatement getId = connection.prepareStatement(query);
        ResultSet resultSet = getId.executeQuery();

        if (resultSet.next())
            return resultSet.getInt("maxId");
        else
            return -1;
    }

    public void updateFileDigest(int id, String digest) throws SQLException {
        String query = "UPDATE remotedocs_files SET digest=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement updateFile = connection.prepareStatement(query);
        updateFile.setString(1, digest);
        updateFile.setInt(2, id);
        updateFile.executeQuery();
    }

    public void updateFileName(int id, String newName) throws SQLException {
        String query = "UPDATE remotedocs_files SET name=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement updateFile = connection.prepareStatement(query);
        updateFile.setString(1, newName);
        updateFile.setInt(2, id);
        updateFile.executeQuery();
    }

    public FileDetails getFileDetails(int id, String username) throws SQLException {
        String query = "SELECT permission, sharedKey FROM remotedocs_permissions WHERE fileId=? AND username=?";

        Connection connection = this.newConnection();
        PreparedStatement getFilePermissions = connection.prepareStatement(query);
        getFilePermissions.setInt(1, id);
        getFilePermissions.setString(2, username);

        ResultSet resultSet = getFilePermissions.executeQuery();

        if (resultSet.next()) {
            return new FileDetails(
                    resultSet.getString("sharedKey"),
                    resultSet.getInt("permission")
            );
        }

        return null;
    }
}
