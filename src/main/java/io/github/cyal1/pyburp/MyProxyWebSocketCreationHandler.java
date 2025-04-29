package io.github.cyal1.pyburp;

import burp.api.montoya.proxy.websocket.*;
import org.python.core.Py;

import javax.swing.*;

public class MyProxyWebSocketCreationHandler implements ProxyWebSocketCreationHandler {
    private final PyBurpTab tab;
    public MyProxyWebSocketCreationHandler(PyBurpTab tab) {
        this.tab = tab;
    }

    @Override
    public void handleWebSocketCreation(ProxyWebSocketCreation webSocketCreation) {
        webSocketCreation.proxyWebSocket().registerProxyMessageHandler(new ProxyMessageHandler() {
            @Override
            public TextMessageReceivedAction handleTextMessageReceived(InterceptedTextMessage interceptedTextMessage) {
                try {
                    return (TextMessageReceivedAction) tab.py_functions.get("handleProxyWsTextMsg").__call__(Py.java2py(interceptedTextMessage)).__tojava__(TextMessageReceivedAction.class);
                }catch (Exception e){
                    SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
                }
                return TextMessageReceivedAction.continueWith(interceptedTextMessage);
            }

            @Override
            public TextMessageToBeSentAction handleTextMessageToBeSent(InterceptedTextMessage interceptedTextMessage) {
                return TextMessageToBeSentAction.continueWith(interceptedTextMessage);
            }

            @Override
            public BinaryMessageReceivedAction handleBinaryMessageReceived(InterceptedBinaryMessage interceptedBinaryMessage) {
                try {
                    return (BinaryMessageReceivedAction) tab.py_functions.get("handleProxyWsBinMsg").__call__(Py.java2py(interceptedBinaryMessage)).__tojava__(BinaryMessageReceivedAction.class);
                }catch (Exception e){
                    SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
                }
                return BinaryMessageReceivedAction.continueWith(interceptedBinaryMessage);
            }

            @Override
            public BinaryMessageToBeSentAction handleBinaryMessageToBeSent(InterceptedBinaryMessage interceptedBinaryMessage) {
                return BinaryMessageToBeSentAction.continueWith(interceptedBinaryMessage);
            }
        });
    }
}
