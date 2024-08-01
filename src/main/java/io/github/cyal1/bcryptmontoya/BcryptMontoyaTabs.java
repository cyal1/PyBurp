package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class BcryptMontoyaTabs extends JPanel {
    public static int sequence = 2;
    private static JTabbedPane tabbedPane;

    public static CollaboratorClient collaboratorClient = createCollaboratorClient();

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

        String existingCollaboratorKey = BcryptMontoya.Api.persistence().extensionData().getString("persisted_collaborator");

        if (existingCollaboratorKey != null)
        {
            BcryptMontoya.Api.logging().logToOutput("Creating Collaborator client from key.");
            collaboratorClient = BcryptMontoya.Api.collaborator().restoreClient(SecretKey.secretKey(existingCollaboratorKey));
        }
        else
        {
            BcryptMontoya.Api.logging().logToOutput("No previously found Collaborator client. Creating new client...");
            collaboratorClient = BcryptMontoya.Api.collaborator().createClient();

            // Save the secret key of the CollaboratorClient so that you can retrieve it later.
            BcryptMontoya.Api.logging().logToOutput("Saving Collaborator secret key.");
            BcryptMontoya.Api.persistence().extensionData().setString("persisted_collaborator", collaboratorClient.getSecretKey().toString());
        }

        return collaboratorClient;
    }

    public static String getOOBUrl(){
        return collaboratorClient.generatePayload().toString();
    }
}
