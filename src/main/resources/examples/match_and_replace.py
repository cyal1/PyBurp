
def fixed_content_length(request):
    if request.hasHeader("Content-Length"):
        return request.withHeader(request.header('Content-Length').name(), str(request.body().length()))
    return request


MATCH_STRING = 'randomplz'


def handleRequest(request, annotations):
    req_raw = request.toString()
    if MATCH_STRING not in req_raw:
        return request, annotations
    return fixed_content_length(httpRequest(request.httpService(),req_raw.replace(MATCH_STRING,randomstring(8)))), annotations

