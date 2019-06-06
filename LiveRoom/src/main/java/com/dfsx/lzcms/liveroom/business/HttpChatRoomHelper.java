package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/4.
 */
public class HttpChatRoomHelper implements IChatRoom {

    private Context context;
    private long roomId;
    private String roomEnterId;

    public HttpChatRoomHelper(Context context, long roomId, String roomEnterId) {
        this.context = context;
        this.roomId = roomId;
        this.roomEnterId = roomEnterId;
    }

    @Override
    public boolean sendChatMessage(ChatMessage message) {
        if (message != null) {
            String text = message.getText();
            String[] images = message.getChatImages();
            boolean isImageAndText = images != null && images.length > 0;
            String url = AppManager.getInstance().getIApp().getHttpBaseUrl();
            url += isImageAndText ?
                    "/public/shows/" + roomId + "/room/image-text/messages"
                    :
                    "/public/shows/" + roomId + "/room/chat/messages";
            JSONObject postJSON = new JSONObject();
            try {
                if (isImageAndText) {

                    postJSON.put("text", text);
                    JSONArray imageArr = new JSONArray();
                    postJSON.put("image_paths", imageArr);
                    for (String image : images) {
                        imageArr.put(image);
                    }
                } else {
                    postJSON.put("message", text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpParameters p = getHttpParams();
            p.setJsonParams(postJSON);
            String res = HttpUtil.execute(url, p, getAPIToken());
            LogUtils.d("Http", "send message == " + res);
            if (TextUtils.isEmpty(res)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public OnLineMember getRoomMember(int page, int pageSize) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/onlines?page=" + page
                + "&size=" + pageSize;
        String res = HttpUtil.executeGet(url, getHttpParams(), null);
        OnLineMember onLineMember = new OnLineMember();
        onLineMember.setCurrentPage(page);
        try {
            JSONObject resJson = new JSONObject(res);
            int total = resJson.optInt("total");
            onLineMember.setTotalNum(total);
            if (total > 0) {
                JSONArray arr = resJson.optJSONArray("data");
                if (arr != null) {
                    ArrayList<ChatMember> members = new ArrayList<>();
                    onLineMember.setChatMemberList(members);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject json = arr.optJSONObject(i);
                        if (json != null) {
                            ChatMember m = new ChatMember();
                            members.add(m);
                            m.setUserId(json.optLong("id"));
                            m.setUserName(json.optString("username"));
                            m.setNickName(json.optString("nickname"));
                            m.setLogo(json.optString("avatar_url"));
                            m.setUserLevelId(json.optLong("user_level_id"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return onLineMember;
    }

    @Override
    public void noTalk(long userId, int minutes, final ICallBack<Boolean> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/forbidden-speak";

        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("expired", minutes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setRequestType(DataReuqestType.POST)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .setJsonParams(json)
                .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                .build(), false)
                .setCallback(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (callBack != null) {
                            callBack.callBack(data);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.callBack(false);
                        }
                    }
                });
    }

    @Override
    public void banUser(long userId, boolean isBanUser, final ICallBack<Boolean> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl()
                + "/public/shows/" + roomId + "/room/banish-user";
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("banished", isBanUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setRequestType(DataReuqestType.POST)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .setJsonParams(json)
                .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                .build(), false)
                .setCallback(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (callBack != null) {
                            callBack.callBack(data);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.callBack(false);
                        }
                    }
                });
    }

    @Override
    public void getNoTalkUserList(final ICallBack<List<NoTalkUser>> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/forbidden-speak/users";
        new DataRequest<List<NoTalkUser>>(context) {

            @Override
            public List<NoTalkUser> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        ArrayList<NoTalkUser> users = new ArrayList<>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.optJSONObject(i);
                            NoTalkUser user = g.fromJson(item.toString(), NoTalkUser.class);
                            users.add(user);
                        }
                        return users;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(new DataRequest.DataCallback<List<NoTalkUser>>() {
                    @Override
                    public void onSuccess(boolean isAppend, List<NoTalkUser> data) {
                        if (callBack != null) {
                            callBack.callBack(data);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.callBack(null);
                        }
                    }
                });
    }

    @Override
    public List<NoTalkUser> getNoTalkMember() {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/forbidden-speak/users";
        String res = HttpUtil.executeGet(url, getHttpParams(), getAPIToken());
        try {
            StringUtil.checkHttpResponseError(res);
            JSONArray arr = new JSONArray(res);
            if (arr != null) {
                ArrayList<NoTalkUser> users = new ArrayList<>();
                Gson g = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.optJSONObject(i);
                    NoTalkUser user = g.fromJson(item.toString(), NoTalkUser.class);
                    users.add(user);
                }
                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getBanUserList(final ICallBack<List<RoomPerson>> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/banish/users";
        new DataRequest<List<RoomPerson>>(context) {

            @Override
            public List<RoomPerson> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        ArrayList<RoomPerson> users = new ArrayList<>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.optJSONObject(i);
                            RoomPerson user = g.fromJson(item.toString(), RoomPerson.class);
                            users.add(user);
                        }
                        return users;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(new DataRequest.DataCallback<List<RoomPerson>>() {
                    @Override
                    public void onSuccess(boolean isAppend, List<RoomPerson> data) {
                        if (callBack != null) {
                            callBack.callBack(data);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.callBack(null);
                        }
                    }
                });
    }

    @Override
    public List<RoomPerson> getBanUserListSync() {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/banish/users";
        String res = HttpUtil.executeGet(url, getHttpParams(), getAPIToken());
        try {
            JSONArray arr = new JSONArray(res);
            if (arr != null) {
                ArrayList<RoomPerson> users = new ArrayList<>();
                Gson g = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.optJSONObject(i);
                    RoomPerson user = g.fromJson(item.toString(), RoomPerson.class);
                    users.add(user);
                }
                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getAPIToken() {
        return AppManager.getInstance().getIApp().getCurrentToken();
    }

    private HttpParameters getHttpParams() {
        return LSLiveUtils.getLiveHttpHeaderParam(roomEnterId);
    }
}
