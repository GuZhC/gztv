package com.dfsx.lzcms.liveroom.business;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by liuwb on 2016/12/23.
 * 字符串（String）类型的XML格式解析
 */
public abstract class AbsXmlStringParser<D> {

    private XmlPullParser parser;
    private StringReader stringReader;
    private BufferedReader bufferedReaderReader;

    public AbsXmlStringParser(String xmlString) {
        parser = Xml.newPullParser();
        String xmlHear = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xmlString = xmlHear + xmlString;
        stringReader = new StringReader(xmlString);
        bufferedReaderReader = new BufferedReader(stringReader);
    }

    public D parserXml() {
        try {
            parser.setInput(bufferedReaderReader);
            return parserXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stringReader != null) {
                stringReader.close();
            }
            if (bufferedReaderReader != null) {
                try {
                    bufferedReaderReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public abstract D parserXml(XmlPullParser parser) throws XmlPullParserException, IOException;
}
