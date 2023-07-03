import sys

# Run the command `python2 -c "import json; import sys; print json.dumps(sys.path)"` on your computer, and replace PIP_PATH below with the result.
PIP_PATH = ["/Users/test/.pyenv/versions/2.7.18/lib/python2.7/site-packages"]

for path in PIP_PATH:
    if path not in sys.path:
        sys.path.append(path)

# pip install futures
from concurrent.futures import ThreadPoolExecutor
import threading


def send_request(url, exit_event):
    if exit_event.is_set():
        return
    sendRequest(makeRequest(url))


def finish():
    exit_event.set()


executor = ThreadPoolExecutor(max_workers=10)
exit_event = threading.Event()

for url in open("/tmp/urls.txt"):
    executor.submit(send_request, url, exit_event)

executor.shutdown(wait=False)

