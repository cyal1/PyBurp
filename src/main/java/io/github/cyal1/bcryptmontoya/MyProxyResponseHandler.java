package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.MessageReceivedAction;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
public class MyProxyResponseHandler implements ProxyResponseHandler {

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return new ProxyResponseReceivedAction() {

            @Override
            public MessageReceivedAction action() {
                return null;
            }

            @Override
            public HttpResponse response() {
                return BcryptMontoya.modifyHttpResponse(interceptedResponse, "handleProxyResponse");
            }

            @Override
            public Annotations annotations() {
                return null;
            }
        };
    }

    // after intercept
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return null;
    }

}