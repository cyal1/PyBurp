
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
# Enum Class HttpParameterType: BODY, COOKIE, JSON, MULTIPART_ATTRIBUTE, URL, XML, XML_ATTRIBUTE

def encrypt(s):
    return base64encode(s)

def decrypt(s):
    return base64decode(s).toString()


def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/")

#ptype = HttpParameterType.BODY
# curl -d 'ec=aGVsbG8g' -x http://127.0.0.1:8080/ https://httpbin.org/post # HttpParameterType.BODY

ptype = HttpParameterType.URL
# curl -x http://127.0.0.1:8080/ "https://httpbin.org/get?ec=aGVsbG8g" # HttpParameterType.URL

def handleRequest(request, annotations):
    pname = "ec"
    #    ptype = HttpParameterType.URL
    pvalue = request.parameterValue(pname, ptype) # decrypt by handleProxyRequest, need be encrypted and send to server
    pvalue += " world"
    return request.withUpdatedParameters(parameter(pname, encrypt(pvalue), ptype)), annotations


def handleProxyRequest(request, annotations):
    pname = "ec"
    #    ptype = HttpParameterType.URL
    pvalue = request.parameterValue(pname, ptype)
    return request.withUpdatedParameters(parameter(pname, decrypt(pvalue), ptype)), annotations


def handleResponse(response, annotations):
    return response, annotations


def handleProxyResponse(response, annotations):
    return response, annotations

