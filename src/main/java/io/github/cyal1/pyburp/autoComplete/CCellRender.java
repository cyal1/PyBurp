package io.github.cyal1.pyburp.autoComplete;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

import javax.swing.*;
import java.awt.*;

public class CCellRender extends CompletionCellRenderer {
    private final Icon variableIcon;
    private final Icon functionIcon;
    private final Icon macroIcon;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        // 调用父类方法获取基础状态
        super.getListCellRendererComponent(list, value, index, selected, hasFocus);

        // 关键：强制开启 HTML 渲染支持
        // Burp 的 UI 可能把这个属性关掉来提高性能或统一风格
        putClientProperty("html.disable", null);

        // 确保组件能根据 HTML 内容计算正确的高度
//        putClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey, null);


        return this;
    }

    public CCellRender() {
        // 1. 不要使用外部传入的 delegate，直接断开与 Burp 定制渲染器的联系
        super();

        // 2. 强制重置 UI，防止 Burp 的全局 LookAndFeel 注入定制的 LabelUI
        setUI(new javax.swing.plaf.basic.BasicLabelUI());

        // 3. 设置为不透明，确保背景颜色受控
        setOpaque(true);

        // 4. 加载图片（注意路径：建议加上开头的斜杠）
        variableIcon = getIcon("/img/var.png");
        functionIcon = getIcon("/img/function.png");
        macroIcon = getIcon("/img/macro.png");
    }
    public CCellRender(DefaultListCellRenderer delegate) {
        setDelegateRenderer(delegate);
        variableIcon = getIcon("/img/var.png");
        functionIcon = getIcon("/img/function.png");
        macroIcon = getIcon("/img/macro.png");
    }
    @Override
    protected void prepareForFunctionCompletion(JList<?> list,
                                                FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(fc.getName());

        char paramListStart = fc.getProvider().getParameterListStart();
        if (paramListStart!=0) { // 0 => no start char
            sb.append(paramListStart);
        }

        int paramCount = fc.getParamCount();
        for (int i=0; i<paramCount; i++) {
            FunctionCompletion.Parameter param = fc.getParam(i);
            String type = param.getType();
            String name = param.getName();
            if (name!=null) {
                sb.append(name);
            }
            if (type!=null) {
                sb.append(": ");
                if (!selected) {
                    sb.append("<font color='").append("#aa0077").append("'>");
                }
                sb.append(type);
                if (!selected) {
                    sb.append("</font>");
                }
//				if (name!=null) {
//					sb.append(' ');
//				}
            }

            if (i<paramCount-1) {
                sb.append(fc.getProvider().getParameterListSeparator());
            }
        }

        char paramListEnd = fc.getProvider().getParameterListEnd();
        if (paramListEnd!=0) { // 0 => No parameter list end char
            sb.append(paramListEnd);
        }

        appendTypeIfNecessary(sb, fc);
        setText(sb.toString());
        setIcon(functionIcon);

    }

    @Override
    protected void prepareForOtherCompletion(JList<?> list,
                                             Completion c, int index, boolean selected, boolean hasFocus) {
        super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
        setIcon(getEmptyIcon());
    }


    @Override
    protected void prepareForVariableCompletion(JList<?> list,
                                                VariableCompletion vc, int index, boolean selected,
                                                boolean hasFocus) {
        super.prepareForVariableCompletion(list, vc, index, selected,
                hasFocus);
        setIcon(macroIcon);
    }

}
