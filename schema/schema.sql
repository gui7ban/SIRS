CREATE TABLE IF NOT EXISTS remotedocs_users
(
    username VARCHAR(100) NOT NULL PRIMARY KEY,
    password CHAR(44) NOT NULL,
    salt     CHAR(24) NOT NULL
);

CREATE TABLE IF NOT EXISTS remotedocs_files
(
    id      VARCHAR(201) NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    digest  VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS remotedocs_permissions
(
    userId     VARCHAR(100) NOT NULL,
    fileId     VARCHAR(201) NOT NULL,
    permission INTEGER NOT NULL,
    sharedKey  VARCHAR(500) NOT NULL,
    FOREIGN KEY (userId)
        REFERENCES remotedocs_users(username) ON DELETE CASCADE,
    FOREIGN KEY (fileId)
        REFERENCES remotedocs_files(id) ON DELETE CASCADE
);