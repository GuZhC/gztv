package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by heyang on 2016/12/30   首页栏目  内容实体详情类
 */
public class ContentCmsInfoEntry implements Serializable {


    /**
     * id : -1
     * column_id : -1
     * column_name :
     * author_id : -1
     * author_name :
     * author_nickname :
     * author_avatar_url :
     * creation_time : 0
     * editor_id : -1
     * editor_name :
     * editor_nickname :
     * editor_avatar_url :
     * edit_time : 0
     * publisher_id : -1
     * publisher_name :
     * publisher_nickname :
     * publisher_avatar_url :
     * publish_time : 0
     * type : 0
     * title :
     * subtitle :
     * source :
     * quoted : false
     * summary :
     * comment_mode : 0
     * comment_count : 0
     * view_count : 0
     * like_count : 0
     * dislike_count : 0
     * body :
     * extension :
     * flags : [{"id":-1,"key":"","name":""}]
     */

    private long id;
    private long column_id;
    private String column_name;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private long creation_time;
    private long editor_id;
    private String editor_name;
    private String editor_nickname;
    private String editor_avatar_url;
    private long edit_time;
    private long publisher_id;
    private String publisher_name;
    private String publisher_nickname;
    private String publisher_avatar_url;
    private long publish_time;
    private String type;
    private String title;
    private String subtitle;
    private String source;
    private boolean quoted;
    private String summary;
    private int comment_mode;
    private long comment_count;
    private long view_count;
    private long like_count;
    private long dislike_count;
    private String body;
    //    private String extension;
    //    private String body_components;
    private int showType;
    private String url;
    private String videoThumb;
    private boolean isAttend;
    private String nextLiveName;

    @SerializedName("fields")
    private HashMap<String, Object> fieldsMap;

    private List<ContentCmsEntry> raletionList;

    public List<ContentCmsEntry> getRaletionList() {
        return raletionList;
    }

    public void setRaletionList(List<ContentCmsEntry> raletionList) {
        this.raletionList = raletionList;
    }

    public String getPraiseList() {
        return praiseList;
    }

    public void setPraiseList(String praiseList) {
        this.praiseList = praiseList;
    }

    private String praiseList;

    public Quickentry getQuickentry() {
        return quickentry;
    }

    public void setQuickentry(Quickentry quickentry) {
        this.quickentry = quickentry;
    }

    private Quickentry quickentry;

    /**
     * id : -1
     * key :
     * name :
     */
    private List<GroupImgsBean> groupimgs;

    public List<VideosBean> getVideoGroups() {
        return videoGroups;
    }

    public void setVideoGroups(List<VideosBean> videoGroups) {
        this.videoGroups = videoGroups;
    }

    private List<VideosBean> videoGroups;

    public List<VideosBean> getAduioGroups() {
        return aduioGroups;
    }

    public void setAduioGroups(List<VideosBean> aduioGroups) {
        this.aduioGroups = aduioGroups;
    }

    private List<VideosBean> aduioGroups;

    private List<FlagsBean> flags;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private List<String> thumbnail_urls;

    public List<String> getThumbnail_urls() {
        return thumbnail_urls;
    }

    public void setThumbnail_urls(List<String> thumbnail_urls) {
        this.thumbnail_urls = thumbnail_urls;
    }

    public long getLiveId() {
        return liveId;
    }

    public void setLiveId(long liveId) {
        this.liveId = liveId;
    }

    private long liveId;

    public boolean isFav() {
        return isFav;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    private boolean isFav;

    public boolean isAttend() {
        return isAttend;
    }

    public void setAttend(boolean attend) {
        isAttend = attend;
    }

    public String getNextLiveName() {
        return nextLiveName;
    }

    public void setNextLiveName(String nextLiveName) {
        this.nextLiveName = nextLiveName;
    }

    //    public String getBody_components() {
    //        return body_components;
    //    }
    //
    //    public void setBody_components(String body_components) {
    //        this.body_components = body_components;
    //    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
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

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getEditor_id() {
        return editor_id;
    }

    public void setEditor_id(long editor_id) {
        this.editor_id = editor_id;
    }

    public String getEditor_name() {
        return editor_name;
    }

    public void setEditor_name(String editor_name) {
        this.editor_name = editor_name;
    }

    public String getEditor_nickname() {
        return editor_nickname;
    }

    public void setEditor_nickname(String editor_nickname) {
        this.editor_nickname = editor_nickname;
    }

    public String getEditor_avatar_url() {
        return editor_avatar_url;
    }

    public void setEditor_avatar_url(String editor_avatar_url) {
        this.editor_avatar_url = editor_avatar_url;
    }

    public long getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(long edit_time) {
        this.edit_time = edit_time;
    }

    public long getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(long publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getPublisher_nickname() {
        return publisher_nickname;
    }

    public void setPublisher_nickname(String publisher_nickname) {
        this.publisher_nickname = publisher_nickname;
    }

    public String getPublisher_avatar_url() {
        return publisher_avatar_url;
    }

    public void setPublisher_avatar_url(String publisher_avatar_url) {
        this.publisher_avatar_url = publisher_avatar_url;
    }

    public long getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(long publish_time) {
        this.publish_time = publish_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isQuoted() {
        return quoted;
    }

    public void setQuoted(boolean quoted) {
        this.quoted = quoted;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getComment_mode() {
        return comment_mode;
    }

    public void setComment_mode(int comment_mode) {
        this.comment_mode = comment_mode;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public long getView_count() {
        return view_count;
    }

    public void setView_count(long view_count) {
        this.view_count = view_count;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    //    public String getExtension() {
    //        return extension;
    //    }

    //    public void setExtension(String extension) {
    //        this.extension = extension;
    //    }

    public List<FlagsBean> getFlags() {
        return flags;
    }

    public void setFlags(List<FlagsBean> flags) {
        this.flags = flags;
    }

    public List<GroupImgsBean> getGroupimgs() {
        return groupimgs;
    }

    public void setGroupimgs(List<GroupImgsBean> groupimgs) {
        this.groupimgs = groupimgs;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public HashMap<String, Object> getFieldsMap() {
        return fieldsMap;
    }

    public void setFieldsMap(HashMap<String, Object> fieldsMap) {
        this.fieldsMap = fieldsMap;
    }

    /**
     * 针对 快捷入口
     */
    public static class Quickentry implements Serializable {
        private String type;
        private long related_content_id;
        private long related_show_id;
        private LiveInfo livInfo;
        private ContentCmsInfoEntry contentInfo;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getRelated_content_id() {
            return related_content_id;
        }

        public void setRelated_content_id(long related_content_id) {
            this.related_content_id = related_content_id;
        }

        public long getRelated_show_id() {
            return related_show_id;
        }

        public void setRelated_show_id(long related_show_id) {
            this.related_show_id = related_show_id;
        }

        public LiveInfo getLivInfo() {
            return livInfo;
        }

        public void setLivInfo(LiveInfo livInfo) {
            this.livInfo = livInfo;
        }

        public ContentCmsInfoEntry getContentInfo() {
            return contentInfo;
        }

        public void setContentInfo(ContentCmsInfoEntry contentInfo) {
            this.contentInfo = contentInfo;
        }
    }

    public static class FlagsBean implements Serializable {
        private long id;
        private String key;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 图集
     */
    public static class GroupImgsBean implements Serializable {
        private long id;
        private String title;
        private String url;
        private int width;
        private int height;
        private String introduction;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }
    }

    public static class VersionsBean implements Serializable {
        private String name;
        private int width;
        private int height;
        private double bitrate;
        private String url;
        private String thumb;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getBitrate() {
            return bitrate;
        }

        public void setBitrate(double bitrate) {
            this.bitrate = bitrate;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

    }

    /**
     * 视频
     */
    /**
     * introduction :
     * cover_id : -1
     * cover_url :
     * modification_time : 0
     * duration : 0
     * versions : [{"name":"","width":0,"height":0,"bitrate":0,"url":""}]
     */
    public static class VideosBean implements Serializable {
        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        //        private long cover_id;
        private String coverUrl;
        private long modification_time;
        private long duration;
        private long id;
        private String introduction;
        /**
         * name :
         * width : 0
         * height : 0
         * bitrate : 0
         * url :
         */

        private VersionsBean versions;

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public long getModification_time() {
            return modification_time;
        }

        public void setModification_time(long modification_time) {
            this.modification_time = modification_time;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public VersionsBean getVersions() {
            return versions;
        }

        public void setVersions(VersionsBean versions) {
            this.versions = versions;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

    }

    /***
     *  直播实体类  电视(1) 和 电台(2)
     */
    public static class LiveBean implements Serializable {
        /**
         * id : -1
         * type : -1
         * name :
         * introduction :
         * creation_time : 0
         * rtmp_url :
         * flv_url :
         * m3u8_url :
         * width : 0
         * height : 0
         */

        private long id;
        private int type;
        private String name;
        private String introduction;
        private long creation_time;
        private String rtmp_url;
        private String flv_url;
        private String m3u8_url;
        private int width;
        private int height;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public long getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(long creation_time) {
            this.creation_time = creation_time;
        }

        public String getRtmp_url() {
            return rtmp_url;
        }

        public void setRtmp_url(String rtmp_url) {
            this.rtmp_url = rtmp_url;
        }

        public String getFlv_url() {
            return flv_url;
        }

        public void setFlv_url(String flv_url) {
            this.flv_url = flv_url;
        }

        public String getM3u8_url() {
            return m3u8_url;
        }

        public void setM3u8_url(String m3u8_url) {
            this.m3u8_url = m3u8_url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}