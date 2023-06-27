
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameterType.html
def getParamByName(params, key, locate=HttpParameterType.URL, index=0):  # do not forget change locate
    for param in params:
        if param.name().lower() == key.lower() and param.type() == locate:
            return param.value()
    return ''


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    return request, annotations


def handleProxyRequest(request, annotations):
    params = request.parameters()
    token = getParamByName(params, "token")
    return request.withUpdatedParameters(parameter("token", base64encode(token), HttpParameterType.URL)), annotations


def handleResponse(response, annotations):
    return response, annotations


def handleProxyResponse(response, annotations):
    return response, annotations
