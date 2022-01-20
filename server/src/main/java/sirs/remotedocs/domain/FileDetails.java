package sirs.remotedocs.domain;

public class FileDetails {

    private String sharedKey;
    private int permission;

    private byte[] content;

    public FileDetails(String sharedKey, int permission) {
        this.sharedKey = sharedKey;
        this.permission = permission;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public int getPermission() {
        return permission;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
