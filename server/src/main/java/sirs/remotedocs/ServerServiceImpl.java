package sirs.remotedocs;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;

import sirs.remotedocs.domain.FileDetails;
import sirs.remotedocs.domain.Server;
import sirs.remotedocs.domain.User;
import sirs.remotedocs.domain.exception.RemoteDocsException;
import sirs.remotedocs.grpc.Contract.*;
import sirs.remotedocs.grpc.RemoteDocsGrpc;

import static io.grpc.Status.INVALID_ARGUMENT;

import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

public class ServerServiceImpl extends RemoteDocsGrpc.RemoteDocsImplBase
{
	private final Server server;

	public ServerServiceImpl(String backupServerPath) {
		this.server = new Server(backupServerPath);
	}

	@Override
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
			PingResponse response = PingResponse.newBuilder().
					setOutputText(server.ping()).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
	}

	@Override
	public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
		try {
			String username = request.getUsername();
			String accessToken = server.login(username, request.getPassword());
			List<FileDetails> listOfDocuments = server.getListDocuments(username, null);
			User user = server.getUser(username);
			LoginResponse.Builder builder = LoginResponse.newBuilder()
			.setToken(accessToken)
			.setSalt(ByteString.copyFrom(Base64.getDecoder().decode(user.getSalt())))
			.setPublicKey(ByteString.copyFrom(Base64.getDecoder().decode(user.getPublicKey())))
			.setPrivateKey(ByteString.copyFrom(Base64.getDecoder().decode(user.getPrivateKey())));
			

			for(FileDetails document: listOfDocuments) {
				DocumentInfo docGrpc = DocumentInfo
						.newBuilder()
						.setId(document.getId())
						.setName(document.getName())
						.setRelationship(document.getPermission())
						.build();

				builder.addDocuments(docGrpc);
			}

			responseObserver.onNext(builder.build());
			responseObserver.onCompleted();
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
		
	}

	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
		try {
			String accessToken = server.register(request.getUsername(),
				request.getPassword(),
				request.getPublicKey().toByteArray(),
				request.getPrivateKey().toByteArray(),
				request.getSalt().toByteArray());
			
			responseObserver.onNext(RegisterResponse.newBuilder()
			.setToken(accessToken)
			.build());
			responseObserver.onCompleted();
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void createFile(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
		try {
			FileDetails fileDetails = server.createFile(request.getName(), 
				request.getUsername(),
				request.getToken(),
				request.getFileKey().toByteArray(),
				request.getIv().toByteArray()
				);
			Timestamp ts = Timestamp.newBuilder().setSeconds(fileDetails
						.getTimeChange()
						.atZone(ZoneId.systemDefault())
						.toEpochSecond()
				).build();
			responseObserver.onNext(CreateFileResponse.newBuilder().setId(fileDetails.getId()).setCreationTime(ts).build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void upload(UploadRequest request, StreamObserver<UploadResponse> responseObserver) {
		try {
			server.uploadFile(
					request.getId(),
					request.getContent().toByteArray(),
					request.getUsername(),
					request.getToken()
			);

			responseObserver.onNext(UploadResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void download(DownloadRequest request, StreamObserver<DownloadResponse> responseObserver) {
		try {
			FileDetails fileDetails = server.downloadFile(
					request.getId(),
					request.getUsername(),
					request.getToken()
			);
			Timestamp ts = Timestamp.newBuilder().setSeconds(fileDetails
				.getTimeChange()
				.atZone(ZoneId.systemDefault())
				.toEpochSecond()
			).build();

			DownloadResponse response = DownloadResponse
					.newBuilder()
					.setContent(ByteString.copyFrom(fileDetails.getContent()))
					.setKey(ByteString.copyFrom(Base64.getDecoder().decode(fileDetails.getSharedKey())))
					.setOwner(fileDetails.getOwner())
					.setLastUpdater(fileDetails.getLastUpdater())
					.setLastChange(ts)
					.setIv(ByteString.copyFrom(Base64.getDecoder().decode(fileDetails.getIv())))
					.build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void updateFileName(UpdateFileNameRequest request, StreamObserver<UpdateFileNameResponse> responseObserver) {
		try {
			server.updateFileName(
					request.getId(),
					request.getName(),
					request.getUsername(),
					request.getToken()
			);

			responseObserver.onNext(UpdateFileNameResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void getDocumentsList(GetDocumentsRequest request, StreamObserver<GetDocumentsResponse> responseObserver) {
		try {
			List<FileDetails> listOfDocuments = server.getListDocuments(
				request.getUsername(),
				request.getToken()
				);
			GetDocumentsResponse.Builder builder = GetDocumentsResponse.newBuilder();

			for(FileDetails document: listOfDocuments) {
				DocumentInfo docGrpc = DocumentInfo
						.newBuilder()
						.setId(document.getId())
						.setName(document.getName())
						.setRelationship(document.getPermission())
						.build();

				builder.addDocuments(docGrpc);
			}

			responseObserver.onNext(builder.build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void deleteFile(DeleteFileRequest request, StreamObserver<DeleteFileResponse> responseObserver) {
		try {
			server.deleteFile(
					request.getId(),
					request.getUsername(),
					request.getToken()
			);

			responseObserver.onNext(DeleteFileResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseObserver) {
		try {
			server.logout(
					request.getUsername(),
					request.getToken()
			);

			responseObserver.onNext(LogoutResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void docUsersList(SharedDocUsersRequest request, StreamObserver<SharedDocUsersResponse> responseObserver) {
		try {
			List<User> listOfUsers = server.getNotOwnerUsersOfDoc(
					request.getId(),
					request.getUsername(),
					request.getToken()
			);
			SharedDocUsersResponse.Builder builder = SharedDocUsersResponse.newBuilder();
			for(User user: listOfUsers) {
				UserGrpc userGrpc = UserGrpc
						.newBuilder()
						.setUsername((user.getName()))
						.setPermission(user.getPermission())
						.build();

				builder.addUsers(userGrpc);
			}

			responseObserver.onNext(builder.build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}


	@Override
	public void updatePermission(UpdatePermissionUserRequest request, StreamObserver<UpdatePermissionUserResponse> responseObserver) {
		try {
			server.updatePermission(
					request.getOwner(),
					request.getToken(),
					request.getUsername(),
					request.getId(),
					request.getPermission()

			);

			responseObserver.onNext(UpdatePermissionUserResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void addPermission(AddPermissionUserRequest request, StreamObserver<AddPermissionUserResponse> responseObserver) {
		try {
			server.addPermission(
					request.getOwner(),
					request.getToken(),
					request.getUsername(),
					request.getId(),
					request.getPermission(),
					request.getIv().toByteArray(),
					request.getFileKey().toByteArray()

			);

			responseObserver.onNext(AddPermissionUserResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void deletePermission(DeletePermissionUserRequest request, StreamObserver<DeletePermissionUserResponse> responseObserver) {
		try {
			server.deletePermission(
					request.getOwner(),
					request.getToken(),
					request.getUsername(),
					request.getId()
			);

			responseObserver.onNext(DeletePermissionUserResponse.newBuilder().build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void getPublicKey(GetPublicKeyRequest request, StreamObserver<GetPublicKeyResponse> responseObserver) {
		try {
			List<Object> info = server.getPublicKey(
									request.getOwner(),
									request.getToken(),
									request.getUsername(),
									request.getFileId()
								);
			User user = (User)info.get(0);
			FileDetails details = (FileDetails)info.get(1);


			responseObserver.onNext(GetPublicKeyResponse.newBuilder()
			.setIv(ByteString.copyFrom(Base64.getDecoder().decode(details.getIv())))
			.setFileKey(ByteString.copyFrom(Base64.getDecoder().decode(details.getSharedKey())))
			.setPublicKey(ByteString.copyFrom(Base64.getDecoder().decode(user.getPublicKey())))
			.build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}
}
