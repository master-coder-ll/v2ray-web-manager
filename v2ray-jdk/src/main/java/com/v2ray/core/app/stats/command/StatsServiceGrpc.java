package com.v2ray.core.app.stats.command;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.21.0)",
        comments = "Source: v2ray.com/core/app/stats/command/command.proto")
public final class StatsServiceGrpc {

    private StatsServiceGrpc() {
    }

    public static final String SERVICE_NAME = "v2ray.core.app.stats.command.StatsService";

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<GetStatsRequest,
            GetStatsResponse> getGetStatsMethod;

    @io.grpc.stub.annotations.RpcMethod(
            fullMethodName = SERVICE_NAME + '/' + "GetStats",
            requestType = GetStatsRequest.class,
            responseType = GetStatsResponse.class,
            methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<GetStatsRequest,
            GetStatsResponse> getGetStatsMethod() {
        io.grpc.MethodDescriptor<GetStatsRequest, GetStatsResponse> getGetStatsMethod;
        if ((getGetStatsMethod = StatsServiceGrpc.getGetStatsMethod) == null) {
            synchronized (StatsServiceGrpc.class) {
                if ((getGetStatsMethod = StatsServiceGrpc.getGetStatsMethod) == null) {
                    StatsServiceGrpc.getGetStatsMethod = getGetStatsMethod =
                            io.grpc.MethodDescriptor.<GetStatsRequest, GetStatsResponse>newBuilder()
                                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                                    .setFullMethodName(generateFullMethodName(
                                            "v2ray.core.app.stats.command.StatsService", "GetStats"))
                                    .setSampledToLocalTracing(true)
                                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            GetStatsRequest.getDefaultInstance()))
                                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            GetStatsResponse.getDefaultInstance()))
                                    .setSchemaDescriptor(new StatsServiceMethodDescriptorSupplier("GetStats"))
                                    .build();
                }
            }
        }
        return getGetStatsMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static StatsServiceStub newStub(io.grpc.Channel channel) {
        return new StatsServiceStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static StatsServiceBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new StatsServiceBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static StatsServiceFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new StatsServiceFutureStub(channel);
    }

    /**
     *
     */
    public static abstract class StatsServiceImplBase implements io.grpc.BindableService {

        /**
         *
         */
        public void getStats(GetStatsRequest request,
                             io.grpc.stub.StreamObserver<GetStatsResponse> responseObserver) {
            asyncUnimplementedUnaryCall(getGetStatsMethod(), responseObserver);
        }

        @Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            getGetStatsMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            GetStatsRequest,
                                            GetStatsResponse>(
                                            this, METHODID_GET_STATS)))
                    .build();
        }
    }

    /**
     *
     */
    public static final class StatsServiceStub extends io.grpc.stub.AbstractStub<StatsServiceStub> {
        private StatsServiceStub(io.grpc.Channel channel) {
            super(channel);
        }

        private StatsServiceStub(io.grpc.Channel channel,
                                 io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected StatsServiceStub build(io.grpc.Channel channel,
                                         io.grpc.CallOptions callOptions) {
            return new StatsServiceStub(channel, callOptions);
        }

        /**
         *
         */
        public void getStats(GetStatsRequest request,
                             io.grpc.stub.StreamObserver<GetStatsResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(getGetStatsMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     *
     */
    public static final class StatsServiceBlockingStub extends io.grpc.stub.AbstractStub<StatsServiceBlockingStub> {
        private StatsServiceBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private StatsServiceBlockingStub(io.grpc.Channel channel,
                                         io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected StatsServiceBlockingStub build(io.grpc.Channel channel,
                                                 io.grpc.CallOptions callOptions) {
            return new StatsServiceBlockingStub(channel, callOptions);
        }

        /**
         *
         */
        public GetStatsResponse getStats(GetStatsRequest request) {
            return blockingUnaryCall(
                    getChannel(), getGetStatsMethod(), getCallOptions(), request);
        }
    }

    /**
     *
     */
    public static final class StatsServiceFutureStub extends io.grpc.stub.AbstractStub<StatsServiceFutureStub> {
        private StatsServiceFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private StatsServiceFutureStub(io.grpc.Channel channel,
                                       io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected StatsServiceFutureStub build(io.grpc.Channel channel,
                                               io.grpc.CallOptions callOptions) {
            return new StatsServiceFutureStub(channel, callOptions);
        }

        /**
         *
         */
        public com.google.common.util.concurrent.ListenableFuture<GetStatsResponse> getStats(
                GetStatsRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(getGetStatsMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_GET_STATS = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final StatsServiceImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(StatsServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_GET_STATS:
                    serviceImpl.getStats((GetStatsRequest) request,
                            (io.grpc.stub.StreamObserver<GetStatsResponse>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static abstract class StatsServiceBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
        StatsServiceBaseDescriptorSupplier() {
        }

        @Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return Command.getDescriptor();
        }

        @Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("StatsService");
        }
    }

    private static final class StatsServiceFileDescriptorSupplier
            extends StatsServiceBaseDescriptorSupplier {
        StatsServiceFileDescriptorSupplier() {
        }
    }

    private static final class StatsServiceMethodDescriptorSupplier
            extends StatsServiceBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
        private final String methodName;

        StatsServiceMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (StatsServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new StatsServiceFileDescriptorSupplier())
                            .addMethod(getGetStatsMethod())
                            .build();
                }
            }
        }
        return result;
    }
}
