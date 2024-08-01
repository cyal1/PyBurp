package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.collaborator.Interaction;
import io.github.cyal1.bcryptmontoya.poller.InteractionHandler;
import org.python.core.Py;

public class MyInteractionHandler implements InteractionHandler {
    BcryptMontoyaTab bcryptMontoyaTab;
    public MyInteractionHandler(BcryptMontoyaTab bcryptMontoyaTab) {
        this.bcryptMontoyaTab = bcryptMontoyaTab;
    }

    @Override
    public void handleInteraction(Interaction interaction) {
        try {
            bcryptMontoyaTab.py_functions.get("handleInteraction").__call__(Py.java2py(interaction));
        }catch (Exception e){
            BcryptMontoyaTabs.logTextArea.append(e.getMessage());
        }
    }
}
