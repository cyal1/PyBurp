package io.github.cyal1.bcryptmontoya;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ContentTypeConverter {
    public Context context;
    public Scriptable scope;
    public ContentTypeConverter() {
        context = Context.enter();
        scope = context.initStandardObjects();
        String jsCode = BcryptMontoyaUI.readFromInputStream(BcryptMontoya.class.getResourceAsStream("/qs.js"));
        context.evaluateString(scope, jsCode, "JavaScript", 1, null);
    }

    public String queryString2JSON(String qs){
        String functionName = "convert2JSON";
        Object[] functionArgs = {qs};
        Object result = ScriptableObject.callMethod(scope, functionName, functionArgs);
        return Context.toString(result);
    }
    public String json2QueryString(String jsonString){
        String functionName = "convert2qs";
        jsonString = jsonString.replace("'", "\\'");
        Context context1 = Context.enter();
        Object jsonResult = context1.evaluateString(scope, "JSON.parse('" + jsonString + "')", "JavaScript", 1, null);
        context1.close();
        Object[] functionArgs = {jsonResult};
        Object result = ScriptableObject.callMethod(scope, functionName, functionArgs);
        return Context.toString(result);
    }
    public String json2XML(String jsonString) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        StringBuilder xml = new StringBuilder();
        xml.append("<root>");
        Object item = new JSONTokener(jsonString).nextValue();
        xml.append(XML.toString(item));
        xml.append("</root>");
        DocumentBuilder builder = factory.newDocumentBuilder();

        ByteArrayInputStream input = new ByteArrayInputStream(xml.toString().getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(input);
        return prettyPrint(doc);
    }
    public String xml2JSON(String xmlString){
        try{
            JSONObject xmlJSONObject = XML.toJSONObject(xmlString);
            JSONObject jo = new JSONObject( xmlJSONObject.toString() );
            return jo.get("root").toString();
        }catch (Exception ignored){
        }
        return "";
    }
    public String prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        return (out.toString());
    }

//    public static void main(String[] args) {
//        String jsonString = "{\"a\":\"b'\"}";
//        String qsString = "a=b%27";
//
//        String a = new ContentTypeConverter().queryString2JSON(qsString);
//        System.out.println(a);
//    }
}
