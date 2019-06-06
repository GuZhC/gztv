package com.dfsx.ganzcms.app.business;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.App;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuwb on 2016/9/6.
 * 新闻收藏
 */
public class NewsStore {

    private Context context;
    private NewsDatailHelper newsDatailHelper;

    public NewsStore(Context context) {
        this.context = context;
        newsDatailHelper = new NewsDatailHelper(context);
    }


    public void store(final long newsId, final StoreListener listener) {
        Account mUser = App.getInstance().getUser();
        if (mUser == null) {
            if (context != null) {
                boolean isFinishing = context instanceof Activity ?
                        ((Activity) context).isFinishing() :
                        false;
                if (isFinishing) {
                    return;
                }
                AlertDialog adig = new AlertDialog.Builder(context).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        Intent intent = new Intent();
//                        intent.setClass(context, LoginActivity.class);
//                        context.startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                }).create();
                adig.show();
            }
            return;
        }
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("flag_name", "favorites");
            obj.put("entity_id", newsId);
            obj.put("uid", mUser.getUser().getId());
            newsDatailHelper.postIsAddedFavorite(obj, new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    if (data) {
                        if (listener != null) {
                            listener.hasStored();
                        }
                    } else {
                        //收藏
                        JSONObject objs = UtilHelp.getJsonObject("", "");
                        try {
                            objs.put("flag_name", "favorites");
                            objs.put("entity_id", newsId);
                            objs.put("action", "flag");
                            objs.put("uid", App.getInstance().getUser().getUser().getId());

                            newsDatailHelper.postAddFavorite(objs, new DataRequest.DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(boolean isAppend, Boolean data) {
                                    if (data) {
                                        if (listener != null) {
                                            listener.storeSuccess();
                                        }
                                    } else {
                                        if (listener != null) {
                                            listener.storeFail(new ApiException("收藏失败"));
                                        }
                                    }
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    if (listener != null) {
                                        listener.storeFail(e);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.storeFail(new ApiException("收藏请求参数错误"));
                            }
                        }
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    if (listener != null) {
                        listener.storeFail(e);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface StoreListener {
        /**
         * 已经收藏
         */
        void hasStored();

        void storeFail(ApiException e);

        /**
         * 收藏成功
         */
        void storeSuccess();
    }
}
