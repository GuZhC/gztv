package com.dfsx.ganzcms.app.model;

public class SpecialTopicItem extends BaseWrapContent<ColumnContentListItem> implements ISpecialTopic {
    @Override
    public long getSpecialId() {
        return getContent().getId();
    }

    @Override
    public String getSpecialThumbImage() {
        try {
            return getContent().getThumbnailUrls().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSpecialTitle() {
        return getContent().getTitle();
    }

    @Override
    public long getSpecialTime() {
        return getContent().getPublishTime();
    }

    @Override
    public String getSpecialSeeNum() {
        return getContent().getViewCount() + "浏览";
    }
}
