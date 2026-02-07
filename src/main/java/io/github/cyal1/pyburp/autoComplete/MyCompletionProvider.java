package io.github.cyal1.pyburp.autoComplete;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MyCompletionProvider extends DefaultCompletionProvider {
    @Override
    public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
        return super.getParameterizedCompletions(tc);
    }

    @Override
    public void loadFromXML(InputStream in, ClassLoader cl) throws IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }
        factory.setValidating(true);
        MyCompletionXMLParser handler = new MyCompletionXMLParser(this, cl);
        try (BufferedInputStream bin = new BufferedInputStream(in)) {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(bin, handler);
            List<Completion> completions = handler.getCompletions();
            addCompletions(completions);
            char startChar = handler.getParamStartChar();
            if (startChar != 0) {
                char endChar = handler.getParamEndChar();
                String sep = handler.getParamSeparator();
                // Sanity check.  Note endChar can be null
                if (sep != null && sep.length() > 0) {
                    setParameterizedCompletionParams(startChar, sep, endChar);
                }
            }
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e.toString());
        }
    }
}
