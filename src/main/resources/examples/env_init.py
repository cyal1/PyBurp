
"""

    You do not need to run this file.
    This file will run before each execution.
    The code to set some initialization information.

"""


import random, string, sys
from java.lang import Runnable, Thread, String
from java.util.concurrent import Executors
import burp.api.montoya.core.ByteArray.byteArray as bytearray
import io.github.cyal1.pyburp.CallFuncClient as rpc

# Import common enumeration types
from burp.api.montoya.http import HttpMode
from burp.api.montoya.core import HighlightColor
from burp.api.montoya.http.message import ContentType, MimeType
from burp.api.montoya.http.message.params import HttpParameterType
from burp.api.montoya.scanner.audit.issues import AuditIssueSeverity, AuditIssueConfidence
from io.github.cyal1.pyburp.MyContextMenuItemsProvider import MenuType

# Import some commonly used methods
from burp.api.montoya.core.Annotations import annotations
from burp.api.montoya.http.HttpService import httpService
from burp.api.montoya.scanner.AuditResult import auditResult
from burp.api.montoya.scanner.audit.issues.AuditIssue import auditIssue
from burp.api.montoya.http.message.HttpRequestResponse import httpRequestResponse
from burp.api.montoya.http.message.requests.HttpRequest import httpRequestFromUrl, httpRequest, http2Request
from burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint import auditInsertionPoint
from burp.api.montoya.http.message.params.HttpParameter import bodyParameter, cookieParameter, parameter, urlParameter
from io.github.cyal1.pyburp.PyBurp import http, proxy, utils, organizer
from io.github.cyal1.pyburp.Tools import addIssue, getResponseHighlights, getOOBCanary, getSelectedText, replaceSelectedText


# Set the default encoding used by the Python interpreter
reload(sys)
sys.setdefaultencoding('utf8')  # print(sys.getdefaultencoding())

EXIT_FLAG = False  # This will become True when click the Stop button


# Class for managing a pool of threads to execute function
class RequestPool:
    def __init__(self, nThreads):
        self.executor = Executors.newFixedThreadPool(nThreads)

    def run(self, func, *args, **kwargs):
        self.executor.execute(lambda: func(*args, **kwargs))

    def shutdown(self):
        self.executor.shutdownNow()
        print("RequestPool shutdown")


# Decorator to run a function in the thread pool
def run_in_pool(pool):
    def decorator(func):
        def wrapper(*args, **kwargs):
            return pool.run(func, *args, **kwargs)
        return wrapper
    return decorator


# Decorator to run a function in a thread
def run_in_thread(func):
    def wrapper(*args, **kwargs):
        class JavaRunnable(Runnable):
            def run(self):
                # You can check if EXIT_FLAG is True in custom function to exit the thread.
                if EXIT_FLAG is False:
                    func(*args, **kwargs)
        thread = Thread(JavaRunnable())
        thread.start()
    return wrapper


# Commonly used function wrappers
def sendRequest(*args):
    return http.sendRequest(*args)


def sendRequests(*args):
    return http.sendRequests(*args)


def sendToOrganizer(*args):
    return organizer.sendToOrganizer(*args)


def history(*args):
    return proxy.history(*args)


def randomstring(length=8):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(random.choice(characters) for _ in range(length))
    return random_string


# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/package-summary.html


def urlencode(text):
    return utils.urlUtils().encode(text.decode())


def urldecode(text):
    return utils.urlUtils().decode(text)


def base64encode(text):
    return utils.base64Utils().encodeToString(text)


def base64decode(text):  # return ByteArray
    return utils.base64Utils().decode(text.decode())


def bytestring(s):
    return bytearray(s).getBytes()


def strz(s, charset="UTF8"):
    return str(String(s, charset))
