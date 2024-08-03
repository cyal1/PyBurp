/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.turboburp;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.ArrayList;

public class MyHttpHandler implements HttpHandler
{
    TurboBurpTab turboBurpTab;

    public MyHttpHandler(TurboBurpTab turboBurpTab) {
        this.turboBurpTab = turboBurpTab;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {

        if(!turboBurpTab.py_functions.containsKey("handleRequest")){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
        }

        // url prefix allowed
        if(!turboBurpTab.isPrefixAllowed(httpRequestToBeSent.url())){
            return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
        }
        try{
            ArrayList<Object> array = turboBurpTab.invokePyRequest(httpRequestToBeSent, httpRequestToBeSent.annotations(), "handleRequest");
            return RequestToBeSentAction.continueWith((HttpRequest) array.get(0), (Annotations) array.get(1));
        }catch (Exception e){
            TurboBurpTabs.logTextArea.append(e.getMessage());
        }
        return RequestToBeSentAction.continueWith(httpRequestToBeSent, httpRequestToBeSent.annotations());
    }
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {

        if(!turboBurpTab.py_functions.containsKey("handleResponse")){
            return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
        }

        // url prefix allowed
        if(!turboBurpTab.isPrefixAllowed(httpResponseReceived.initiatingRequest().url())){
            return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
        }

        try{
            ArrayList<Object> array = turboBurpTab.invokePyResponse(httpResponseReceived, httpResponseReceived.annotations(), "handleResponse");
            return ResponseReceivedAction.continueWith((HttpResponse) array.get(0), (Annotations) array.get(1));
        }catch (Exception e){
            TurboBurpTabs.logTextArea.append(e.getMessage());
        }
        return ResponseReceivedAction.continueWith(httpResponseReceived, httpResponseReceived.annotations());
    }
}
