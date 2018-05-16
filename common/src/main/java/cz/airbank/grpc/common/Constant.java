package cz.airbank.grpc.common;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.Metadata;

public class Constant {
    public static final Metadata.Key<String> SECRET_CTX_KEY = Metadata.Key.of("secret", ASCII_STRING_MARSHALLER);
    public static final String SECRET = "twycmpbP79SvxWSd";
}
