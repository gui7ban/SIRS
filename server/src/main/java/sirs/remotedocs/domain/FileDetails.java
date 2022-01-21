package sirs.remotedocs.domain;

import java.time.LocalDateTime;

public class FileDetails {

    private int id;
    private String name;
    private String digest;
    private LocalDateTime time_change;
    private String last_updater;
    private String sharedKey;
    private int permission;
    private String owner;
   
    private byte[] content;
    // Response to login 
    public FileDetails(int id, String name, int permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }


    public FileDetails(String sharedKey, int permission) {
        this.sharedKey = sharedKey;
        this.permission = permission;
    }
    //Response to create file
    public FileDetails(int id, LocalDateTime time_change){
        this.id = id;
        this.time_change = time_change;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDigest(){
        return digest;
    }

    public LocalDateTime getTimeChange(){
        return time_change;
    }

    public String getLastUpdater(){
        return last_updater;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public int getPermission() {
        return permission;
    }
    
    public String getOwner(){
        return owner;
    }
    
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
