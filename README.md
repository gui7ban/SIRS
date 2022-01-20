# SIRS

### Enable TLS between Client and Server

In order to establish secure sessions between Clients and the Server, it is necessary to use TLS Protocol. For that,
a TLS certificate must be created, along with a private key.

- Create the TLS certificate with the following command:
`openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365`
  
- Create a file with the unencrypted private key, so that it can be used by the server:
`openssl pkcs8 -topk8 -inform PEM -outform PEM -in key.pem -out unencrypted_key.pem -nocrypt`

- Finally, we must add the TLS certificate (`cert.pem` file) to java's cacerts keystore:
  `keytool -import -alias sirs -keystore "/etc/ssl/certs/java/cacerts" -file cert.pem`

- To establish a connection with the database, it is necessary to create env variables:
  `export DB_URL=jdbc:postgresql://localhost:5432/SIRS`
  `export DB_USERNAME=postgres`
  `export DB_PASSWORD=SIRS2022`
  `export DB_DIR=~/Transferências/SIRS/SIRS_Project/schema/schema.sql`