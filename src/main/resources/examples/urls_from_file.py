

def finish():
    pool.shutdown()


pool = RequestPool(20)

ip = "127.0.0.1"

for url in open('/tmp/urls.txt'):
    pool.sendRequest(makeRequest(url).withHeader("X-Forwarded-For", ip))

