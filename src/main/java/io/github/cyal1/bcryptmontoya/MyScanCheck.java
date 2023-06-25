package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.AuditResult;
import burp.api.montoya.scanner.ConsolidationAction;
import burp.api.montoya.scanner.ScanCheck;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import org.python.core.Py;
import org.python.core.PyObject;

class MyScanCheck implements ScanCheck {
    @Override
    public AuditResult activeAudit(HttpRequestResponse baseRequestResponse, AuditInsertionPoint auditInsertionPoint) {
        if (BcryptMontoya.status != BcryptMontoya.STATUS.RUNNING) {
            return AuditResult.auditResult();
        }
        PyObject method = BcryptMontoya.pyInterp.get("activeScan");
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(baseRequestResponse);
        pythonArguments[1] = Py.java2py(auditInsertionPoint);
        AuditIssue r = (AuditIssue) method.__call__(pythonArguments).__tojava__(AuditIssue.class);
        if (r != null) {
            return AuditResult.auditResult(r);
        }
        return AuditResult.auditResult();
    }

    @Override
    public AuditResult passiveAudit(HttpRequestResponse baseRequestResponse) {
        if (BcryptMontoya.status != BcryptMontoya.STATUS.RUNNING) {
            return AuditResult.auditResult();
        }
        PyObject method = BcryptMontoya.pyInterp.get("passiveScan");
        PyObject[] pythonArguments = Py.javas2pys(baseRequestResponse);
        AuditIssue r = (AuditIssue) method.__call__(pythonArguments).__tojava__(AuditIssue.class);
        if (r != null) {
            return AuditResult.auditResult(r);
        }
        return AuditResult.auditResult();
    }

    @Override
    public ConsolidationAction consolidateIssues(AuditIssue newIssue, AuditIssue existingIssue) {
        return (existingIssue.name().equals(newIssue.name()) &&
                existingIssue.detail().equals(newIssue.detail()) &&
                existingIssue.baseUrl().equals(newIssue.baseUrl())
        ) ? ConsolidationAction.KEEP_EXISTING : ConsolidationAction.KEEP_BOTH;
    }
}