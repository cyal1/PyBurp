package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

// https://javadoc.io/doc/org.python/jython/2.7-b3/org/python/core/util/StringUtil.html
public class MyContextMenuItemsProvider implements ContextMenuItemsProvider {
    public EnumMap<MenuType, Map<String, PyFunction>> MENUS;

    public enum MenuType {
        CARET, // insertAtCursor lo4j
        SELECTED_TEXT, // md5 hex
        MESSAGE_EDITOR,
        REQUEST, // sendtoxray nosqlinjectioin, fuzz param
        REQUEST_RESPONSE, // checkCachePoisoning
    }

    public MyContextMenuItemsProvider() {
        MENUS = new EnumMap<>(MenuType.class);
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        if (BcryptMontoya.status != BcryptMontoya.STATUS.RUNNING || MENUS.size() == 0) {
            return null;
        }

        if (event.selectedRequestResponses().size() != 0) {
            List<Component> menus = registerIterm(MenuType.REQUEST, event);
            List<Component> requestResponseMenu = registerIterm(MenuType.REQUEST_RESPONSE, event);
            menus.addAll(requestResponseMenu);
            return menus;

        } else if (event.messageEditorRequestResponse().isPresent()) {

            MessageEditorHttpRequestResponse messageEditor = event.messageEditorRequestResponse().get();
            HttpRequest request = messageEditor.requestResponse().request();
            HttpResponse response = messageEditor.requestResponse().response();

            if (request.httpService() == null) {
                return null;
            }

            List<Component> messageEditorMenu = registerIterm(MenuType.MESSAGE_EDITOR, event);
            List<Component> menus = new ArrayList<>(messageEditorMenu);

            if(messageEditorMenu.size()!=0){
                menus.add(new JSeparator(JSeparator.HORIZONTAL));
            }

            // request
            List<Component> requestMenu = registerIterm(MenuType.REQUEST, event);
            menus.addAll(requestMenu);

            if (response != null) {
                List<Component> requestResponseMenu = registerIterm(MenuType.REQUEST_RESPONSE, event);
                menus.addAll(requestResponseMenu);
            }

            if(menus.size()!=0){
                menus.add(new JSeparator(JSeparator.HORIZONTAL));
            }

            // todo caret
            if (event.isFromTool(ToolType.REPEATER) && messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                List<Component> caretMenu = registerIterm(MenuType.CARET, event);

                menus.addAll(caretMenu);

                if(caretMenu.size() != 0 && messageEditor.selectionOffsets().isPresent()){
                    menus.add(new JSeparator(JSeparator.HORIZONTAL));
                }
            }

            // selected text
            if (messageEditor.selectionOffsets().isPresent()) {
                List<Component> selectTextMenu = registerIterm(MenuType.SELECTED_TEXT, event);
                menus.addAll(selectTextMenu);
            }
            return menus;
        }
        return null;
    }

    private List<Component> registerIterm(MenuType type, ContextMenuEvent event) {
        List<Component> menuItemList = new ArrayList<>();
        Map<String, PyFunction> typeMap = MENUS.get(type);
        if (typeMap != null && !typeMap.isEmpty()) {
            for (Map.Entry<String, PyFunction> entry : typeMap.entrySet()) {
                String funcName = entry.getKey();
                PyFunction func = entry.getValue();
                JMenuItem retrieveRequestItem = new JMenuItem(funcName);
                switch (type) {
                    case CARET -> {
                        retrieveRequestItem.addActionListener(e -> handleCaret(event, func));
                        menuItemList.add(retrieveRequestItem);
                    }
                    case SELECTED_TEXT -> {
                        retrieveRequestItem.addActionListener(e -> handleSelectText(event, func));
                        menuItemList.add(retrieveRequestItem);
                    }
                    case MESSAGE_EDITOR -> {
                        retrieveRequestItem.addActionListener(e -> handleMessageEditor(event, func));
                        menuItemList.add(retrieveRequestItem);
                    }
                    case REQUEST -> {
                        retrieveRequestItem.addActionListener(e -> handleRequest(event, func));
                        menuItemList.add(retrieveRequestItem);
                    }
                    case REQUEST_RESPONSE -> {
                        retrieveRequestItem.addActionListener(e -> handleRequestResponse(event, func));
                        menuItemList.add(retrieveRequestItem);
                    }
                    default -> throw new RuntimeException("new such Menu Type " + type);
                }
            }
        }
        return menuItemList;
    }

    public void register(String menuName, PyFunction callback, MenuType menuType) {
        Map<String, PyFunction> menuItemMap = MENUS.computeIfAbsent(menuType, k -> new LinkedHashMap<>());
        menuItemMap.put(menuName, callback);
    }

    public void handleRequest(ContextMenuEvent event, PyFunction func) {
        if (!event.selectedRequestResponses().isEmpty()) {
            for (HttpRequestResponse httpRequestResponse : event.selectedRequestResponses()) {
                HttpRequest req = httpRequestResponse.request();
                PyObject pythonArguments = Py.java2py(req);
                func.__call__(pythonArguments);
            }
        } else {
            PyObject pythonArguments = Py.java2py(event.messageEditorRequestResponse().get().requestResponse().request());
            func.__call__(pythonArguments);
        }
    }


    public void handleRequestResponse(ContextMenuEvent event, PyFunction func) {
        if (!event.selectedRequestResponses().isEmpty()) {
            for (HttpRequestResponse httpRequestResponse : event.selectedRequestResponses()) {
                HttpRequest req = httpRequestResponse.request();
                HttpResponse resp = httpRequestResponse.response();
                if (resp != null) {
                    PyObject[] pythonArguments = new PyObject[2];
                    pythonArguments[0] = Py.java2py(req);
                    pythonArguments[1] = Py.java2py(resp);
                    func.__call__(pythonArguments);
                }
            }
        } else {
            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().get().requestResponse();
            PyObject[] pythonArguments = new PyObject[2];
            pythonArguments[0] = Py.java2py(requestResponse.request());
            pythonArguments[1] = Py.java2py(requestResponse.response());
            func.__call__(pythonArguments);
        }
    }

    public void handleCaret(ContextMenuEvent event, PyFunction func) {
        MessageEditorHttpRequestResponse messageEditor = event.messageEditorRequestResponse().get();

        int currentPosition = messageEditor.caretPosition();

        HttpRequest request = messageEditor.requestResponse().request();
        ByteArray httpMessage = request.toByteArray();
        ByteArray firstSection = httpMessage.subArray(0, currentPosition);
        ByteArray lastSection;
        if (currentPosition != httpMessage.length()) {
            lastSection = httpMessage.subArray(currentPosition, httpMessage.length());
        } else {
            lastSection = ByteArray.byteArrayOfLength(0);
        }
        PyObject r = func.__call__();
        String newText = (String) r.__tojava__(String.class);
        messageEditor.setRequest(HttpRequest.httpRequest(firstSection.withAppended(newText).withAppended(lastSection)));
    }

    public void handleSelectText(ContextMenuEvent event, PyFunction func) {

        MessageEditorHttpRequestResponse messageEditor = event.messageEditorRequestResponse().get();
        ByteArray selectText = getSelectedText(messageEditor);
        if (event.isFromTool(ToolType.REPEATER) && messageEditor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
            PyObject pythonArguments = Py.java2py(selectText.toString());
            PyObject r = func.__call__(pythonArguments);
            String newText = (String) r.__tojava__(String.class);
            messageEditor.setRequest(replaceSelectedText(messageEditor, newText));
        } else {
            PyObject pythonArguments = Py.java2py(selectText.toString());
            PyObject r = func.__call__(pythonArguments);
            String newText = (String) r.__tojava__(String.class);
            JFrame parentFrame = new JFrame();
            JDialog dialog = new JDialog(parentFrame, "Editable Message Box", true);
            JTextArea textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setText(newText);
            JScrollPane scrollPane = new JScrollPane(textArea);
            dialog.getContentPane().add(scrollPane);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(500, 200);
            dialog.setLocationRelativeTo(parentFrame);
            dialog.setVisible(true);
        }
    }

    public void handleMessageEditor(ContextMenuEvent event, PyFunction func) {
            PyObject pythonArguments = Py.java2py(event.messageEditorRequestResponse().get());
            func.__call__(pythonArguments);
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
}
