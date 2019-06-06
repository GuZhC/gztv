package com.dfsx.ganzcms.app.model;

import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchItemInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2015-10-24.  主题实体类
 */
public class TopicalEntry implements Serializable, ISearchData {

    /**
     * id : 0
     * author_id : 0
     * author_name :
     * author_avatar_url :
     * column_id : 0
     * column_name :
     * title :
     * content :
     * post_time :
     * view_count : 0
     * reply_count : 0
     * last_editor_id : 0
     * last_eidtor_name :
     * last_editor_avatar_url :
     * last_edit_time :
     * last_replier_id : 0
     * last_replier_name :
     * last_replier_avatar_url :
     * last_reply_time :
     * flags : 0
     * like_count : 0
     * dislike_count : 0
     * attachments : [0,0]
     */

    private long id;
    private long author_id;
    private String author_name;
    private String author_avatar_url;
    private long column_id;
    private String column_name;
    private String title;
    private String content;
    private long post_time;
    private long view_count;
    private long reply_count;
    private long last_editor_id;
    private String last_eidtor_name;
    private String last_editor_avatar_url;
    private String last_edit_time;
    private long last_replier_id;
    private String last_replier_name;
    private String last_replier_avatar_url;
    private String last_replier_nickname;
    private String last_reply_time;
    private String author_nickname;
    private int flags;
    private List<Long> attachments;
    private List<Attachment> attachmentInfos;
    private long like_count;   //点赞
    private long dislike_count;   //点踩
    private List<String> tags;  //   2017/7/27
    private int type;   //1:图片  2:视频
    private String videoThumb;
    private String urls;
    private List<VisitorBean> visitList;
    private String user_level_img;
    private int user_level_id;
    private List<PraiseBean> praiseBeanList;

    public List<ReplyEntry> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ReplyEntry> replyList) {
        this.replyList = replyList;
    }

    private List<ReplyEntry> replyList;

    public int getAttitude() {
        return attitude;
    }

    public void setAttitude(int attitude) {
        this.attitude = attitude;
    }

    private int attitude;  //0:未操作  1点赞  2 点踩

    public String getPraiseList() {
        return praiseList;
    }

    public void setPraiseList(String praiseList) {
        this.praiseList = praiseList;
    }

    private String praiseList;

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    private boolean isHome = false;   //查看的是个人主页的东西

    private boolean isFavl = false;   //是否收藏

    private int relationRole = -1;   //    0:未关注  1：已关注  2:相互关注

    public int getRelationRole() {
        return relationRole;
    }

    public List<PraiseBean> getPraiseBeanList() {
        return praiseBeanList;
    }

    public void setPraiseBeanList(List<PraiseBean> praiseBeanList) {
        this.praiseBeanList = praiseBeanList;
    }

    public void setRelationRole(int relationRole) {
        this.relationRole = relationRole;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getLast_replier_nickname() {
        return last_replier_nickname;
    }

    public void setLast_replier_nickname(String last_replier_nickname) {
        this.last_replier_nickname = last_replier_nickname;
    }

    public String getUser_level_img() {
        return user_level_img;
    }

    public void setUser_level_img(String user_level_img) {
        this.user_level_img = user_level_img;
    }

    public boolean isFavl() {
        return isFavl;
    }

    public void setIsFavl(boolean isFavl) {
        this.isFavl = isFavl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<VisitorBean> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<VisitorBean> visitList) {
        this.visitList = visitList;
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

    public long getColumn_id() {
        return column_id;
    }

    public void setColumn_id(long column_id) {
        this.column_id = column_id;
    }

    public String getColumn_name() {
        return column_name;
    }

    public int getUser_level_id() {
        return user_level_id;
    }

    public void setUser_level_id(int user_level_id) {
        this.user_level_id = user_level_id;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getView_count() {
        return view_count;
    }

    public void setView_count(long view_count) {
        this.view_count = view_count;
    }

    public long getReply_count() {
        return reply_count;
    }

    public void setReply_count(long reply_count) {
        this.reply_count = reply_count;
    }

    public long getLast_editor_id() {
        return last_editor_id;
    }

    public void setLast_editor_id(long last_editor_id) {
        this.last_editor_id = last_editor_id;
    }

    public String getLast_eidtor_name() {
        return last_eidtor_name;
    }

    public void setLast_eidtor_name(String last_eidtor_name) {
        this.last_eidtor_name = last_eidtor_name;
    }

    public String getLast_editor_avatar_url() {
        return last_editor_avatar_url;
    }

    public void setLast_editor_avatar_url(String last_editor_avatar_url) {
        this.last_editor_avatar_url = last_editor_avatar_url;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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

    public String getLast_edit_time() {
        return last_edit_time;
    }

    public void setLast_edit_time(String last_edit_time) {
        this.last_edit_time = last_edit_time;
    }

    public long getLast_replier_id() {
        return last_replier_id;
    }

    public void setLast_replier_id(long last_replier_id) {
        this.last_replier_id = last_replier_id;
    }

    public String getLast_replier_name() {
        return last_replier_name;
    }

    public void setLast_replier_name(String last_replier_name) {
        this.last_replier_name = last_replier_name;
    }

    public String getLast_replier_avatar_url() {
        return last_replier_avatar_url;
    }

    public void setLast_replier_avatar_url(String last_replier_avatar_url) {
        this.last_replier_avatar_url = last_replier_avatar_url;
    }

    public String getLast_reply_time() {
        return last_reply_time;
    }

    public void setLast_reply_time(String last_reply_time) {
        this.last_reply_time = last_reply_time;
    }

    public int getFlags() {
        return flags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public List<Long> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Long> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getAttachmentInfos() {
        return attachmentInfos;
    }

    public void setAttachmentss(List<Attachment> attachmentss) {
        this.attachmentInfos = attachmentss;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public static class VisitorBean implements Serializable {
        long id;
        String username;
        String nickname;
        String avatar_url;
        long view_time;
        long user_level_id;
        private int relatRole = -1;   //    0:未关注  1：已关注  2:相互关注


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public long getView_time() {
            return view_time;
        }

        public void setView_time(long view_time) {
            this.view_time = view_time;
        }

        public long getUser_level_id() {
            return user_level_id;
        }

        public void setUser_level_id(long user_level_id) {
            this.user_level_id = user_level_id;
        }

        public int getRelatRole() {
            return relatRole;
        }

        public void setRelatRole(int relatRole) {
            this.relatRole = relatRole;
        }
    }

    public static class PraiseBean implements Serializable {

        /**
         * user_id : -1
         * username :
         * nickname :
         * avatar_url :
         * state : 0
         * occur_time :
         */

        private long user_id;
        private String username;
        private String nickname;
        private String avatar_url;
        private int state;
        private String occur_time;

        public int getRelatRole() {
            return relatRole;
        }

        public void setRelatRole(int relatRole) {
            this.relatRole = relatRole;
        }

        private int relatRole;

        public long getUser_id() {
            return user_id;
        }

        public void setUser_id(long user_id) {
            this.user_id = user_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getOccur_time() {
            return occur_time;
        }

        public void setOccur_time(String occur_time) {
            this.occur_time = occur_time;
        }
    }

    private SearchItemInfo searchItemInfo;

    @Override
    public SearchShowStyle getShowStyle() {
        return SearchShowStyle.STYLE_QUANZI;
    }

    @Override
    public Object getContentData() {
        return this;
    }

    @Override
    public SearchItemInfo getSearchItemInfo() {
        return searchItemInfo;
    }

    @Override
    public void setSearchItemInfo(SearchItemInfo itemInfo) {
        this.searchItemInfo = itemInfo;
    }
}

