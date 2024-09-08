import json

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html

def encrypt(s):
    return base64encode(s)

def decrypt(s):
    return base64decode(s).toString()


def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/post")

#  curl --json '{ "ec": "aGVsbG8g" }' -x http://127.0.0.1:8080/ "https://httpbin.org/post" # HttpParameterType.JSON

def handleRequest(request, annotations):
    json_body = json.loads(request.bodyToString())
    pname = "ec"
    ec = json_body.get(pname) + "world" # decrypt by handleProxyRequest
    json_body[pname] = encrypt(ec)
    return request.withBody(json.dumps(json_body)), annotations


def handleProxyRequest(request, annotations):
    json_body = json.loads(request.bodyToString())
    pname = "ec"
    json_body[pname] =decrypt(json_body.get(pname))
    return request.withBody(json.dumps(json_body)), annotations


def handleResponse(response, annotations):
    json_body = json.loads(response.bodyToString())
    data = json.loads(json_body["data"])
    data["ec"] = decrypt(data["ec"])
    json_body["data"] = json.dumps(data)
    return response.withBody(json.dumps(json_body)), annotations


def handleProxyResponse(response, annotations):
    json_body = json.loads(response.bodyToString())
    data = json.loads(json_body["data"])
    data["ec"] = encrypt(data["ec"])
    json_body["data"] = json.dumps(data)
    return response.withBody(json.dumps(json_body)), annotations

