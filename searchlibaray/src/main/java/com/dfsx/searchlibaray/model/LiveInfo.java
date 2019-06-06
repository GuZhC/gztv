package com.dfsx.searchlibaray.model;

import java.util.List;

public class LiveInfo implements ISearchData<LiveInfo> {


    /**
     * password : false
     * tags : []
     * creation_time : 1511336096
     * start_time : null
     * closed : false
     * playback_state : 1
     * owner_id : 1
     * title : test
     * cover_id : 0
     * introduction :
     * plan_start_time : 1512632125
     * forenotice : true
     * screen_mode : 1
     * privacy : false
     * cover_url :
     * current_visitor_count : 0
     * category_key : default
     * owner_avatar_url : http://file.baview.cn:8101/general/pictures/20171116/7BB9A2EA5027C57D0F13F44B324FB568/7BB9A2EA5027C57D0F13F44B324FB568.jpg
     * owner_username : admin
     * owner_nickname : 超级管理员
     * owner_level_id : 10552
     * category_name : 默认分类
     * id : 1249677
     * state : 1
     * type : 2
     */

    private boolean password;
    private long creation_time;
    private Object start_time;
    private boolean closed;
    private int playback_state;
    private long owner_id;
    private String title;
    private long cover_id;
    private String introduction;
    private long plan_start_time;
    private boolean forenotice;
    private int screen_mode;
    private boolean privacy;
    private String cover_url;
    private long current_visitor_count;
    private String category_key;
    private String owner_avatar_url;
    private String owner_username;
    private String owner_nickname;
    private long owner_level_id;
    private String category_name;
    private long id;
    private int state;
    private int type;
    private List<String> tags;

    private SearchItemInfo searchItemInfo;

    @Override
    public SearchShowStyle getShowStyle() {
        if (type == 1) {
            return SearchShowStyle.STYLE_LIVE_SHOW;
        }
        return SearchShowStyle.STYLE_LIVE_SERVICE;
    }

    @Override
    public LiveInfo getContentData() {
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

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public Object getStart_time() {
        return start_time;
    }

    public void setStart_time(Object start_time) {
        this.start_time = start_time;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getPlayback_state() {
        return playback_state;
    }

    public void setPlayback_state(int playback_state) {
        this.playback_state = playback_state;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCover_id() {
        return cover_id;
    }

    public void setCover_id(long cover_id) {
        this.cover_id = cover_id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getPlan_start_time() {
        return plan_start_time;
    }

    public void setPlan_start_time(long plan_start_time) {
        this.plan_start_time = plan_start_time;
    }

    public boolean isForenotice() {
        return forenotice;
    }

    public void setForenotice(boolean forenotice) {
        this.forenotice = forenotice;
    }

    public int getScreen_mode() {
        return screen_mode;
    }

    public void setScreen_mode(int screen_mode) {
        this.screen_mode = screen_mode;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
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

    public String getCategory_key() {
        return category_key;
    }

    public void setCategory_key(String category_key) {
        this.category_key = category_key;
    }

    public String getOwner_avatar_url() {
        return owner_avatar_url;
    }

    public void setOwner_avatar_url(String owner_avatar_url) {
        this.owner_avatar_url = owner_avatar_url;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }

    public String getOwner_nickname() {
        return owner_nickname;
    }

    public void setOwner_nickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }

    public long getOwner_level_id() {
        return owner_level_id;
    }

    public void setOwner_level_id(long owner_level_id) {
        this.owner_level_id = owner_level_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getRoomStatus() {
        String status = "";
        if (state == 1) {
            status = "预告";
        } else if (state == 2) {
            status = "直播中";
        } else if (state == 3) {
            status = "回放";
        } else {
            status = "其他";
        }
        return status;
    }
}
