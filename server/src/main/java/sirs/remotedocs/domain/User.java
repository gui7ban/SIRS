package sirs.remotedocs.domain;

public class User {
    private String name;
    private String hashedPassword;
    private String salt;
    private String privateKey;
    private String publicKey;
    private int permission;
   
    public User(String name, String hashedPassword, String salt, String privateKey, String publicKey) {
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
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


    public String getPrivateKey() {
        return privateKey;
    }
    

    public String getPublicKey() {
        return publicKey;
    }

    public String getSalt() { return salt; }
}
