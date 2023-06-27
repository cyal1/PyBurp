import json

def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleResponse(response, annotations):
    try:
        json_object = json.loads(response.bodyToString())
        print(json_object['username'])
        json_object['username'] = "new username"
        print(json_object)
        return response.withBody(json.dumps(json_object))
    except Exception as e:
        print(e)
    return response, annotations
