
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

    # Various data conversion and querying features.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
    Utilities Utils

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html#http()
    burp.api.montoya.http.Http http

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html#proxy()
    burp.api.montoya.proxy.Proxy proxy

    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html
    # Only provide for message editor context menu.
    MessageEditorHttpRequestResponse MessageEditor


    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/collaborator/Interaction.html
    Interaction interaction


    # Decorator
    @run_in_pool(pool: RequestPool)
    @run_in_thread


"""
    FUNCTIONS
"""
    # Build HttpRequest from url or raw http
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
    httpRequestFromUrl()
    httpRequest()

    # An HTTP service for the HttpRequest.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/HttpService.html
    httpService() -> HttpService

    # Send HTTP requests
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/Http.html
    sendReqeust()
    sendReqeusts()

    # Create annotations with requests and responses
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html
    annotations()

    urlencode()
    urldecode()
    base64encode()
    base64decode()
    randomstring(length=8)

    # Create a new ByteArray from the provided.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(byte...)
    bytearray()

    # Create a new Instance of HttpParameter with the specified type.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameter.html
    parameter()
    urlparameter()
    bodyparameter()
    cookieparameter()

    # Get all items in the Proxy HTTP history.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/Proxy.html#history()
    history()

    # Create an audit issue for a URL.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/issues/AuditIssue.html
    auditIssue() -> AuditIssue

    # Add issue to dashboard
    addIssue(auditIssue: AuditIssue)

    # Create an audit insertion point based on offsets.
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/insertionpoint/AuditInsertionPoint.html
    auditInsertionPoint() -> AuditInsertionPoint

    # Create a list with the added markers. Used as the argument for withResponseMarkers and withRequestMarkers.
    getResponseHighlights(requestResponse: HttpRequestResponse, highlightString: str) -> List<Marker>

    # Only work within message editor context menu. (MESSAGE_EDITOR menu type)
    getSelectedText(MessageEditor: MessageEditorHttpRequestResponse) -> ByteArray
    replaceSelectedText(MessageEditor: MessageEditorHttpRequestResponse, newText: str) -> HTTPRequest

    # Generate new Burp Collaborator payloads.
    getOOBCanary()

"""
    
              handleProxyRequest                          handleRequest
     client   ----------------------->    BurpSuit    ----------------------->  server
              <-----------------------                <-----------------------
              handleProxyResponse                         handleResponse

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
                  The menu types include CARET, SELECTED_TEXT, REQUEST, REQUEST_RESPONSE and MESSAGE_EDITOR.
                  For example:  menus.register("Base64 Encode", base64Encode, MenuType.SELECTED_TEXT)
    :return:
    """
    pass


def passiveScan(baseRequestResponse):
    """
    The BurpSuite invokes this method for each base request / response that is passively audited.
    Note: Extensions should only analyze the HTTP messages provided during a passive audit, and should not make any new HTTP requests of their own.

    :param (HttpRequestResponse) baseRequestResponse: A coupling of HttpRequest and HttpResponse.
    :return (AuditResult):
    """
    pass


def activeScan(baseRequestResponse, auditInsertionPoint):
    """
    The BurpSuite invokes this method for each insertion point that is actively audited.
    Extensions may issue HTTP requests as required to carry out an active audit, and should use the AuditInsertionPoint object provided to build requests for particular payloads.
    Note: Scan checks should submit raw non-encoded payloads to insertion points, and the insertion point has responsibility for performing any data encoding that is necessary given the nature and location of the insertion point.

    :param (HttpRequestResponse) baseRequestResponse: A coupling of HttpRequest and HttpResponse.
    :param (AuditInsertionPoint) auditInsertionPoint: Define an insertion point for use by active Scan checks
    :return (AuditResult):
    """
    pass


def handleInteraction(interaction):
    """
    Provides details of an interaction with the Burp Collaborator server.

    :param (Interaction) interaction:
    :return:
    """
    pass


def finish():
    """
    Tasks performed upon stopping (clicking "Stop" button).

    :return:
    """
    pass

