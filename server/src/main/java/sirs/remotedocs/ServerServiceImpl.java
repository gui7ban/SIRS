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
	public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
		try {
			server.login(request.getName(), request.getPassword());
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
		
	}
	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
		try {
			server.register(request.getName(), request.getPassword());
		}
		catch (RemoteDocsException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}
}