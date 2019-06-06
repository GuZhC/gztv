package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.util.AttributeSet;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.GiftGetter;
import com.dfsx.lzcms.liveroom.model.GiftModel;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/7/5.
 */
public class LiveGiftSelectView extends SelectGiftView {

    public LiveGiftSelectView(Context context) {
        this(context, null);
    }

    public LiveGiftSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveGiftSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGetter(new GiftGetter(context));
    }

    public ArrayList<GiftModel> getGifts() {
        ArrayList<GiftModel> list = new ArrayList<GiftModel>();
        for (int i = 0; i < 20; i++) {
            list.add(createGift(i));
        }
        return list;
    }

    private GiftModel createGift(int id) {
        GiftModel gift = new GiftModel(id, "test", 10);
        if (id == 0) {
            gift.setSelected(true);
            super.gift = gift;
        }
        gift.setImgResId1(getGift1(id));
        if (id == 0) {
            gift.setImgResId2(R.drawable.red_money);
        } else {
            gift.setImgResId2(0);
        }
        return gift;
    }

    private int getGift1(int id) {
        if (id < giftRes.length) {
            return giftRes[id];
        }
        return R.drawable.icon_test_gift;
    }

    private int giftRes[] = {
            R.drawable.icon_hongbao,
            R.drawable.icon_bangbang,
            R.drawable.icon_ganbei,
            R.drawable.icon_huangguan,
            R.drawable.icon_juqiandai,
            R.drawable.icon_miantiao,
            R.drawable.icon_nainao,
            R.drawable.icon_paoche,
            R.drawable.icon_xiannvbang,
            R.drawable.icon_youting};
}
