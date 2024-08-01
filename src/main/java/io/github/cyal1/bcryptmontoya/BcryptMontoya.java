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
import burp.api.montoya.http.Http;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.utilities.Utilities;

import java.util.LinkedList;
import java.util.List;

public class BcryptMontoya implements BurpExtension
{
    public static MontoyaApi Api;
    public static Http http;
    public static Proxy proxy;
    public static Utilities Utils;
    public static Organizer organizer;

    @Override
    public void initialize(MontoyaApi api) {
        BcryptMontoya.Api = api;
        BcryptMontoya.http = api.http();
        BcryptMontoya.proxy = api.proxy();
        BcryptMontoya.organizer = api.organizer();
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        BcryptMontoya.Utils = api.utilities();
        api.extension().setName("BcryptMontoya");
        api.userInterface().registerSuiteTab("BcryptMontoya", new BcryptMontoyaTabs());
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());
    }

    public static void addIssue(AuditIssue auditIssue){
        Api.siteMap().add(auditIssue);
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