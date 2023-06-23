import json

def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
def handleRequest(request):
    return request


def handleProxyRequest(request):
    return request


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
def handleResponse(response):
    try:
        json_object = json.loads(response.bodyToString())
        print(json_object['username'])
        json_object['username'] = "new username"
        print(json_object)
        return response.withBody(json.dumps(json_object))
    except Exception as e:
        print(e)
    return response


def handleProxyResponse(response):
    return response

