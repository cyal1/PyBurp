import sys
import threading

# Run the command `python2 -c "import json; import sys; print json.dumps(sys.path)"` on your computer, and replace PIP_PATH below with the result.
PIP_PATH = ["/Library/Python/2.7/site-packages"]

for path in PIP_PATH:
    if path not in sys.path:
        sys.path.append(path)

from concurrent.futures import ThreadPoolExecutor # pip install futures


def send_request(url, exit_event):
    if exit_event.is_set():
        return
    print(sendRequest(httpRequestFromUrl(url)).response().statusCode())


def finish():
    print("exit")
    exit_event.set()


executor = ThreadPoolExecutor(max_workers=10)
exit_event = threading.Event()

for url in open("/tmp/urls.txt"):
    executor.submit(send_request, url, exit_event)

# If wait is set to True, the main thread's user interface (UI) gets blocked and wait for all tasks to be executed.
executor.shutdown(wait=False)

# https://github.com/jython/book/tree/master/src/chapter19
# https://github.com/jython/book/blob/master/src/chapter19/test_futures.py

