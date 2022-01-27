package sirs.remotedocs;

import org.apache.ibatis.jdbc.ScriptRunner;
import sirs.remotedocs.domain.FileDetails;
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
    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    public ServerRepo() {
        this.logger = new Logger("Server", "DB");
        this.dbUrl = System.getenv("DB_URL");
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        this.dbDir = System.getenv("DB_DIR");

        try {
            connection = this.newConnection();
            this.logger.log("Connected to database successfully!");

            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(new BufferedReader(new FileReader(this.dbDir)));
            this.logger.log("Database structure created successfully!");
        } catch (SQLException | FileNotFoundException e) {
            this.logger.log(e.getMessage());
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { 
                    /* Ignored */}
            }
        }
    }

    private Connection newConnection() throws SQLException {
        return DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
    }

    private void closeConnection(){
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { 
                this.logger.log(e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                this.logger.log(e.getMessage());
            }
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                this.logger.log(e.getMessage());
            }
        }
    }

    public User getUser(String username) throws SQLException {
        try {
            String query = "SELECT * FROM remotedocs_users WHERE username=?";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("salt"),
                    resultSet.getString("private_key"),
                    resultSet.getString("public_key")
                    );            
            }

            return null;
        } finally{
            closeConnection();
        }
    }

    public String getOwner(int fileId) throws SQLException {
        try {
            String query = "SELECT userId FROM remotedocs_permissions WHERE" 
            +" fileId=? and permission = 0";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, fileId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("userId");
            }
            return null;
        } finally {
            closeConnection();
        }

    }

    public Map<Integer, String> getFilesDigests() throws SQLException {
        try{
            String query = "SELECT id, digest FROM remotedocs_files";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            Map<Integer, String> digests = new HashMap<>();

            while (resultSet.next()) {
                digests.put(
                        resultSet.getInt("id"),
                        resultSet.getString("digest")
                );
            }

            return digests;
        } finally {
            closeConnection();
        }
    }


    public FileDetails getFileDetails(int fileId, String username) throws SQLException {
        try {
            String query = "SELECT digest,time_change,last_updater,sharedKey, iv FROM remotedocs_files,"
            +" remotedocs_permissions WHERE fileId = id and fileId=? and userId=?"; 
            String owner = getOwner(fileId);
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, fileId);
            statement.setString(2, username);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new FileDetails(
                    resultSet.getString("digest"),
                    resultSet.getString("sharedKey"),
                    owner,
                    resultSet.getString("iv"),
                    resultSet.getString("last_updater"),
                    resultSet.getTimestamp("time_change").toLocalDateTime()
                );
            }
            return null;
        } finally {
            closeConnection();
        }

    }

    public void registerUser(String username, String hashedPassword, String salt, String publicKey, String privateKey) throws SQLException {
        try {
            String query = "INSERT INTO remotedocs_users (username, password, salt, public_key, private_key) VALUES (?, ?, ?, ?, ?)";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, salt);
            statement.setString(4, publicKey);
            statement.setString(5, privateKey);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public FileDetails createFile(int id, String name, String username, String fileKey, String iv) throws SQLException {
        try {
            String query = "INSERT INTO remotedocs_files (id, name, digest, last_updater) VALUES (?, ?, ?, ?)";
            String query2 = "SELECT time_change from remotedocs_files where id =?";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, "");
            statement.setString(4, username);
            statement.executeUpdate();
            
            addPermission(username, id, 0, fileKey, iv, false);
            statement = connection.prepareStatement(query2);
            statement.setInt(1,id);
            resultSet = statement.executeQuery();
            resultSet.next();
            LocalDateTime time_change = resultSet.getTimestamp("time_change").toLocalDateTime();
            return new FileDetails(id, time_change);
        } finally {
            closeConnection();
        }
        
    }

    public boolean fileExists(String username, String name) throws SQLException {
        try {
            String query = "SELECT COUNT(A.id) AS total FROM remotedocs_files AS A, remotedocs_permissions AS B WHERE " +
                "A.id = B.fileId AND B.userId=? AND A.name=? AND B.permission = 0";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, name);
            resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt("total") > 0;
        } finally {
            closeConnection();
        }
    }

    public int getMaxFileId() throws SQLException {
        try {
            String query = "SELECT MAX(id) AS maxId FROM remotedocs_files";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getInt("maxId");
            else
                return -1;
    
        } finally {
            closeConnection();
        }
    }
    public void updateFileDigest(int id, String digest, String username) throws SQLException {
        try {
            String query = "UPDATE remotedocs_files SET digest=?, time_change=now(), last_updater=? WHERE id=?";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, digest);
            statement.setString(2, username);
            statement.setInt(3, id);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public void updateFileName(int id, String newName) throws SQLException {
        try {
            String query = "UPDATE remotedocs_files SET name=? WHERE id=?";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, newName);
            statement.setInt(2, id);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public int getFilePermission(int id, String username) throws SQLException {
        try {
            String query = "SELECT permission FROM remotedocs_permissions WHERE fileId=? AND userId=?";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, username);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("permission");
            }

            return -1;
        } finally {
            closeConnection();
        }
    }
    public List<FileDetails> getListDocuments(String username) throws SQLException {
        try {
            String query = "SELECT fileId,name,permission FROM remotedocs_permissions,remotedocs_files WHERE fileId = id and userId=?";
            ArrayList<FileDetails> listOfDocuments = new ArrayList<>();
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);

            resultSet = statement.executeQuery();
            while(resultSet.next()){
                int fileId = resultSet.getInt("fileId");
                String name = resultSet.getString("name");
                int permission = resultSet.getInt("permission");         
                listOfDocuments.add(new FileDetails(fileId, name, permission));
            }

            return listOfDocuments;
        } finally {
            closeConnection();
        }
    }

    public void deleteFile(int id) throws SQLException {
        try {
            String query = "DELETE FROM remotedocs_files where id=?";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public List<User> getUsersExceptOwnerOfDoc(int id) throws SQLException{
        try{ 
            String query = "SELECT userId,permission FROM remotedocs_permissions WHERE fileId = ? and permission > 0";
            ArrayList<User> listOfUsers = new ArrayList<>();
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();
            while(resultSet.next()){
                String username = resultSet.getString("userId");
                int permission = resultSet.getInt("permission");         
                listOfUsers.add(new User(username, permission));
            }

            return listOfUsers;
        } finally {
            closeConnection();
        }
    }

    public void updatePermission(String username, int id, int permission) throws SQLException {
        try {
            String query = "UPDATE remotedocs_permissions SET permission=? WHERE fileId=? AND userId=?";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, permission);
            statement.setInt(2, id);
            statement.setString(3, username);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public void addPermission(String username, int id, int permission, String fileKey, String iv, boolean flag) throws SQLException {
        try {
            String query = "INSERT INTO remotedocs_permissions (userId, fileId, permission, sharedKey, iv) VALUES (?, ?, ?, ?, ?)";

            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setInt(2, id);
            statement.setInt(3, permission);
            statement.setString(4, fileKey);
            statement.setString(5, iv);
            statement.executeUpdate();
        } finally {
            if (flag)
                closeConnection();
        }
    }

    public void deletePermission(String username, int id) throws SQLException {
        try {
            String query = "DELETE FROM remotedocs_permissions WHERE userId = ? AND fileId = ?";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setInt(2, id);
            statement.executeUpdate();
        } finally {
            closeConnection();
        }
    }

    public FileDetails getSharedKey(String username, int id) throws SQLException {
        try {
            String query = "SELECT iv, sharedKey, permission FROM remotedocs_permissions WHERE fileId = ? and userId = ?";
            connection = this.newConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, username);
            resultSet = statement.executeQuery();
            if (resultSet.next())
                return new FileDetails(resultSet.getString("sharedKey"), resultSet.getString("iv") , resultSet.getInt("permission"));

            return null;
        } finally {
            closeConnection();
        }
    }
}
