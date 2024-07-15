# -*- coding: utf-8 -*-

import io.github.cyal1.bcryptmontoya.CallFuncClient as Grpc
from java.lang import Runnable, Thread
from java.util.concurrent import Executors, TimeUnit
import random, string
from java.lang import System

# Enum Class
from burp.api.montoya.http import HttpMode
from io.github.cyal1.bcryptmontoya.MyContextMenuItemsProvider import MenuType
from burp.api.montoya.http.message.params import HttpParameterType
from burp.api.montoya.http.message import ContentType
from burp.api.montoya.core import HighlightColor
from burp.api.montoya.scanner.audit.issues import AuditIssueSeverity, AuditIssueConfidence
from burp.api.montoya.http.message.HttpRequestResponse import httpRequestResponse

# Static Method or Fields
from io.github.cyal1.bcryptmontoya.BcryptMontoya import addIssue, getResponseHighlights, http, proxy, Utils, organizer
from burp.api.montoya.http.HttpService import httpService
from burp.api.montoya.http.message.requests.HttpRequest import httpRequestFromUrl, httpRequest
from burp.api.montoya.http.message.params.HttpParameter import bodyParameter, cookieParameter, parameter, urlParameter
import burp.api.montoya.core.ByteArray.byteArray as bytearray
from burp.api.montoya.core.Annotations import annotations
from burp.api.montoya.scanner.audit.issues.AuditIssue import auditIssue
from burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint import auditInsertionPoint
from io.github.cyal1.bcryptmontoya.MyContextMenuItemsProvider import getSelectedText, replaceSelectedText

import sys

reload(sys)
sys.setdefaultencoding('utf8')
# print(sys.getdefaultencoding())

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


def sendToOrganizer(*args):
    return organizer.sendToOrganizer(*args)


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


def ts():
    return System.currentTimeMillis()

