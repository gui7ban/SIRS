package sirs.remotedocs;
import io.grpc.stub.StreamObserver;

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
			server.login(request.getUsername(), request.getPassword());
			responseObserver.onNext(LoginResponse.newBuilder().build());
			responseObserver.onCompleted();
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
		
	}

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
			String fileId = server.createFile(request.getName(), request.getUsername(), request.getToken());
			responseObserver.onNext(CreateFileResponse.newBuilder().setId(fileId).build());
			responseObserver.onCompleted();
		} catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}
}
