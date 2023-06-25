package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.ui.editor.extension.HttpResponseEditorProvider;
import org.json.JSONException;
import org.json.JSONObject;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class MyHttpResponseEditorProvider implements HttpResponseEditorProvider {

    @Override
    public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(EditorCreationContext editorCreationContext) {
        return new ExtensionProvidedHttpResponseEditor() {
            private final RawEditor responseEditor = BcryptMontoya.Api.userInterface().createRawEditor();
            private HttpRequestResponse httpRequestRequest;
            @Override
            public HttpResponse getResponse() {
                return httpRequestRequest.response();
            }

            @Override
            public void setRequestResponse(HttpRequestResponse httpRequestResponse) {
                httpRequestRequest = httpRequestResponse;
                String body = httpRequestResponse.response().bodyToString();
                try{
                    JSONObject jsonObj = new JSONObject(body);
                    this.responseEditor.setContents(ByteArray.byteArray(jsonObj.toString(2).getBytes(StandardCharsets.UTF_8)));
                } catch (JSONException e) {
                    this.responseEditor.setContents(httpRequestResponse.response().toByteArray());
                }
            }

            @Override
            public boolean isEnabledFor(HttpRequestResponse httpRequestResponse) {
                String body = httpRequestResponse.response().bodyToString().strip();
                return ((body.startsWith("{") && body.endsWith("}")) || (body.startsWith("[") && body.endsWith("]"))) &&
                        body.toLowerCase().contains(":\"\\u");
            }

            @Override
            public String caption() {
                return "Json";
            }

            @Override
            public Component uiComponent() {
                return responseEditor.uiComponent();
            }

            @Override
            public Selection selectedData() {
                return responseEditor.selection().isPresent() ? responseEditor.selection().get() : null;
            }

            @Override
            public boolean isModified() {
                return responseEditor.isModified();
            }
        };
    }
}
