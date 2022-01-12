CREATE TABLE IF NOT EXISTS remotedocs_users
(
    username VARCHAR(30) NOT NULL PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    salt     INTEGER NOT NULL,
);

CREATE TABLE IF NOT EXISTS remotedocs_files
(
    id      SERIAL NOT NULL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    digest  VARCHAR(5000) NOT NULL,
    ownerId INTEGER NOT NULL,
    FOREIGN KEY (ownerId)
        REFERENCES remotedocs_users(ownerId) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS remotedocs_permissions
(
    userId     INTEGER NOT NULL,
    fileId     INTEGER NOT NULL,
    permission INTEGER NOT NULL,
    FOREIGN KEY (userId)
        REFERENCES remotedocs_users(userId) ON DELETE CASCADE
    FOREIGN KEY (fileId)
        REFERENCES remotedocs_files(fileId) ON DELETE CASCADE
);