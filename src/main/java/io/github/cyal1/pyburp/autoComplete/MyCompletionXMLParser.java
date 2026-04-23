package io.github.cyal1.pyburp.autoComplete;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionXMLParser;

public class MyCompletionXMLParser extends CompletionXMLParser {
    public MyCompletionXMLParser(CompletionProvider provider, ClassLoader cl) {
        super(provider, cl);
    }
}
