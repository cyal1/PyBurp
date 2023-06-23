# -*- coding: utf-8 -*-
import io.github.cyal1.bcryptmontoya.CallFuncClient as Grpc
from io.github.cyal1.bcryptmontoya.BcryptMontoya import addIssue, getResponseHighlights
from io.github.cyal1.bcryptmontoya.MyContextMenuItemsProvider import MenuType
from io.github.cyal1.bcryptmontoya import RequestPool
from burp.api.montoya.http.message.params import HttpParameterType, HttpParameter
from burp.api.montoya.http.message import ContentType
import burp.api.montoya.core.ByteArray.byteArray as bytearray
from burp.api.montoya.core import Annotations, HighlightColor
from burp.api.montoya.scanner.audit.issues import AuditIssue, AuditIssueSeverity, AuditIssueConfidence

def urlPrefixAllowed(urls):
    pass

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html
def handleRequest(request):
    return request

def handleProxyRequest(request):
    return request

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html
def handleResponse(response):
    return response

def handleProxyResponse(response):
    return response

def registerContextMenu(menus):
    pass

def finish():
    pass


# build-in function
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

