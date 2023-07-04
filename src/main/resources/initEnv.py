import io.github.cyal1.bcryptmontoya.CallFuncClient as Grpc
from io.github.cyal1.bcryptmontoya.BcryptMontoya import addIssue, getResponseHighlights, http
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

    def shutdown(self):
        self.executor.shutdownNow()
        try:
            if not self.executor.awaitTermination(1, TimeUnit.SECONDS):
                print("Some tasks were not terminated.")
        except InterruptedException as e:
            print(e.getMessage())
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


def makeRequest(url):
    return HttpRequest.httpRequestFromUrl(url)


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


def urlPrefixAllowed(urls):
    pass


def handleRequest(request, annotations):
    return request, annotations


def handleProxyRequest(request, annotations):
    return request, annotations


def handleResponse(response, annotations):
    return response, annotations


def handleProxyResponse(response, annotations):
    return response, annotations


def registerContextMenu(menus):
    pass


def finish():
    pass


def passiveScan(baseRequestResponse):
    pass


def activeScan(baseRequestResponse, auditInsertionPoint):
    pass
