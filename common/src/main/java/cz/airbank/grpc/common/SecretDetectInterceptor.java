package cz.airbank.grpc.common;

import java.util.Objects;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class SecretDetectInterceptor implements ServerInterceptor {

    private static final ServerCall.Listener NOOP_LISTENER = new ServerCall.Listener() {
    };

    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String userId = headers.get(Constant.SECRET_CTX_KEY);
        if (!Objects.equals(userId, Constant.SECRET)){
            call.close(Status.UNAUTHENTICATED.withDescription("Not Authorized"), headers);
            return NOOP_LISTENER;
        }
        return Contexts.interceptCall(Context.current(), call, headers, next);
    }
}
