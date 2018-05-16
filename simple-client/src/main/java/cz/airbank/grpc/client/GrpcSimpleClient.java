package cz.airbank.grpc.client;

import java.util.Iterator;

import cz.airbank.grpc.Demo;
import cz.airbank.grpc.TestServiceGrpc;
import cz.airbank.grpc.common.Constant;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

public class GrpcSimpleClient {

    private final ManagedChannel channel ;

    public GrpcSimpleClient() {
        Metadata md = new Metadata();
        md.put(Constant.SECRET_CTX_KEY, Constant.SECRET);
        channel = NettyChannelBuilder
                .forAddress("localhost", 9898)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(md))
                .usePlaintext()
                .build();
    }


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


}
