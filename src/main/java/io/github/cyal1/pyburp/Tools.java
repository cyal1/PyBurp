package io.github.cyal1.pyburp;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
            int startIndex = selectionRange.get().startIndexInclusive();
            int endIndex = selectionRange.get().endIndexExclusive();
            ByteArray httpMessage = request.toByteArray();
            ByteArray firstSection = httpMessage.subArray(0, startIndex);
            ByteArray lastSection;
            if (endIndex != httpMessage.length()) {
                lastSection = httpMessage.subArray(endIndex, httpMessage.length());
            } else {
                lastSection = ByteArray.byteArray();
            }
            request = HttpRequest.httpRequest(request.httpService(), firstSection.withAppended(newString).withAppended(lastSection));
            if(request.body().length() != 0){
                request = request.withHeader("Content-Length", String.valueOf(request.body().length()));
            }
            return request;
        }
        return request;
    }

    public static ByteArray getSelectedText(MessageEditorHttpRequestResponse messageEditor){
        if (messageEditor.selectionOffsets().isPresent()) {
            HttpRequest request = messageEditor.requestResponse().request();
            HttpResponse response = messageEditor.requestResponse().response();
            Optional<Range> selectionRange = messageEditor.selectionOffsets();
            if (messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST){
                return request.toByteArray().subArray(selectionRange.get());
            }else{
                return response.toByteArray().subArray(selectionRange.get());
            }
        }
        return ByteArray.byteArray();
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

    public static JFrame getBurpFrame()
    {
        for(Frame f : Frame.getFrames())
        {
            if(f.isVisible() && f.getTitle().startsWith(("Burp Suite")))
            {
                return (JFrame) f;
            }
        }
        return null;
    }
}

