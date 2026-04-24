import json

class OpenAIClient:
    def __init__(self, api_key):
        self.api_key = api_key
        self.base_url = "https://dashscope.aliyuncs.com/compatible-mode/v1"

    def _make_request(self, endpoint, data):
        url = "{}/{}".format(self.base_url, endpoint)
        headers = {
            "Content-Type": "application/json",
            "Authorization": "Bearer {}".format(self.api_key)
        }
        

        req = httpRequestFromUrl(url).withMethod("POST").withHeader("Content-Type", "application/json").withHeader("Authorization", "Bearer {}".format(self.api_key)).withBody(json.dumps(data))
        
        try:
            reqResp = sendRequest(req)
            return reqResp.response().bodyToString()
        except Exception as e:
            return json.dumps({"error": e})

    def complete(self, prompt, model="qwen-max", max_tokens=100):
        endpoint = "completions"
        data = {
            "model": model,
            "prompt": prompt,
            "max_tokens": max_tokens
        }
        return self._make_request(endpoint, data)


if __name__ == "__main__":

    client = OpenAIClient("DASHSCOPE_API_KEY")
    response = client.complete("Python is ")
    print(json.dumps(json.loads(response.decode("utf8")), ensure_ascii=False, indent=2))


