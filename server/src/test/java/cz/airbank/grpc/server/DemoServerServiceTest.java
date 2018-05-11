package cz.airbank.grpc.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.airbank.grpc.Demo;
import cz.airbank.grpc.TestServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

/**
 * Test service with in process server.
 */
public class DemoServerServiceTest {

    private DemoServerService service = new DemoServerService();
    private ManagedChannel channel;


    @Before
    public void setUp() throws Exception {
        String uniqueName = InProcessServerBuilder.generateName();
        InProcessServerBuilder.forName(uniqueName)
                .directExecutor() // directExecutor is fine for unit tests
                .addService(service)
                .build().start();
        channel = InProcessChannelBuilder.forName(uniqueName)
                .directExecutor()
                .build();
    }

    @Test
    public void testUnaryCall() {
        Demo.TestRequest request = Demo.TestRequest.newBuilder().setText("test").build();
        TestServiceGrpc.TestServiceBlockingStub stub = TestServiceGrpc.newBlockingStub(channel);
        Demo.TestResponse response = stub.test(request);
        Assert.assertEquals("Hello 0 test", response.getText());
        response = stub.test(request);
        Assert.assertEquals("Hello 1 test", response.getText());
    }

}