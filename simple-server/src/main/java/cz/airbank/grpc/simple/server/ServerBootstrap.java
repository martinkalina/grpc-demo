package cz.airbank.grpc.simple.server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.airbank.grpc.common.SecretDetectInterceptor;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

public class ServerBootstrap {

    public static void main(String ... args) throws IOException, InterruptedException {
        new ServerBootstrap().run();

    }



    private void run() throws IOException, InterruptedException {
        Server server  = NettyServerBuilder
                .forPort(9898)
                .addService(new DemoServerService())
                .intercept(new SecretDetectInterceptor())
                .keepAliveTime(1000, TimeUnit.MILLISECONDS)
                .build()
                .start();
        Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);
        logger.info("Started GRPC server at port {}, services {}.", server.getPort(), server.getServices());
        server.awaitTermination();
    }

}
