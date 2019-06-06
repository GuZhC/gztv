package com.dfsx.ganzcms.app.business;

/**
 * Created by liuwb on 2017/6/28.
 */
public interface IGradeResource {

    enum Grade {
        VIP1,
        VIP2,
        VIP3,
        VIP4,
    }

    /**
     * 仅获取等级的图标 指黄红蓝绿
     *
     * @return
     */
    int getGradeTagResId(Grade grade);

    /**
     * 获取有VIP 等级文字的图标
     *
     * @return
     */
    int getGradeTextResId(Grade grade);

    CharSequence getGradeTagText(Grade grade);


    Grade getGrade(long userExperience);
}
