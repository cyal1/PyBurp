package io.github.cyal1.pyburp;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static burp.api.montoya.core.ByteArray.byteArray;

public class Tools {
    public static String readFromInputStream(InputStream inputStream){
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultStringBuilder.toString();
    }
    public static HttpRequest replaceSelectedText(MessageEditorHttpRequestResponse messageEditor, String newString){
        HttpRequest request = messageEditor.requestResponse().request();
        if (messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST && messageEditor.selectionOffsets().isPresent()) {
            Optional<Range> selectionRange = messageEditor.selectionOffsets();
            if(selectionRange.isEmpty()){return request;}
            int startIndex = selectionRange.get().startIndexInclusive();
            int endIndex = selectionRange.get().endIndexExclusive();
            String httpMessage = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
            String firstSection = httpMessage.substring(0, startIndex);
            String lastSection;
            if (endIndex != httpMessage.length()) {
                lastSection = httpMessage.substring(endIndex);
            } else {
                lastSection = "";
            }
            request = HttpRequest.httpRequest(request.httpService(), byteArray((firstSection + newString + lastSection).getBytes()));
            request = request.withUpdatedHeader("Content-Length", String.valueOf(request.body().length()));
            return request;
        }
        return request;
    }

    public static String getSelectedText(MessageEditorHttpRequestResponse messageEditor){
        if (messageEditor.selectionOffsets().isPresent()) {
            HttpRequest request = messageEditor.requestResponse().request();
            HttpResponse response = messageEditor.requestResponse().response();
            Optional<Range> selectionRange = messageEditor.selectionOffsets();
            if (selectionRange.isEmpty()){
                return "";
            }
            String requestUtf8 = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
            String responseUtf8 = new String(response.toByteArray().getBytes(), StandardCharsets.UTF_8);
            if (messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST){
                return requestUtf8.substring(selectionRange.get().startIndexInclusive(), selectionRange.get().endIndexExclusive());
            }else{
                return responseUtf8.substring(selectionRange.get().startIndexInclusive(), selectionRange.get().endIndexExclusive());
            }
        }
        return "";
    }

    public static String getOOBCanary(){
        return PyBurpTabs.collaboratorClient.generatePayload().toString();
    }

    public static void addIssue(AuditIssue auditIssue){
        PyBurp.api.siteMap().add(auditIssue);
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

