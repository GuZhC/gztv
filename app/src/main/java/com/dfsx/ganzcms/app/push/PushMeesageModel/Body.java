package com.dfsx.ganzcms.app.push.PushMeesageModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wen on 2017/3/27.
 */

public class Body {
    private String link_type;
    private String link_value;
    private long user_id;
    private long fan_user_id;
    private long content_id;
    private long follow_user_id;
    private long video_id;
    private long show_id;
    /**
     * 直播 <int, 状态, 1 – 预告，2 – 直播，3 – 回放>,
     */
    private int state;
    /**
     * <int, 直播类型：1 – 个人直播，2 – 活动直播>,
     */
    @SerializedName("type")
    private String live_type;
    private long thread_id;

    /**
     * 中奖id
     */
    @SerializedName("id")
    private long lottery_drawn_id;

    public Body() {

    }

    public Body(String link_type, String link_value, long user_id, long fan_user_id, long content_id, long follow_user_id, long video_id, long show_id,
                int state, int live_type, long thread_id) {
        this.link_type = link_type;
        this.link_value = link_value;
        this.user_id = user_id;
        this.fan_user_id = fan_user_id;
        this.content_id = content_id;
        this.follow_user_id = follow_user_id;
        this.video_id = video_id;
        this.show_id = show_id;
        this.state = state;
        this.live_type = live_type + "";
        this.thread_id = thread_id;
    }

    public String getLink_type() {
        return link_type;
    }

    public void setLink_type(String link_type) {
        this.link_type = link_type;
    }

    public String getLink_value() {
        return link_value;
    }

    public void setLink_value(String link_value) {
        this.link_value = link_value;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getFan_user_id() {
        return fan_user_id;
    }

    public void setFan_user_id(long fan_user_id) {
        this.fan_user_id = fan_user_id;
    }

    public long getContent_id() {
        return content_id;
    }

    public void setContent_id(long content_id) {
        this.content_id = content_id;
    }

    public long getFollow_user_id() {
        return follow_user_id;
    }

    public void setFollow_user_id(long follow_user_id) {
        this.follow_user_id = follow_user_id;
    }

    public long getVideo_id() {
        return video_id;
    }

    public void setVideo_id(long video_id) {
        this.video_id = video_id;
    }

    public long getShow_id() {
        return show_id;
    }

    public void setShow_id(long channel_id) {
        this.show_id = channel_id;
    }

    public long getThread_id() {
        return thread_id;
    }

    public void setThread_id(long thread_id) {
        this.thread_id = thread_id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getType() {
        return live_type;
    }

    public int getLive_type() {
        int liveType = 0;
        try {
            liveType = Integer.valueOf(live_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liveType;
    }

    public void setLive_type(int live_type) {
        this.live_type = live_type + "";
    }

    public long getLottery_drawn_id() {
        return lottery_drawn_id;
    }

    public void setLottery_drawn_id(long lottery_drawn_id) {
        this.lottery_drawn_id = lottery_drawn_id;
    }
}
