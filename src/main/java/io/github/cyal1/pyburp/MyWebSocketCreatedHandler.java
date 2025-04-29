package io.github.cyal1.pyburp;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.websocket.*;
import org.python.core.*;

import javax.swing.*;

public class MyWebSocketCreatedHandler implements WebSocketCreatedHandler {
    private final PyBurpTab tab;
    public MyWebSocketCreatedHandler(PyBurpTab tab) {
        this.tab = tab;
    }

    @Override
    public void handleWebSocketCreated(WebSocketCreated webSocketCreated) {
        webSocketCreated.webSocket().registerMessageHandler(new MessageHandler() {
            @Override
            public TextMessageAction handleTextMessage(TextMessage textMessage) {
                try {
                    String result = tab.py_functions.get("handleWsTextMsg").__call__(Py.java2py(textMessage)).asString();
                    return TextMessageAction.continueWith(result);
                }catch (Exception e){
                    SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
                }
                return TextMessageAction.continueWith(textMessage);
            }

            @Override
            public BinaryMessageAction handleBinaryMessage(BinaryMessage binaryMessage) {
                try{
                    ByteArray result = (ByteArray) tab.py_functions.get("handleWsBinMsg").__call__(Py.java2py(binaryMessage)).__tojava__(ByteArray.class);
                    return BinaryMessageAction.continueWith(result);
                }catch (Exception e){
                    SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
                }
                return BinaryMessageAction.continueWith(binaryMessage);
            }
        });
    }
}
