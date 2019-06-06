package com.dfsx.ganzcms.app.business;

import android.content.Context;

import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Messages;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;


/**
 * Created by wen on 2017/3/23.
 */

public class PushApi {

    /**
     * 更新我的推送设备
     *
     * @param context
     * @param deviceId 设备类型 1 – android,2 – ios
     * @param callback 设备推送id
     */
    public static void submitDeviceId(Context context, String deviceId, DataRequest.DataCallback<Void> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/user-device";
        DataRequest<Void> request = new DataRequest<Void>(context) {
            @Override
            public Void jsonToBean(JSONObject json) {
                return null;
            }
        };
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_type", 1);
            jsonObject.put("device_id", deviceId);
        } catch (Exception e) {
        }

        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).setToken(
                App.getInstance().getCurrentToken()).setJsonParams(jsonObject).
                setRequestType(DataReuqestType.PUT).build(), false);
        request.setCallback(callback);
    }

    public static void removeMessage(Context context, long id, DataRequest.DataCallback<Void> callback) {
        long[] ids = {id};
        removeMessage(context, ids, callback);
    }

    public static void removeMessage(Context context, long[] ids, DataRequest.DataCallback<Void> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/messages/";

        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            if (i == 0) {
                url += id;
            } else {
                url += "," + id;
            }
        }

        DataRequest<Void> request = new DataRequest<Void>(context) {
            @Override
            public Void jsonToBean(JSONObject json) {
                return null;
            }
        };
        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).setToken(
                App.getInstance().getCurrentToken()).
                setRequestType(DataReuqestType.DEL).build(), false);
        request.setCallback(callback);
    }

    public static void removeAllMessage(Context context, int type, DataRequest.DataCallback<Void> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/messages?category=" + type;
        DataRequest<Void> request = new DataRequest<Void>(context) {
            @Override
            public Void jsonToBean(JSONObject json) {
                return null;
            }
        };
        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).setToken(
                App.getInstance().getCurrentToken()).
                setRequestType(DataReuqestType.DEL).build(), false);
        request.setCallback(callback);
    }


    /**
     * @param context
     * @param type     消息类型：0-获取全部,1-系统类消息,2-关注类消息
     * @param state    消息状态：0-获取全部,1-未读,2-已读
     * @param page
     * @param size
     * @param isAppend
     * @param callback
     */
    public static void getMessages(Context context, int type,
                                   int state, int page, int size, boolean isAppend, DataRequest.DataCallback<Messages> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/messages?";
        url += "category=" + type + "&";
        url += "state=" + state + "&";
        url += "page=" + page + "&";
        url += "size=" + size;
        DataRequest<Messages> request = new DataRequest<Messages>(context) {
            @Override
            public Messages jsonToBean(JSONObject json) {
                Gson gson = new Gson();
                String t = json.toString();
                Messages messages = gson.fromJson(t, Messages.class);
                dealExtention(messages);
                return messages;
            }
        };

        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).setToken(App.getInstance().getCurrentToken())
                .setRequestType(DataReuqestType.GET).build(), isAppend);
        request.setCallback(callback);
    }

    public static Messages getMessagesSync(Context context, int type,
                                           int state, int page, int size) {
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/messages?";
        url += "category=" + type + "&";
        url += "state=" + state + "&";
        url += "page=" + page + "&";
        url += "size=" + size;

        String res = HttpUtil.executeGet(url, new HttpParameters(),
                App.getInstance().getCurrentToken());
        try {
            StringUtil.checkHttpResponseError(res);
            Messages messages = new Gson().fromJson(res, Messages.class);
            dealExtention(messages);
            return messages;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void dealExtention(Messages messages) {
        List<Message> messageList = messages.getData();
        Gson gson = new Gson();
        for (Message message : messageList) {
            String e = message.getExtension();
            Extension extension = gson.fromJson(e, Extension.class);
            message.setExt(extension);
        }
    }

}
