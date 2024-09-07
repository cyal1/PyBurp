package io.github.cyal1.pyburp;

import burp.api.montoya.collaborator.Interaction;
import io.github.cyal1.pyburp.poller.InteractionHandler;
import org.python.core.Py;

public class MyInteractionHandler implements InteractionHandler {
    PyBurpTab pyBurpTab;
    public MyInteractionHandler(PyBurpTab pyBurpTab) {
        this.pyBurpTab = pyBurpTab;
    }

    @Override
    public void handleInteraction(Interaction interaction) {
        try {
            pyBurpTab.py_functions.get("handleInteraction").__call__(Py.java2py(interaction));
        }catch (Exception e){
            PyBurpTabs.logTextArea.append(e.getMessage());
        }
    }
}
