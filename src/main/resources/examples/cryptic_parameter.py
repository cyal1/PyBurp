# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameterType.html
def getParamByName(params, key, locate=HttpParameterType.URL, index=0):  # do not forget change locate
    for param in params:
        if param.name().lower() == key.lower() and param.type() == locate:
            return param.value()
    return ''


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
def handleRequest(request):
    return request


def handleProxyRequest(request):
    params = request.parameters()
    token = getParamByName(params, "token")
    return request.withUpdatedParameters(parameter("token", base64encode(token), HttpParameterType.URL))


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
def handleResponse(response):
    return response


def handleProxyResponse(response):
    return response
