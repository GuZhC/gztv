package com.dfsx.logonproject.model;

/**
 * 修改的个人信息实体类
 * Created by wxl on 2016/11/11.
 */

/**
 * “nickname”: <string, 昵称>,
 * “email”: <string, 邮件地址>,
 * “mobile”:<string,手机号>,
 * “sex”:<int,性别:1-男,2-女,0-保密>
 * “signature”:<string, 个性签名>,
 * “province”:<string, 省>,
 * “city”:<string, 市>,
 * “region”:<string, 区>,
 * “avatar_id”: <long, 用户头像ID（用户头像ID和相对路径只有一个有效）>,
 * “avatar_path”: <long, 用户头像相对路径（用户头像ID和相对路径只有一个有效）>
 */

public class ChangedUserInfo {
    private String nickname;
    private String email;
    private String mobile;
    private String signature;
    private String province;
    private String city;
    private String region;
    private String avatar_path;
    private int sex = -1;
    private long birthday;
    private long avatar_id = -1;
    private  String  telePhone;
    private String  detail_address;

    @Override
    public String toString() {
        return "ChangedUserInfo{" +
                "nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", signature='" + signature + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", avatar_path='" + avatar_path + '\'' +
                ", sex=" + sex +
                ", avatar_id=" + avatar_id +
                '}';
    }

    public void resetValues() {
        nickname = null;
        email = null;
        mobile = null;
        signature = null;
        province = null;
        city = null;
        region = null;
        avatar_path = null;
        sex = -1;
        avatar_id = -1;
        detail_address=null;
        birthday=-1;
    }

    public boolean hasNoModified() {
        if (nickname == null && email == null && mobile == null &&
                signature == null && province == null && city == null &&
                birthday==-1 && detail_address==null &
                region == null && avatar_path == null && avatar_id == -1 && sex == -1)
            return true;
        return false;
    }

    public String getTelePhone() {
        return telePhone;
    }

    public void setTelePhone(String telePhone) {
        this.telePhone = telePhone;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public int getSex() {
        return sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }


    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getAvatar_id() {
        return avatar_id;
    }

    public void setAvatar_id(long avatar_id) {
        this.avatar_id = avatar_id;
    }

    public String getDetail_address() {
        return detail_address;
    }

    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }
}
