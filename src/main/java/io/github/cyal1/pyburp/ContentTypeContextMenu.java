package io.github.cyal1.pyburp;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.ContentType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static burp.api.montoya.core.ByteArray.byteArray;

public class ContentTypeContextMenu implements ContextMenuItemsProvider {
    JMenuItem convert2json,convert2xml,convert2querystring;
    ContentTypeConverter  nestContentTypeConvert;
    public ContentTypeContextMenu() {
        nestContentTypeConvert = new ContentTypeConverter();
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        if(!event.invocationType().containsHttpMessage()){
            return null;
        }
        if(event.messageEditorRequestResponse().isPresent()){
            MessageEditorHttpRequestResponse messageEditor = event.messageEditorRequestResponse().get();
            HttpRequest request = messageEditor.requestResponse().request();
            List<Component> menuItemList = new ArrayList<>();
            if(event.isFromTool(ToolType.REPEATER) && (request.contentType() == ContentType.JSON||request.contentType() == ContentType.URL_ENCODED||request.contentType() == ContentType.XML)){
                    convert2json = new JMenuItem("Convert to JSON");
                    convert2xml = new JMenuItem("Convert to XML");
                    convert2querystring = new JMenuItem("Convert to QueryString");
                    contentTypeConvert(messageEditor, request);
                    menuItemList.add(convert2json);
                    menuItemList.add(convert2xml);
                    menuItemList.add(convert2querystring);
                    menuItemList.add(new JSeparator(JSeparator.HORIZONTAL));
                    return menuItemList;
            }
        }
        return null;
    }
    private void contentTypeConvert(MessageEditorHttpRequestResponse messageEditor, HttpRequest request){
        this.convert2json.addActionListener(e -> {
            String body = new String(request.body().getBytes(), StandardCharsets.UTF_8);
            if(request.contentType() == ContentType.URL_ENCODED){
                String newBody = nestContentTypeConvert.queryString2JSON(body);
                messageEditor.setRequest(request.withHeader("Content-Type","application/json;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
            }
            else if(request.contentType() == ContentType.XML){
                String newBody = nestContentTypeConvert.xml2JSON(body);
                messageEditor.setRequest(request.withHeader("Content-Type","application/json;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
            }
        });
        this.convert2xml.addActionListener(e -> {
            String body = new String(request.body().getBytes(), StandardCharsets.UTF_8);
            String newBody;
            if(request.contentType() == ContentType.JSON){
                try {
                    newBody = nestContentTypeConvert.json2XML(body);
                    messageEditor.setRequest(request.withHeader("Content-Type","application/xml;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
                } catch (Exception ex) {
                    PyBurpTabs.logTextArea.append(ex + "\n");
                }
            } else if (request.contentType() == ContentType.URL_ENCODED) {
                try {
                    newBody = nestContentTypeConvert.queryString2JSON(body);
                    newBody = nestContentTypeConvert.json2XML(newBody);
                    messageEditor.setRequest(request.withHeader("Content-Type","application/xml;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
                } catch (Exception ex) {
                    PyBurpTabs.logTextArea.append(ex + "\n");
                }
            }
        });
        this.convert2querystring.addActionListener(e -> {
            String body = new String(request.body().getBytes(), StandardCharsets.UTF_8);
            if(request.contentType() == ContentType.JSON){
                String newBody = nestContentTypeConvert.json2QueryString(body);
                messageEditor.setRequest(request.withHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
            } else if (request.contentType() == ContentType.XML) {
                String newBody = nestContentTypeConvert.xml2JSON(body);
                newBody = nestContentTypeConvert.json2QueryString(newBody);
                messageEditor.setRequest(request.withHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8").withBody(byteArray(newBody.getBytes())));
            }
        });
    }
}
