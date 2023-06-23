# Server code
# https://github.com/cyal1/BcryptRpcServer/tree/main/python
# https://protobuf.dev/reference/java/api-docs/com/google/protobuf/ByteString
server = Grpc("localhost", 30051)

def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
def handleRequest(request):
    result = server.callFunc('encrypt',request.bodyToString())
    return request.withBody(result)


def handleProxyRequest(request):
    result = server.callFunc('decrypt',request.bodyToString())
    return request.withBody(result)


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
def handleResponse(response):
    result = server.callFunc('decrypt', response.bodyToString())
    return response.withBody(result)


def handleProxyResponse(response):
    result = server.callFunc('encrypt', response.bodyToString())
    return response.withBody(result)


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
