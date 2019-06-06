package com.dfsx.ganzcms.app.model;

import android.widget.TextView;

import java.io.Serializable;

/**
 * 关注
 * Created by wxl on 2016/12/8.
 */

public class TagItem implements Serializable {

    /**
     * data : [{"creation_time":1481100296,"follow_us"cai","follow_user_nickname":"cai","follow_user_avatar_url":"http://192.168.6.15:8101/files/20161202/ED6C882EA1077C775653DF2BF500CFFC/ED6C882EA1077C775653DF2BF500CFFC","fanned":false}]
     */
    public String tagText;  //标签上的文本信息：
    public boolean tagCustomEdit;
    public int idx;
    public TextView mView;
}
