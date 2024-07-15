import sys

"""
You can add CPython libraries to Jython's sys.path.

Note that not all CPython libraries can be used in Jython.

Use the command `python2 -c "import json; import sys; print json.dumps(sys.path)"` to get the local pip package path.

"""

PIP_PATH = ["/Library/Python/2.7/site-packages"]

for path in PIP_PATH:
    if path not in sys.path:
        sys.path.append(path)


from bs4 import BeautifulSoup # # pip2 install beautifulsoup4

"""

You should not make HTTP requests in the Swing thread, it will freeze the UI.

Using a third-party networking library (rather than Burp's) to perform the request, the Logger does not record.

"""
@run_in_thread
def test():
    html_doc = sendRequest(httpRequestFromUrl("https://www.example.com/")).response().bodyToString()
    soup = BeautifulSoup(html_doc, 'html.parser')
    print(soup.title)
    print(soup.find_all('a'))

test()

