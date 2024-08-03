# https://portswigger.net/burp/documentation/desktop/tools/proxy/http-history/bambdas
# https://portswigger.net/research/finding-that-one-weird-endpoint-with-bambdas#:~:text=Large%20redirect%20responses


for requestResponse in history(
        lambda rr: rr.hasResponse()
                   and rr.response().statusCode()/100 == 3
                   and rr.response().body().length() > 1
):

    print(requestResponse.request().url(), requestResponse.response().statusCode(), requestResponse.response().body().length())


for requestResponse in history(
        lambda rr: rr.hasResponse()
                   and rr.response().hasCookie("rememberMe")
):
    print(requestResponse.request().url())

