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
    urls.add("https://www.example.com/api/")

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html

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

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html

def handleResponse(response):
    """
    Handle responses from the Server to BurpSuite.

    Args:
        response (HttpResponse): HTTP response.

    Returns:
        response (HttpResponse)
    """
    return response.withAddedHeader("BcryptMontoya","https://github.com/cyal1/BcryptMontoya")


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
