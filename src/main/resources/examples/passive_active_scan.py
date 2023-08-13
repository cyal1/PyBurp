
def passiveScan(baseRequestResponse):
    if "secretKey" in baseRequestResponse.response().bodyToString():
        addIssue(addIssue("SecretKey Found", "String detail", "String remediation", baseRequestResponse.request().url(),
                 AuditIssueSeverity.HIGH, AuditIssueConfidence.CERTAIN, "String background",
                 "String remediationBackground", AuditIssueSeverity.MEDIUM,
                 baseRequestResponse.withResponseMarkers(getResponseHighlights(baseRequestResponse, "SecretKey"))))


def activeScan(baseRequestResponse, auditInsertionPoint):
    pass
