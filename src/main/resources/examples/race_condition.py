request_list = []

for i in range(30):
    request_list.append(makeRequest("https://www.example.com/")
                        .withMethod("POST")
                        .withHeader("Cookie","a=b;")
                        .withHeader("Content-Type","application/x-www-form-urlencoded")
                        .withBody("a=admin%d@example.com" % i))


@run_in_thread
def race_condition():
    """
        AUTO                Use the HTTP protocol specified by the server
        HTTP_1              Use HTTP 1 protocol for the connection. Will error if server is HTTP 2 only.
        HTTP_2              Use HTTP 2 protocol for the connection. Will error if server is HTTP 1 only.
        HTTP_2_IGNORE_ALPN  Force HTTP 2 and ignore ALPN. Will not error if server is HTTP 1 only.
    """
    httpRequestResponses = sendRequests(request_list, HttpMode.AUTO)

    for httpReqResp in httpRequestResponses:
        resp = httpReqResp.response()
        # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
        if resp is None:
            print("reqeust error")
            continue
        print(resp.statusCode(), resp.body().length())


race_condition()

