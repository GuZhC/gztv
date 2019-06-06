package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * 聊天的文本模型
 * <p>
 * Created by liuwb on 2016/12/23.
 */
public class ChatText implements Serializable {

    private String name;

    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
