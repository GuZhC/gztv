package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2015-10-30. 回复实体类
 */
public class ReplyEntry implements Serializable {

    /**
     * id : -1
     * author_id : -1
     * author_name :
     * author_avatar_url :
     * content :
     * post_time : 0
     * last_editor_id : -1
     * last_editor_name :
     * last_editor_avatar_url :
     * last_edit_time : 0
     * ref_replies : [{"id":-1,"author_id":-1,"author_name":"","author_avatar_url":"","post_time":0,"content":"","like_count":0,"dislike_count":0}]
     * like_count : 0
     * dislike_count : 0
     * attachments : [0,0]
     */

    private long id;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private String content;
    private long post_time;
    private long last_editor_id;
    private String last_editor_name;
    private String last_editor_avatar_url;
    private long last_edit_time;
    private long like_count;
    private long dislike_count;
    private long sub_reply_count;
    String mthumImage;
    private List<RefRepliesBean> ref_replies;
    private List<Long> attachments;
    private long attmentId;
    private long topicId;

    public String getMthumImage() {
        return mthumImage;
    }

    public void setMthumImage(String mthumImage) {
        this.mthumImage = mthumImage;
    }

    /**
     * id : -1
     * author_id : -1
     * author_name :
     * author_avatar_url :
     * post_time : 0
     * content :
     * like_count : 0
     * dislike_count : 0
     */

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

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public long getSub_reply_count() {
        return sub_reply_count;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public void setSub_reply_count(long sub_reply_count) {
        this.sub_reply_count = sub_reply_count;
    }

    public long getAttmentId() {
        return attmentId;
    }

    public void setAttmentId(long attmentId) {
        this.attmentId = attmentId;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }

    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPost_time() {
        return post_time;
    }

    public void setPost_time(long post_time) {
        this.post_time = post_time;
    }

    public long getLast_editor_id() {
        return last_editor_id;
    }

    public void setLast_editor_id(long last_editor_id) {
        this.last_editor_id = last_editor_id;
    }

    public String getLast_editor_name() {
        return last_editor_name;
    }

    public void setLast_editor_name(String last_editor_name) {
        this.last_editor_name = last_editor_name;
    }

    public String getLast_editor_avatar_url() {
        return last_editor_avatar_url;
    }

    public void setLast_editor_avatar_url(String last_editor_avatar_url) {
        this.last_editor_avatar_url = last_editor_avatar_url;
    }

    public long getLast_edit_time() {
        return last_edit_time;
    }

    public void setLast_edit_time(long last_edit_time) {
        this.last_edit_time = last_edit_time;
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

    public List<RefRepliesBean> getRef_replies() {
        return ref_replies;
    }

    public void setRef_replies(List<RefRepliesBean> ref_replies) {
        this.ref_replies = ref_replies;
    }

    public List<Long> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Long> attachments) {
        this.attachments = attachments;
    }

    public static class RefRepliesBean implements Serializable {
        private long id;
        private long author_id;
        private String author_name;
        private String author_avatar_url;
        private String author_nickname;
        private long post_time;
        private String content;
        private long like_count;
        private long dislike_count;

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

        public String getAuthor_avatar_url() {
            return author_avatar_url;
        }

        public void setAuthor_avatar_url(String author_avatar_url) {
            this.author_avatar_url = author_avatar_url;
        }

        public long getPost_time() {
            return post_time;
        }

        public void setPost_time(long post_time) {
            this.post_time = post_time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getAuthor_nickname() {
            return author_nickname;
        }

        public void setAuthor_nickname(String author_nickname) {
            this.author_nickname = author_nickname;
        }
    }
}

