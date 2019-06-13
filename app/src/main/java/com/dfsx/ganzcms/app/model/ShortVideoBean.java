package com.dfsx.ganzcms.app.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;

/**
 * @author : GuZhC
 * @date : 2019/6/5 9:33
 * @description : 短视频实体类
 */
public class ShortVideoBean implements MultiItemEntity {
    public static final int TYPE_SHARE = 100;
    public static final int TYPE_VIDEO = 101;
    private int vitemType = TYPE_VIDEO;
    private ContentCmsInfoEntry contentCmsInfoEntry;

    public ContentCmsInfoEntry getContentCmsInfoEntry() {
        return contentCmsInfoEntry;
    }

    public void setContentCmsInfoEntry(ContentCmsInfoEntry contentCmsInfoEntry) {
        this.contentCmsInfoEntry = contentCmsInfoEntry;
    }

    private int  video_state = ShortVideoAdapter.VIDEO_NULL;

    public int getVideo_state() {
        return video_state;
    }

    public void setVideo_state(int video_state) {
        this.video_state = video_state;
    }

    @Override
    public int getItemType() {
        return vitemType;
    }

    public void setVitemType(int vitemType) {
        this.vitemType = vitemType;
    }
}
