package cz.airbank.grpc.client;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cz.airbank.grpc.Demo;
import io.grpc.stub.StreamObserver;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GrpcClientServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcClientService.class);

    @Autowired
    private GrpcClientService clientService;


    @Test
    public void sendMessage() {
        double count = 1000;
        long ts = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            LOG.info(clientService.sendMessage("message " + i));
        }
        long d = System.currentTimeMillis() - ts;
        LOG.info("Send/received {} messages, {} ms each", count, d / count);
    }

    @Test
    public void sendServerStream() {
        long ts = System.currentTimeMillis();
        Iterator<Demo.TestResponse> iterator = clientService.sendMessageServerStream("Server Stream");
        int count = 0;
        while (iterator.hasNext()) {
            LOG.info(iterator.next().getText());
            count++;
        }
        long d = System.currentTimeMillis() - ts;
        LOG.info("Received {} messages, {} ms each", count, d / count);
    }

    private AtomicReference<Throwable> connectionError = new AtomicReference<>();

    @Test
    public void sendClientStream() throws InterruptedException {
        CountDownLatch finishedLatch = new CountDownLatch(1);
        StreamObserver<Demo.TestRequest> requestStreamObserver = clientService.sendMessageClientStream(new TestResponseStreamObserver(finishedLatch));
        sendAndBenchmark(finishedLatch, requestStreamObserver);
    }

    @Test
    public void bidiStream() throws InterruptedException {

        CountDownLatch finishedLatch = new CountDownLatch(1);
        StreamObserver<Demo.TestRequest> requestStreamObserver = clientService.sendBidiStream(new TestResponseStreamObserver(finishedLatch));
        sendAndBenchmark(finishedLatch, requestStreamObserver);
    }

    private void sendAndBenchmark(CountDownLatch finishedLatch, StreamObserver<Demo.TestRequest> requestStreamObserver) throws InterruptedException {
        double count = 10000;

        long ts = System.currentTimeMillis();
        Demo.TestRequest.Builder builder = Demo.TestRequest.newBuilder();

        for (int i = 0; i < count; i++) {
            if (connectionError.get() != null){
                LOG.warn("Error on receiving, stopping sending");
                throw new RuntimeException(connectionError.get());
            }
            Demo.TestRequest request = builder.setText("Request " + i).build();
            //client send request
            requestStreamObserver.onNext(request);
            LOG.info("Sending: " + request.getText());
//            Thread.sleep(1000);
        }
        requestStreamObserver.onCompleted(); // send finish relation

        long d = System.currentTimeMillis() - ts;
        LOG.info("Send {} messages, {} ms each", count, d / count);
        finishedLatch.await();

        long d2 = System.currentTimeMillis() - ts;
        LOG.info("Received {} messages, {} ms each", count, d2 / count);
    }

    private class TestResponseStreamObserver implements StreamObserver<Demo.TestResponse> {
        private final CountDownLatch finishedLatch;

        public TestResponseStreamObserver(CountDownLatch finishedLatch) {
            this.finishedLatch = finishedLatch;
        }

        @Override
        public void onNext(Demo.TestResponse value) {
            LOG.info("Received " + value.getText());
        }

        @Override
        public void onError(Throwable t) {
            LOG.error("Error received", t);
            connectionError.set(t);
        }

        @Override
        public void onCompleted() {
            finishedLatch.countDown();
        }
    }
}