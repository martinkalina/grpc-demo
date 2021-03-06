package cz.airbank.grpc.client;

import java.util.Iterator;

import org.springframework.stereotype.Service;

import cz.airbank.grpc.Demo;
import cz.airbank.grpc.TestServiceGrpc;
import cz.airbank.grpc.common.Constant;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;


@Service
public class GrpcClientService {


    @GrpcClient(value = "local-grpc-server", interceptors = Interceptor.class)
    private Channel channel;


    public String sendMessage(String text) {
        TestServiceGrpc.TestServiceBlockingStub stub = TestServiceGrpc.newBlockingStub(channel);
        Demo.TestResponse response = stub.test(Demo.TestRequest.newBuilder().setText(text).build());
        return response.getText();
    }


    public Iterator<Demo.TestResponse> sendMessageServerStream(String text) {
        TestServiceGrpc.TestServiceBlockingStub stub = TestServiceGrpc.newBlockingStub(channel);
        Iterator<Demo.TestResponse> response = stub.testServerStream(Demo.TestRequest.newBuilder().setText(text).build());
        return response;
    }

    public StreamObserver<Demo.TestRequest> sendMessageClientStream(StreamObserver<Demo.TestResponse> text) {
        TestServiceGrpc.TestServiceStub stub = TestServiceGrpc.newStub(channel);
        return stub.testClientStream(text);

    }

    public StreamObserver<Demo.TestRequest> sendBidiStream(StreamObserver<Demo.TestResponse> responseObserver){
        TestServiceGrpc.TestServiceStub stub = TestServiceGrpc.newStub(channel);
        return stub.testBidi(responseObserver);

    }

    public static class Interceptor implements ClientInterceptor{
        private final ClientInterceptor delegate ;
        public Interceptor() {
            Metadata md = new Metadata();
            md.put(Constant.SECRET_CTX_KEY, Constant.SECRET);
            delegate = MetadataUtils.newAttachHeadersInterceptor(md);
        }

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            return delegate.interceptCall(method, callOptions, next);
        }
    }

}
