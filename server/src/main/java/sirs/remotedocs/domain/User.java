package sirs.remotedocs.domain;

public class User {
    private String name;
    private String hashedPassword;
    private String salt;
    private int permission;
   
    public User(String name, String hashedPassword, String salt) {
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public User (String name, int permission){
        this.name = name;
        this.permission = permission;
    }

    public int getPermission(){
        return permission;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() { return salt; }
}
