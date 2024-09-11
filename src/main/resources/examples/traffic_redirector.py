# https://github.com/PortSwigger/burp-extensions-montoya-api-examples/tree/main/trafficredirector

# host redirect
# test url: https://www.google.com/
host_from = "www.google.com"
host_to = "www.example.com"

def handleRequest(request, annotations):
    service = request.httpService()
    if service.host() == host_from:
        update_service_req = request.withService(httpService(host_to, service.port(), service.secure()))
        update_host_header_req = update_service_req.withUpdatedHeader("Host", host_to)
        return update_host_header_req, annotations
    return request, annotations


# path redirect
# test url: https://httpbin.org/static/favicon.ico
"""

def handleRequest(request, annotations):
    service = request.httpService()
    if service.host() == "httpbin.org" and request.path().startswith("/img/"):
        return request.withPath(request.path().replace('/img/','/static/', 1)), annotations
    return request, annotations

"""
