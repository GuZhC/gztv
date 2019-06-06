package com.dfsx.procamera.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2017/1/2  内容回复评论
 */
public class CommendEntry implements Serializable {

    /**
     * id : -1
     * author_id : -1
     * author_name :
     * author_nickname :
     * author_avatar_url :
     * text :
     * ref_comments : [{"id":-1,"author_id":-1,"author_name":"","author_nickname":"","author_avatar_url":"","text":"","creation_time":0}]
     * like_count : 0
     * dislike_count : 0
     * closed : false
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
    private long sub_comment_count;
    private List<SubCommentsBean> mSubCommendList;

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    private boolean  isUser=false;   //判断评论是不是用户自己发的

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getSub_comment_count() {
        return sub_comment_count;
    }

    public void setSub_comment_count(long sub_comment_count) {
        this.sub_comment_count = sub_comment_count;
    }

    public List<SubCommentsBean> getmSubCommendList() {
        return mSubCommendList;
    }

    public void setmSubCommendList(List<SubCommentsBean> mSubCommendList) {
        this.mSubCommendList = mSubCommendList;
    }

    /**
     * id : -1
     * author_id : -1
     * author_name :
     * author_nickname :
     * author_avatar_url :
     * text :
     * creation_time : 0
     */

    private List<RefCommentsBean> ref_comments;
    public List<RefCommentsBean> getRef_comments() {
        return ref_comments;
    }

    public void setRef_comments(List<RefCommentsBean> ref_comments) {
        this.ref_comments = ref_comments;
    }


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

    public static class SubCommentsBean implements Serializable {
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

        public List<RefCommentsBean> getRef_comments() {
            return ref_comments;
        }

        public void setRef_comments(List<RefCommentsBean> ref_comments) {
            this.ref_comments = ref_comments;
        }

        private long id;
        private long author_id;
        private String author_name;
        private String author_nickname;
        private String author_avatar_url;
        private String text;
        private long creation_time;
        private List<RefCommentsBean> ref_comments;

    }

    public static class RefCommentsBean implements Serializable {
        private long id;
        private long author_id;
        private String author_name;
        private String author_nickname;
        private String author_avatar_url;
        private String text;
        private long creation_time;

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
    }
}