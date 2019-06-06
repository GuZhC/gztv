package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/9.
 * 请运行在子线程中
 */
public class HttpGetUserInfo implements IGetUserInfo {

    private Context context;

    public HttpGetUserInfo(Context context) {
        this.context = context;
    }

    @Override
    public UserDetailsInfo getUserInfo(String userName) {
        List<UserDetailsInfo> userInfoList = getUserInfoList(userName);
        if (userInfoList != null && userInfoList.size() == 1) {
            return userInfoList.get(0);
        }
        return null;
    }

    @Override
    public List<UserDetailsInfo> getUserInfoList(String... userNames) {
        String url = AppManager.getInstance().getIApp().getCommonHttpUrl();
        url += "/public/users/by-usernames/";
        if (userNames == null) {
            return null;
        }
        for (String userName : userNames) {
            url += userName + ",";
        }
        if (url.endsWith(",")) {
            url = url.substring(0, url.length() - 1);
        }
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);

        try {
            JSONObject jsonObject = JsonCreater.jsonParseString(res);
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if (jsonArray != null) {
                Gson gson = new Gson();
                List<UserDetailsInfo> userInfoList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.optJSONObject(i);
                    if (json != null) {
                        UserDetailsInfo userInfo = gson.fromJson(json.toString(), UserDetailsInfo.class);
                        userInfoList.add(userInfo);
                    }
                }
                return userInfoList;
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
