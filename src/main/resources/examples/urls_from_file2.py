# https://github.com/jython/book/tree/master/src/chapter19
# https://github.com/jython/book/blob/master/src/chapter19/test_futures.py


pool = RequestPool(10)

ip = "127.0.0.1"


@run_in_pool(pool)
def fetch_url_task(url):
    requestResponse = sendRequest(httpRequestFromUrl(url).withHeader("X-Forwarded-For", ip). \
                                  withHeader("X-Originating-IP", ip).withHeader("X-Remote-IP", ip). \
                                  withHeader("X-Remote-Addr", ip).withHeader("X-Real-IP", ip). \
                                  withHeader("X-Forwarded-Host", ip).withHeader("X-Client-IP", ip).withHeader("X-Host",ip))
    print(requestResponse.request().url(), requestResponse.response().statusCode())


def finish():
    pool.shutdown()


for url in open('/tmp/urls.txt'):
    fetch_url_task(url)

