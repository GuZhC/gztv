package com.dfsx.ganzcms.app.model;

public class NewsGridItem extends GridNews<ContentCmsEntry> {

    public NewsGridItem(ContentCmsEntry data) {
        super(data);
    }

    @Override
    public long getId() {
        return data != null ? data.getId() : 0;
    }

    @Override
    public String getImagePath() {
        return data != null ?
                data.getThumbnail_urls() != null && data.getThumbnail_urls().size() > 0 ? data.getThumbnail_urls().get(0) : null
                : null;
    }

    @Override
    public String getShowTitle() {
        return data != null ? data.getTitle() : null;
    }

    @Override
    public long getShowTime() {
        return data != null ? data.getPublish_time() : 0;
    }
}
