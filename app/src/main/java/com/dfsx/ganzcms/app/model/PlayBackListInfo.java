package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2016/12/13.
 */
public class PlayBackListInfo implements Serializable {


    /**
     * total : 111111111111111
     * data : [{"id":2222222222222,"title":"回放标题","duration":33333333333333332,"thumb_id":111111111111111100,"thumb_url":"回放缩略图地址","play_times":1111111111111111200,"like_count":1.1111111111111111E21,"creation_time":"11111111111111110000","channel_id":1111111111111111200,"channel_title":"来源的频道标题","category_key":"所属分类Key","category_name":"所属分类名称","closed":true,"flags":1}]
     */

    private long total;
    private List<PlayBackInfo> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<PlayBackInfo> getData() {
        return data;
    }

    public void setData(List<PlayBackInfo> data) {
        this.data = data;
    }


}
