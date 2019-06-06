package com.dfsx.ganzcms.app.push.PushMeesageModel;

/**
 * Created by wen on 2017/3/27.
 */

public class Message {
    private long id;
    private int category;
    private boolean has_read;
    private String content;
    private String extension;
    private Extension ext;
    private long creation_time;
    private long user_id;
    private String username;
    private String nickname;
    private String avatar_url;

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

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

    public Message() {
    }

    public Message(long id, int category, String content, String extension) {
        this.id = id;
        this.category = category;
        this.content = content;
        this.extension = extension;
    }

    public Extension getExt() {
        return ext;
    }

    public void setExt(Extension ext) {
        this.ext = ext;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getShowTitle() {
        if (getExt() != null) {
            Extension.MessageType type = getExt().getMessageType();
            String title = "";
            switch (type) {
                case general_notice:
                    title = "系统通知";
                    break;
                case general_followed:
                    title = "关注";
                    break;
                case cms_content:
                    title = "新闻";
                    break;
                case cms_follow_video_uploaded:
                    title = "视频";
                    break;
                case live_personal_show:
                    title = "推荐直播";
                    break;
                case live_personal_playback:
                    title = "精彩回放";
                    break;
                case live_follow_channel_living:
                    title = "直播";
                    break;
                case community_thread:
                    title = "圈子";
                    break;
                case community_follow_thread_posted:
                    title = "帖子";
                    break;
                case unknwn:
                    title = "系统通知";
                    break;
            }
            return title;
        }
        return "";
    }

    public boolean isHas_read() {
        return has_read;
    }

    public void setHas_read(boolean has_read) {
        this.has_read = has_read;
    }
}
