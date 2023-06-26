package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

public class MyProxyRequestHandler implements ProxyRequestHandler {

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        // todo drop && highlight
        return ProxyRequestReceivedAction.continueWith(BcryptMontoya.modifyHttpRequest(interceptedRequest,"handleProxyRequest"));
    }
    // after intercept
    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }
}
