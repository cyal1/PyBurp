package io.github.cyal1.bcryptmontoya;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.55.1)",
    comments = "Source: burpextender.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CallFuncServiceGrpc {

  private CallFuncServiceGrpc() {}

  public static final String SERVICE_NAME = "CallFuncService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Burpextender.Request,
      Burpextender.Response> getCallFuncMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CallFunc",
      requestType = Burpextender.Request.class,
      responseType = Burpextender.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Burpextender.Request,
      Burpextender.Response> getCallFuncMethod() {
    io.grpc.MethodDescriptor<Burpextender.Request, Burpextender.Response> getCallFuncMethod;
    if ((getCallFuncMethod = CallFuncServiceGrpc.getCallFuncMethod) == null) {
      synchronized (CallFuncServiceGrpc.class) {
        if ((getCallFuncMethod = CallFuncServiceGrpc.getCallFuncMethod) == null) {
          CallFuncServiceGrpc.getCallFuncMethod = getCallFuncMethod =
              io.grpc.MethodDescriptor.<Burpextender.Request, Burpextender.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CallFunc"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Burpextender.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Burpextender.Response.getDefaultInstance()))
              .setSchemaDescriptor(new CallFuncServiceMethodDescriptorSupplier("CallFunc"))
              .build();
        }
      }
    }
    return getCallFuncMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CallFuncServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceStub>() {
        @Override
        public CallFuncServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallFuncServiceStub(channel, callOptions);
        }
      };
    return CallFuncServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CallFuncServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceBlockingStub>() {
        @Override
        public CallFuncServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallFuncServiceBlockingStub(channel, callOptions);
        }
      };
    return CallFuncServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CallFuncServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CallFuncServiceFutureStub>() {
        @Override
        public CallFuncServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CallFuncServiceFutureStub(channel, callOptions);
        }
      };
    return CallFuncServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void callFunc(Burpextender.Request request,
                          io.grpc.stub.StreamObserver<Burpextender.Response> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCallFuncMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CallFuncService.
   */
  public static abstract class CallFuncServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return CallFuncServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CallFuncService.
   */
  public static final class CallFuncServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CallFuncServiceStub> {
    private CallFuncServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CallFuncServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallFuncServiceStub(channel, callOptions);
    }

    /**
     */
    public void callFunc(Burpextender.Request request,
                         io.grpc.stub.StreamObserver<Burpextender.Response> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCallFuncMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CallFuncService.
   */
  public static final class CallFuncServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CallFuncServiceBlockingStub> {
    private CallFuncServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CallFuncServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallFuncServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public Burpextender.Response callFunc(Burpextender.Request request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCallFuncMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CallFuncService.
   */
  public static final class CallFuncServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CallFuncServiceFutureStub> {
    private CallFuncServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CallFuncServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CallFuncServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Burpextender.Response> callFunc(
        Burpextender.Request request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCallFuncMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALL_FUNC = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL_FUNC:
          serviceImpl.callFunc((Burpextender.Request) request,
              (io.grpc.stub.StreamObserver<Burpextender.Response>) responseObserver);
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

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getCallFuncMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Burpextender.Request,
              Burpextender.Response>(
                service, METHODID_CALL_FUNC)))
        .build();
  }

  private static abstract class CallFuncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CallFuncServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Burpextender.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CallFuncService");
    }
  }

  private static final class CallFuncServiceFileDescriptorSupplier
      extends CallFuncServiceBaseDescriptorSupplier {
    CallFuncServiceFileDescriptorSupplier() {}
  }

  private static final class CallFuncServiceMethodDescriptorSupplier
      extends CallFuncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CallFuncServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (CallFuncServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CallFuncServiceFileDescriptorSupplier())
              .addMethod(getCallFuncMethod())
              .build();
        }
      }
    }
    return result;
  }
}
