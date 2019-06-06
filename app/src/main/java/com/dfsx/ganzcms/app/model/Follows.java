package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.fragment.AbsMyAttentionFansFragment;

import java.io.Serializable;
import java.util.List;

/**
 * 关注
 * Created by wxl on 2016/12/8.
 */

public class Follows implements Serializable {

    /**
     * total : 6
     * data : [{"creation_time":1481100296,"follow_user_id":42,"follow_user_name":"cai","follow_user_nickname":"cai","follow_user_avatar_url":"http://192.168.6.15:8101/files/20161202/ED6C882EA1077C775653DF2BF500CFFC/ED6C882EA1077C775653DF2BF500CFFC","fanned":false}]
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
         * creation_time : 1481100296
         * follow_user_id : 42
         * follow_user_name : cai
         * follow_user_nickname : cai
         * follow_user_avatar_url : http://192.168.6.15:8101/files/20161202/ED6C882EA1077C775653DF2BF500CFFC/ED6C882EA1077C775653DF2BF500CFFC
         * fanned : false
         */

        private int creation_time;
        private long follow_user_id;
        private String follow_user_name;
        private String follow_user_nickname;
        private String follow_user_avatar_url;
        private boolean fanned;

        private String signature;

        public int getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(int creation_time) {
            this.creation_time = creation_time;
        }

        public long getFollow_user_id() {
            return follow_user_id;
        }

        public void setFollow_user_id(long follow_user_id) {
            this.follow_user_id = follow_user_id;
        }

        public String getFollow_user_name() {
            return follow_user_name;
        }

        public void setFollow_user_name(String follow_user_name) {
            this.follow_user_name = follow_user_name;
        }

        public String getFollow_user_nickname() {
            return follow_user_nickname;
        }

        public void setFollow_user_nickname(String follow_user_nickname) {
            this.follow_user_nickname = follow_user_nickname;
        }

        public String getFollow_user_avatar_url() {
            return follow_user_avatar_url;
        }

        public void setFollow_user_avatar_url(String follow_user_avatar_url) {
            this.follow_user_avatar_url = follow_user_avatar_url;
        }

        public boolean isFanned() {
            return fanned;
        }

        public void setFanned(boolean fanned) {
            this.fanned = fanned;
        }

        @Override
        public String getNickName() {
            return getFollow_user_nickname();
        }

        @Override
        public String getUserName() {
            return getFollow_user_name();
        }

        @Override
        public String getLogoUrl() {
            return getFollow_user_avatar_url();
        }

        @Override
        public String getSignature() {
            return signature;
        }

        @Override
        public long getUserId() {
            return getFollow_user_id();
        }

        @Override
        public int getModeType() {
            return isFanned() ?
                    AbsMyAttentionFansFragment.MODE_MUTUAL_ATTENTION
                    :
                    AbsMyAttentionFansFragment.MODE_ATTENTION;
        }

        @Override
        public void setModeType(int modeType) {
            fanned = modeType == AbsMyAttentionFansFragment.MODE_MUTUAL_ATTENTION;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}
