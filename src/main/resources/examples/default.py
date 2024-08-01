"""

                  handleProxyRequest                         handleRequest
      client   ----------------------->    BurpSuit    ----------------------->  server
     (browser) <-----------------------                <-----------------------
                  handleProxyResponse                        handleResponse

Github: https://github.com/cyal1/BcryptMontoya
Cooperation with Frida: https://youtu.be/zfvNqd5VmY0
Cooperation with Chrome: https://youtu.be/g8cT4YJwGM4

"""


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")
    urls.add("http")  # for all url


# request  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# response  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
# annotations  https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html


def handleProxyRequest(request, annotations):
    return request.withParameter(urlParameter("motoya","brower2burp")), annotations


# You can view the actual requests to the server in the Logger.
def handleRequest(request, annotations):
    return request.withParameter(urlParameter("motoya","burp2server")), annotations


def handleResponse(response, annotations):
    return response.withAddedHeader("BcryptMontoya", "https://github.com/cyal1/BcryptMontoya"), annotations


def handleProxyResponse(response, annotations):
    return response.withRemovedHeader("BcryptMontoya"), annotations

