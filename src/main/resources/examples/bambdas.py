# https://portswigger.net/burp/documentation/desktop/tools/proxy/http-history/bambdas
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html
import re


"""
    Find large redirect responses
"""
for requestResponse in history(
        lambda rr: rr.hasResponse()
                   and rr.response().statusCode()/100 == 3
                   and rr.response().body().length() > 1
):

    print(requestResponse.request().url(), requestResponse.response().statusCode(), requestResponse.response().body().length())


"""
    Find secretKey from history
"""
for requestResponse in history(
        lambda rr: rr.hasResponse()
                   and rr.response().contains("secretKey", False)
):
    print(requestResponse.request().url())


"""
    Custom word list generat from history
"""

words = []
word_regex = r'[^a-zA-Z]'
word_regex2 = r'[^a-zA-Z\-]' # word contain -
word_regex3 = r'[^a-zA-Z_]'  # word contain _
min_len = 2
max_len = 20


def starts_or_ends_with(text, c):
    return text.startswith(c) or text.endswith(c)


for requestResponse in history(
        lambda rr: rr.httpService().host().endswith(".example.com")
):
    # todo - words from request, headers, url
    if requestResponse.hasResponse() \
            and requestResponse.response().mimeType() in [ MimeType.JSON, MimeType.PLAIN_TEXT, MimeType.SCRIPT, MimeType.XML, MimeType.HTML ]:
        print(requestResponse.url())
        body = requestResponse.response().bodyToString()
        # body = body.lower()
        words += set(re.split(word_regex, body) + re.split(word_regex2, body) + re.split(word_regex3, body))

words = sorted(set(words))
words = [i for i in words if ((i != '') and not starts_or_ends_with(i, '_') and not starts_or_ends_with(i, '-') and (len(i) >= min_len) and (len(i) <= max_len))]
print(len(words))

# save wordlist to file
with open("/tmp/dicts.txt", 'w') as f:
    for word in words:
        f.write(word + "\n")

