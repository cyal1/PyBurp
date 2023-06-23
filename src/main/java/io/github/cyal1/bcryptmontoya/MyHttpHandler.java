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
public class MyHttpHandler implements HttpHandler
{

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        return new RequestToBeSentAction() {
            @Override
            public HttpRequest request() {
              return BcryptMontoya.modifyHttpRequest(httpRequestToBeSent,"handleRequest");
            }

            @Override
            public Annotations annotations() {
                return null;
            }
        };
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return new ResponseReceivedAction() {
            @Override
            public HttpResponse response() {
                return BcryptMontoya.modifyHttpResponse(httpResponseReceived, "handleResponse");
            }

            @Override
            public Annotations annotations() {
                return null;
            }
        };
    }
}
