import hashlib

def getHeader(headers, key, index=0):
    for header in headers:
        if header.name().lower() == key.lower():
            return header.value()
    return ''


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
def handleProxyRequest(request, annotations):
    cookie = getHeader(request.headers(), "cookie")
    sign = hashlib.md5(request.bodyToString() + request.path() + cookie + "salt").hexdigest()
    request = request.withHeader("X-sign", sign)
    return request, annotations
