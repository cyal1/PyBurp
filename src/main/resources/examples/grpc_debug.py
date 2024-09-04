
# grpc example server
# https://github.com/cyal1/BcryptRpcServer/blob/main/examples/server_frida.py
# https://youtu.be/zfvNqd5VmY0
# https://youtu.be/g8cT4YJwGM4


server = Grpc("localhost", 30051)

result = server.callFunc('test')

# https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values
print(result, type(result))
print(bytearray(result))

