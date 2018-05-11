package cz.airbank.grpc.client;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import cz.airbank.grpc.Demo;
import cz.airbank.grpc.TestServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 2016/11/8
 */
@Service
public class GrpcClientService {


    @GrpcClient("local-grpc-server")
    private ManagedChannel channel;


    public String sendMessage(String text) {
//        ManagedChannel channel= NettyChannelBuilder.forAddress("localhost", 30390).usePlaintext().build();

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
