package com.dfsx.core.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2016/10/26  用户实体类
 */
public class Account implements Serializable {

    /**
     * token :
     * user : {"id":0,"username":"","email":"","mobile":"","sex":0,"signature":"","province":"","city":"","region":"","avatar_id":0,"avatar_url":"","is_enabled":true,"creation_time":"","last_login_time":"","is_admin":true}
     */

    private String token = "";
    /**
     * id : 0
     * username :
     * email :
     * mobile :
     * sex : 0
     * signature :
     * province :
     * city :
     * region :
     * avatar_id : 0
     * avatar_url :
     * is_enabled : true
     * creation_time :
     * last_login_time :
     * is_admin : true
     */

    public LoginParams loginInfo;

    public String sessionId;
    public String sessionName;


    private UserBean user;

    private List<AuthorThrid>  athorList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public List<AuthorThrid> getAthorList() {
        return athorList;
    }

    public void setAthorList(List<AuthorThrid> athorList) {
        this.athorList = athorList;
    }

    public static class AuthorThrid implements Serializable {
        private int type;
        private String nickName;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

    public static class UserBean implements Serializable {
        private long id;
        private String username = "";
        private String nickname = "";
        private String email = "";
        private String mobile;
        private long birthday;
        private int sex;
        private String signature;
        private String province;
        private String city;
        private String region;
        private long avatar_id;
        private String avatar_url;
        private long creation_time;
        private long last_login_time;
        private boolean is_admin;
        private boolean is_verified;
        private boolean is_enabled;

        /**
         * <int,授权登录方式:0-平台,1-微信,2-微博,3-QQ>
         */
        private int auth_type;
        private long follow_count;
        private long fan_count;
        //        @SerializedName("favorite_count")
        private long store_count;
        private long favorite_count;
        /**
         * 用户经验值
         */
        private long exp;
        /**
         * 用户等级id
         */
        private long user_level_id;

        private String phone_number;
        private String detail_address;


        public long getFollow_count() {
            return follow_count;
        }

        public void setFollow_count(long follow_count) {
            this.follow_count = follow_count;
        }

        public long getFan_count() {
            return fan_count;
        }

        public void setFan_count(long fan_count) {
            this.fan_count = fan_count;
        }

        public int getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(int auth_type) {
            this.auth_type = auth_type;
        }

        public boolean is_verified() {
            return is_verified;
        }

        public void setIs_verified(boolean is_verified) {
            this.is_verified = is_verified;
        }

        public boolean is_admin() {
            return is_admin;
        }

        public boolean is_enabled() {
            return is_enabled;
        }

        public UserBean(String username, String signature, String avatar_url) {
            this.username = username;
            this.signature = signature;
            this.avatar_url = avatar_url;
        }

        public UserBean() {
        }

        @Override
        public String toString() {
            return "UserBean{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", email='" + email + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", sex=" + sex +
                    ", signature='" + signature + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", region='" + region + '\'' +
                    ", avatar_id=" + avatar_id +
                    ", avatar_url='" + avatar_url + '\'' +
                    ", is_enabled=" + is_enabled +
                    ", creation_time='" + creation_time + '\'' +
                    ", last_login_time='" + last_login_time + '\'' +
                    ", is_admin=" + is_admin +
                    ", auth_type=" + auth_type +
                    ", is_verified=" + is_verified +
                    '}';
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public long getBirthday() {
            return birthday;
        }

        public void setBirthday(long birthday) {
            this.birthday = birthday;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public long getAvatar_id() {
            return avatar_id;
        }

        public void setAvatar_id(long avatar_id) {
            this.avatar_id = avatar_id;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public boolean isIs_enabled() {
            return is_enabled;
        }

        public void setIs_enabled(boolean is_enabled) {
            this.is_enabled = is_enabled;
        }

        public long getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(long creation_time) {
            this.creation_time = creation_time;
        }

        public long getLast_login_time() {
            return last_login_time;
        }

        public void setLast_login_time(long last_login_time) {
            this.last_login_time = last_login_time;
        }

        public boolean isIs_admin() {
            return is_admin;
        }

        public void setIs_admin(boolean is_admin) {
            this.is_admin = is_admin;
        }

        public long getExp() {
            return exp;
        }

        public void setExp(long exp) {
            this.exp = exp;
        }

        public long getUser_level_id() {
            return user_level_id;
        }

        public void setUser_level_id(long user_level_id) {
            this.user_level_id = user_level_id;
        }

        public long getStore_count() {
            return store_count;
        }

        public void setStore_count(long store_count) {
            this.store_count = store_count;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getDetail_address() {
            return detail_address;
        }

        public void setDetail_address(String detail_address) {
            this.detail_address = detail_address;
        }

        public long getFavorite_count() {
            return favorite_count;
        }

        public void setFavorite_count(long favorite_count) {
            this.favorite_count = favorite_count;
        }

    }
}

