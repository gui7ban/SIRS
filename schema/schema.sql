DROP TABLE IF EXISTS remotedocs_permissions;
DROP TABLE IF EXISTS remotedocs_users;
DROP TABLE IF EXISTS remotedocs_files;
CREATE TABLE remotedocs_users
(
    username VARCHAR(100) NOT NULL PRIMARY KEY,
    password CHAR(44) NOT NULL,
    salt     CHAR(24) NOT NULL,
    public_key VARCHAR(5000) NOT NULL,
    private_key VARCHAR(5000) NOT NULL
);

CREATE TABLE remotedocs_files
(
    id      INTEGER NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    digest  VARCHAR(500) NOT NULL,
    time_change timestamp NOT NULL DEFAULT now(),
    last_updater VARCHAR(100) NOT NULL,
    backup  boolean NOT NULL DEFAULT FALSE,
    dirty boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE remotedocs_permissions
(
    userId     VARCHAR(100) NOT NULL,
    fileId     INTEGER NOT NULL,
    permission INTEGER NOT NULL,
    sharedKey  VARCHAR(500) NOT NULL,
    iv         VARCHAR(500) NOT NULL,
    PRIMARY KEY (userId, fileId),
    FOREIGN KEY (userId)
        REFERENCES remotedocs_users(username) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (fileId)
        REFERENCES remotedocs_files(id) ON DELETE CASCADE ON UPDATE CASCADE
);