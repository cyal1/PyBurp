# Server code
# Server https://github.com/cyal1/pyburpRPC/tree/main/python
# https://youtu.be/FRCnZ8a7UGI
import json


server = rpc("localhost", 30051)

print(server.callFunc('encrypt', "1234"))


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    json_obj = json.loads(request.bodyToString())
    json_obj["password"] = server.callFunc('encrypt', json_obj["password"])
    return request.withBody(json.dumps(json_obj)), annotations


def finish():
    server.shutdown()

