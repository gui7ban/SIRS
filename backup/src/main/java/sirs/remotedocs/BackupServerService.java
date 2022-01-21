package sirs.remotedocs;

import sirs.remotedocs.backupgrpc.RemoteDocsBackupGrpc;

public class BackupServerService extends RemoteDocsBackupGrpc.RemoteDocsBackupImplBase {

    private final BackupServer backupServer = new BackupServer();

}
