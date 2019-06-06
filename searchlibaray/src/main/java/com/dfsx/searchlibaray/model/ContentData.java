package com.dfsx.searchlibaray.model;

import java.util.List;

public class ContentData implements ISearchData<ContentData> {


    /**
     * creation_time : 1496990887
     * source :
     * id : 1000876
     * column_name : 列表
     * author_id : 1
     * comment_mode : 1
     * column_id : 33
     * editor_id : 1
     * publisher_id : 1
     * poster_id : 0
     * title : test
     * list_item_mode : 1
     * poster_url :
     * summary :
     * quoted : false
     * publish_time : 1496990903
     * edit_time : 1496992471
     * comment_count : 0
     * view_count : 40
     * editor_name : admin
     * editor_nickname : 超级管理员
     * editor_avatar_url : http://file.baview.cn:8101/general/pictures/20171116/7BB9A2EA5027C57D0F13F44B324FB568/7BB9A2EA5027C57D0F13F44B324FB568.jpg
     * publisher_name : admin
     * publisher_nickname : 超级管理员
     * publisher_avatar_url : http://file.baview.cn:8101/general/pictures/20171116/7BB9A2EA5027C57D0F13F44B324FB568/7BB9A2EA5027C57D0F13F44B324FB568.jpg
     * sub_title : teste
     * thumbnail_urls : ["http://file.baview.cn:8101/cms/pictures/20170609/c0e934619befaa4649118a29f82d10bf/c0e934619befaa4649118a29f82d10bf.jpg"]
     * author_name : admin
     * author_nickname : 超级管理员
     * author_avatar_url : http://file.baview.cn:8101/general/pictures/20171116/7BB9A2EA5027C57D0F13F44B324FB568/7BB9A2EA5027C57D0F13F44B324FB568.jpg
     * type : default
     * extension : null
     */

    private long creation_time;
    private String source;
    private long id;
    private String column_name;
    private long author_id;
    private int comment_mode;
    private long column_id;
    private long editor_id;
    private long publisher_id;
    private long poster_id;
    private String title;
    private int list_item_mode;
    private String poster_url;
    private String summary;
    private boolean quoted;
    private long publish_time;
    private long edit_time;
    private long comment_count;
    private long view_count;
    private String editor_name;
    private String editor_nickname;
    private String editor_avatar_url;
    private String publisher_name;
    private String publisher_nickname;
    private String publisher_avatar_url;
    private String sub_title;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private String type;
    private Object extension;
    private List<String> thumbnail_urls;

    private SearchItemInfo searchItemInfo;

    @Override
    public SearchShowStyle getShowStyle() {
        return SearchShowStyle.STYLE_WORD;
    }

    @Override
    public ContentData getContentData() {
        return this;
    }

    @Override
    public SearchItemInfo getSearchItemInfo() {
        return searchItemInfo;
    }

    @Override
    public void setSearchItemInfo(SearchItemInfo itemInfo) {
        searchItemInfo = itemInfo;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public int getComment_mode() {
        return comment_mode;
    }

    public void setComment_mode(int comment_mode) {
        this.comment_mode = comment_mode;
    }

    public long getColumn_id() {
        return column_id;
    }

    public void setColumn_id(long column_id) {
        this.column_id = column_id;
    }

    public long getEditor_id() {
        return editor_id;
    }

    public void setEditor_id(long editor_id) {
        this.editor_id = editor_id;
    }

    public long getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(long publisher_id) {
        this.publisher_id = publisher_id;
    }

    public long getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(long poster_id) {
        this.poster_id = poster_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getList_item_mode() {
        return list_item_mode;
    }

    public void setList_item_mode(int list_item_mode) {
        this.list_item_mode = list_item_mode;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isQuoted() {
        return quoted;
    }

    public void setQuoted(boolean quoted) {
        this.quoted = quoted;
    }

    public long getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(long publish_time) {
        this.publish_time = publish_time;
    }

    public long getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(long edit_time) {
        this.edit_time = edit_time;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public long getView_count() {
        return view_count;
    }

    public void setView_count(long view_count) {
        this.view_count = view_count;
    }

    public String getEditor_name() {
        return editor_name;
    }

    public void setEditor_name(String editor_name) {
        this.editor_name = editor_name;
    }

    public String getEditor_nickname() {
        return editor_nickname;
    }

    public void setEditor_nickname(String editor_nickname) {
        this.editor_nickname = editor_nickname;
    }

    public String getEditor_avatar_url() {
        return editor_avatar_url;
    }

    public void setEditor_avatar_url(String editor_avatar_url) {
        this.editor_avatar_url = editor_avatar_url;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getPublisher_nickname() {
        return publisher_nickname;
    }

    public void setPublisher_nickname(String publisher_nickname) {
        this.publisher_nickname = publisher_nickname;
    }

    public String getPublisher_avatar_url() {
        return publisher_avatar_url;
    }

    public void setPublisher_avatar_url(String publisher_avatar_url) {
        this.publisher_avatar_url = publisher_avatar_url;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }

    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getExtension() {
        return extension;
    }

    public void setExtension(Object extension) {
        this.extension = extension;
    }

    public List<String> getThumbnail_urls() {
        return thumbnail_urls;
    }

    public void setThumbnail_urls(List<String> thumbnail_urls) {
        this.thumbnail_urls = thumbnail_urls;
    }
}
