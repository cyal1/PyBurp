"""

                  handleProxyRequest                         handleRequest
      client   -----------------------> BurpSuit proxy ----------------------->  server
     (browser) <-----------------------                <-----------------------
                  handleProxyResponse                        handleResponse
"""

def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    return request, annotations


def handleProxyRequest(request, annotations):
    for param in request.parameters():
        if param.value().startswith("http"):
            annotations.setHighlightColor(HighlightColor.RED)
            annotations.setNotes("maybe ssrf")
            break
    return request, annotations


def handleResponse(response, annotations):
    return response.withAddedHeader("BcryptMontoya","https://github.com/cyal1/BcryptMontoya"), annotations


def secret_disclosure_annotations(annotations):
    return annotations.withHighlightColor(HighlightColor.BLUE).withNotes("secret")


def handleProxyResponse(response, annotations):
    if "secretKey" in response.bodyToString():
        annotations = secret_disclosure_annotations(annotations)
    return response, annotations


def registerContextMenu(menus):
    pass


def finish():
    pass
