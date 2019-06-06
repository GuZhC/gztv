package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserCMSPlatformInfo implements Serializable {


    /**
     * username : admin
     * nickname : 超级管理员
     * role_id : 3
     * avatar_url : null
     * content_count : 835
     * role_name : 管理员
     * permissions : ["bg","bg.admin_user","bg.admin_role","bg.admin_flag","bg.admin_column","bg.admin_config","bg.admin_log","bg.admin_ad","bg.admin_content","bg.admin_content.add","bg.admin_content.edit","bg.admin_content.delete","bg.admin_content.audit","bg.admin_content.publish","bg.admin_content.push","bg.admin_content.admin_comment"]
     * id : 1
     */

    private String username;
    private String nickname;
    @SerializedName("role_id")
    private long roleId;
    @SerializedName("avatar_url")
    private Object avatarUrl;
    @SerializedName("content_count")
    private long contentCount;
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

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public Object getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(Object avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
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

    public boolean isHasAddWordPermission() {
        if (permissions != null) {
            for (String p : permissions) {
                boolean is = TextUtils.equals("fg.contribute", p);
                if (is) {
                    return true;
                }
            }
        }
        return false;
    }
}
