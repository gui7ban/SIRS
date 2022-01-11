CREATE TABLE IF NOT EXISTS remotedocs_users
(
    id       SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(50) NOT NULL,
    salt     INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS remotedocs_files
(
    id   SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    digest VARCHAR(5000) NOT NULL
);