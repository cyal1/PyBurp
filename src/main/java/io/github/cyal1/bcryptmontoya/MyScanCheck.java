package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.AuditResult;
import burp.api.montoya.scanner.ConsolidationAction;
import burp.api.montoya.scanner.ScanCheck;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;

class MyScanCheck implements ScanCheck {
    public BcryptMontoyaTab bcryptMontoyaTab;

    public MyScanCheck(BcryptMontoyaTab bcryptMontoyaTab) {
        this.bcryptMontoyaTab = bcryptMontoyaTab;
    }

    @Override
    public AuditResult activeAudit(HttpRequestResponse baseRequestResponse, AuditInsertionPoint auditInsertionPoint) {
        PyFunction activeAudit = bcryptMontoyaTab.py_functions.get("activeAudit");
//        if (activeAudit == null) {
//            return AuditResult.auditResult();
//        }
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(baseRequestResponse);
        pythonArguments[1] = Py.java2py(auditInsertionPoint);
        AuditResult r = (AuditResult) activeAudit.__call__(pythonArguments).__tojava__(AuditResult.class);
        if (r != null) {
            return r;
        }
        return AuditResult.auditResult();
    }

    @Override
    public AuditResult passiveAudit(HttpRequestResponse baseRequestResponse) {
        PyFunction passiveAudit = bcryptMontoyaTab.py_functions.get("passiveAudit");
//        if (passiveAudit == null) {
//            return AuditResult.auditResult();
//        }

        PyObject pythonArguments = Py.java2py(baseRequestResponse);
        AuditResult r = (AuditResult) passiveAudit.__call__(pythonArguments).__tojava__(AuditResult.class);;
        if (r != null) {
            return r;
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