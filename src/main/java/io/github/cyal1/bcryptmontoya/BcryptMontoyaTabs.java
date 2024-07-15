package io.github.cyal1.bcryptmontoya;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

public class BcryptMontoyaTabs extends JPanel {
    public static int sequence = 2;
    private static JTabbedPane tabbedPane;

    public BcryptMontoyaTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.add(" 1 ", new BcryptMontoyaTab());
        tabbedPane.add("+", null);
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.getModel().addChangeListener(new ChangeListener() {
            private boolean ignore = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                if (!ignore) {
                    ignore = true;
                    try {
                        int selected = tabbedPane.getSelectedIndex();
                        String title = tabbedPane.getTitleAt(selected);
                        if ("+".equals(title)) {
                            BcryptMontoyaTab tab = new BcryptMontoyaTab();
                            String newTitle = " " + sequence + " ";
                            sequence++;
                            tabbedPane.insertTab(newTitle, null, tab, null,
                                    tabbedPane.getTabCount() - 1);
                            tabbedPane.setSelectedComponent(tab);
                        }
                    } finally {
                        ignore = false;
                    }
                }
            }
        });
    }

    public static void registerTabExtender(BcryptMontoyaTab tab){
        int tabId = tabbedPane.getSelectedIndex() + 1;
        tab.plugins = new ArrayList<>();
        if(tab.py_functions.containsKey("registerContextMenu")){
            BcryptMontoya.Api.logging().logToOutput("registerContextMenuItemsProvider tab:" + tabId);
            tab.plugins.add(BcryptMontoya.Api.userInterface().registerContextMenuItemsProvider(tab.myContextMenu));
        }

        if(tab.py_functions.containsKey("passiveAudit") || tab.py_functions.containsKey("activeAudit")){
            BcryptMontoya.Api.logging().logToOutput("registerScanCheck tab:" + tabId);
            tab.plugins.add(BcryptMontoya.Api.scanner().registerScanCheck(new MyScanCheck(tab)));
        }

        if(tab.py_functions.containsKey("handleRequest")|| tab.py_functions.containsKey("handleResponse")){
            BcryptMontoya.Api.logging().logToOutput("registerHttpHandler tab:" + tabId);
            tab.plugins.add(BcryptMontoya.Api.http().registerHttpHandler(new MyHttpHandler(tab)));
        }

        if(tab.py_functions.containsKey("handleProxyRequest")){
            BcryptMontoya.Api.logging().logToOutput("registerRequestHandler tab:" + tabId);
            tab.plugins.add(BcryptMontoya.Api.proxy().registerRequestHandler(new MyProxyRequestHandler(tab)));
        }

        if(tab.py_functions.containsKey("handleProxyResponse")){
            BcryptMontoya.Api.logging().logToOutput("registerResponseHandler tab:" + tabId);
            tab.plugins.add(BcryptMontoya.Api.proxy().registerResponseHandler(new MyProxyResponseHandler(tab)));
        }
    }

    public static void closeTab(){
        int selected = tabbedPane.getSelectedIndex();
        int tabCount = tabbedPane.getTabCount();
        if (selected != -1 && selected != tabbedPane.getTabCount() - 1 && tabCount > 2) {
            tabbedPane.remove(selected);
            if (selected == tabCount-2){
                tabbedPane.setSelectedIndex(tabCount-3);
            }
        }
    }

    public static void setTabColor(Color color){
        tabbedPane.setForegroundAt(tabbedPane.getSelectedIndex(), color);
    }
}
