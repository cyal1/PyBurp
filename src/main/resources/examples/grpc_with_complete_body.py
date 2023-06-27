# Server code
# https://github.com/cyal1/BcryptRpcServer/tree/main/python
# https://protobuf.dev/reference/java/api-docs/com/google/protobuf/ByteString
server = Grpc("localhost", 30051)

def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    result = server.callFunc('encrypt', request.bodyToString())
    return request.withBody(result), annotations


def handleProxyRequest(request, annotations):
    result = server.callFunc('decrypt', request.bodyToString())
    return request.withBody(result), annotations


def handleResponse(response, annotations):
    result = server.callFunc('decrypt', response.bodyToString())
    return response.withBody(result), annotations


def handleProxyResponse(response, annotations):
    result = server.callFunc('encrypt', response.bodyToString())
    return response.withBody(result), annotations


def finish():
    server.shutdown()

# register custom context menu
def encrypt(selectText):
    return server.callFunc('encrypt', selectText)

def decrypt(selectText):
    return server.callFunc('decrypt', selectText)

def registerContextMenu(menus):
    menus.register("grpc encrypt", encrypt, MenuType.SELECTED_TEXT)
    menus.register("grpc decrypt", decrypt, MenuType.SELECTED_TEXT)
