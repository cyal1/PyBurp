/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.ArrayList;

public class MyHttpHandler implements HttpHandler
{
    BcryptMontoyaTab bcryptMontoyaTab;

    public MyHttpHandler(BcryptMontoyaTab bcryptMontoyaTab) {
        this.bcryptMontoyaTab = bcryptMontoyaTab;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {

        if(!bcryptMontoyaTab.py_functions.containsKey("handleRequest")){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
        }

        // url prefix allowed
        if(!bcryptMontoyaTab.isPrefixAllowed(httpRequestToBeSent.url())){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
        }
        try{
            ArrayList<Object> array = bcryptMontoyaTab.invokePyRequest(httpRequestToBeSent, httpRequestToBeSent.annotations(), "handleRequest");
            return RequestToBeSentAction.continueWith((HttpRequest) array.get(0), (Annotations) array.get(1));
        }catch (Exception e){
            BcryptMontoyaTabs.logTextArea.append(e.getMessage());
        }
        return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
    }
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {

        if(!bcryptMontoyaTab.py_functions.containsKey("handleResponse")){
            return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
        }

        // url prefix allowed
        if(!bcryptMontoyaTab.isPrefixAllowed(httpResponseReceived.initiatingRequest().url())){
            return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
        }

        try{
            ArrayList<Object> array = bcryptMontoyaTab.invokePyResponse(httpResponseReceived, httpResponseReceived.annotations(), "handleResponse");
            return ResponseReceivedAction.continueWith((HttpResponse) array.get(0), (Annotations) array.get(1));
        }catch (Exception e){
            BcryptMontoyaTabs.logTextArea.append(e.getMessage());
        }
        return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
    }
}
