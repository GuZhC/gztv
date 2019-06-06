package com.dfsx.searchlibaray.model;

import java.io.Serializable;
import java.util.List;

public class SearchItemInfo implements Serializable {


    /**
     * source : general
     * attrs : []
     * creation_time : 1512380802
     * title : <em>test</em>
     * body : <em>test</em>
     * id : 1
     * type : test
     */

    private String source;
    private long creation_time;
    private String title;
    private String body;
    private long id;
    private String type;
    private List<String> attrs;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<?> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }
}
