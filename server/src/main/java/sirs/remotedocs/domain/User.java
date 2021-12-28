package sirs.remotedocs;

public class User {
    private String name;
    private String hashedPassword;
    private String salt;
   
    public User(String name, String password) {
        this.name = name;
        this.hashedPassword = password ; // FIXME É PARA GUARDAR UMA HASH PASSWORD E NÃO PLAINTEXT.

    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
