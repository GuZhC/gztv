package com.dfsx.lzcms.liveroom.model;

import android.content.Context;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.GiftManager;
import com.dfsx.lzcms.liveroom.util.GiftSpannableStringSyncHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuwb on 2017/7/4.
 */
public class GiftMessage extends LiveMessage {

    /**
     * user_id : 10000
     * user_name : 用户名
     * user_nickname : 用户呢称
     * user_avatar_url : 用户头像地址
     * gift_id : 1000000000000
     * count : 5
     */

    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_nickname")
    private String userNickName;
    @SerializedName("user_avatar_url")
    private String userAvatarUrl;
    @SerializedName("user_level_id")
    private long userLevelId;
    @SerializedName("gift_id")
    private long giftId;
    private int count;
    @SerializedName("total_coins")
    private double giftCoins;
    /**
     * 用于在礼物的文本显示
     */
    private CharSequence giftContentText;

    public long getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getGiftCoins() {
        return giftCoins;
    }

    public void setGiftCoins(double giftCoins) {
        this.giftCoins = giftCoins;
    }

    @Override
    public long getChatUserId() {
        return getUserId();
    }

    @Override
    public String getChatUserNickName() {
        return getUserNickName();
    }

    @Override
    public String getChatUserLogo() {
        return getUserAvatarUrl();
    }

    @Override
    public CharSequence getChatContentText() {
        return giftContentText;
    }

    public CharSequence getGiftContentText() {
        return giftContentText;
    }

    public void setGiftContentText(CharSequence giftContentText) {
        this.giftContentText = giftContentText;
    }

    /**
     * 这个方法请运行在子线程中
     * <p>
     * 设置礼物的显示的文本内容
     *
     * @param context
     * @return
     */
    public boolean initGiftContentTextSync(Context context) {
        GiftModel gift = GiftManager.getInstance().getGiftByIdSync(context,
                 getGiftId());

        if (gift != null) {
            CharSequence content = new GiftSpannableStringSyncHelper(context)
                    .getGiftContentString(getCount(), gift.getImgPath(), R.drawable.icon_test_gift);
            setGiftContentText(content);
            return true;
        }
        return false;
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}
