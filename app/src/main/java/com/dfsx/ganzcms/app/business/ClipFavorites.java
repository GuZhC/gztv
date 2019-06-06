package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.App;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by halim on 2015/7/31.
 */
public class ClipFavorites {


    static public void FavriteClip(Context host, final Handler hostHandler, final int index, final String clipType) {
        final int NO_LOGON = 17;
        final int TOAST_MSG = 22;

        final Account mUser = App.getInstance().getUser();
        if (mUser == null) {
            Message msg = hostHandler.obtainMessage(NO_LOGON);
            msg.obj = "请先登录！！！！";
            hostHandler.sendMessage(msg);
            return;
        }

        final NewsDatailHelper helper = new NewsDatailHelper(host);
        //判断是否备受擦鞥
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("flag_name", "favorites");
            obj.put("entity_id", index);
            obj.put("uid", mUser.getUser().getId());
            helper.postIsAddedFavorite(obj, new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    if (data) {
                        Message msg = hostHandler.obtainMessage(TOAST_MSG);
                        msg.obj = "该" + clipType + "已收藏！！！！";
                        hostHandler.sendMessage(msg);
                    } else {
                        //收藏
                        JSONObject objs = UtilHelp.getJsonObject("", "");
                        try {
                            objs.put("flag_name", "favorites");
                            objs.put("entity_id", index);
                            objs.put("action", "flag");
                            objs.put("uid", mUser.getUser().getId());

                            helper.postAddFavorite(objs, new DataRequest.DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(boolean isAppend, Boolean data) {
                                    if (data) {
                                        Message msg = hostHandler.obtainMessage(TOAST_MSG);
                                        msg.obj = clipType + "收藏成功！！！！";
                                        hostHandler.sendMessage(msg);
                                    }
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    Log.e("ClipFavorites", "postAddFavorite fail");
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    Log.e("ClipFavorites", "postIsAddedFavorite fail");
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
