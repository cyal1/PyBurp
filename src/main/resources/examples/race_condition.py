
raw_request = """GET /wtf/?nottime=0.000 HTTP/1.1
Host: x.psres.net
User-Agent: test

"""

request = httpRequest(httpService("https://x.psres.net/"), raw_request)

if request.body().length() != 0:
    request = request.withHeader("Content-Length", str(request.body().length()))



pool = RequestPool(10)
@run_in_pool(pool)
def race_condition():
    """
        AUTO                Use the HTTP protocol specified by the server
        HTTP_1              Use HTTP 1 protocol for the connection. Will error if server is HTTP 2 only.
        HTTP_2              Use HTTP 2 protocol for the connection. Will error if server is HTTP 1 only.
        HTTP_2_IGNORE_ALPN  Force HTTP 2 and ignore ALPN. Will not error if server is HTTP 1 only.
    """
    httpRequestResponses = sendRequests([request] * 10, HttpMode.AUTO)

    for httpReqResp in httpRequestResponses:
        resp = httpReqResp.response()
        # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
        if resp is None:
            print("reqeust error")
            continue
        print(resp.statusCode(), resp.headers())


race_condition()


def finish():
    pool.shutdown()


