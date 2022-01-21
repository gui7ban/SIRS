package sirs.remotedocs;

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

    public FileDetails(int id, String name, int permission, String owner, LocalDateTime time_change,
            String last_updater) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.owner = owner;
        this.time_change = time_change;
        this.last_updater = last_updater;
    }


    public FileDetails(String sharedKey, int permission) {
        this.sharedKey = sharedKey;
        this.permission = permission;
    }

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
