package io.github.cyal1.turboburp;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class TurboBurpTabs extends JFrame {
    public static int sequence = 2;
    private static JTabbedPane tabbedPane;
    public static JTextArea logTextArea;
    private static JSplitPane jSplitPane;
    public static CollaboratorClient collaboratorClient = createCollaboratorClient();
    public JMenu show;

    public TurboBurpTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.add(" 1 ", new TurboBurpTab());
        tabbedPane.add("+", null);
        jSplitPane = new JSplitPane();
        jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        logTextArea = new JTextArea(0,0);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane bottomScrollPane= new JScrollPane(logTextArea);
        bottomScrollPane.setBorder(null);
        jSplitPane.setTopComponent(tabbedPane);
        bottomScrollPane.setMinimumSize(new Dimension(0,0));
        jSplitPane.setBottomComponent(bottomScrollPane);
        jSplitPane.setResizeWeight(1.0);
        jSplitPane.setDividerLocation(1.0);
        this.setVisible(true);
        this.setSize(900,500);
        this.setTitle("Turbo Burp");
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(jSplitPane, BorderLayout.CENTER);

        show = new JMenu("Turbo Burp");
        JMenuItem showItem = new JMenuItem("Show");
        show.add(showItem);
        showItem.addActionListener(e -> this.setVisible(true));

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
                            TurboBurpTab tab = new TurboBurpTab();
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

    private static CollaboratorClient createCollaboratorClient()
    {
        CollaboratorClient collaboratorClient;

        String existingCollaboratorKey = TurboBurp.api.persistence().extensionData().getString("persisted_collaborator");

        if (existingCollaboratorKey != null)
        {
            TurboBurp.api.logging().logToOutput("Creating Collaborator client from key.");
            collaboratorClient = TurboBurp.api.collaborator().restoreClient(SecretKey.secretKey(existingCollaboratorKey));
        }
        else
        {
            TurboBurp.api.logging().logToOutput("No previously found Collaborator client. Creating new client...");
            collaboratorClient = TurboBurp.api.collaborator().createClient();

            // Save the secret key of the CollaboratorClient so that you can retrieve it later.
            TurboBurp.api.logging().logToOutput("Saving Collaborator secret key.");
            TurboBurp.api.persistence().extensionData().setString("persisted_collaborator", collaboratorClient.getSecretKey().toString());
        }

        return collaboratorClient;
    }


    public static void showLogConsole(){
        if (jSplitPane.getResizeWeight() > 0.9) {
            jSplitPane.setResizeWeight(0.8);
            jSplitPane.setDividerLocation(0.8);
        }
    }

    public static String getCurrentTabId(){
        return  tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    }
}
