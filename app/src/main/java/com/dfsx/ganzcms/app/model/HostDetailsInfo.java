package com.dfsx.ganzcms.app.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HostDetailsInfo implements IHostDetails {


    /**
     * id : long, 内容ID
     * column_id : long, 栏目ID
     * column_name : string, 栏目名称
     * author_id : long, 作者ID
     * author_name : string, 作者名称
     * author_nickname : string, 作者呢称
     * author_avatar_url : string, 作者头像地址
     * creation_time : long, 创建时间
     * editor_id : long, 编辑者ID
     * editor_name : string, 编辑者名称
     * editor_nickname : string, 编辑者呢称
     * editor_avatar_url : string, 编辑者头像地址
     * edit_time : long, 编辑时间
     * publisher_id : long, 发布者ID
     * publisher_name : string, 发布者名称
     * publisher_nickname : string, 发布者呢称
     * publisher_avatar_url : string, 发布者头像地址
     * publish_time : long, 发布时间
     * type : string, 内容类型
     * title : string, 标题
     * subtitle : string, 副标题
     * poster_url : string, 海报图片URL
     * source : string, 文章来源
     * quoted : bool, 文章是否转载
     * summary : string, 摘要
     * comment_mode : int, 评论模式： 1 – 禁用，2 – 启用
     * comment_count : long, 评论数
     * view_count : long, 浏览数
     * like_count : long, 点赞数
     * dislike_count : long, 点踩数
     * body : string, 内容正文
     * body_components : object, 正文使用到的组件
     * extension : json, 内容扩展
     * fields : {"slideset_id":"long, 幻灯片ID","audio_id":"long, 声音介绍ID","site_user_id":"long, 本站用户ID"}
     */

    private long id;
    private long column_id;
    private String column_name;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private String creation_time;
    private String editor_id;
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
    private String poster_url;
    private String source;
    private boolean quoted;
    private String summary;
    private int comment_mode;
    private long comment_count;
    private long view_count;
    private long like_count;
    private long dislike_count;
    private String body;
    private BodyComponent body_components;
    private JSONObject extension;
    private CustomField fields;
    private List<String> thumbnail_urls;

    private String hostPersonIntroduce;

    @Override
    public long getHostDetailsId() {
        return getId();
    }

    @Override
    public long getHostUserId() {
        if (getFields() != null) {
            return getFields().getSite_user_id();
        }
        return 0;
    }

    @Override
    public String getHostUserName() {
        return getAuthor_name();
    }

    @Override
    public String getHostNickName() {
        return getTitle();
    }

    @Override
    public String getHostIntroduce() {
        return hostPersonIntroduce;
    }

    @Override
    public List<String> getHostImageList() {
        if (getFields() != null &&
                getFields().getPictureSetContent() != null &&
                getFields().getPictureSetContent().getPictures() != null) {
            ArrayList<String> list = new ArrayList<>();
            for (PictureSetContent.PictureSet picture : getFields().getPictureSetContent().getPictures()) {
                list.add(picture.getUrl());
            }
            return list;
        }
        return null;
    }

    @Override
    public String getHostAudio() {
        if (getFields() != null && getFields().getAudioContent() != null &&
                getFields().getAudioContent().getVersions() != null &&
                getFields().getAudioContent().getVersions().size() > 0) {
            return getFields().getAudioContent().getVersions()
                    .get(0).getUrl();
        }
        return null;
    }

    @Override
    public String getHostAudioTitle() {
        if (getFields() != null && getFields().getAudioContent() != null) {
            return getFields().getAudioContent().getTitle();
        }
        return "";
    }

    @Override
    public String getHostAudioImage() {
        if(thumbnail_urls != null && thumbnail_urls.size() > 0) {
            return thumbnail_urls.get(0);
        }
        return "";
    }

    @Override
    public String getHostAudioIntro() {
        if (getFields() != null && getFields().getAudioContent() != null) {
            return getFields().getAudioContent().getIntroduction();
        }
        return null;
    }

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

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getEditor_id() {
        return editor_id;
    }

    public void setEditor_id(String editor_id) {
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

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
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

    public BodyComponent getBody_components() {
        return body_components;
    }

    public void setBody_components(BodyComponent body_components) {
        this.body_components = body_components;
    }

    public JSONObject getExtension() {
        return extension;
    }

    public void setExtension(JSONObject extension) {
        this.extension = extension;
    }

    public CustomField getFields() {
        return fields;
    }

    public void setFields(CustomField fields) {
        this.fields = fields;
    }

    public String getHostPersonIntroduce() {
        return hostPersonIntroduce;
    }

    public void setHostPersonIntroduce(String hostPersonIntroduce) {
        this.hostPersonIntroduce = hostPersonIntroduce;
    }

    public List<String> getThumbnail_urls() {
        return thumbnail_urls;
    }

    public void setThumbnail_urls(List<String> thumbnail_urls) {
        this.thumbnail_urls = thumbnail_urls;
    }

    /**
     * 扩展页面
     */
    public static class CustomField {
        /**
         * slideset_id : long, 幻灯片ID
         * audio_id : long, 声音介绍ID
         * site_user_id : long, 本站用户ID
         */

        private long slideset_id;
        private long audio_id;
        private long site_user_id;

        private PictureSetContent pictureSetContent;
        private AudioContent audioContent;

        public long getSlideset_id() {
            return slideset_id;
        }

        public void setSlideset_id(long slideset_id) {
            this.slideset_id = slideset_id;
        }

        public long getAudio_id() {
            return audio_id;
        }

        public void setAudio_id(long audio_id) {
            this.audio_id = audio_id;
        }

        public long getSite_user_id() {
            return site_user_id;
        }

        public void setSite_user_id(long site_user_id) {
            this.site_user_id = site_user_id;
        }

        public PictureSetContent getPictureSetContent() {
            return pictureSetContent;
        }

        public void setPictureSetContent(PictureSetContent pictureSetContent) {
            this.pictureSetContent = pictureSetContent;
        }

        public AudioContent getAudioContent() {
            return audioContent;
        }

        public void setAudioContent(AudioContent audioContent) {
            this.audioContent = audioContent;
        }
    }

    /**
     * 组件内容数据
     */
    public static class BodyComponent {
        private List<Long> pictures;
        private List<Long> videos;
        private List<Long> audios;

        private HashMap<Long, PictureContent> pictureComponentMap;
        private HashMap<Long, VideoContent> videoComponentMap;
        private HashMap<Long, AudioContent> audioComponentMap;

        public List<Long> getPictures() {
            return pictures;
        }

        public void setPictures(List<Long> pictures) {
            this.pictures = pictures;
        }

        public List<Long> getVideos() {
            return videos;
        }

        public void setVideos(List<Long> videos) {
            this.videos = videos;
        }

        public List<Long> getAudios() {
            return audios;
        }

        public void setAudios(List<Long> audios) {
            this.audios = audios;
        }

        public HashMap<Long, PictureContent> getPictureComponentMap() {
            return pictureComponentMap;
        }

        public void setPictureComponentMap(HashMap<Long, PictureContent> pictureComponentMap) {
            this.pictureComponentMap = pictureComponentMap;
        }

        public HashMap<Long, VideoContent> getVideoComponentMap() {
            return videoComponentMap;
        }

        public void setVideoComponentMap(HashMap<Long, VideoContent> videoComponentMap) {
            this.videoComponentMap = videoComponentMap;
        }

        public HashMap<Long, AudioContent> getAudioComponentMap() {
            return audioComponentMap;
        }

        public void setAudioComponentMap(HashMap<Long, AudioContent> audioComponentMap) {
            this.audioComponentMap = audioComponentMap;
        }

        public boolean isEmpty() {
            return (pictures == null || pictures.isEmpty())
                    &&
                    (videos == null || videos.isEmpty())
                    &&
                    (audios == null || audios.isEmpty());
        }
    }
}
