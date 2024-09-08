package io.github.cyal1.pyburp;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import javax.swing.*;
import java.util.ArrayList;

public class MyProxyRequestHandler implements ProxyRequestHandler {
    PyBurpTab pyBurpTab;

    public MyProxyRequestHandler(PyBurpTab pyBurpTab) {
        this.pyBurpTab = pyBurpTab;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {

//        if(!pyBurpTab.py_functions.containsKey("handleProxyRequest")){
//            return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations());
//        }

        // url prefix allowed
        if(!pyBurpTab.isPrefixAllowed(interceptedRequest.url())){
            return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations());
        }

        // todo drop
        try{
            ArrayList<Object> array = pyBurpTab.invokePyRequest(interceptedRequest, interceptedRequest.annotations(), "handleProxyRequest");
            return ProxyRequestReceivedAction.continueWith((HttpRequest) array.get(0), (Annotations) array.get(1));
        }catch (Exception e){
            SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
        }
        return ProxyRequestReceivedAction.continueWith(interceptedRequest, interceptedRequest.annotations());
    }
    // after intercept
    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest, interceptedRequest.annotations());
    }
}
