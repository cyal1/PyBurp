package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.Registration;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import io.github.cyal1.bcryptmontoya.poller.Poller;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static io.github.cyal1.bcryptmontoya.BcryptMontoyaTabs.collaboratorClient;
import static io.github.cyal1.bcryptmontoya.BcryptMontoyaTabs.logTextArea;

public class BcryptMontoyaTab extends JPanel {
    public enum STATUS {
        RUNNING,
        STOP
    }
    public ArrayList<String> ALLOWED_URL_PREFIX;
    private STATUS status = STATUS.STOP;
    public MyContextMenuItemsProvider myContextMenu;
    public HashMap<String, PyFunction> py_functions;
    public ArrayList<Registration> plugins;
    private PythonInterpreter pyInterp;
    JButton saveButton = new JButton("Save");
    JButton runButton = new JButton("Run");
    JButton clearLogButton = new JButton("Clear logs");
    JComboBox<Object> codeCombo = new JComboBox<>();
    JButton loadDirectoryButton = new JButton("Choose scripts dir");
    JButton closeTab = new JButton("Close");
    RSyntaxTextArea codeEditor = new RSyntaxTextArea();


    public BcryptMontoyaTab(){
        javax.swing.text.JTextComponent.removeKeymap("RTextAreaKeymap");
        javax.swing.UIManager.put("RTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RTextAreaUI.actionMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        codeEditor.setAntiAliasingEnabled(true);
        codeEditor.setAutoIndentEnabled(true);
        codeEditor.setPaintTabLines(true);
        codeEditor.setTabSize(4);
        codeEditor.setFont(codeEditor.getFont().deriveFont(14.0F));
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setTabsEmulated(true);
        codeEditor.setLineWrap(true);
        codeEditor.setWrapStyleWord(true);
        codeEditor.setHighlightCurrentLine(true);
//        textEditor.setWhitespaceVisible(true);
        RTextScrollPane scrollableTextEditor = new RTextScrollPane( codeEditor );
        JPanel toolBar = new JPanel();
        codeCombo.setRenderer(new ComboBoxRenderer(6));
        codeCombo.setPreferredSize(new Dimension(400, 24));
        readScriptDirectories();
        toolBar.add(loadDirectoryButton);
        toolBar.add(codeCombo);
        saveButton.setEnabled(false);
        toolBar.add(saveButton);
        toolBar.add(runButton);
        toolBar.add(clearLogButton);
        toolBar.add(closeTab);
        JPanel topPane = new JPanel(new BorderLayout());
        topPane.add(toolBar, BorderLayout.NORTH);
        topPane.add(scrollableTextEditor, BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(topPane, BorderLayout.CENTER);

        codeEditor.setText(getDefaultScript());
        if(BcryptMontoya.Api.userInterface().currentTheme() == burp.api.montoya.ui.Theme.DARK){
            setDarkTheme();
        }
        ALLOWED_URL_PREFIX = new ArrayList<>();
        myContextMenu = new MyContextMenuItemsProvider(this);

        initPyEnv();


        runButton.addActionListener(e -> {
            if(status == STATUS.STOP){
                // do start things on STOP status while button clicked
                runBtnClick();
            }else{
                // do stop things on RUNNING status while button clicked
                stopBtnClick();
            }
        });

        clearLogButton.addActionListener(e -> {
            logTextArea.setText("");
        });

        closeTab.addActionListener(e -> {
            if (getStatus() == STATUS.RUNNING){
                stopBtnClick();
            }
            logTextArea.setText("");
            BcryptMontoyaTabs.closeTab();
        });

        loadDirectoryButton.addActionListener(e -> {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = directoryChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = directoryChooser.getSelectedFile();
                BcryptMontoya.Api.persistence().preferences().setString("scriptsPath", file.getAbsolutePath());
//                readScriptDirectories();
            }
        });

        saveButton.addActionListener(e -> {
            String comboItem = (String) codeCombo.getSelectedItem();
            assert comboItem != null;
            try {
                Files.write( Paths.get(comboItem), getCode().getBytes());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        codeCombo.addActionListener(e -> {
            if (codeCombo.getSelectedIndex() == 0) {
                saveButton.setEnabled(false);
                codeEditor.setText(getDefaultScript());
                readScriptDirectories();
                return;
            }
            String fileName = Objects.requireNonNull(codeCombo.getSelectedItem()).toString();
            if (fileName.toLowerCase().endsWith(".md")){
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
            }else{
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
            }
            if (fileName.startsWith("examples/")) {
                codeEditor.setText(Tools.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/"+fileName)));
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
                try {
                    codeEditor.setText(new String(Files.readAllBytes(Paths.get(fileName))).replace("\r\n","\n"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            codeEditor.setCaretPosition(0);
        });
    }

    private void runBtnClick(){
        new Thread(() -> {
        try{
            initPyEnv();
            pyInterp.exec(getCode());
            py_functions = getPyFunctions();
            registerTabExtender();
            PyObject pythonArguments = Py.java2py(myContextMenu);
            if(py_functions.containsKey("registerContextMenu")){
                py_functions.get("registerContextMenu").__call__(pythonArguments);
            }

            PyObject[] urls = Py.javas2pys(ALLOWED_URL_PREFIX);
            if(py_functions.containsKey("urlPrefixAllowed")){
                py_functions.get("urlPrefixAllowed").__call__(urls);
            }
        }catch (Exception ex){
            SwingUtilities.invokeLater(() -> {
                BcryptMontoyaTabs.showLogConsole();
                logTextArea.append(ex.getMessage() + "\n");
            });
//            JOptionPane.showMessageDialog(null, ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
            SwingUtilities.invokeLater(() -> {
                BcryptMontoyaTabs.showLogConsole();
                logTextArea.append("Tab " + BcryptMontoyaTabs.getCurrentTabId() + " is running\n");
                codeEditor.setHighlightCurrentLine(false);
                codeEditor.setEnabled(false);
                loadDirectoryButton.setEnabled(false);
                codeCombo.setEnabled(false);
                status = STATUS.RUNNING;
                runButton.setText("Stop");
                BcryptMontoya.Api.persistence().preferences().setString("defaultScript", getCode().replace("\r\n", "\n"));
                BcryptMontoyaTabs.setTabColor(Color.decode("#ec6033"));
            });
        }).start();
    }

    public void stopBtnClick(){
        try {
            if (py_functions.containsKey("finish")){
                py_functions.get("finish").__call__();
            }
        } catch (Exception e){
            logTextArea.append(e.getMessage() + "\n");
        }
        myContextMenu.MENUS.clear();
        ALLOWED_URL_PREFIX.clear();
        py_functions.clear();
        pyInterp.cleanup();
        pyInterp.close();
        codeEditor.setHighlightCurrentLine(true);
        codeEditor.setEnabled(true);
        loadDirectoryButton.setEnabled(true);
        codeCombo.setEnabled(true);
        status = STATUS.STOP;
        runButton.setText("Run");
        for(Registration plugin: plugins){
            plugin.deregister();
        }
        BcryptMontoyaTabs.setTabColor(Color.black);
//        initPyEnv();
        logTextArea.append("Tab " + BcryptMontoyaTabs.getCurrentTabId() + " is stopped\n");
    }

    private void initPyEnv(){
        pyInterp = new PythonInterpreter();
        PrintStream printStream = new PrintStream(new OutputStream() {
            final int MAX_LINES = 10000;
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(() -> {
                    logTextArea.append(String.valueOf((char) b));
                    if (logTextArea.getLineCount() > MAX_LINES) {
                        removeLines(logTextArea, logTextArea.getLineCount() - MAX_LINES);
                    }
                });
            }
        });
//        System.setOut(printStream);
//        System.setErr(printStream);
        pyInterp.setOut(printStream);
        pyInterp.setErr(printStream);
        pyInterp.exec(Tools.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/initEnv.py")));
    }

    private static void removeLines(JTextArea textArea, int linesToRemove) {
        try {
            int endPos = textArea.getLineEndOffset(linesToRemove - 1);
            int startPos = textArea.getLineStartOffset(0);
            textArea.replaceRange("", startPos, endPos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getCode(){
        return codeEditor.getText().replace("\r\n", "\n").replace("\n", "\r\n");
    }

    private ArrayList<String> getExamplesFiles(){
        ArrayList<String> fileNames = new ArrayList<>();
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        final JarFile jar;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        while(entries.hasMoreElements()) {
            final String name = entries.nextElement().getName();
            if (name.startsWith("examples/")) {
                fileNames.add(name);
            }
        }
        try {
            jar.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileNames;
    }

    private void readScriptDirectories(){
        codeCombo.removeAllItems();
        codeCombo.addItem("Last code used");
        String scriptsPath = BcryptMontoya.Api.persistence().preferences().getString("scriptsPath");
        if(scriptsPath != null && !scriptsPath.isEmpty()){
            File folder = new File(scriptsPath);
            if (folder.isDirectory()) {
                File[] folderList = folder.listFiles();
                assert folderList != null;
                Arrays.sort(folderList);
                for (File file : folderList) {
                    if (!file.getName().startsWith(".")) {
                        codeCombo.addItem(folder.getAbsolutePath() + "/" + file.getName());
                    }
                }
            }
        }
        codeCombo.addItem(new JSeparator(JSeparator.HORIZONTAL));
        ArrayList<String> files = getExamplesFiles();
        Collections.sort(files);
        for (String fileName : files) {
            codeCombo.addItem(fileName);
        }
    }
    public String getDefaultScript(){
        String defaultScript = BcryptMontoya.Api.persistence().preferences().getString ("defaultScript");
        return Objects.requireNonNullElseGet(defaultScript, () -> Tools.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/examples/default.py")));
    }

    public void setDarkTheme(){
        try {
//            dark,default-alt,default,druid,eclipse,idea,monokai,vs
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(codeEditor);
            codeEditor.setFont(codeEditor.getFont().deriveFont(14.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPrefixAllowed(String url){
        if (ALLOWED_URL_PREFIX.size() == 0){
            return true;
        }
        for(String u: ALLOWED_URL_PREFIX){
            if(url.startsWith(u)){
                return true;
            }
        }
        return false;
    }

    public HashMap<String, PyFunction> getPyFunctions(){
        ArrayList<String> MontoyaPyFunctions = new ArrayList<>(Arrays.asList(
                "registerContextMenu",
                "passiveAudit","activeAudit",
                "handleRequest", "handleResponse",
                "handleProxyRequest", "handleProxyResponse",
                "urlPrefixAllowed",
                "handleInteraction",
                "finish"
        ));

        HashMap<String,PyFunction> functionList = new HashMap<>();
        String get_function_names_code = "[(name,obj) for name, obj in globals().items() if callable(obj) and not name.startswith('__')]";

        PyObject pyFunctionList = pyInterp.eval(get_function_names_code);
        if (pyFunctionList instanceof PyList) {
            for (Object pyTuple : (PyList)pyFunctionList) {
                if (pyTuple instanceof PyTuple) {
                    String functionName = ((PyTuple) pyTuple).__getitem__(0).toString();
                    PyObject functionObject = ((PyTuple) pyTuple).__getitem__(1);
                    if (MontoyaPyFunctions.contains(functionName) && functionObject instanceof PyFunction pyFunction) {
                        functionList.put(functionName, pyFunction);
                    }
//                        else if (functionObject instanceof PyJavaType) {
//                            PyFunction pyFunction = (PyFunction) ((PyJavaType) functionObject).__call__();
//                            String functionName = ((PyTuple) pyTuple).__getitem__(0).toString();
//                            functionList.put(functionName, pyFunction);
//                        }
                }
            }
        }
        return functionList;
    }

    public ArrayList<Object> invokePyRequest(HttpRequest httpRequest, Annotations annotations, String pyFuncName){
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(httpRequest);
        pythonArguments[1] = Py.java2py(annotations);
        PyTuple result = (PyTuple) py_functions.get(pyFuncName).__call__(pythonArguments);
        HttpRequest newHttpRequest;
        if(result.__len__() != 2){
            logTextArea.append(pyFuncName+ " return type error\n");
            return new ArrayList<>(List.of(httpRequest, annotations));
        }
        newHttpRequest = (HttpRequest) result.get(0);
        annotations = (Annotations) result.get(1);
        return new ArrayList<>(List.of(newHttpRequest, annotations));
    }

    public ArrayList<Object> invokePyResponse (HttpResponse httpResponse, Annotations annotations, String pyFuncName){
        PyObject[] pythonArguments = new PyObject[2];
        pythonArguments[0] = Py.java2py(httpResponse);
        pythonArguments[1] = Py.java2py(annotations);
        PyTuple result = (PyTuple) py_functions.get(pyFuncName).__call__(pythonArguments);
        HttpResponse newHttpResponse;
        // jython need to return response and annotations
        if (result.__len__() != 2){
            logTextArea.append(pyFuncName+ " return type error\n");
            return new ArrayList<>(List.of(httpResponse, annotations));
        }
        newHttpResponse = (HttpResponse) result.get(0);
        annotations = (Annotations) result.get(1);
        return new ArrayList<>(List.of(newHttpResponse, annotations));
    }

    public STATUS getStatus(){
        return this.status;
    }

    public void registerTabExtender(){
        plugins = new ArrayList<>();
        if(py_functions.containsKey("registerContextMenu")){
            plugins.add(BcryptMontoya.Api.userInterface().registerContextMenuItemsProvider(myContextMenu));
        }

        if(py_functions.containsKey("passiveAudit") || py_functions.containsKey("activeAudit")){
            plugins.add(BcryptMontoya.Api.scanner().registerScanCheck(new MyScanCheck(this)));
        }

        if(py_functions.containsKey("handleRequest") || py_functions.containsKey("handleResponse")){
            plugins.add(BcryptMontoya.Api.http().registerHttpHandler(new MyHttpHandler(this)));
        }

        if(py_functions.containsKey("handleProxyRequest")){
            plugins.add(BcryptMontoya.Api.proxy().registerRequestHandler(new MyProxyRequestHandler(this)));
        }

        if(py_functions.containsKey("handleProxyResponse")){
            plugins.add(BcryptMontoya.Api.proxy().registerResponseHandler(new MyProxyResponseHandler(this)));
        }

        if(py_functions.containsKey("handleInteraction")){
            Poller collaboratorPoller = new Poller(collaboratorClient, Duration.ofSeconds(10));
            collaboratorPoller.registerInteractionHandler(new MyInteractionHandler(this));
            collaboratorPoller.start();
            plugins.add(collaboratorPoller);
        }
    }
}
