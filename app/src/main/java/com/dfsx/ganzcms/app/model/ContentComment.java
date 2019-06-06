package com.dfsx.ganzcms.app.model;

import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.model.IChatData;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentComment implements IChatData {


    /**
     * id : long, 评论ID
     * author_id : long, 作者ID
     * author_name : string, 作者名称
     * author_nickname : string, 作者呢称
     * author_avatar_url : string, 作者头像地址
     * text : string, 评论内容
     * creation_time : long, 引用评论创建时间
     * like_count : long, 点赞数
     * dislike_count : long, 点踩数
     * closed : bool, 评论是否被关闭
     */

    private long id;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private String text;
    private long creation_time;
    private long like_count;
    private long dislike_count;
    private boolean closed;
    @SerializedName("ref_comments")
    private ContentComment referenceComment;
    private long sub_comment_count;
    @SerializedName("sub_comments")
    private List<ContentComment> subCommentList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }

    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getLike_count() {
        return like_count;
    }

    public void setLike_count(long like_count) {
        this.like_count = like_count;
    }

    public long getDislike_count() {
        return dislike_count;
    }

    public void setDislike_count(long dislike_count) {
        this.dislike_count = dislike_count;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public ContentComment getReferenceComment() {
        return referenceComment;
    }

    public void setReferenceComment(ContentComment referenceComment) {
        this.referenceComment = referenceComment;
    }

    public List<ContentComment> getSubCommentList() {
        return subCommentList;
    }

    public void setSubCommentList(List<ContentComment> subCommentList) {
        this.subCommentList = subCommentList;
    }

    public long getSub_comment_count() {
        return sub_comment_count;
    }

    public void setSub_comment_count(long sub_comment_count) {
        this.sub_comment_count = sub_comment_count;
    }

    @Override
    public long getChatId() {
        return id;
    }

    @Override
    public long getChatUserId() {
        return author_id;
    }

    @Override
    public long getUserLevelId() {
        return 0;
    }

    @Override
    public long getChatTime() {
        return creation_time;
    }

    @Override
    public String getChatUserNickName() {
        return author_nickname;
    }

    @Override
    public String getChatUserLogo() {
        return author_avatar_url;
    }

    @Override
    public String getChatTimeText() {
        return StringUtil.getChatTimeText(creation_time);
    }

    @Override
    public CharSequence getChatContentText() {
        return text;
    }

    @Override
    public ChatViewType getChatViewType() {
        long curUserId = AppManager.getInstance().getIApp().getLoginUserId();
        if (curUserId != 0 && curUserId == getChatUserId()) {
            return ChatViewType.CURRENT_USER;
        }
        return ChatViewType.NO_CURRENT_USER;
    }
}
