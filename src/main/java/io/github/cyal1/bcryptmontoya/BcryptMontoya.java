/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Marker;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.Theme;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BcryptMontoya implements BurpExtension
{
    public static MontoyaApi Api;
    public static PythonInterpreter pyInterp;
    private BcryptMontoyaUI codePanel;
    public static ArrayList<String> ALLOWED_URL_PREFIX;
    public static STATUS status = STATUS.STOP;
    public MyContextMenuItemsProvider contextMenu;
//    public Map<String, PyFunction> pyContextMenu;
    public enum STATUS {
        RUNNING,
        STOP
    }

    @Override
    public void initialize(MontoyaApi api)
    {
        BcryptMontoya.Api = api;
        this.codePanel = new BcryptMontoyaUI();
        ALLOWED_URL_PREFIX = new ArrayList<>();
        codePanel.textEditor.setText(codePanel.getDefaultScript());
        if(api.userInterface().currentTheme() == Theme.DARK){
            codePanel.setDarkTheme();
        }
        contextMenu = new MyContextMenuItemsProvider();
        api.extension().setName("BcryptMontoya");
        api.userInterface().registerSuiteTab("BcryptMontoya", codePanel);
        api.http().registerHttpHandler(new MyHttpHandler());
        api.proxy().registerRequestHandler(new MyProxyRequestHandler());
        api.proxy().registerResponseHandler(new MyProxyResponseHandler());
        api.scanner().registerScanCheck(new MyScanCheck());
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());
        api.userInterface().registerContextMenuItemsProvider(contextMenu);
//        api.userInterface().registerHttpResponseEditorProvider(new MyHttpResponseEditorProvider());
        codePanel.runButton.addActionListener(e -> {
            if(status==STATUS.STOP){
                    // do start things on STOP status while button clicked
                    runBtnClick();
                }else{
                    // do stop things on RUNNING status while button clicked
                    stopBtnClick();
                }
        });
    }
    private void initPyEnv(){
        pyInterp = new PythonInterpreter();
        pyInterp.setOut(Api.logging().output());
        pyInterp.setErr(Api.logging().error());
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        pyInterp.set("Utils", Api.utilities());
        pyInterp.set("contextMenu", contextMenu);
        pyInterp.set("urls", ALLOWED_URL_PREFIX);
        pyInterp.exec(BcryptMontoyaUI.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/initEnv.py")));
//        pyInterp.set("menus",pyContextMenu);
//        pyInterp.exec("registerContextMenu(menus)");
    }
    private void runBtnClick(){
        try{
            initPyEnv();
            pyInterp.exec(codePanel.getCode());
            pyInterp.exec("registerContextMenu(contextMenu)");
            pyInterp.exec("urlPrefixAllowed(urls)");
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        codePanel.textEditor.setHighlightCurrentLine(false);
        codePanel.textEditor.setEnabled(false);
        codePanel.loadDirectoryButton.setEnabled(false);
        codePanel.codeCombo.setEnabled(false);
        status = STATUS.RUNNING;
        codePanel.runButton.setText("Stop");
        Api.persistence().preferences().setString("defaultScript", codePanel.getCode().replace("\r\n","\n"));
    }
    private void stopBtnClick(){
        pyInterp.exec("finish()");
        contextMenu.MENUS.clear();
        ALLOWED_URL_PREFIX.clear();
        codePanel.textEditor.setHighlightCurrentLine(true);
        codePanel.textEditor.setEnabled(true);
        codePanel.loadDirectoryButton.setEnabled(true);
        codePanel.codeCombo.setEnabled(true);
        pyInterp = null;
        status = STATUS.STOP;
        codePanel.runButton.setText("Run");
    }

    /**
     * Modify HttpResponse By Python
     *
     * @param httpResponse Montoya Api HttpResponse instance
     * @param pyFuncName python function
     * @return Modified HttpResponse instance by Python
     */
    public static <T extends HttpResponse> HttpResponse modifyHttpResponse(T httpResponse, String pyFuncName){
        if(status == STATUS.STOP){
            return httpResponse;
        }

        // url prefix allowed
        String url;
        if(httpResponse instanceof HttpResponseReceived){
            url = ((HttpResponseReceived) httpResponse).initiatingRequest().url();
        }else if(httpResponse instanceof InterceptedResponse){
            url = ((InterceptedResponse) httpResponse).initiatingRequest().url();
        }else{
            throw new RuntimeException("Unexpect Instance" + httpResponse.getClass().getName());
        }
        if(!isPrefixAllowed(url)){
            return httpResponse;
        }

        PyObject method = pyInterp.get( pyFuncName );
        PyObject[] pythonArguments = Py.javas2pys( httpResponse );
        PyObject r = method.__call__( pythonArguments );
        return (HttpResponse) r.__tojava__( HttpResponse.class);
    }
    /**
     * Modify HttpRequest By Python
     *
     * @param httpRequest Montoya Api HttpRequest instance
     * @param pyFuncName python function
     * @return Modified HttpRequest instance by Python
     */
    public static  <T extends HttpRequest> HttpRequest modifyHttpRequest(T httpRequest, String pyFuncName){
        if(status == STATUS.STOP){
            return httpRequest;
        }

        // url prefix allowed
        if(!isPrefixAllowed(httpRequest.url())){
            return httpRequest;
        }

        PyObject method = pyInterp.get( pyFuncName );
        PyObject[] pythonArguments = Py.javas2pys( httpRequest );
        PyObject r = method.__call__( pythonArguments );
        return (HttpRequest) r.__tojava__( HttpRequest.class);
    }

    private static boolean isPrefixAllowed(String url){
        if (ALLOWED_URL_PREFIX.size() == 0){
            return false;
        }
        for(String u: ALLOWED_URL_PREFIX){
            if(url.startsWith(u)){
                return true;
            }
        }
        return false;
    }

    public static void addIssue(java.lang.String name, java.lang.String detail, java.lang.String remediation, java.lang.String baseUrl, burp.api.montoya.scanner.audit.issues.AuditIssueSeverity severity, burp.api.montoya.scanner.audit.issues.AuditIssueConfidence confidence, java.lang.String background, java.lang.String remediationBackground, burp.api.montoya.scanner.audit.issues.AuditIssueSeverity typicalSeverity, burp.api.montoya.http.message.HttpRequestResponse... requestResponses){
        Api.siteMap().add(AuditIssue.auditIssue(name, detail,remediation,baseUrl, severity,confidence,background,remediationBackground,typicalSeverity,requestResponses));
    }
    public static void addIssue(java.lang.String name, java.lang.String detail, java.lang.String remediation, java.lang.String baseUrl, burp.api.montoya.scanner.audit.issues.AuditIssueSeverity severity, burp.api.montoya.scanner.audit.issues.AuditIssueConfidence confidence, java.lang.String background, java.lang.String remediationBackground, burp.api.montoya.scanner.audit.issues.AuditIssueSeverity typicalSeverity, java.util.List<burp.api.montoya.http.message.HttpRequestResponse> requestResponses){
        Api.siteMap().add(AuditIssue.auditIssue(name,detail,remediation,baseUrl,severity,confidence,background,remediationBackground,typicalSeverity,requestResponses));
    }
    public static HttpRequestResponse  sendRequest(HttpRequest args){
        return BcryptMontoya.Api.http().sendRequest(args);
    }
    public static List<Marker> getResponseHighlights(HttpRequestResponse requestResponse, String match)
    {
        List<Marker> highlights = new LinkedList<>();
        String response = requestResponse.response().toString();

        int start = 0;

        while (start < response.length())
        {
            start = response.indexOf(match, start);

            if (start == -1)
            {
                break;
            }

            Marker marker = Marker.marker(start, start+match.length());
            highlights.add(marker);

            start += match.length();
        }

        return highlights;
    }
}