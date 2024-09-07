"""

                  handleProxyRequest                         handleRequest
      client   ----------------------->    BurpSuit    ----------------------->  server
     (browser) <-----------------------                <-----------------------
                  handleProxyResponse                        handleResponse

Github: https://github.com/cyal1/PyBurp
Cooperation with Frida: https://youtu.be/zfvNqd5VmY0
Cooperation with Chrome: https://youtu.be/FRCnZ8a7UGI

"""


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")
    urls.add("http")  # for all url


# request  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# response  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
# annotations  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html


def handleProxyRequest(request, annotations):
    return request.withParameter(urlParameter("pyburp", "brower2burp")), annotations


# You can view the actual requests to the server in the Logger.
def handleRequest(request, annotations):
    return request.withParameter(urlParameter("pyburp", "burp2server")), annotations


def handleResponse(response, annotations):
    return response.withAddedHeader("PyBurp", "https://github.com/cyal1/PyBurp"), annotations


def handleProxyResponse(response, annotations):
    return response.withRemovedHeader("PyBurp"), annotations

