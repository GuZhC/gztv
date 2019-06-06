package com.dfsx.lzcms.liveroom.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.view.LXDialog;

import java.util.HashMap;

/**
 * 直播间的公共工具类
 * Created by liuwb on 2017/7/4.
 */
public class LSLiveUtils {

    /**
     * 获取直播间通用带有Header的参数
     *
     * @param p
     * @return
     */
    public static HttpParameters getLiveHttpHeaderParam(String roomEnterId, HttpParameters p) {
        if (p == null) {
            p = new HttpParameters();
        }
        p.setHeader(getLiveHttpHeader(roomEnterId));
        return p;
    }

    public static HashMap<String, String> getLiveHttpHeader(String roomEnterId) {
        HashMap<String, String> liveRoomHeader = new HashMap<>();
        liveRoomHeader.put("X-DFSX-RoomId", roomEnterId);
        return liveRoomHeader;
    }


    public static HttpParameters getLiveHttpHeaderParam(String roomEnterId) {
        return getLiveHttpHeaderParam(roomEnterId, null);
    }


    /**
     * 余额不足警告
     *
     * @param activity
     */
    public static void showNoEnoughMoneyDialog(final Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            LXDialog.Builder builder = new LXDialog.Builder(activity);
            LXDialog dialog = builder.setMessage("账户余额不足,请充值")
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            IntentUtil.goAddMoneyActivity(activity);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    public static boolean isCurrentUser(long userId) {
        return AppManager.getInstance().getIApp().getLoginUserId() == userId;
    }

    public static void showUserLogoImage(Context context, ImageView imageView, String logoUrl) {
        showUserLogoImage(context, imageView, logoUrl, R.drawable.icon_defalut_no_login_logo);
    }

    /**
     * 显示用户头像的默认图片
     *
     * @param context
     * @param imageView
     * @param logoUrl
     * @param defaultRes
     */
    public static void showUserLogoImage(Context context, ImageView imageView, String logoUrl, int defaultRes) {
        GlideImgManager.getInstance().showImg(context, imageView,
                logoUrl, defaultRes, defaultRes);
    }
}
