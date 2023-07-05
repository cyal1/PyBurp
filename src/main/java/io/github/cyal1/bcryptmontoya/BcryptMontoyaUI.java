package io.github.cyal1.bcryptmontoya;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BcryptMontoyaUI extends JPanel {
    JButton saveButton = new JButton("Save");
    JButton runButton = new JButton("Run");
    JComboBox<Object> codeCombo = new JComboBox<>();
    JButton loadDirectoryButton = new JButton("Choose scripts dir");
    RSyntaxTextArea textEditor = new RSyntaxTextArea();

    public BcryptMontoyaUI() {
        javax.swing.text.JTextComponent.removeKeymap("RTextAreaKeymap");
        javax.swing.UIManager.put("RTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RTextAreaUI.actionMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        textEditor.setAntiAliasingEnabled(true);
        textEditor.setAutoIndentEnabled(true);
        textEditor.setPaintTabLines(true);
        textEditor.setTabSize(4);
        textEditor.setFont(textEditor.getFont().deriveFont(14.0F));
        textEditor.setCodeFoldingEnabled(true);
        textEditor.setTabsEmulated(true);
        textEditor.setLineWrap(true);
        textEditor.setHighlightCurrentLine(true);
//        textEditor.setWhitespaceVisible(true);
        RTextScrollPane scrollableTextEditor = new RTextScrollPane( textEditor );
        JPanel topPanel = new JPanel();
        codeCombo.setRenderer(new ComboBoxRenderer(6));
        codeCombo.setPreferredSize(new Dimension(400, 24));
        readScriptDirectories();
        topPanel.add(codeCombo);
        topPanel.add(loadDirectoryButton);
        saveButton.setEnabled(false);
        topPanel.add(saveButton);
        topPanel.add(runButton);
        textEditor.setText(getDefaultScript());
        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        this.add(scrollableTextEditor, BorderLayout.CENTER);

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
                textEditor.setText(getDefaultScript());
                readScriptDirectories();
                return;
            }
            String fileName = Objects.requireNonNull(codeCombo.getSelectedItem()).toString();
            if (fileName.toLowerCase().endsWith(".md")){
                textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
            }else{
                textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
            }
            if (fileName.startsWith("examples/")) {
                textEditor.setText(readFromInputStream(BcryptMontoya.class.getResourceAsStream("/"+fileName)));
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
                try {
                    textEditor.setText(new String(Files.readAllBytes(Paths.get(fileName))).replace("\r\n","\n"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            textEditor.setCaretPosition(0);
        });
    }

    public String getCode(){
        return textEditor.getText().replace("\r\n", "\n").replace("\n", "\r\n");
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
    String getDefaultScript(){
        String defaultScript = BcryptMontoya.Api.persistence().preferences().getString ("defaultScript");
        return Objects.requireNonNullElseGet(defaultScript, () -> readFromInputStream(BcryptMontoya.class.getResourceAsStream("/examples/default.py")));
    }

    public static String readFromInputStream(InputStream inputStream){
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultStringBuilder.toString();
    }
    public void setDarkTheme(){
        try {
//            dark,default-alt,default,druid,eclipse,idea,monokai,vs
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(textEditor);
            textEditor.setFont(textEditor.getFont().deriveFont(14.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
