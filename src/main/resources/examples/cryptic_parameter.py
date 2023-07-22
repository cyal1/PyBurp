import hashlib
import json


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/params/HttpParameterType.html
def getParamByName(params, key, locate=HttpParameterType.URL, index=0):  # do not forget change locate
    for param in params:
        if param.name().lower() == key.lower() and param.type() == locate:
            return param.value()
    return ''


def getHeader(headers, key, index=0):
    for header in headers:
        if header.name().lower() == key.lower():
            return header.value()
    return ''


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


"""

                  handleProxyRequest                         handleRequest
      client   -----------------------> BurpSuit proxy ----------------------->  server
     (browser) <-----------------------                <-----------------------
                  handleProxyResponse                        handleResponse
"""


# Add sign header, to check if the X-sign header has been successfully added, you can view the Logger tab.
def handleRequest(request, annotations):
    cookie = getHeader(request.headers(), "cookie")
    sign = hashlib.md5(request.bodyToString() + request.path() + cookie + "salt").hexdigest()
    request = request.withHeader("X-sign", sign)
    return request, annotations


def handleProxyRequest(request, annotations):
    params = request.parameters()
    token = getParamByName(params, "token")
    return request.withUpdatedParameters(parameter("token", base64encode(token), HttpParameterType.URL)), annotations


def handleResponse(response, annotations):
    try:
        json_object = json.loads(response.bodyToString())
        print(json_object['username'])
        json_object['username'] = "new username"
        print(json_object)
        return response.withBody(json.dumps(json_object))
    except Exception as e:
        print(e)
    return response, annotations

