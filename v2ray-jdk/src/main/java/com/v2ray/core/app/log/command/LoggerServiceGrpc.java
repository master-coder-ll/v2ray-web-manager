package com.v2ray.core.app.log.command;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.21.0)",
    comments = "Source: v2ray.com/core/app/log/command/config.proto")
public final class LoggerServiceGrpc {

  private LoggerServiceGrpc() {}

  public static final String SERVICE_NAME = "v2ray.core.app.log.command.LoggerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<RestartLoggerRequest,
      RestartLoggerResponse> getRestartLoggerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RestartLogger",
      requestType = RestartLoggerRequest.class,
      responseType = RestartLoggerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RestartLoggerRequest,
      RestartLoggerResponse> getRestartLoggerMethod() {
    io.grpc.MethodDescriptor<RestartLoggerRequest, RestartLoggerResponse> getRestartLoggerMethod;
    if ((getRestartLoggerMethod = LoggerServiceGrpc.getRestartLoggerMethod) == null) {
      synchronized (LoggerServiceGrpc.class) {
        if ((getRestartLoggerMethod = LoggerServiceGrpc.getRestartLoggerMethod) == null) {
          LoggerServiceGrpc.getRestartLoggerMethod = getRestartLoggerMethod = 
              io.grpc.MethodDescriptor.<RestartLoggerRequest, RestartLoggerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "v2ray.core.app.log.command.LoggerService", "RestartLogger"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestartLoggerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestartLoggerResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LoggerServiceMethodDescriptorSupplier("RestartLogger"))
                  .build();
          }
        }
     }
     return getRestartLoggerMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LoggerServiceStub newStub(io.grpc.Channel channel) {
    return new LoggerServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LoggerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new LoggerServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LoggerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new LoggerServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class LoggerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void restartLogger(RestartLoggerRequest request,
                              io.grpc.stub.StreamObserver<RestartLoggerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRestartLoggerMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRestartLoggerMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                RestartLoggerRequest,
                RestartLoggerResponse>(
                  this, METHODID_RESTART_LOGGER)))
          .build();
    }
  }

  /**
   */
  public static final class LoggerServiceStub extends io.grpc.stub.AbstractStub<LoggerServiceStub> {
    private LoggerServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LoggerServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LoggerServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LoggerServiceStub(channel, callOptions);
    }

    /**
     */
    public void restartLogger(RestartLoggerRequest request,
                              io.grpc.stub.StreamObserver<RestartLoggerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestartLoggerMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LoggerServiceBlockingStub extends io.grpc.stub.AbstractStub<LoggerServiceBlockingStub> {
    private LoggerServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LoggerServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LoggerServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LoggerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public RestartLoggerResponse restartLogger(RestartLoggerRequest request) {
      return blockingUnaryCall(
          getChannel(), getRestartLoggerMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LoggerServiceFutureStub extends io.grpc.stub.AbstractStub<LoggerServiceFutureStub> {
    private LoggerServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LoggerServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LoggerServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LoggerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<RestartLoggerResponse> restartLogger(
        RestartLoggerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRestartLoggerMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RESTART_LOGGER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LoggerServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LoggerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RESTART_LOGGER:
          serviceImpl.restartLogger((RestartLoggerRequest) request,
              (io.grpc.stub.StreamObserver<RestartLoggerResponse>) responseObserver);
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

  private static abstract class LoggerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LoggerServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ConfigOuterClass.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LoggerService");
    }
  }

  private static final class LoggerServiceFileDescriptorSupplier
      extends LoggerServiceBaseDescriptorSupplier {
    LoggerServiceFileDescriptorSupplier() {}
  }

  private static final class LoggerServiceMethodDescriptorSupplier
      extends LoggerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LoggerServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (LoggerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LoggerServiceFileDescriptorSupplier())
              .addMethod(getRestartLoggerMethod())
              .build();
        }
      }
    }
    return result;
  }
}
