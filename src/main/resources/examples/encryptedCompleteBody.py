
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html

def encrypt(s):
    return base64encode(s)

def decrypt(s):
    return base64decode(s)


def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/base64/U0ZSVVVFSkpUaUJwY3lCaGQyVnpiMjFs")

# curl -XGET -d "aGVsbG8g" -x http://127.0.0.1:8080/ "https://httpbin.org/base64/U0ZSVVVFSkpUaUJwY3lCaGQyVnpiMjFs"

def handleRequest(request, annotations):
    print(request.bodyToString())
    body = request.bodyToString() + "world"
    return request.withBody(encrypt(body)), annotations


def handleProxyRequest(request, annotations):
    return request.withBody(decrypt(request.bodyToString())), annotations


def handleResponse(response, annotations):
    return response.withBody(decrypt(response.bodyToString())), annotations


def handleProxyResponse(response, annotations):
    return response.withBody(encrypt(response.bodyToString())), annotations # encrypt body to browser

