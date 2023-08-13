import io.github.cyal1.bcryptmontoya.CallFuncClient as Grpc
from io.github.cyal1.bcryptmontoya.BcryptMontoya import addIssue, getResponseHighlights, http, proxy
from io.github.cyal1.bcryptmontoya.MyContextMenuItemsProvider import MenuType
from burp.api.montoya.http.message.requests import HttpRequest
from burp.api.montoya.http import HttpMode
from burp.api.montoya.http.message.params import HttpParameterType, HttpParameter
from burp.api.montoya.http.message import ContentType
import burp.api.montoya.core.ByteArray.byteArray as bytearray
from burp.api.montoya.core import Annotations, HighlightColor
from burp.api.montoya.scanner.audit.issues import AuditIssue, AuditIssueSeverity, AuditIssueConfidence
from burp.api.montoya.scanner.audit.insertionpoint import AuditInsertionPoint
from java.lang import Runnable, Thread
from java.util.concurrent import Executors, TimeUnit
import random, string


class RequestPool:
    def __init__(self, nThreads):
        self.executor = Executors.newFixedThreadPool(nThreads)

    def run(self, func, *args, **kwargs):
        self.executor.execute(lambda: func(*args, **kwargs))

    def shutdown(self, timeout=1):
        self.executor.shutdownNow()
        try:
            if not self.executor.awaitTermination(timeout, TimeUnit.SECONDS):
                print("pool was not terminated.")
        except Exception as e:
            print(e)
        print("RequestPool shutdown")


def run_in_pool(pool):
    def decorator(func):
        def wrapper(*args, **kwargs):
            return pool.run(func, *args, **kwargs)
        return wrapper
    return decorator


def run_in_thread(func):  # todo stop thread while click stop
    def wrapper(*args, **kwargs):
        class JavaRunnable(Runnable):
            def run(self):
                func(*args, **kwargs)
        thread = Thread(JavaRunnable())
        thread.start()
    return wrapper


def sendRequest(*args):
    return http.sendRequest(*args)


def sendRequests(*args):
    return http.sendRequests(*args)


def history(*args):
    return proxy.history(*args)


def randomstring(length=8):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(random.choice(characters) for _ in range(length))
    return random_string


def urlencode(text):
    return Utils.urlUtils().encode(text).encode()


def urldecode(text):
    return Utils.urlUtils().decode(text).encode()


def base64encode(text):
    return Utils.base64Utils().encodeToString(text).encode()


def base64decode(text):
    return Utils.base64Utils().decode(text).toString().encode()


def parameter(*args):
    return HttpParameter.parameter(*args)


def urlparameter(*args):
    return HttpParameter.urlParameter(*args)


def bodyparameter(*args):
    return HttpParameter.bodyParameter(*args)


def cookieparameter(*args):
    return HttpParameter.cookieParameter(*args)


def makeRequest(url, raw_http=None, fix_content_length=True):
    request = HttpRequest.httpRequestFromUrl(url)
    if raw_http is None:
        return request
    for header in request.headers():
        request = request.withRemovedHeader(header)
    header_raw, body = raw_http.replace("\r\n", "\n").split("\n\n", 1)
    header_lines = header_raw.split("\n")
    method_path = header_lines[0]
    method, path, http_version = method_path.split(" ", 2)
    request = request.withMethod(method).withPath(path).withBody(body)
    for header_line in header_lines[1:]:
        key, value = header_line.split(":", 1)
        if key.lower() == "content-length" and fix_content_length is True:
            request = request.withRemovedHeader("Content-Length")
            request = request.withHeader(key, str(request.body().length()))
            continue
        request = request.withHeader(key, value.strip())
    return request
