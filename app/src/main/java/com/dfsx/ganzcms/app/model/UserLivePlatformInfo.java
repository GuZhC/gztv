package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserLivePlatformInfo implements Serializable {

    /**
     * username : bowen
     * nickname : 火星
     * user_type : 1
     * role_id : 2
     * avatar_url : http://www.baview.cn:8101/general/pictures/20170619/59AB3CCCD34B386495294D1CE0E3D013/59AB3CCCD34B386495294D1CE0E3D013.jpg
     * role_name : 会员
     * permissions : ["fg","fg.visit_category_22","fg.visit_category_4444","fg.visit_category_default","fg.visit_category_game","fg.visit_category_lol","fg.visit_category_xxx"]
     * id : 53
     */

    private String username;
    private String nickname;
    /**
     * <int, 用户类型：1 – 普通用户, 2 – 服务账户>
     */
    @SerializedName("user_type")
    private int userType;
    @SerializedName("role_id")
    private long roleId;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("role_name")
    private String roleName;
    private long id;
    private List<String> permissions;

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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isCreateLivePermission() {
        if (permissions != null) {
            for (String permission : permissions) {
                boolean is = TextUtils.equals(permission, "fg.visit_category_default.create_personal_show");
                if (is) {
                    return is;
                }
            }
        }
        return false;
    }
}
