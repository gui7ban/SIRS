package sirs.remotedocs;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

import sirs.remotedocs.domain.FileDetails;
import sirs.remotedocs.domain.Server;
import sirs.remotedocs.domain.exception.RemoteDocsException;
import sirs.remotedocs.grpc.Contract.*;
import sirs.remotedocs.grpc.RemoteDocsGrpc;

import static io.grpc.Status.INVALID_ARGUMENT;

public class ServerServiceImpl extends RemoteDocsGrpc.RemoteDocsImplBase
{
	private final Server server = new Server();


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
			// TODO: Return files list, one for own files and another one for shared files.
			String accessToken = server.login(request.getUsername(), request.getPassword());
			responseObserver.onNext(LoginResponse.newBuilder().setToken(accessToken).build());
			responseObserver.onCompleted();
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
		
	}

	// TODO: Implement logout in GRPC.

	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
		try {
			server.register(request.getUsername(), request.getPassword());
			responseObserver.onNext(RegisterResponse.newBuilder().build());
			responseObserver.onCompleted();
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void createFile(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
		try {
			int fileId = server.createFile(request.getName(), request.getUsername(), request.getToken());
			responseObserver.onNext(CreateFileResponse.newBuilder().setId(fileId).build());
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

			DownloadResponse response = DownloadResponse
					.newBuilder()
					.setContent(ByteString.copyFrom(fileDetails.getContent()))
					.setKey(fileDetails.getSharedKey())
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
}
