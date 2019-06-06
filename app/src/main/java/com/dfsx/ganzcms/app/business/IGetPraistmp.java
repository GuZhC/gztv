package com.dfsx.ganzcms.app.business;

import com.dfsx.ganzcms.app.model.PriseModel;
import com.dfsx.ganzcms.app.model.TopicalEntry;

import java.util.List;
import java.util.Map;

/**
 * create by  heayng   2017/7/13
 * 分为圈子 和新闻 点赞  点擦
 */


public interface IGetPraistmp {

    /*8
     *   得到列表
     */
    Map<Long, PriseModel> getDList();

    /**
     * 保存
     */
    void saveListToFile();

    /**
     * 修改标志位 点赞
     */
    void updateValuse(long id, boolean isPrise, boolean isStrmp, boolean isRead);


    /**
     * 判断是否被赞  true:已赞
     */
    boolean isPriseFlag(long id);

    /**
     * 判断是否被踩  true :已踩
     */
    boolean isStrmpFlag(long id);


    /**
     * 判断是否已浏览过
     */
    boolean isRead(long id);

    /**
     * 判断是否关注用户ID
     */
    public int isAttionUuser(long userId, int role);

    /**
     * 判断是否收藏
     */
    boolean isFavority(long communityId);

    /**
     * 判断是否关注用户ID
     */
    public void updateAttionUuser(long userId, int role);


    /**
     * 修改标志位 点赞
     */
    void updateValuse(long id, int role, boolean isfavr);



//    /**
//     * 判断是否关注用户ID
//     */
//    public int isAttionUuser(long userId, int role);
//
//    /**
//     * 判断是否收藏
//     */
//    boolean isFavority(long communityId);


}
