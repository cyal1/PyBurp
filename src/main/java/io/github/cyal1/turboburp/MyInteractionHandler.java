package io.github.cyal1.turboburp;

import burp.api.montoya.collaborator.Interaction;
import io.github.cyal1.turboburp.poller.InteractionHandler;
import org.python.core.Py;

public class MyInteractionHandler implements InteractionHandler {
    TurboBurpTab turboBurpTab;
    public MyInteractionHandler(TurboBurpTab turboBurpTab) {
        this.turboBurpTab = turboBurpTab;
    }

    @Override
    public void handleInteraction(Interaction interaction) {
        try {
            turboBurpTab.py_functions.get("handleInteraction").__call__(Py.java2py(interaction));
        }catch (Exception e){
            TurboBurpTabs.logTextArea.append(e.getMessage());
        }
    }
}
