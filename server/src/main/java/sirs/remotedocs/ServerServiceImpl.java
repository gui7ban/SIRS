package java.sirs.remotedocs;
import io.grpc.stub.StreamObserver;

import java.sirs.remotedocs.grpc.RemoteDOCS;
import java.sirs.remotedocs.grpc.RemoteDOCS.*;
import java.sirs.remotedocs.grpc.RemoteDOCSGrpc;

import static io.grpc.Status.INVALID_ARGUMENT;

public class ServerServiceImpl extends RemoteDOCSGrpc.RemoteDOCSImplBase
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
