package io.github.cyal1.pyburp;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 搜索功能管理类
 * 负责处理文本查找相关的所有功能
 */
public class SearchManager {
    private JDialog searchDialog;
    private JTextField searchField;
    private JCheckBox caseSensitiveCheckBox;
    private SearchContext searchContext;
    private RSyntaxTextArea textArea;
    
    public SearchManager(RSyntaxTextArea textArea) {
        this.textArea = textArea;
        this.searchContext = new SearchContext();
        this.searchContext.setMatchCase(false);
        this.searchContext.setWholeWord(false);
        this.searchContext.setSearchForward(true);
    }
    
    /**
     * 初始化搜索对话框
     */
    public void initSearchDialog(Frame mainFrame) {
        searchDialog = new JDialog(mainFrame, "Find", false);
        searchDialog.setSize(400, 120);
        searchDialog.setResizable(true);
        searchDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        searchDialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        searchDialog.getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDialog.setVisible(false);
            }
        });
        
        // 使用GridBagLayout实现精确布局
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Find label
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 10);
        mainPanel.add(new JLabel("Find:"), gbc);
        
        // Search field
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        searchField = new JTextField(20);
        mainPanel.add(searchField, gbc);
        
        // Case sensitive checkbox
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        caseSensitiveCheckBox = new JCheckBox("Case sensitive");
        mainPanel.add(caseSensitiveCheckBox, gbc);
        
        // Button panel (right side)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton findPrevBtn = new JButton("Previous");
        JButton findNextBtn = new JButton("Next");
        
        buttonPanel.add(findPrevBtn);
        buttonPanel.add(findNextBtn);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        searchDialog.add(mainPanel);
        
        // 事件绑定
        searchField.addActionListener(e -> performSearch(true));
        findNextBtn.addActionListener(e -> performSearch(true));
        findPrevBtn.addActionListener(e -> performSearch(false));
        
        searchDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                searchDialog.setVisible(false);
            }
        });
    }
    
    /**
     * 显示搜索对话框
     */
    public void showSearchDialog(Frame mainFrame) {
        if (searchDialog == null) {
            initSearchDialog(mainFrame);
        }
        searchDialog.setLocationRelativeTo(mainFrame);
        searchDialog.setVisible(true);
        searchField.requestFocusInWindow();
        searchField.selectAll();
    }
    
    /**
     * 执行搜索
     * @param forward 是否向前查找
     */
    public void performSearch(boolean forward) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(searchDialog, "Please input keywords！", "Note", JOptionPane.INFORMATION_MESSAGE);
            searchField.requestFocusInWindow();
            return;
        }

        // 更新搜索上下文
        searchContext = new SearchContext(searchText);
        searchContext.setMatchCase(caseSensitiveCheckBox.isSelected());
        searchContext.setWholeWord(false);
        searchContext.setSearchForward(forward);

        // 执行搜索
        SearchResult searchResult = SearchEngine.find(textArea, searchContext);

        // 处理未找到的情况
        if (searchResult == null) {
            // 循环查找：根据方向从相应位置开始重新搜索
            if (forward) {
                textArea.setCaretPosition(0);
            } else {
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            searchResult = SearchEngine.find(textArea, searchContext);

            if (searchResult == null) {
                JOptionPane.showMessageDialog(searchDialog, "Not Found：" + searchText, "Note", JOptionPane.INFORMATION_MESSAGE);
            } else {
                scrollToSelection();
            }
        } else {
            scrollToSelection();
        }
    }
    
    /**
     * 滚动到选中区域
     */
    private void scrollToSelection() {
        int selectionStart = textArea.getSelectionStart();
        if (selectionStart < 0 || selectionStart >= textArea.getText().length()) {
            return;
        }
        try {
            Rectangle selectionRect = textArea.modelToView(selectionStart);
            if (selectionRect != null) {
                textArea.scrollRectToVisible(selectionRect);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 绑定快捷键
     */
    public void bindSearchShortcut(JComponent component, Frame mainFrame) {
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog(mainFrame);
            }
        };

        String osName = System.getProperty("os.name").toLowerCase();
        String findShortcut = osName.contains("mac") ? "meta F" : "ctrl F";
        
        component.registerKeyboardAction(
                searchAction,
                KeyStroke.getKeyStroke(findShortcut),
                JComponent.WHEN_FOCUSED
        );
    }
    
    // Getter方法
    public JDialog getSearchDialog() {
        return searchDialog;
    }
    
    public JTextField getSearchField() {
        return searchField;
    }
    
    public JCheckBox getCaseSensitiveCheckBox() {
        return caseSensitiveCheckBox;
    }
}