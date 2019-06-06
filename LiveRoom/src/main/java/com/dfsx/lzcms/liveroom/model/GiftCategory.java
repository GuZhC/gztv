package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/11/4.
 */
public class GiftCategory implements Serializable {

    /**
     * id : 256
     * name : 分类名称
     * description : 分类说明
     * weight : 8 <int, 分类权重，权重越大显示越靠前>
     */

    private long id;
    private String name;
    private String description;
    private int weight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
