
# grpc example server
# https://github.com/cyal1/BcryptRpcServer/blob/main/examples/server_frida.py
# https://youtu.be/zfvNqd5VmY0


server = Grpc("localhost",30051)

result = server.callFunc('test',"arg1",2,"arg3",1,1)

# https://protobuf.dev/reference/java/api-docs/com/google/protobuf/ByteString
print(dir(result))

result=result.toStringUtf8().encode()

print(result, type(result))

result = server.callFunc('test',"arg1",2,"arg3",1).encode()
print(result, type(result))
