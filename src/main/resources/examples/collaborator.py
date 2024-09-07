
# https://github.com/PortSwigger/burp-extensions-montoya-api-examples/tree/main/collaborator/src/main/java/example/collaborator
# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/collaborator/Interaction.html

canary = getOOBCanary()


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

    proxyHttpRequestResponseList = history(lambda rr: oob_id in rr.finalRequest().toString())
    for item in proxyHttpRequestResponseList:
        print("URL:", item.finalRequest().url())
        addIssue(auditIssue(
            "Collaborator issue",
            "detail",
            "Remediation",
            item.finalRequest().url(),
            AuditIssueSeverity.HIGH,
            AuditIssueConfidence.CERTAIN,
            "Background",
            "RemediationBackground",
            AuditIssueSeverity.MEDIUM,
            httpRequestResponse(item.finalRequest(), item.originalResponse())
        ))


def urlPrefixAllowed(urls):
    urls.add("https://portswigger-labs.net/ssrf-dns.php")


# curl -x http://127.0.0.1:8080/ "https://portswigger-labs.net/ssrf-dns.php?host=www.google.com"
def handleProxyRequest(request, annotations):
    return request.withUpdatedParameters(urlParameter("host", canary)), annotations

