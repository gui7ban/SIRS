# SIRS Remote Docs

Our project allows several users to create and share files to a remote server in a secure and efficient manner, while focusing on the prevention ransomware attacks' consequences:
The files are encrypted end-to-end (no one with access to the server is able to access the contents of the files stored there)
and the system ensures authenticity, confidentiality and integrity.

In addition to this, users are able to share files created by them with other users, by giving them permission to view and/or edit
a specific file: only authorized users are allowed to change files.

## General Information

The system is composed of three main parts:
- **Client application:** allows clients to interact with the server and manage the files they have access to;
- **Primary Server:** is responsible for managing all files created and edited by the clients and ensure the integrity of the files.
  The primary server is connected to a database, in order to store information of clients and files;
- **Backup Server:** stores a specific number of versions for a certain file, to allow a person to recover it, in case of an attack
  or breach on the main server, which may cause the file to be deleted, corrupted or encrypted.
  
Furthermore, all communication channels between the system's parts are encrypted:
- **Client <-> Primary Server:** This channel is encrypted under TLS protocol;
- **Primary Server <-> Database:** This channel is encrypted under TLS protocol;
- **Primary Server <-> Backup Server:** This channel is encrypted with a custom protocol designed by us.

### Built With

This project uses certain technologies available, such as:

* [Java](https://openjdk.java.net/) - Programming Language and Platform
* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [Grpc](https://grpc.io/docs/languages/java/basics/) - Communication protocol
* [Java Swing](https://netbeans.apache.org/kb/docs/java/quickstart-gui.html) - Widget toolkit GUI
* [PostgreSQL](https://www.postgresql.org/) - Database Engine

## Getting Started

The following instructions will allow one to run the project on their local environment.

### Prerequisites

We recommend that you run this project on a computer with a linux distribution, such as Ubuntu 20.04 LTS.
The instructions shown in this tutorial were executed in a machine running the OS mentioned above.

#### Java

The java recommended version is 11. In order to install it, you must open a shell and run:
```shell
$ apt-get install openjdk-11-jdk
```

#### Maven

This project also relies on Maven. You can install the latest version, by running the following command:
```shell
$ apt-get install maven
```

#### PostgreSQL
Finally, you must install PostgreSQL, by running the following command:
```shell
$ apt-get install postgresql postgresql-contrib
```

This project relies on a database, so you must create one through the postgreSQL command line. Run the following commands:
```shell
$ su -- postgres
$ psql
psql> CREATE DATABASE remotedocs;
psql> \q
```

#### TLS certificate
You also need to generate a TLS certificate, for encrypted communication between client and server. In order to do that,
you must run the following commands:
```shell
$ cd server/resources
$ openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365
$ openssl pkcs8 -topk8 -inform PEM -outform PEM -in key.pem -out unencrypted_key.pem -nocrypt
$ keytool -import -alias sirs -keystore "/etc/ssl/certs/java/cacerts" -file cert.pem
```

Once you run the commands above, you should be ready to move on to the next section.

### Installing

You need to first clone the project to your local environment:
```shell
$ git clone git@github.com:gui7ban/SIRS.git
```

After this, change your working directory to `SIRS/`, which was just created:
```shell 
$ cd SIRS/
```

You're now on the project's root directory. Therefore, you must install the maven dependencies and
compile the project's modules:
```shell
$ mvn clean install
```

The project is now compiled and all the dependencies are installed.
You will need to open three (or more) new terminals, in order to run the backup server, the primary server
and an instance (or more) of the client application.

It is important to outline that the backup server **must be started before** the primary one and the client
can only be started after both servers start. Let's focus on the primary server now.

You need to set 4 environment variables which will be used by the primary server, in order to connect to the database:
```shell
$ export DB_URL=jdbc:postgresql://localhost:5432/remotedocs
$ export DB_USERNAME=postgres
$ export DB_PASSWORD=<The password for this user>
$ export DB_DIR=<The path to the schema/schema.sql file>
```

Once this is done, we can start the backup server, on a new terminal:
```shell
$ cd backup
$ mvn exec:java
```

After the backup server is online, we can start the primary server, on a new terminal:
```shell
$ cd server
$ mvn exec:java
```

Finally, when both servers are online, we can start one instance (or more) of the client, on a new terminal (or more):
```shell
$ cd client
$ mvn exec:java
```

### Testing

Explain how to run the automated tests for this system.

Give users explicit instructions on how to run all necessary tests.
Explain the libraries, such as JUnit, used for testing your software and supply all necessary commands.

Explain what these tests test and why

```
Give an example command
```

## Demo

Give a tour of the best features of the application.
Add screenshots when relevant.

## Deployment

In order to host the system in a live environment, there are some adjustments to be made.

### Primary Server
Change the environment variables with the details of the production environment:
- Location of the database (host and port);
- Username and password to access the database.

Furthermore, the primary server's pom.xml file must be changed. The `backupServerPath` property must be
set to the path of the live backup server (host and port) and the `port` property must be changed to the port where the
primary server will run on.

### Backup Server
The backup server's pom.xml file must be changed as well. The `port` property must be changed to the port where the
backup server will run on.

### Client
The client's pom.xml file must be changed as well, accordingly. The `server.host` and `server.port` properties
must be changed to the ones where the primary server is running on.

## Additional Information

### Authors

* **Guilherme Nunes** - [Gui7Ban](https://github.com/gui7ban)
* **Pedro Maximino** - [PMax5](https://github.com/PMax5)
* **Larissa Tomaz** - [Larissa-Tomaz](https://github.com/Larissa-Tomaz)

### License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

### Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.