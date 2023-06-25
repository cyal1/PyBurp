def passiveScan(baseRequestResponse):
    """
    :param baseRequestResponse: HttpRequestResponse

    """
    if "secretKey" in baseRequestResponse.response().bodyToString():
        addIssue("SecretKey Found", "String detail", "String remediation", baseRequestResponse.request().url(),
                 AuditIssueSeverity.HIGH, AuditIssueConfidence.CERTAIN, "String background",
                 "String remediationBackground", AuditIssueSeverity.MEDIUM,
                 baseRequestResponse.withResponseMarkers(getResponseHighlights(baseRequestResponse, "SecretKey")))


def activeScan(baseRequestResponse, auditInsertionPoint):
    """
    :param baseRequestResponse: HttpRequestResponse
    :param auditInsertionPoint: AuditInsertionPoint
    :return auditIssue: AuditIssue

    """
    return None