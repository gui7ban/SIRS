package sirs.remotedocs;

import org.apache.ibatis.jdbc.ScriptRunner;
import sirs.remotedocs.domain.FileDetails;
import sirs.remotedocs.domain.Permissions;
import sirs.remotedocs.domain.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM remotedocs_users WHERE username=?";

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

        return null;
    }

    public String getOwner(int fileId) throws SQLException {
        String query = "SELECT userId FROM remotedocs_permissions WHERE" 
        +" fileId=? and permission = 0";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, fileId);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("userId");
        }

        return null;
    }

    public Map<Integer, String> getFilesDigests() throws SQLException {
        String query = "SELECT id, digest FROM remotedocs_files";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        Map<Integer, String> digests = new HashMap<>();

        while (resultSet.next()) {
            digests.put(
                    resultSet.getInt("id"),
                    resultSet.getString("digest")
            );
        }

        return digests;
    }


    public FileDetails getFileDetails(int fileId, String username) throws SQLException {
        String query = "SELECT time_change,last_updater,sharedKey FROM remotedocs_files,"
        +" remotedocs_permissions WHERE fileId = id and fileId=? and userId=?"; 
        String owner = getOwner(fileId);
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, fileId);
        statement.setString(2, username);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new FileDetails(
                resultSet.getString("sharedKey"),
                owner,
                resultSet.getString("last_updater"),
                resultSet.getTimestamp("time_change").toLocalDateTime()
            );
        }
        return null;

    }

    public void registerUser(String username, String hashedPassword, String salt) throws SQLException {
        String query = "INSERT INTO remotedocs_users (username, password, salt, public_key) VALUES (?, ?, ?, ?)";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, hashedPassword);
        statement.setString(3, salt);
        statement.setString(4, "");
        statement.executeUpdate();
    }

    public FileDetails createFile(int id, String name, String username) throws SQLException {
        String query = "INSERT INTO remotedocs_files (id, name, digest, last_updater) VALUES (?, ?, ?, ?)";
        String query2 = "INSERT INTO remotedocs_permissions (userId, fileId, permission, sharedKey) VALUES (?, ?, ?, ?)";
        String query3 = "SELECT time_change from remotedocs_files where id =?";
        Connection connection = this.newConnection();
        PreparedStatement createFile = connection.prepareStatement(query);
        PreparedStatement addFilePermissions = connection.prepareStatement(query2);
        PreparedStatement getTimeChange = connection.prepareStatement(query3);
        createFile.setInt(1, id);
        createFile.setString(2, name);
        createFile.setString(3, "");
        createFile.setString(4, username);
        createFile.executeUpdate();

        addFilePermissions.setString(1, username);
        addFilePermissions.setInt(2, id);
        addFilePermissions.setInt(3, Permissions.OWNER.getValue());
        addFilePermissions.setString(4, ""); // TODO: Add encrypted shared key.
        addFilePermissions.executeUpdate();

        getTimeChange.setInt(1,id);
        ResultSet getTime = getTimeChange.executeQuery();
        getTime.next();
        LocalDateTime time_change = getTime.getTimestamp("time_change").toLocalDateTime();
        return new FileDetails(id, time_change);
        
    }

    public boolean fileExists(String username, String name) throws SQLException {
        String query = "SELECT COUNT(A.id) AS total FROM remotedocs_files AS A, remotedocs_permissions AS B WHERE " +
                "A.id = B.fileId AND B.userId=? AND A.name=? AND B.permission = 0";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, name);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() && resultSet.getInt("total") > 0;
    }

    public int getMaxFileId() throws SQLException {
        String query = "SELECT MAX(id) AS maxId FROM remotedocs_files";
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next())
            return resultSet.getInt("maxId");
        else
            return -1;
    }

    public void updateFileDigest(int id, String digest, String username) throws SQLException {
        String query = "UPDATE remotedocs_files SET digest=?, time_change=now(), last_updater=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, digest);
        statement.setString(2, username);
        statement.setInt(3, id);
        statement.executeUpdate();
    }

    public void updateFileName(int id, String newName) throws SQLException {
        String query = "UPDATE remotedocs_files SET name=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, newName);
        statement.setInt(2, id);
        statement.executeUpdate();
    }

    public int getFilePermission(int id, String username) throws SQLException {
        String query = "SELECT permission FROM remotedocs_permissions WHERE fileId=? AND userId=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.setString(2, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("permission");
        }

        return -1;
    }
    public List<FileDetails> getListDocuments(String username) throws SQLException {
        String query = "SELECT fileId,name,permission FROM remotedocs_permissions,remotedocs_files WHERE fileId = id and userId=?";
        ArrayList<FileDetails> listOfDocuments = new ArrayList<>();
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            int fileId = resultSet.getInt("fileId");
            String name = resultSet.getString("name");
            int permission = resultSet.getInt("permission");         
            listOfDocuments.add(new FileDetails(fileId, name, permission));
        }

        return listOfDocuments;
    }

    public void deleteFile(int id) throws SQLException {
        String query = "DELETE FROM remotedocs_files where id=?";
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public List<User> getUsersExceptOwnerOfDoc(int id) throws SQLException{
        String query = "SELECT userId,permission FROM remotedocs_permissions WHERE fileId = ? and permission > 0";
        ArrayList<User> listOfUsers = new ArrayList<>();
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String username = resultSet.getString("userId");
            int permission = resultSet.getInt("permission");         
            listOfUsers.add(new User(username, permission));
        }

        return listOfUsers;
    }

    public void updatePermission(String username, int id, int permission) throws SQLException {
        String query = "UPDATE remotedocs_permissions SET permission=? WHERE fileId=? AND userId=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, permission);
        statement.setInt(2, id);
        statement.setString(3, username);
        statement.executeUpdate();
    }

    public void addPermission(String username, int id, int permission) throws SQLException {
        String query = "INSERT INTO remotedocs_permissions (userId, fileId, permission, sharedKey) VALUES (?, ?, ?, ?)";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, id);
        statement.setInt(3, permission);
        statement.setString(4, ""); // TODO: Add encrypted shared key.
        statement.executeUpdate();
    }

    public void deletePermission(String username, int id) throws SQLException {
        String query = "DELETE FROM remotedocs_permissions WHERE userId = ? AND fileId = ?";
        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setInt(2, id);
        statement.executeUpdate();
    }
}
