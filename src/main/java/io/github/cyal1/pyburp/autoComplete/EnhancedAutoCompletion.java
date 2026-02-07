package io.github.cyal1.pyburp.autoComplete;

import org.fife.ui.autocomplete.*;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//import org.fife.ui.autocomplete.ParameterizedCompletionContext;

public class EnhancedAutoCompletion extends AutoCompletion {

    public EnhancedAutoCompletion(CompletionProvider provider) {

        super(provider);
    }

    @Override
    protected void insertCompletion(Completion c, boolean typedParamListStartChar) {
        forceFixHtml();
        super.insertCompletion(c, typedParamListStartChar);




    }

    /**
     * 重写此方法，在补全触发时进行拦截
     */


    public void forceFixHtml() {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. 反射获取私有的 pcc (ParameterizedCompletionContext)
                Field pccField = AutoCompletion.class.getDeclaredField("pcc");
                pccField.setAccessible(true);
                Object pcc = pccField.get(this);
                if (pcc != null) {
                    // 2. 获取私有的 tip (ParameterizedCompletionDescriptionToolTip)
                    Field tipField = pcc.getClass().getDeclaredField("tip");
                    tipField.setAccessible(true);
                    Object tip = tipField.get(pcc);
                    tipField.set(pcc, null);
                    return;

                    // 3. 获取 descLabel (JLabel)
                    // 关键修复：
                    // 1. 清除禁用标志
                    // 2. 强制触发 JLabel 的 HTML 解析引擎重新检查文本
                    // 很多人忽略了这一步：即使文本已经是以 <html> 开头，
                    // 如果首次设置时解析引擎没启动，后续必须手动重设文本
                    //                            if (text != null && !text.startsWith("<html>")) {
                    //                            }
                    //
                    //                            // 重新设置一次文本，强制 JLabel 检查 <html> 标签
                    //                            descLabel.setText(null);
                    // 3. 立即重绘
                    //                            if (descLabel.getMouseListeners().length == 0) { // 防止重复添加
                    //                                descLabel.addMouseListener(new MouseAdapter() {
                    //                                    @Override
                    //                                    public void mouseClicked(MouseEvent e) {
                    //                                        // 如果你只需要点击固定的链接，可以直接在这里触发
                    //                                        // 如果需要解析 HTML 里的多个链接，建议使用 JEditorPane
                    //                                        if (ac.getExternalURLHandler() != null) {
                    //                                            // 模拟一个简单的链接触发，或者在这里跳转
                    //                                            // 注意：由于 JLabel 无法精确获知点击了哪个 <a>，
                    //                                            // 建议直接调用你之前定义的 URL 处理器
                    //                                            System.out.println("检测到提示框点击，正在尝试跳转...");
                    //                                        }
                    //                                    }
                    //                                });
                    //                            }
                    // 4. 强制 JWindow 打包（防止首次显示时高度不对）
                    // 关键点 A: 允许窗口获取焦点（这样链接才能点击）
                    // 关键点 B: 确保内部的 JLabel 能处理 HTML 链接
                    // JLabel 默认不支持链接点击，我们需要它处于一个支持 HyperlinkListener 的组件中
                    // 但由于库限制，最快的方法是给 Label 添加鼠标监听器手动模拟点击，
                    // 或者将 descLabel 替换为 JEditorPane（风险较大）。
                }
            } catch (Exception ignored) {
                // 静默处理
            }
        });
    }
}
