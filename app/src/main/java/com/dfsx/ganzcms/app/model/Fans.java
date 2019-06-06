package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.fragment.AbsMyAttentionFansFragment;

import java.io.Serializable;
import java.util.List;

/**
 * 粉丝
 * Created by wxl on 2016/12/8.
 */

public class Fans implements Serializable {

    /**
     * total : 1
     * data : [{"creation_time":1481160862,"fan_user_id":67,"fan_user_name":"test22","fan_user_nickname":"test22","fan_user_avatar_url":null,"followed":false}]
     */

    private int total;
    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements AbsMyAttentionFansFragment.IConcernData {
        /**
         * creation_time : 1481160862
         * fan_user_id : 67
         * fan_user_name : test22
         * fan_user_nickname : test22
         * fan_user_avatar_url : null
         * followed : false
         */

        private int creation_time;
        private long fan_user_id;
        private String fan_user_name;
        private String fan_user_nickname;
        private String fan_user_avatar_url;
        private boolean followed;

        private String signature;

        public int getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(int creation_time) {
            this.creation_time = creation_time;
        }

        public long getFan_user_id() {
            return fan_user_id;
        }

        public void setFan_user_id(long fan_user_id) {
            this.fan_user_id = fan_user_id;
        }

        public String getFan_user_name() {
            return fan_user_name;
        }

        public void setFan_user_name(String fan_user_name) {
            this.fan_user_name = fan_user_name;
        }

        public String getFan_user_nickname() {
            return fan_user_nickname;
        }

        public void setFan_user_nickname(String fan_user_nickname) {
            this.fan_user_nickname = fan_user_nickname;
        }

        public String getFan_user_avatar_url() {
            return fan_user_avatar_url;
        }

        public void setFan_user_avatar_url(String fan_user_avatar_url) {
            this.fan_user_avatar_url = fan_user_avatar_url;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        @Override
        public String getNickName() {
            return getFan_user_nickname();
        }

        @Override
        public String getUserName() {
            return getFan_user_name();
        }

        @Override
        public String getLogoUrl() {
            return getFan_user_avatar_url();
        }

        @Override
        public String getSignature() {
            return signature;
        }

        @Override
        public long getUserId() {
            return getFan_user_id();
        }

        @Override
        public int getModeType() {
            return isFollowed() ? AbsMyAttentionFansFragment.MODE_ATTENTION :
                    AbsMyAttentionFansFragment.MODE_NO_ATTENTION;
        }

        @Override
        public void setModeType(int modeType) {
            followed = modeType == AbsMyAttentionFansFragment.MODE_ATTENTION;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}
