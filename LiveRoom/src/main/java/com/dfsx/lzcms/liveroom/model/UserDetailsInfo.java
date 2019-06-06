package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/3/13.
 */
public class UserDetailsInfo implements Serializable {

    /**
     * creation_time : 1481265100
     * role_id : 0
     * username : bowen1
     * nickname : 罗斯塔
     * email : tt@qq.com
     * avatar_id : 1120
     * last_login_time : 1489368648
     * is_admin : false
     * mobile :
     * sex : 0
     * province : null
     * city : null
     * region : null
     * signature : 阿鲁拖摸摸哦哦弄
     * auth_type : 0
     * is_enabled : true
     * is_verified : false
     * role_name : null
     * avatar_url : http://file.dfsxcms.cn:8101/general/pictures/20161209/ADC664F2F69688CA5AC2C1D88F82BF15/ADC664F2F69688CA5AC2C1D88F82BF15.jpg
     * id : 71
     */

    private long creation_time;
    private long role_id;
    private String username;
    private String nickname;
    private String email;
    private long avatar_id;
    private long last_login_time;
    private boolean is_admin;
    private String mobile;
    private int sex;
    private String province;
    private String city;
    private String region;
    private String signature;
    private int auth_type;
    private boolean is_enabled;
    private boolean is_verified;
    private Object role_name;
    private String avatar_url;
    private long id;
    private long follow_count;//<关注人数>,
    private long fan_count;//<粉丝人数>

    public UserDetailsInfo() {
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getRole_id() {
        return role_id;
    }

    public void setRole_id(long role_id) {
        this.role_id = role_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getAvatar_id() {
        return avatar_id;
    }

    public void setAvatar_id(long avatar_id) {
        this.avatar_id = avatar_id;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(int auth_type) {
        this.auth_type = auth_type;
    }

    public boolean isIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public Object getRole_name() {
        return role_name;
    }

    public void setRole_name(Object role_name) {
        this.role_name = role_name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
