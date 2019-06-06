package com.dfsx.ganzcms.app.business;

import com.dfsx.ganzcms.app.R;

import java.util.HashMap;

/**
 * Created by liuwb on 2017/6/28.
 */
public class GradeResourceImpl implements IGradeResource {

    public static final HashMap<Grade, Integer> gradeTagMap = new HashMap<Grade, Integer>() {
        {
            put(Grade.VIP1, R.drawable.grade_tag_red);
            put(Grade.VIP2, R.drawable.grade_tag_yellow);
            put(Grade.VIP3, R.drawable.grade_tag_blue);
            put(Grade.VIP4, R.drawable.grade_tag_green);
        }
    };
    public static final HashMap<Grade, Integer> gradeTextMap = new HashMap<Grade, Integer>() {
        {
            put(Grade.VIP1, R.drawable.grade_vip1);
            put(Grade.VIP2, R.drawable.grade_vip2);
            put(Grade.VIP3, R.drawable.grade_vip3);
            put(Grade.VIP4, R.drawable.grade_vip3);
        }
    };

    @Override
    public int getGradeTagResId(Grade grade) {
        return gradeTagMap.get(grade);
    }

    @Override
    public int getGradeTextResId(Grade grade) {
        return gradeTextMap.get(grade);
    }

    @Override
    public CharSequence getGradeTagText(Grade grade) {
        String text = "绿钻";
        if (grade == Grade.VIP1) {
            text = "红钻";
        } else if (grade == Grade.VIP2) {
            text = "黄钻";
        } else if (grade == Grade.VIP3) {
            text = "蓝钻";
        } else if (grade == Grade.VIP4) {
            text = "绿钻";
        }
        return text;
    }

    @Override
    public Grade getGrade(long userExperience) {
        Grade g = Grade.VIP3;
        if (userExperience <= 1500) {
            g = Grade.VIP1;
        } else if (userExperience <= 7000) {
            g = Grade.VIP2;
        } else if (userExperience <= 25000) {
            g = Grade.VIP3;
        }
        return g;
    }
}
