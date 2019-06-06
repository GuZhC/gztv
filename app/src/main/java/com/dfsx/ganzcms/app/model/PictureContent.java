package com.dfsx.ganzcms.app.model;

/**
 * CMS图片内容
 */
public class PictureContent {


    /**
     * id : long, 图像ID
     * width : int, 图像宽度
     * height : int, 图像高度
     * url : string, 图像地址
     */

    private long id;
    private long width;
    private long height;
    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
