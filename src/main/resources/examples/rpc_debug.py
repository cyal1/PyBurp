
# rpc example server
# https://github.com/cyal1/pyburpRPC/tree/main/examples
# https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values
# https://youtu.be/zfvNqd5VmY0
# https://youtu.be/FRCnZ8a7UGI


client = rpc("localhost", 30051)

result = client.callFunc('test')

print(result, type(result))
print(bytearray(result))


def finish():
    client.shutdown()

