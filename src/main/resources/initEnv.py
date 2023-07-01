import io.github.cyal1.bcryptmontoya.CallFuncClient as Grpc
from io.github.cyal1.bcryptmontoya.BcryptMontoya import addIssue, getResponseHighlights, sendRequest
from io.github.cyal1.bcryptmontoya.MyContextMenuItemsProvider import MenuType
from io.github.cyal1.bcryptmontoya import RequestPool
from burp.api.montoya.http.message.params import HttpParameterType, HttpParameter
from burp.api.montoya.http.message import ContentType
import burp.api.montoya.core.ByteArray.byteArray as bytearray
from burp.api.montoya.core import Annotations, HighlightColor
from burp.api.montoya.scanner.audit.issues import AuditIssue, AuditIssueSeverity, AuditIssueConfidence
from burp.api.montoya.scanner.audit.insertionpoint import AuditInsertionPoint
from threading import Thread
import random, string


def randomstring(length=8):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(random.choice(characters) for _ in range(length))
    return random_string


def run_in_thread(func):
    def wrapper(*args, **kwargs):
        thread = Thread(target=func, args=args, kwargs=kwargs)
        thread.start()
    return wrapper

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
