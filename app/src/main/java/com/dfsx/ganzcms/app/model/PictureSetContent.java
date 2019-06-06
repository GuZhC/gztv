package com.dfsx.ganzcms.app.model;

import java.util.List;

/**
 * 图组类型的CMS内容
 */
public class PictureSetContent {

    /**
     * id : long, 图组ID
     * title : string, 图组标题
     * pictures : [{"id":"long, 图像ID","width":"int, 图像宽度","height":"int, 图像高度","url":"string, 图像地址","introduction":"string, 图像说明"}]
     */

    private long id;
    private String title;
    private List<PictureSet> pictures;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PictureSet> getPictures() {
        return pictures;
    }

    public void setPictures(List<PictureSet> pictures) {
        this.pictures = pictures;
    }

    public static class PictureSet {
        /**
         * id : long, 图像ID
         * width : int, 图像宽度
         * height : int, 图像高度
         * url : string, 图像地址
         * introduction : string, 图像说明
         */

        private long id;
        private int width;
        private int height;
        private String url;
        private String introduction;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }
    }
}
