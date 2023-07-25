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
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.Marker;
import burp.api.montoya.core.Registration;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.Theme;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import javax.swing.*;
import java.util.*;

public class BcryptMontoya implements BurpExtension
{
    public static MontoyaApi Api;
    public static Http http;
    public static Proxy proxy;
    public static PythonInterpreter pyInterp  = new PythonInterpreter();
    private BcryptMontoyaUI codePanel;
    public static ArrayList<String> ALLOWED_URL_PREFIX;
    public static STATUS status = STATUS.STOP;
    private ArrayList<Registration> plugins;
    public MyContextMenuItemsProvider myContextMenu;
//    public Map<String, PyFunction> pyContextMenu;
    public static HashMap<String, PyFunction> py_functions;
    public enum STATUS {
        RUNNING,
        STOP
    }

    @Override
    public void initialize(MontoyaApi api) {
        BcryptMontoya.http = api.http();
        BcryptMontoya.proxy = api.proxy();
        BcryptMontoya.Api = api;
        this.codePanel = new BcryptMontoyaUI();
        ALLOWED_URL_PREFIX = new ArrayList<>();
        codePanel.textEditor.setText(codePanel.getDefaultScript());
        if(api.userInterface().currentTheme() == Theme.DARK){
            codePanel.setDarkTheme();
        }
        myContextMenu = new MyContextMenuItemsProvider();
        api.extension().setName("BcryptMontoya");
        api.userInterface().registerSuiteTab("BcryptMontoya", codePanel);
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());
        api.userInterface().registerHttpResponseEditorProvider(new MyHttpResponseEditorProvider());
        initPyEnv();
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
    private void registerExtender(){

        py_functions = getPyFunctions();
        plugins = new ArrayList<>();
        if(py_functions.containsKey("registerContextMenu")){
            plugins.add(Api.userInterface().registerContextMenuItemsProvider(myContextMenu));
        }

        if(py_functions.containsKey("passiveScan") || py_functions.containsKey("activeScan")){
            plugins.add(Api.scanner().registerScanCheck(new MyScanCheck()));
        }

        if(py_functions.containsKey("handleRequest")|| py_functions.containsKey("handleResponse")){
            plugins.add(Api.http().registerHttpHandler(new MyHttpHandler()));
        }

        if(py_functions.containsKey("handleProxyRequest")){
            plugins.add(Api.proxy().registerRequestHandler(new MyProxyRequestHandler()));
        }

        if(py_functions.containsKey("handleProxyResponse")){
            plugins.add(Api.proxy().registerResponseHandler(new MyProxyResponseHandler()));
        }
        if(py_functions.containsKey("unloading")){
            plugins.add(Api.extension().registerUnloadingHandler(() ->
            {
                try {
                    py_functions.get("unloading").__call__();
                } catch (Exception e){
                    BcryptMontoya.Api.logging().logToError(e.getMessage());
                }
            }));
        }
    }

    private HashMap<String,PyFunction> getPyFunctions(){
        ArrayList<String> MontoyaPyFunctions = new ArrayList<>(Arrays.asList(
                "registerContextMenu",
                "passiveScan", "activeScan",
                "handleRequest", "handleResponse",
                "handleProxyRequest", "handleProxyResponse",
                "urlPrefixAllowed",
                "finish",
                "unloading"
        ));

        HashMap<String,PyFunction> functionList = new HashMap<>();
        String get_function_names_code = "[(name,obj) for name, obj in globals().items() if callable(obj) and not name.startswith('__')]";

        PyObject pyFunctionList = pyInterp.eval(get_function_names_code);
            if (pyFunctionList instanceof PyList) {
                for (Object pyTuple : (PyList)pyFunctionList) {
                    if (pyTuple instanceof PyTuple) {
                        String functionName = ((PyTuple) pyTuple).__getitem__(0).toString();
                        PyObject functionObject = ((PyTuple) pyTuple).__getitem__(1);
                        if (MontoyaPyFunctions.contains(functionName) && functionObject instanceof PyFunction pyFunction) {
                            functionList.put(functionName, pyFunction);
                        }
//                        else if (functionObject instanceof PyJavaType) {
//                            PyFunction pyFunction = (PyFunction) ((PyJavaType) functionObject).__call__();
//                            String functionName = ((PyTuple) pyTuple).__getitem__(0).toString();
//                            functionList.put(functionName, pyFunction);
//                        }
                    }
                }
            }
        return functionList;
    }
    private void initPyEnv(){
//        pyInterp = new PythonInterpreter();
        pyInterp.setOut(Api.logging().output());
        pyInterp.setErr(Api.logging().error());
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        pyInterp.set("Utils", Api.utilities());
        pyInterp.exec(BcryptMontoyaUI.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/initEnv.py")));
    }
    private void runBtnClick(){
        try{
            pyInterp.exec(codePanel.getCode());
            registerExtender();

            PyObject[] urls = Py.javas2pys(ALLOWED_URL_PREFIX);
            if(py_functions.containsKey("urlPrefixAllowed")){
                py_functions.get("urlPrefixAllowed").__call__(urls);
            }
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
        try {
            if (py_functions.containsKey("finish")){
                py_functions.get("finish").__call__();
            }
        } catch (Exception e){
            BcryptMontoya.Api.logging().logToError(e.getMessage());
        }
        myContextMenu.MENUS.clear();
        ALLOWED_URL_PREFIX.clear();
        py_functions.clear();
        //        pyInterp.close();
        pyInterp.cleanup();
        pyInterp.exec("globals().clear()");
        codePanel.textEditor.setHighlightCurrentLine(true);
        codePanel.textEditor.setEnabled(true);
        codePanel.loadDirectoryButton.setEnabled(true);
        codePanel.codeCombo.setEnabled(true);
        status = STATUS.STOP;
        codePanel.runButton.setText("Run");
        for(Registration plugin: plugins){
            plugin.deregister();
        }
        initPyEnv();
    }
    public static <T extends HttpRequest> ArrayList<Object> invokePyRequest(T httpRequest, Annotations annotations, String pyFuncName){
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(httpRequest);
        pythonArguments[1] = Py.java2py(annotations);
        PyObject result = py_functions.get(pyFuncName).__call__(pythonArguments);
        HttpRequest newHttpRequest;
        if (result instanceof PyTuple tupleResult) {
            newHttpRequest = (HttpRequest) tupleResult.__getitem__(0).__tojava__(HttpRequest.class);
            annotations = (Annotations) tupleResult.__getitem__(1).__tojava__(Annotations.class);
        } else {
            newHttpRequest = (HttpRequest) result.__tojava__( HttpRequest.class);
        }
        return new ArrayList<>(List.of(newHttpRequest, annotations));
    }
    public static <T extends HttpResponse> ArrayList<Object> invokePyResponse(T httpResponse, Annotations annotations, String pyFuncName){
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(httpResponse);
        pythonArguments[1] = Py.java2py(annotations);
        PyObject result =  py_functions.get(pyFuncName).__call__(pythonArguments);
        HttpResponse newHttpResponse;
        if (result instanceof PyTuple tupleResult) {
            newHttpResponse = (HttpResponse) tupleResult.__getitem__(0).__tojava__(HttpResponse.class);
            annotations = (Annotations) tupleResult.__getitem__(1).__tojava__(Annotations.class);
        } else {
            newHttpResponse = (HttpResponse) result.__tojava__( HttpResponse.class);
        }
        return new ArrayList<>(List.of(newHttpResponse, annotations));
    }
    public static boolean isPrefixAllowed(String url){
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