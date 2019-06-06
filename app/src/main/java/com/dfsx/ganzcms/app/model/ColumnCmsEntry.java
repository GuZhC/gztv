package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by heyang on 2016/12/21   首页栏目 实体类
 */
public class ColumnCmsEntry implements Serializable {


    /**
     * id : -1
     * parent_id : -1
     * name :
     * description :
     * weight : -1
     * content_count : -1
     * rate_mode : -1
     * comment_mode : -1
     * audit_mode : -1
     * visible : false
     */

    private long id;
    private long parent_id;
    private String name;
    private String description;
    private int weight;
    private long content_count;
    private int rate_mode;
    private int comment_mode;
    private int audit_mode;
    private boolean visible;
    private String  key;
    private String type;   //  区分news  或 其他类型
    private String icon_url;
    private String machine_code;
    private long  dynamicId;  // 手机动态入口

    private String list_type;   // 便民  九宫格   还是 多栏目

    public String getList_type() {
        return list_type;
    }

    public void setList_type(String list_type) {
        this.list_type = list_type;
    }

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    private int  content_id;

    public long getSliderId() {
        return sliderId;
    }

    public void setSliderId(long sliderId) {
        this.sliderId = sliderId;
    }

    private long sliderId;

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ColumnCmsEntry> getDlist() {
        return dlist;
    }

    public void setDlist(List<ColumnCmsEntry> dlist) {
        this.dlist = dlist;
    }

    private List<ColumnCmsEntry>  dlist;

    public List<BackItem> getData() {
        return data;
    }

    public void setData(List<BackItem> data) {
        this.data = data;
    }

    private List<BackItem> data;

    public long getId() {
        return id;
    }

    public String getMachine_code() {
        return machine_code;
    }

    public void setMachine_code(String machine_code) {
        this.machine_code = machine_code;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getContent_count() {
        return content_count;
    }

    public void setContent_count(long content_count) {
        this.content_count = content_count;
    }

    public int getRate_mode() {
        return rate_mode;
    }

    public void setRate_mode(int rate_mode) {
        this.rate_mode = rate_mode;
    }

    public int getComment_mode() {
        return comment_mode;
    }

    public void setComment_mode(int comment_mode) {
        this.comment_mode = comment_mode;
    }

    public int getAudit_mode() {
        return audit_mode;
    }

    public void setAudit_mode(int audit_mode) {
        this.audit_mode = audit_mode;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public static class BackItem {

        public long getId() {
            return cid;
        }

        public void setId(long id) {
            this.cid = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private long cid;
        private String key;
        private String name;
    }
}