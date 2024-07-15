import time

def sign(s):
    return base64encode(s)


def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/post")

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html

# curl -H "X-Sign: a2V5PXZhbHVlMTcyMDkxNjA0OA==" -d "a=b"  -x http://127.0.0.1:8080/ "https://httpbin.org/post"
def handleRequest(request, annotations):
    return request.withHeader("X-Sign", sign(request.bodyToString() + str(time.time()))), annotations


def handleProxyRequest(request, annotations):
    return request, annotations

def handleResponse(response, annotations):
    return response, annotations


def handleProxyResponse(response, annotations):
    return response, annotations

