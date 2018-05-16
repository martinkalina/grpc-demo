package cz.airbank.grpc.simple.server;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.airbank.grpc.Demo;
import cz.airbank.grpc.TestServiceGrpc;
import io.grpc.stub.StreamObserver;

public class DemoServerService extends TestServiceGrpc.TestServiceImplBase {

    private int counter;
    private static final Logger LOG = LoggerFactory.getLogger(TestServiceGrpc.class);

    @Override
    public void test(Demo.TestRequest testRequest, StreamObserver<Demo.TestResponse> responseObserver) {
        LOG.info("Received " + counter);
        String text = testRequest.getText();
        Demo.TestResponse testResponse = Demo.TestResponse.newBuilder().setText(String.format("Hello %d %s", counter++, text)).build();

        responseObserver.onNext(testResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void testServerStream(Demo.TestRequest request, StreamObserver<Demo.TestResponse> responseObserver) {
        String text = request.getText();
        for (int i = 0; i < 10000; i++) {
            Demo.TestResponse testResponse = Demo.TestResponse.newBuilder().setText(String.format("Hello %d %s", i, text)).build();
            responseObserver.onNext(testResponse);
            LoggerFactory.getLogger(DemoServerService.class).info("Send server stream msg " + i);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Demo.TestRequest> testClientStream(StreamObserver<Demo.TestResponse> responseObserver) {
        return new StreamObserver<Demo.TestRequest>() {
            int count = 0;

            @Override
            public void onNext(Demo.TestRequest testRequest) {
                LOG.info("Received " + testRequest.getText());
                count++;
            }

            @Override
            public void onError(Throwable throwable) {
                LOG.error("RPC error", throwable);
//                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Demo.TestResponse.newBuilder().setText("Hello ClientStream count = " + count).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Demo.TestRequest> testBidi(StreamObserver<Demo.TestResponse> responseObserver) {
        final Collection<String> buffer = new ArrayList<>();
        return new StreamObserver<Demo.TestRequest>() {
            @Override
            public void onNext(Demo.TestRequest testRequest) {
                //  new request from client
                String requestText = testRequest.getText();
                if (Math.random() > 0.5) {
                    send(requestText);
                } else {
                    buffer.add(requestText);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                buffer.clear();
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // client finished
                buffer.forEach(this::send);
                responseObserver.onCompleted();
            }

            private void send(String requestText) {
                Demo.TestResponse testResponse = Demo.TestResponse.newBuilder().setText("Hello " + requestText).build();
                responseObserver.onNext(testResponse);
                LoggerFactory.getLogger(DemoServerService.class).info("Send msg " + requestText);
            }
        };
    }
}
