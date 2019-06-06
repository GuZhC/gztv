package com.dfsx.lzcms.liveroom.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by liuwb on 2017/7/3.
 */
public class ChatMessage implements Serializable {
    private String text;
    private String[] chatImages;

    public ChatMessage() {

    }

    public static ChatMessage getTextMessage(String text) {
        ChatMessage message = new ChatMessage();
        message.setText(text);
        return message;
    }

    public static ChatMessage getImageMessage(String... images) {
        ChatMessage message = new ChatMessage();
        message.setChatImages(images);
        return message;
    }

    public static ChatMessage getTextAndImageMessage(String text, String... images) {
        ChatMessage message = new ChatMessage();
        message.setText(text);
        message.setChatImages(images);
        return message;
    }

    public String[] getChatImages() {
        return chatImages;
    }

    public void setChatImages(String[] chatImages) {
        this.chatImages = chatImages;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
