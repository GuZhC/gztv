package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ColumnContentListItem implements Serializable {

    private long id;
    @SerializedName("column_id")
    private long columnId;
    @SerializedName("column_name")
    private String columnName;
    @SerializedName("author_nickname")
    private String authorNickname;
    @SerializedName("creation_time")
    private long creationTime;
    @SerializedName("editor_nickname")
    private String editorNickname;
    @SerializedName("edit_time")
    private long editTime;
    @SerializedName("publisher_nickname")
    private String publisherNickname;
    @SerializedName("publish_time")
    private long publishTime;
    private String type;
    private String title;
    private String subtitle;
    @SerializedName("list_item_mode")
    private int listItemMode;
    @SerializedName("poster_url")
    private String posterUrl;
    private String source;
    private boolean quoted;
    private String summary;
    @SerializedName("comment_mode")
    private int commentMode;
    @SerializedName("comment_count")
    private long commentCount;
    @SerializedName("view_count")
    private long viewCount;
    @SerializedName("thumbnail_urls")
    private List<String> thumbnailUrls;
    @SerializedName("fields")
    private HashMap<String, Object> fieldsMap;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getEditorNickname() {
        return editorNickname;
    }

    public void setEditorNickname(String editorNickname) {
        this.editorNickname = editorNickname;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public String getPublisherNickname() {
        return publisherNickname;
    }

    public void setPublisherNickname(String publisherNickname) {
        this.publisherNickname = publisherNickname;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getListItemMode() {
        return listItemMode;
    }

    public void setListItemMode(int listItemMode) {
        this.listItemMode = listItemMode;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isQuoted() {
        return quoted;
    }

    public void setQuoted(boolean quoted) {
        this.quoted = quoted;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getCommentMode() {
        return commentMode;
    }

    public void setCommentMode(int commentMode) {
        this.commentMode = commentMode;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public List<String> getThumbnailUrls() {
        return thumbnailUrls;
    }

    public void setThumbnailUrls(List<String> thumbnailUrls) {
        this.thumbnailUrls = thumbnailUrls;
    }

    public HashMap<String, Object> getFieldsMap() {
        return fieldsMap;
    }

    public void setFieldsMap(HashMap<String, Object> fieldsMap) {
        this.fieldsMap = fieldsMap;
    }
}
