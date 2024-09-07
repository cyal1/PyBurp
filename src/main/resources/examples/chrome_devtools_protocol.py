import json
from com.github.kklisura.cdt.services.impl import ChromeServiceImpl
# https://github.com/kklisura/chrome-devtools-java-client
# Launch Chrome: /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --remote-debugging-port=9222 --remote-allow-origins='*'


def getTabByUrl(url):
    for tab in chromeService.getTabs():
        # print(tab.getId(), tab.getTitle(), tab.getUrl())
        if tab.getUrl() == url:
            return tab
    return None


chromeService = ChromeServiceImpl(9222)  # the remote debugging port of chrome
tab = getTabByUrl("https://www.example.com/")  # the first tab found that is equal to this URL
devToolsService = chromeService.createDevToolsService(tab)
runtime = devToolsService.getRuntime()


def encrypt(s):
    # js code
    evaluation = runtime.evaluate('''
        temp1(__REPLACEMENT__);
    '''.replace("__REPLACEMENT__", json.dumps(s)))
    ex = evaluation.getExceptionDetails()
    if ex is not None:
        print(ex.getException().getDescription())
        return None
    else:
        return evaluation.getResult().getValue()

print(encrypt("test"))


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/")


def handleRequest(request, annotations):
    json_obj = json.loads(request.bodyToString())
    json_obj["password"] = encrypt(json_obj["password"])
    return request.withBody(json.dumps(json_obj)), annotations


def finish():
    devToolsService.close()


def registerContextMenu(menus):
    menus.register("rpc encrypt", encrypt, MenuType.SELECTED_TEXT)

