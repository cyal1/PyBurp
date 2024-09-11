package io.github.cyal1.pyburp;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.intruder.PayloadData;
import burp.api.montoya.intruder.PayloadProcessingResult;
import burp.api.montoya.intruder.PayloadProcessor;
import org.python.core.Py;
import org.python.core.PyObject;

import javax.swing.*;

import static burp.api.montoya.intruder.PayloadProcessingResult.usePayload;

public class MyPayloadProcessor implements PayloadProcessor
{
    private final PyBurpTab currentTab;
    private final String name;
    public MyPayloadProcessor(PyBurpTab currentTab)
    {
        this.currentTab = currentTab;
        this.name = "PyBurp tab" + PyBurpTabs.getCurrentTabId();
    }

    @Override
    public String displayName()
    {
        return this.name;
    }

    @Override
    public PayloadProcessingResult processPayload(PayloadData payloadData)
    {
        try{
            PyObject r = currentTab.py_functions.get("processPayload").__call__(Py.java2py(payloadData.currentPayload().toString()));
            return usePayload(ByteArray.byteArray((String) r.__tojava__(String.class)));
        }catch (Exception e){
            SwingUtilities.invokeLater(() -> PyBurpTabs.logTextArea.append(e + "\n"));
        }
        return usePayload(payloadData.currentPayload());
    }
}