# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html#request()
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html
# Enum Class HttpParameterType: BODY, COOKIE, JSON, MULTIPART_ATTRIBUTE, URL, XML, XML_ATTRIBUTE

"""

In general, you should use passive scanning rather than the following approach.
Please refer to the `passive_active_scan.py` file.

"""

# curl -x http://127.0.0.1:8080/ "https://httpbin.org/get?url=https://www.google.com/"
def handleResponse(response, annotations):

    request = response.initiatingRequest()
    #    shiro = response.hasCookie("rememberMe")
    ssrf = False

    for param in request.parameters(HttpParameterType.URL):
        pvalue = param.value().strip()
        if pvalue.startswith('http'):
            ssrf = True
            break

    if ssrf is False:
        req_body = request.bodyToString()
        if '=http' in req_body or '"http' in req_body:
            ssrf = True

    if ssrf:
        # reqResp = httpRequestResponse(response.initiatingRequest(), response)
        annotations = annotations.withNotes("ssrf").withHighlightColor(HighlightColor.RED)
        # sendToOrganizer(reqResp.withAnnotations(annotations))

    return response, annotations

