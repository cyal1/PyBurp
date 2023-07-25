
"""
    CLASS
"""
    # RequestPool
    pool = RequestPool(10)
    pool.run(function, *args, **kwargs)
    pool.shutdown()
    
    # Grpc
    server = Grpc("localhost", 30051)
    server.callFunc('remoteFuncName', *args)
    server.shutdown()


"""
    INSTANCE OF CLASS
"""
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html
    HttpRequestResponse requestResponse

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
    HttpRequest request

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
    HttpResponse response

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html
    Annotations annotations

    # Provide when registering a context menu of type EDIT_REQUEST.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html
    MessageEditorHttpRequestResponse  editor

    # Various data conversion and querying features.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
    Utilities Utils

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html#http()
    burp.api.montoya.http.Http http

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html#proxy()
    burp.api.montoya.proxy.Proxy proxy

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html
    # Only provide for repeater context menu
    MessageEditorHttpRequestResponse MessageEditor


"""
    FUNCTIONS
"""
    makeRequest(url: str) -> HttpRequest
    sendReqeust() # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/Http.html
    urlencode()
    urldecode()
    base64encode()
    base64decode()
    randomstring(length=8)

    # Create a new ByteArray from the provided.
    # Same to the `ByteArray byteArray()`.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(byte...)
    bytearray()

    # Create a new Instance of HttpParameter with the specified type.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameter.html
    parameter()
    urlparameter()
    bodyparameter()
    cookieparameter()
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/Proxy.html#history()
    # get all items in the Proxy HTTP history.
    history()
    # Create an issue. Same to auditIssue(*args)
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/issues/AuditIssue.html
    addIssue()

    # Create a list with the added markers. Used as the argument for withResponseMarkers and withRequestMarkers.
    getResponseHighlights(requestResponse: HttpRequestResponse, highlightString: str)


"""
    
              handleProxyRequest                         handleRequest
     client   -----------------------> BurpSuit proxy ----------------------->  server
              <-----------------------                <-----------------------
              handleProxyResponse                        handleResponse

"""


def urlPrefixAllowed(urls):
    """
    Match requests based on URL prefixes and only work on those prefixes.

    :param (ArrayList<String>) urls: URL prefix.
    :return:
    """
    pass


def handleRequest(request, annotations):
    """
    Handle requests between BurpSuite proxy and the Server.

    :param (HttpRequest) request: Burp HTTP request able to retrieve and modify details of an HTTP request.
    :param (Annotations) annotations: Annotations stored with requests and responses in Burp Suite.
    :return: request, annotations
    """
    return request, annotations


def handleProxyRequest(request, annotations):
    """
    Handle requests between Client and the BurpSuite proxy.

    :param (HttpRequest) request: Burp HTTP request able to retrieve and modify details of an HTTP request.
    :param (Annotations) annotations: Annotations stored with requests and responses in Burp Suite.
    :return: request, annotations
    """
    return request, annotations


def handleResponse(response, annotations):
    """
    Handle responses from the Server to BurpSuite.

    :param (HttpResponse) response: Burp HTTP response able to retrieve and modify details about an HTTP response.
    :param (Annotations) annotations: Annotations stored with requests and responses in Burp Suite.
    :return: response, annotations
    """
    return response, annotations


def handleProxyResponse(response, annotations):
    """
    Handle responses from the BurpSuite to Client.

    :param (HttpResponse) response: Burp HTTP response able to retrieve and modify details about an HTTP response.
    :param (Annotations) annotations: Annotations stored with requests and responses in Burp Suite.
    :return: response, annotations
    """
    return response, annotations


def registerContextMenu(menus):
    """
    To register a custom context menu.

    :param menus: Using the `register(String menuItemName, PyFunction functionName, MenuType menuType)` method to register a custom context menu.
                  The menu types include CARET, SELECTED_TEXT, REQUEST, and REQUEST_RESPONSE.
                  For example:  menus.register("Base64 Encode", base64Encode, MenuType.SELECTED_TEXT)
    :return:
    """
    pass


def passiveScan(baseRequestResponse):
    """
    The BurpSuite invokes this method for each base request / response that is passively audited.
    Note: Extensions should only analyze the HTTP messages provided during a passive audit, and should not make any new HTTP requests of their own.

    :param (HttpRequestResponse) baseRequestResponse: A coupling of HttpRequest and HttpResponse.
    :return:
    """
    pass


def activeScan(baseRequestResponse, auditInsertionPoint):
    """
    The BurpSuite invokes this method for each insertion point that is actively audited.
    Extensions may issue HTTP requests as required to carry out an active audit, and should use the AuditInsertionPoint object provided to build requests for particular payloads.
    Note: Scan checks should submit raw non-encoded payloads to insertion points, and the insertion point has responsibility for performing any data encoding that is necessary given the nature and location of the insertion point.

    :param (HttpRequestResponse) baseRequestResponse: A coupling of HttpRequest and HttpResponse.
    :param (AuditInsertionPoint) auditInsertionPoint: Define an insertion point for use by active Scan checks
    :return:
    """
    pass


def finish():
    """
    Tasks performed upon stopping (clicking "Stop" button).

    :return:
    """
    pass


def uploading():
    """
    This method is invoked when the extension is unloaded.

    :return:
    """
    pass