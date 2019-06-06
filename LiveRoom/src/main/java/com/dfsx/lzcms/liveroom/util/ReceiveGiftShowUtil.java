package com.dfsx.lzcms.liveroom.util;

import android.content.Context;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.GiftManager;
import com.dfsx.lzcms.liveroom.model.GiftMessage;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.view.BarrageListViewSimple;
import com.dfsx.lzcms.liveroom.view.LiveSpecialEffectView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liuwb on 2016/11/7.
 */
public class ReceiveGiftShowUtil {

    public static void showReceiveGiftInList(Context context, boolean isScrollToBottom,
                                             BarrageListViewSimple barrageListView,
                                             List<GiftMessage> receiveGiftInfo) {
        showReceiveGiftInList(context, isScrollToBottom, 0, barrageListView, receiveGiftInfo);
    }

    public static void showReceiveGiftInList(Context context,
                                             BarrageListViewSimple barrageListView,
                                             List<GiftMessage> receiveGiftInfo) {
        showReceiveGiftInList(context, 0, barrageListView, receiveGiftInfo);
    }

    /**
     * @param context
     * @param barrageItemNameColor 设置聊天信息中名字的颜色值
     * @param barrageListView      显示的控件View
     * @param receiveGiftInfo      收到礼物的信息
     */
    public static void showReceiveGiftInList(Context context, int barrageItemNameColor,
                                             BarrageListViewSimple barrageListView,
                                             List<GiftMessage> receiveGiftInfo) {
        boolean isScrollBottom = receiveGiftInfo != null && receiveGiftInfo.size() == 1 &&
                receiveGiftInfo.get(0) != null &&
                StringUtil.isCurrentUserName(receiveGiftInfo.get(0).getUserName());
        showReceiveGiftInList(context, isScrollBottom,
                barrageItemNameColor, barrageListView, receiveGiftInfo);
    }

    /**
     * @param context
     * @param isScrollBottom       是否滚到barrageListView的底部
     * @param barrageItemNameColor 设置聊天信息中名字的颜色值
     * @param barrageListView      显示的控件View
     * @param receiveGiftInfoList  收到礼物的信息
     */
    public static void showReceiveGiftInList(final Context context, final boolean isScrollBottom, final int barrageItemNameColor,
                                             final BarrageListViewSimple barrageListView,
                                             List<GiftMessage> receiveGiftInfoList) {
        if (receiveGiftInfoList != null && receiveGiftInfoList.size() > 0) {
            Observable.from(receiveGiftInfoList)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.newThread())
                    .flatMap(new Func1<GiftMessage, Observable<BarrageListViewSimple.BarrageItem>>() {
                        @Override
                        public Observable<BarrageListViewSimple.BarrageItem> call(GiftMessage giftMessage) {
                            BarrageListViewSimple.BarrageItem item = new BarrageListViewSimple.BarrageItem();
                            item.name = giftMessage.getUserNickName();
                            GiftModel gift = GiftManager.getInstance().getGiftByIdSync(context,
                                     giftMessage.getGiftId());
                            if (gift != null) {
                                item.content = new GiftSpannableStringSyncHelper(context).getGiftContentString(giftMessage.getCount(),
                                        gift.getImgPath(), R.drawable.icon_test_gift);
                            } else {
                                item.content = "没有礼物信息";
                            }
                            item.userLevelId = giftMessage.getUserLevelId();
                            item.nameColor = barrageItemNameColor;
                            return Observable.just(item);
                        }
                    })
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<BarrageListViewSimple.BarrageItem>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(List<BarrageListViewSimple.BarrageItem> barrageItems) {
                            barrageListView.addItemData(barrageItems,
                                    isScrollBottom);
                        }
                    });
        } else {
            barrageListView.addItemData(new
                    BarrageListViewSimple.BarrageItem("系统", barrageItemNameColor,
                    MessageUtils.getGiftString(context)));
        }
    }

    public static BarrageListViewSimple.BarrageItem getBarrageItemByGiftMessage(Context context1, GiftMessage receiveGiftInfo) {
        BarrageListViewSimple.BarrageItem item = new BarrageListViewSimple.BarrageItem();
        item.name = receiveGiftInfo.getUserNickName();
        GiftModel gift = GiftManager.getInstance().getGiftByIdSync(context1,
                 receiveGiftInfo.getGiftId());
        if (gift != null) {
            item.content = new GiftSpannableStringSyncHelper(context1).getGiftContentString(receiveGiftInfo.getCount(),
                    gift.getImgPath(), R.drawable.icon_test_gift);
        } else {
            item.content = "没有礼物信息";
        }
        return item;
    }

    public static void showReceiveGift(final Context context, final LiveSpecialEffectView liveSpecialEffectView,
                                       GiftMessage receiveGiftInfo, final int num) {
        Observable.just(receiveGiftInfo)
                .subscribeOn(Schedulers.io())
                .map(new Func1<GiftMessage, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> call(GiftMessage receiveGiftInfo) {
                        String key1 = "receive_gift_info";
                        String key2 = "gift_model";
                        String key3 = "user_logo";
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        GiftModel g = GiftManager.getInstance().getGiftByIdSync(context,
                                 receiveGiftInfo.getGiftId());
                        hashMap.put(key1, receiveGiftInfo);
                        hashMap.put(key2, g);
                        String logo = receiveGiftInfo == null ? "" : receiveGiftInfo.getUserAvatarUrl();
                        hashMap.put(key3, logo);
                        return hashMap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HashMap<String, Object>>() {
                    @Override
                    public void call(HashMap<String, Object> stringObjectHashMap) {
                        String key1 = "receive_gift_info";
                        String key2 = "gift_model";
                        String key3 = "user_logo";
                        GiftMessage receiveInfo = (GiftMessage) stringObjectHashMap.get(key1);
                        GiftModel gift = (GiftModel) stringObjectHashMap.get(key2);
                        liveSpecialEffectView.receiveGiftView(receiveInfo.getUserNickName(),
                                (String) stringObjectHashMap.get(key3),
                                receiveInfo.getUserNickName(),
                                gift, num);
                    }
                });
    }

    /**
     * 重复多次送礼
     *
     * @param receiveGiftInfo
     * @return
     */
    public static boolean isManySendGift(GiftMessage receiveGiftInfo) {
        return false;
    }
}
