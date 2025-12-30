package io.github.cyal1.pyburp.autoComplete;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;

public class CallbackFunctionCompletion extends FunctionCompletion{
    public CallbackFunctionCompletion(CompletionProvider provider, String name, String returnType) {
        super(provider, name, returnType);
    }
    @Override
    public String getDefinitionString() {
        StringBuilder sb = new StringBuilder();

        // Add the name of the described item
        sb.append(getName());

        // Add parameters for functions.
        CompletionProvider provider = getProvider();
        char start = provider.getParameterListStart();
        if (start!=0) {
            sb.append(start);
        }
        String type = "";
        for (int i=0; i<getParamCount(); i++) {
            Parameter param = getParam(i);
            type = param.getType();
            String name = param.getName();
            if (name!=null) {
                sb.append(name);
            }
            if (type!=null) {
                sb.append(": ").append(type);
//                if (name!=null) {
//                    sb.append(' ');
//                }
            }
            if (i<getParamCount()-1) {
                sb.append(provider.getParameterListSeparator());
            }
        }
        char end = provider.getParameterListEnd();
        if (end!=0) {
            sb.append(end);
        }
        // Add the return type if applicable (C macros like NULL have no type).
        type = getType();
        if (type!=null) {
            sb.append(" -> ").append(type);
        }
        return sb.toString();
    }

    @Override
    public String getReplacementText() {
        System.out.println(super.getReplacementText());
        return super.getReplacementText();
    }
}
