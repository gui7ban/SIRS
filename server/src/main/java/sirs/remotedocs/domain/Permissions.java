package sirs.remotedocs.domain;

public enum Permissions {

    OWNER(0),
    READ(1),
    WRITE(2);

    public final int value;

    Permissions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}