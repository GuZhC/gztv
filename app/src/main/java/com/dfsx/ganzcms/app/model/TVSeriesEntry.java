package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.business.ObjectUtil;

public class TVSeriesEntry extends ContentCmsEntry implements ITVSeries {

    @Override
    public String getTVName() {
        return getTitle();
    }

    @Override
    public String getTVImage() {
        try {
            return getThumbnail_urls().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getTVSize() {
        int size = 0;
        try {
            Object ob = getCustomFields().get("jishu");
            size = ObjectUtil.objectToInt(ob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public String getTVDesc() {
        return getSummary();
    }
}
