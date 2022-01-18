CREATE TABLE IF NOT EXISTS remotedocs_users
(
    username VARCHAR(30) NOT NULL PRIMARY KEY,
    password CHAR(64) NOT NULL,
    salt     CHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS remotedocs_files
(
    id      SERIAL NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    digest  CHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS remotedocs_permissions
(
    userId     VARCHAR(30) NOT NULL,
    fileId     INTEGER NOT NULL,
    permission INTEGER NOT NULL,
    sharedKey  VARCHAR(500) NOT NULL,
    FOREIGN KEY (userId)
        REFERENCES remotedocs_users(username) ON DELETE CASCADE,
    FOREIGN KEY (fileId)
        REFERENCES remotedocs_files(id) ON DELETE CASCADE
);