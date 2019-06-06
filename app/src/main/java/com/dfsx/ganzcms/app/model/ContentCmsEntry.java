package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchItemInfo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by heyang on 2016/12/21   首页栏目  内容实体 类
 */
public class ContentCmsEntry implements Serializable, ISearchData {

    /**
     * id : -1
     * column_id : -1
     * column_name :
     * author_nickname :
     * creation_time : 0
     * editor_nickname :
     * edit_time : 0
     * publisher_nickname :
     * publish_time : 0
     * type : 0
     * title :
     * subtitle :
     * thumbnail_urls : ["",""]
     * source :
     * quoted : false
     * summary :
     * comment_mode : -1
     * comment_count : 0
     * view_count : 0
     */

    private long id;
    private long column_id;
    private String column_name;
    private String author_nickname;
    private long creation_time;
    private String editor_nickname;
    private long edit_time;
    private String publisher_nickname;
    private long publish_time;
    private String type;  // 新版本
    private String title;
    private String subtitle;
    private int list_item_mode;  //0-无图 1-单图 2-大图 3-多图
    private String source;
    private boolean quoted;
    private String summary;
    private int comment_mode;
    private long comment_count;
    private long view_count;
    private List<String> thumbnail_urls;
    private String poster_url;
    private int modeType;
    private long emergencyIcon;  //应急消息类型的图标id
    private ArrayList<EmergencyIcon> emergencyIcons;    //应急消息图标组
    private String emergencyType;   //应急消息图标类型

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public ArrayList<EmergencyIcon> getEmergencyIcons() {
        return emergencyIcons;
    }

    public void setEmergencyIcons(ArrayList<EmergencyIcon> emergencyIcons) {
        this.emergencyIcons = emergencyIcons;
    }

    @SerializedName("fields")
    private HashMap<String, Object> customFields;

    public long getEmergencyIcon() {
        return emergencyIcon;
    }

    public void setEmergencyIcon(long emergencyIcon) {
        this.emergencyIcon = emergencyIcon;
    }

    public long getShow_id() {
        return show_id;
    }

    public void setShow_id(long show_id) {
        this.show_id = show_id;
    }

    public LiveInfo getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(LiveInfo liveInfo) {
        this.liveInfo = liveInfo;
    }

    private long  show_id;  //  直播回放id
    private LiveInfo liveInfo;  //直播详情

    public ShowExtends getShowExtends() {
        return showExtends;
    }

    public void setShowExtends(ShowExtends showExtends) {
        this.showExtends = showExtends;
    }

    private ShowExtends showExtends;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    private int showType;

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

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public String getEditor_nickname() {
        return editor_nickname;
    }

    public void setEditor_nickname(String editor_nickname) {
        this.editor_nickname = editor_nickname;
    }

    public long getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(long edit_time) {
        this.edit_time = edit_time;
    }

    public String getPublisher_nickname() {
        return publisher_nickname;
    }

    public void setPublisher_nickname(String publisher_nickname) {
        this.publisher_nickname = publisher_nickname;
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

    public int getList_item_mode() {
        return list_item_mode;
    }

    public void setList_item_mode(int list_item_mode) {
        this.list_item_mode = list_item_mode;
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

    public List<String> getThumbnail_urls() {
        return thumbnail_urls;
    }

    public void setThumbnail_urls(List<String> thumbnail_urls) {
        this.thumbnail_urls = thumbnail_urls;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    public HashMap<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(HashMap<String, Object> customFields) {
        this.customFields = customFields;
    }

    public static class ShowExtends implements Serializable {
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(long owner_id) {
            this.owner_id = owner_id;
        }

        public String getOwner_nickname() {
            return owner_nickname;
        }

        public void setOwner_nickname(String owner_nickname) {
            this.owner_nickname = owner_nickname;
        }

        public String getOwner_avatar_url() {
            return owner_avatar_url;
        }

        public void setOwner_avatar_url(String owner_avatar_url) {
            this.owner_avatar_url = owner_avatar_url;
        }

        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public long getCurrent_visitor_count() {
            return current_visitor_count;
        }

        public void setCurrent_visitor_count(long current_visitor_count) {
            this.current_visitor_count = current_visitor_count;
        }

        public long getStart_time() {
            return start_time;
        }

        public void setStart_time(long start_time) {
            this.start_time = start_time;
        }

        public long getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(long creation_time) {
            this.creation_time = creation_time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        private long id;
        private String title;
        private long owner_id;
        private String owner_nickname;
        private String owner_avatar_url;
        private String cover_url;
        private long current_visitor_count;
        private long start_time;
        private long creation_time;
        private int type;   //1:个人直播  2：活动直播
        private int state;  //<int, 直播状态, 1 – 未直播，2 – 正在直播，3 – 直播已结束>,
    }

    private SearchItemInfo searchItemInfo;

    @Override
    public SearchShowStyle getShowStyle() {
        if (TextUtils.equals(type, "video")) {
            return SearchShowStyle.STYLE_CMS_VIDEO;
        } else if (TextUtils.equals(type, "groups") || TextUtils.equals(type, "pictureset")) {
            return SearchShowStyle.STYLE_WORD_THREE;
        } else if (TextUtils.equals(type, "ad") ||
                TextUtils.equals(type, "link")) {
            return SearchShowStyle.STYLE_CMS_ACTIVITY;
        } else if (TextUtils.equals(type, "signup")
                || TextUtils.equals(type, "questionnaire")
                || TextUtils.equals(type, "special")
                || TextUtils.equals(type, "vote")) {
            return SearchShowStyle.STYLE_CMS_ACTIVITY;
        } else
            return SearchShowStyle.STYLE_WORD;
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

    /**
     * url: "http://file.ganzitv.com/cms/pictures/20190114/b5b61e8eb7c4a009fdeb1a50ebc2164d/b5b61e8eb7c4a009fdeb1a50ebc2164d.png",
     width: 1000,
     height: 666,
     id: 1239430109
     */
    public class EmergencyIcon implements Serializable{
        private String url;
        private int width;
        private int height;
        private long id;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

}