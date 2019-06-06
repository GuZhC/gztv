package com.dfsx.core.common.view.banner;

import com.dfsx.core.common.view.banner.BannerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成标准的Banner显示数据
 * Created by liuwb on 2016/5/11.
 */
public abstract class BannerDataHelper<E> {

    public ArrayList<BannerItem> getBannerItems(List<E> list) {
        ArrayList<BannerItem> bannerItems = new ArrayList<BannerItem>();
        if (list != null) {
            for (E data : list) {
                BannerItem item = changeToBannerItem(data);
                bannerItems.add(item);
            }
        }

        return bannerItems;
    }


    public abstract BannerItem changeToBannerItem(E data);
}
