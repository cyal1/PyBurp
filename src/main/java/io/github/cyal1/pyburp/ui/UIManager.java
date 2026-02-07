package io.github.cyal1.pyburp.ui;

import io.github.cyal1.pyburp.ComboBoxRenderer;
import io.github.cyal1.pyburp.PyBurp;

import javax.swing.*;
import java.awt.*;

/**
 * UI组件管理器
 * 负责UI组件的创建、配置和管理
 */
public class UIManager {
    private final Component parent;
    
    public UIManager(Component parent) {
        this.parent = parent;
    }
    
    /**
     * 创建工具栏面板
     */
    public JPanel createToolBar(JComboBox<Object> codeCombo, JButton... buttons) {
        JPanel toolBar = new JPanel();
        codeCombo.setRenderer(new ComboBoxRenderer(6));
        codeCombo.setPreferredSize(new Dimension(400, 24));
        
        toolBar.add(createButton("Choose scripts dir"));
        
        for (JButton button : buttons) {
            toolBar.add(button);
        }
        
        return toolBar;
    }
    
    /**
     * 创建标准按钮
     */
    public JButton createButton(String text) {
        return new JButton(text);
    }
    
    /**
     * 创建带滚动条的文本编辑器面板
     */
    public JScrollPane createScrollableTextEditor(JComponent textComponent) {
        return new JScrollPane(textComponent);
    }
    
    /**
     * 创建主面板布局
     */
    public JPanel createMainPanel(JComponent toolBar, JComponent content) {
        JPanel topPane = new JPanel(new BorderLayout());
        topPane.add(toolBar, BorderLayout.NORTH);
        topPane.add(content, BorderLayout.CENTER);
        return topPane;
    }
    
    /**
     * 应用深色主题
     */
    public void applyDarkTheme(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea textArea) {
        try {
            org.fife.ui.rsyntaxtextarea.Theme theme = 
                org.fife.ui.rsyntaxtextarea.Theme.load(
                    getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(textArea);
            textArea.setFont(textArea.getFont().deriveFont(14.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 检查是否应该应用深色主题
     */
    public boolean shouldApplyDarkTheme() {
        return PyBurp.api.userInterface().currentTheme() == burp.api.montoya.ui.Theme.DARK;
    }
}