package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import java.util.ArrayList;

public class MyProxyRequestHandler implements ProxyRequestHandler {
    BcryptMontoyaTab bcryptMontoyaTab;

    public MyProxyRequestHandler(BcryptMontoyaTab bcryptMontoyaTab) {
        this.bcryptMontoyaTab = bcryptMontoyaTab;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {

        if(bcryptMontoyaTab.getStatus() == BcryptMontoyaTab.STATUS.STOP || !bcryptMontoyaTab.py_functions.containsKey("handleProxyRequest")){
            return ProxyRequestReceivedAction.continueWith(interceptedRequest);
        }

        // url prefix allowed
        if(!bcryptMontoyaTab.isPrefixAllowed(interceptedRequest.url())){
            return ProxyRequestReceivedAction.continueWith(interceptedRequest);
        }
        // todo drop
        ArrayList<Object> array = bcryptMontoyaTab.invokePyRequest(interceptedRequest, interceptedRequest.annotations(), "handleProxyRequest");
        return ProxyRequestReceivedAction.continueWith((HttpRequest) array.get(0), (Annotations) array.get(1));
    }
    // after intercept
    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}
