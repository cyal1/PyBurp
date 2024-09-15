package io.github.cyal1.pyburp;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import javax.net.ssl.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static burp.api.montoya.core.ByteArray.byteArray;

public class Tools {
    public static String readFromInputStream(InputStream inputStream) {
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

    public static HttpRequest replaceSelectedText(MessageEditorHttpRequestResponse messageEditor, String newString) {
        HttpRequest request = messageEditor.requestResponse().request();
        if (messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST && messageEditor.selectionOffsets().isPresent()) {
            Optional<Range> selectionRange = messageEditor.selectionOffsets();
            if (selectionRange.isEmpty()) {
                return request;
            }
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

    public static String getSelectedText(MessageEditorHttpRequestResponse messageEditor) {
        if (messageEditor.selectionOffsets().isPresent()) {
            HttpRequest request = messageEditor.requestResponse().request();
            HttpResponse response = messageEditor.requestResponse().response();
            Optional<Range> selectionRange = messageEditor.selectionOffsets();
            if (selectionRange.isEmpty()) {
                return "";
            }
            String requestUtf8 = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
            String responseUtf8 = new String(response.toByteArray().getBytes(), StandardCharsets.UTF_8);
            if (messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                return requestUtf8.substring(selectionRange.get().startIndexInclusive(), selectionRange.get().endIndexExclusive());
            } else {
                return responseUtf8.substring(selectionRange.get().startIndexInclusive(), selectionRange.get().endIndexExclusive());
            }
        }
        return "";
    }

    public static String getOOBCanary() {
        return PyBurpTabs.collaboratorClient.generatePayload().toString();
    }

    public static void addIssue(AuditIssue auditIssue) {
        PyBurp.api.siteMap().add(auditIssue);
    }

    public static List<Marker> getResponseHighlights(HttpRequestResponse requestResponse, String match) {
        List<Marker> highlights = new LinkedList<>();
        String response = requestResponse.response().toString();

        int start = 0;

        while (start < response.length()) {
            start = response.indexOf(match, start);

            if (start == -1) {
                break;
            }

            Marker marker = Marker.marker(start, start + match.length());
            highlights.add(marker);

            start += match.length();
        }
        return highlights;
    }

    public static void sendWithProxy(HttpRequest request, String host, int port) {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Network requests must not be executed on the event dispatch thread, please ensure that you perform network operations in a separate thread or an asynchronous task.");
        }
        try {
            // Set up a TrustManager that trusts all certificates
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Trust all certificates
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Trust all certificates
                        }
                    }
            };
            // Set up an SSL context to use the trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Set up hostname verification to ignore hostnames
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> {
                // Trust all hostnames
                return true;
            });
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            String urlString = request.url();
            ByteArray body = request.body();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection(proxy);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                for (HttpHeader header : request.headers()) {
                    connection.setRequestProperty(header.name(), header.value());
                }
                connection.setRequestMethod(request.method());
                if (!request.method().equals("GET")) {
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(body.getBytes());
                        os.flush();
                    }
                }
                int responseCode = connection.getResponseCode();
                SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append("Send with proxy success, url: " + urlString + " => " + responseCode + "\n"));
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append("Send with proxy error! url: " + request.url() + " " + e + "\n"));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}


