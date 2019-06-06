package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2017/7/5.
 */
public class OnLineMember implements Serializable {
    private int totalNum;
    private List<ChatMember> chatMemberList;
    /**
     * 记录当前取得是那页数据
     */
    private int currentPage;

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<ChatMember> getChatMemberList() {
        return chatMemberList;
    }

    public void setChatMemberList(List<ChatMember> chatMemberList) {
        this.chatMemberList = chatMemberList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
