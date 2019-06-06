package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2016/10/24.
 */
public class Category implements Serializable {


    /**
     * key : 分类
     * parent_key : 父分类key
     * name : 分类名称
     * description : 分类描述
     * icon_id : 图标图像ID
     * icon_url : 图标图像URL地址
     * weight : 分类权重，值越大显示越靠前
     * can_moderating : 是否可以主持直播间
     * can_participating : 是否可以参与直播间
     * admins : [{"id":11111,"username":"用户名","nickname":"呢称"}]
     */

    private long id;
    private String key;
    private String parent_key;
    private String name;
    private String description;
    @SerializedName("icon_id")
    private long iconId;
    @SerializedName("icon_url")
    private String iconUrl;
    private String weight;
    @SerializedName("can_moderating")
    private boolean canModerating;
    @SerializedName("can_participating")
    private boolean canParticipating;
    private boolean visible;
    @SerializedName("parent_id")
    private Long parentId;
    /**
     * id : 11111
     * username : 用户名
     * nickname : 呢称
     */

    private List<Person> admins;

    private CategoryPermission permission;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParent_key() {
        return parent_key;
    }

    public void setParent_key(String parent_key) {
        this.parent_key = parent_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIconId() {
        return iconId;
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public boolean isCanModerating() {
        return canModerating;
    }

    public void setCanModerating(boolean canModerating) {
        this.canModerating = canModerating;
    }

    public boolean isCanParticipating() {
        return canParticipating;
    }

    public void setCanParticipating(boolean canParticipating) {
        this.canParticipating = canParticipating;
    }

    public List<Person> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Person> admins) {
        this.admins = admins;
    }


    public CategoryPermission getPermission() {
        return permission;
    }

    public void setPermission(CategoryPermission permission) {
        this.permission = permission;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
