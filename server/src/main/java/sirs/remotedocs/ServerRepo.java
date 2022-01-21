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
import java.util.List;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

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
        +"fileId=? and permission = 0";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, fileId);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("userId");
        }

        return null;
    }

    public void registerUser(String username, String hashedPassword, String salt) throws SQLException {
        String query = "INSERT INTO remotedocs_users (username, password, salt) VALUES (?, ?, ?)";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, hashedPassword);
        statement.setString(3, salt);
        statement.executeQuery();
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
        createFile.executeQuery();

        addFilePermissions.setString(1, username);
        addFilePermissions.setInt(2, id);
        addFilePermissions.setInt(3, Permissions.OWNER.getValue());
        addFilePermissions.setString(4, ""); // TODO: Add encrypted shared key.
        addFilePermissions.executeQuery();

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

    public void updateFileDigest(int id, String digest) throws SQLException {
        String query = "UPDATE remotedocs_files SET digest=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, digest);
        statement.setInt(2, id);
        statement.executeQuery();
    }

    public void updateFileName(int id, String newName) throws SQLException {
        String query = "UPDATE remotedocs_files SET name=? WHERE id=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, newName);
        statement.setInt(2, id);
        statement.executeQuery();
    }

    public FileDetails getFileDetails(int id, String username) throws SQLException {
        String query = "SELECT permission, sharedKey FROM remotedocs_permissions WHERE fileId=? AND username=?";

        Connection connection = this.newConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.setString(2, username);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new FileDetails(
                    resultSet.getString("sharedKey"),
                    resultSet.getInt("permission")
            );
        }

        return null;
    }
    public List<FileDetails> getListDocuments(String username) throws SQLException {
        String query = "SELECT fileId,name,permission FROM remotedocs_permissions,remotedocs_files WHERE fileId = id and username=?";
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
}
