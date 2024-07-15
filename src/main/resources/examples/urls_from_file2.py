
pool = RequestPool(10)

ip = "127.0.0.1"


@run_in_pool(pool)
def fetch_url_task(url):
    requestResponse = sendRequest(httpRequestFromUrl(url).withHeader("X-Forwarded-For", "127.0.0.1"))
    print(requestResponse.request().url(), requestResponse.response().statusCode())


def finish():
    pool.shutdown()


for url in open('/tmp/urls.txt'):
    fetch_url_task(url)

