
"""
    CLASS
"""
    # RequestPool
    pool = RequestPool(10)
    pool.sendRequest(String url)
    pool.sendRequest(HttpRequest request) # check response in Logger
    pool.sendRequest(String url, PyFunction callback)
    pool.sendRequest(HttpRequest request, PyFunction callback) # Handling response through callback functions. callback(HttpRequestResponse httpRequestResponse).
    pool.shutdown()
    
    # Grpc
    server = Grpc("localhost", 30051)
    server.callFunc('remoteFuncName', *args)
    server.shutdown()


"""
    INSTANCE OF CLASS
"""
    # Provide when registering a context menu of type EDIT_REQUEST.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html
    MessageEditorHttpRequestResponse  editor

    # Various data conversion and querying features
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
    Utilities Utils


"""
    FUNCTIONS
"""
    urlencode()
    urldecode()
    base64encode()
    base64decode()

    # Create a new ByteArray from the provided.
    # Same to the `ByteArray byteArray()`.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(byte...)
    bytearray()

    # Create a new Instance of HttpParameter with the specified type.
    # Same as the `static HttpParameter parameter(String name, String value, HttpParameterType type)`
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameter.html
    parameter(String name, String value, HttpParameterType type)

    # Create an issue. Same to auditIssue(*args)
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/issues/AuditIssue.html
    addIssue(*args)

    # Create a list with the added markers. Used as the argument for withResponseMarkers and withRequestMarkers.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html
    getResponseHighlights(HttpRequestResponse requestResponse, String highlightString)


"""
    
              handleProxyRequest                         handleRequest
     client   -----------------------> BurpSuit proxy ----------------------->  server
    (browser) <-----------------------                <-----------------------
              handleProxyResponse                        handleResponse

"""


def urlPrefixAllowed(urls):
    """
    Match requests based on URL prefixes and only work on those prefixes.

    Args:
        urls (ArrayList<String>): URL prefix.
    """
    pass


def handleRequest(request):
    """
     Handle requests between BurpSuite proxy and the Server.

     Args:
         request (HttpRequest): HTTP request.

     Returns:
         request (HttpRequest)
     """
    return request


def handleProxyRequest(request):
    """
     Handle requests between Client and the BurpSuite proxy.

     Args:
         request (HttpRequest): HTTP request.

     Returns:
         request (HttpRequest)
    """
    return request


def handleResponse(response):
    """
    Handle responses from the Server to BurpSuite.

    Args:
        response (HttpResponse): HTTP response.

    Returns:
        response (HttpResponse)
    """
    return response


def handleProxyResponse(response):
    """
    Handle responses from the BurpSuite to Client.

    Args:
        response (HttpResponse): HTTP response.

    Returns:
        response (HttpResponse)
    """
    return response


def registerContextMenu(menus):
    """
    To register a custom context menu.

    Args:
        menus: using the `register(String menuItemName, PyFunction functionName, MenuType menuType)` method to register a custom context menu.
               The menu types include CARET, SELECTED_TEXT, REQUEST, and EDIT_REQUEST.
               For example:  menus.register("Base64 Encode", base64Encode, MenuType.SELECTED_TEXT)
    """
    pass


def finish():
    """
    Tasks performed upon stopping (clicking "Stop" button).
    """
    pass

