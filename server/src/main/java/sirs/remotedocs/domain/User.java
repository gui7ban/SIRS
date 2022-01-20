package sirs.remotedocs.domain;

public class User {
    private String name;
    private String hashedPassword;
    private String salt;
   
    public User(String name, String hashedPassword, String salt) {
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() { return salt; }
}
