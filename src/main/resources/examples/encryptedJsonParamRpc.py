# Server code
# Server https://github.com/cyal1/pyburpRPC/tree/main/python
# You can view the actual requests to the server in the Logger.

import json


client = rpc("localhost", 30051)

print(client.callFunc('encrypt', "1234"))


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    json_obj = json.loads(request.bodyToString())
    json_obj["password"] = client.callFunc('encrypt', json_obj["password"])
    return request.withBody(json.dumps(json_obj)), annotations


def finish():
    client.shutdown()

