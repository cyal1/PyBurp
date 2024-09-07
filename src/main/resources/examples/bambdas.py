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
    Custom word list generate from history
"""
words = []
word_regex = r'[^a-zA-Z]'
word_regex2 = r'[^a-zA-Z0-9\-_]' # word contain digit, -, _
min_len = 2
max_len = 20
lower_case = False


for requestResponse in history(
        lambda rr: rr.httpService().host().endswith(".example.com")
):
    # wordlist from request
    req = requestResponse.request().toString()
    if lower_case:
        req = req.lower()
    words += set(re.split(word_regex, req))
    words += set(re.split(word_regex2, req))  # word contain digit, -, _

    # wordlist from response
    if requestResponse.hasResponse() \
            and requestResponse.response().mimeType() in [ MimeType.JSON, MimeType.PLAIN_TEXT, MimeType.SCRIPT, MimeType.XML, MimeType.HTML ]:
        print(requestResponse.url())
        resp = requestResponse.response().toString()
        if lower_case:
            resp = resp.lower()
        words += set(re.split(word_regex, resp))
        words += set(re.split(word_regex2, resp))  # word contain digit, -, _

words = sorted(set(words))

def starts_or_ends_with(text, c):
    return text.startswith(c) or text.endswith(c)

# filter
words = [i for i in words if ((i != '') \
                and not i[0].isdigit() \                   # exclude number and start with digit
                and not starts_or_ends_with(i, '_') \
                and not starts_or_ends_with(i, '-') \
                and (len(i) >= min_len) \
                and (len(i) <= max_len))
        ]

print(len(words)) # wordlist size

# save wordlist to file
with open("/tmp/dicts.txt", 'w') as f:
    for word in words:
        f.write(word + "\n")

