package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.MessageReceivedAction;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

public class MyProxyRequestHandler implements ProxyRequestHandler {

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        return new ProxyRequestReceivedAction() {
            @Override
            public MessageReceivedAction action() {
                return null;
            }

            @Override
            public HttpRequest request() {
                return BcryptMontoya.modifyHttpRequest(interceptedRequest,"handleProxyRequest");
            }

            @Override
            public Annotations annotations() {
                return null;
            }
        };
    }
    // after intercept
    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }
}
