package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.ChatText;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by liuwb on 2016/12/23.
 */
public class ChatTextXmlParser extends AbsXmlStringParser<ChatText> {
    public ChatTextXmlParser(String xmlString) {
        super(xmlString);
    }

    @Override
    public ChatText parserXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        ChatText chatText = null;
        if (parser != null) {
            int eventType = parser.getEventType();// 获取事件类型
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                        chatText = new ChatText();
                        break;
                    case XmlPullParser.START_TAG://开始读取某个标签
                        //通过getName判断读到哪个标签，然后通过nextText()获取文本节点值，或通过getAttributeValue(i)获取属性节点值
                        String tagName = parser.getName();
                        if ("message".equals(tagName)) {
                            chatText.setName(parser.getAttributeValue("", "from"));
                        } else if ("body".equals(tagName)) {
                            chatText.setContent(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:// 结束元素事件
                        break;
                }
                eventType = parser.next();
            }
        }
        return chatText;
    }
}
