package com.dfsx.searchlibaray.businness;

import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchItemInfo;

public class UserInfo implements ISearchData<UserInfo> {


    /**
     * creation_time : 1473366703
     * role_id : 0
     * username : admin
     * nickname : 超级管理员
     * avatar_id : 28470758
     * is_admin : true
     * email : null
     * last_login_time : 1512611236
     * mobile : null
     * sex : 1
     * province : 湖南
     * city : 益阳
     * region : 资阳区
     * signature : 78901
     * auth_type : 0
     * follow_count : 10
     * fan_count : 16
     * favorite_count : 0
     * exp : 100
     * user_level_id : 10552
     * register_source : 0
     * phone_number : null
     * avatar_url : http://file.baview.cn:8101/general/pictures/20171116/7BB9A2EA5027C57D0F13F44B324FB568/7BB9A2EA5027C57D0F13F44B324FB568.jpg
     * is_enabled : true
     * is_verified : false
     * role_name : null
     * id : 1
     */

    private long creation_time;
    private long role_id;
    private String username;
    private String nickname;
    private long avatar_id;
    private boolean is_admin;
    private String email;
    private long last_login_time;
    private String mobile;
    private int sex;
    private String province;
    private String city;
    private String region;
    private String signature;
    private int auth_type;
    private long follow_count;
    private long fan_count;
    private long favorite_count;
    private long exp;
    private long user_level_id;
    private long register_source;
    private String phone_number;
    private String avatar_url;
    private boolean is_enabled;
    private boolean is_verified;
    private String role_name;
    private long id;

    private SearchItemInfo searchItemInfo;
    /**
     * 是否关注对方
     */
    private boolean isFollowed;
    /**
     * 对方是否是我的粉丝
     */
    private boolean isFanned;

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

    public long getAvatar_id() {
        return avatar_id;
    }

    public void setAvatar_id(long avatar_id) {
        this.avatar_id = avatar_id;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(long last_login_time) {
        this.last_login_time = last_login_time;
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

    public long getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(long favorite_count) {
        this.favorite_count = favorite_count;
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

    public long getRegister_source() {
        return register_source;
    }

    public void setRegister_source(long register_source) {
        this.register_source = register_source;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
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

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public long getId() {
        return id;
    }

    @Override
    public SearchShowStyle getShowStyle() {
        return SearchShowStyle.STYLE_USER;
    }

    @Override
    public UserInfo getContentData() {
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

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFanned() {
        return isFanned;
    }

    public void setFanned(boolean fanned) {
        isFanned = fanned;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }
}
