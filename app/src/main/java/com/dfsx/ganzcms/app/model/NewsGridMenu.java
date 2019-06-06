package com.dfsx.ganzcms.app.model;

public class NewsGridMenu extends GridNews<ColumnCmsEntry> {
    public NewsGridMenu(ColumnCmsEntry data) {
        super(data);
    }

    public String getDesc() {
        return data != null ? data.getDescription() : null;
    }

    @Override
    public long getId() {
        return data != null ? data.getId() : 0;
    }

    @Override
    public String getImagePath() {
        return data != null ? data.getIcon_url() : null;
    }

    @Override
    public String getShowTitle() {
        return data != null ? data.getName() : "";
    }

    @Override
    public long getShowTime() {
        return 0;
    }
}
