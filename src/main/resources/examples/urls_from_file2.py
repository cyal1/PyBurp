

def respCallback(requestResponse):
    print(requestResponse.request().url(), requestResponse.response().statusCode())


pool = RequestPool(20)


ip = "127.0.0.1"

for url in open('/tmp/urls.txt'):
    pool.sendRequest(makeRequest(url).withHeader("X-Forwarded-For", ip). \
                     withHeader("X-Originating-IP", ip).withHeader("X-Remote-IP", ip). \
                     withHeader("X-Remote-Addr", ip).withHeader("X-Real-IP", ip). \
                     withHeader("X-Forwarded-Host", ip).withHeader("X-Client-IP", ip).withHeader("X-Host", ip), respCallback)


def finish():
    pool.shutdown()
