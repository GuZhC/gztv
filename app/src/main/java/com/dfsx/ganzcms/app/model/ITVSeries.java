package com.dfsx.ganzcms.app.model;

public interface ITVSeries {
    long getId();

    /**
     * 获取电视剧的名称
     *
     * @return
     */
    String getTVName();

    /**
     * 获取电视剧的图片
     *
     * @return
     */
    String getTVImage();

    /**
     * 获取电视剧的集数
     *
     * @return
     */
    int getTVSize();

    /**
     * 获取电视剧简单描述
     *
     * @return
     */
    String getTVDesc();
}
