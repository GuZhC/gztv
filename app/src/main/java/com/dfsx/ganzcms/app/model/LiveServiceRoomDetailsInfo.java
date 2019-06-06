package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2017/7/14.
 */
public class LiveServiceRoomDetailsInfo implements Serializable {


    /**
     * password : null
     * published : true
     * output_mode : 2
     * input_streams : [{"rmtp_url":"rtmp://192.168.6.86/live/live1066185-104935-1499332510864?925b339702314456bfb9a6881aa6a2d0","id":104935,"flv_address":"http://192.168.6.86:8100/live/live1066185-104935-1499332510864.flv","m3u8_address":"http://192.168.6.32:8101/live/videos/live117/1066185/104935/hls/playlist1.m3u8","name":"内场"},{"rmtp_url":"rtmp://192.168.6.86/live/live1066185-105034-1499332510880?65e6896e7d7449f3abd3b030fe7ff9b0","id":105034,"flv_address":"http://192.168.6.86:8100/live/live1066185-105034-1499332510880.flv","m3u8_address":"http://192.168.6.32:8101/live/videos/live117/1066185/105034/hls/playlist1.m3u8","name":"外场"}]
     * output_streams : [{"rmtp_url":"rtmp://192.168.6.86/live/live1066185-104935-1499332510864?925b339702314456bfb9a6881aa6a2d0","id":104935,"flv_address":"http://192.168.6.86:8100/live/live1066185-104935-1499332510864.flv","m3u8_address":"http://192.168.6.32:8101/live/videos/live117/1066185/104935/hls/playlist1.m3u8","name":"内场"},{"rmtp_url":"rtmp://192.168.6.86/live/live1066185-105034-1499332510880?65e6896e7d7449f3abd3b030fe7ff9b0","id":105034,"flv_address":"http://192.168.6.86:8100/live/live1066185-105034-1499332510880.flv","m3u8_address":"http://192.168.6.32:8101/live/videos/live117/1066185/105034/hls/playlist1.m3u8","name":"外场"}]
     * total_coins : 20
     * interaction_plugins : image-text,send-gift,chat,intro
     * start_time : 1499332612
     * creation_time : 1499332510
     * playback_state : 3
     * privacy : false
     * title : 第二个直播
     * cover_id : 0
     * owner_id : 2
     * cover_url : http://192.168.6.32:8101/general/pictures/20170704/6CD3B5C2CE1E32779773B154DFD7F3E8/6CD3B5C2CE1E32779773B154DFD7F3E8.jpg
     * plan_start_time : 1499962400
     * screen_mode : 1
     * introduction : null
     * category_key : default
     * owner_avatar_url : http://192.168.6.32:8101/general/pictures/20170704/6CD3B5C2CE1E32779773B154DFD7F3E8/6CD3B5C2CE1E32779773B154DFD7F3E8.jpg
     * owner_username : zhangsan
     * owner_nickname : 张三1
     * category_name : 默认分类
     * current_visitor_count : 0
     * id : 1066185
     * state : 3
     * type : 2
     */

    private String password;
    private boolean published;
    private int output_mode;
    private double total_coins;
    private String interaction_plugins;
    private long start_time;
    private long creation_time;
    private int playback_state;
    private boolean privacy;
    private String title;
    private long cover_id;
    private long owner_id;
    private String cover_url;
    private long plan_start_time;
    private long screen_mode;
    private String introduction;
    private String category_key;
    private String owner_avatar_url;
    private String owner_username;
    private String owner_nickname;
    private String category_name;
    private int current_visitor_count;
    private long id;
    private int state;
    private int type;
    private List<LiveInputStreamsIno> input_streams;
    private List<LiveOutputStreamsInfo> output_streams;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getOutput_mode() {
        return output_mode;
    }

    public void setOutput_mode(int output_mode) {
        this.output_mode = output_mode;
    }

    public double getTotal_coins() {
        return total_coins;
    }

    public void setTotal_coins(double total_coins) {
        this.total_coins = total_coins;
    }

    public String getInteraction_plugins() {
        return interaction_plugins;
    }

    public void setInteraction_plugins(String interaction_plugins) {
        this.interaction_plugins = interaction_plugins;
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

    public int getPlayback_state() {
        return playback_state;
    }

    public void setPlayback_state(int playback_state) {
        this.playback_state = playback_state;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
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

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public long getPlan_start_time() {
        return plan_start_time;
    }

    public void setPlan_start_time(long plan_start_time) {
        this.plan_start_time = plan_start_time;
    }

    public long getScreen_mode() {
        return screen_mode;
    }

    public void setScreen_mode(long screen_mode) {
        this.screen_mode = screen_mode;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCurrent_visitor_count() {
        return current_visitor_count;
    }

    public void setCurrent_visitor_count(int current_visitor_count) {
        this.current_visitor_count = current_visitor_count;
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

    public List<LiveInputStreamsIno> getInput_streams() {
        return input_streams;
    }

    public void setInput_streams(List<LiveInputStreamsIno> input_streams) {
        this.input_streams = input_streams;
    }

    public List<LiveOutputStreamsInfo> getOutput_streams() {
        return output_streams;
    }

    public void setOutput_streams(List<LiveOutputStreamsInfo> output_streams) {
        this.output_streams = output_streams;
    }

    public static class LiveInputStreamsIno implements Serializable, ILiveService.ILiveInputStream {
        /**
         * rmtp_url : rtmp://192.168.6.86/live/live1066185-104935-1499332510864?925b339702314456bfb9a6881aa6a2d0
         * id : 104935
         * flv_address : http://192.168.6.86:8100/live/live1066185-104935-1499332510864.flv
         * m3u8_address : http://192.168.6.32:8101/live/videos/live117/1066185/104935/hls/playlist1.m3u8
         * name : 内场
         */

        private String rtmp_url;
        private long id;
        private String flv_address;
        private String m3u8_address;
        private String name;

        private boolean isSelected;

        public String getRtmp_url() {
            return rtmp_url;
        }

        public void setRtmp_url(String rtmp_url) {
            this.rtmp_url = rtmp_url;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFlv_address() {
            return flv_address;
        }

        public void setFlv_address(String flv_address) {
            this.flv_address = flv_address;
        }

        public String getM3u8_address() {
            return m3u8_address;
        }

        public void setM3u8_address(String m3u8_address) {
            this.m3u8_address = m3u8_address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public long getInputId() {
            return id;
        }

        @Override
        public String getInputName() {
            return name;
        }

        @Override
        public String getInputRtmpUrl() {
            return rtmp_url;
        }

        @Override
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

    public static class LiveOutputStreamsInfo {
        /**
         * rmtp_url : rtmp://192.168.6.86/live/live1066185-104935-1499332510864?925b339702314456bfb9a6881aa6a2d0
         * id : 104935
         * flv_address : http://192.168.6.86:8100/live/live1066185-104935-1499332510864.flv
         * m3u8_address : http://192.168.6.32:8101/live/videos/live117/1066185/104935/hls/playlist1.m3u8
         * name : 内场
         */

        private String rtmp_url;
        private long id;
        private String flv_address;
        private String m3u8_address;
        private String name;

        public String getRtmp_url() {
            return rtmp_url;
        }

        public void setRtmp_url(String rtmp_url) {
            this.rtmp_url = rtmp_url;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFlv_address() {
            return flv_address;
        }

        public void setFlv_address(String flv_address) {
            this.flv_address = flv_address;
        }

        public String getM3u8_address() {
            return m3u8_address;
        }

        public void setM3u8_address(String m3u8_address) {
            this.m3u8_address = m3u8_address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
