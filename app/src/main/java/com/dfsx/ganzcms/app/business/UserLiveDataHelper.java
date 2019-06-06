package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.LiveServiceRoomDetailsInfo;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.ganzcms.app.model.SearchLiveData;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/3/14.
 */
public class UserLiveDataHelper {

    private Context context;

    private ChannelManager channelManager;

    public UserLiveDataHelper(Context context) {
        this.context = context;
        channelManager = new ChannelManager(context);
    }

    /**
     * @param userId
     * @param page     page start index 1
     * @param pageSize
     * @param callback
     */
    public void getData(long userId, int page, int pageSize,
                        DataRequest.DataCallback<List<Room>> callback) {
        getUserLiveRoomDataList(userId, page, pageSize, 1, 3, callback);
    }

    /**
     * 获取用户的直播信息列表
     *
     * @param userId   userId为0 表示自己的直播
     * @param page
     * @param pageSize
     * @param type     <直播类型：0 - 不限，1 – 个人，2 – 活动>
     * @param state    <0 - 不限，1 - 未开始，2 – 正在直播，3 – 直播结束>
     * @param callback
     */
    public void getUserLiveRoomDataList(long userId, int page, int pageSize, int type, int state,
                                        DataRequest.DataCallback<List<Room>> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl();
        if (userId == 0 ||
                userId == AppManager.getInstance().getIApp().getLoginUserId()) {
            url += "/public/users/current/shows?page=" + page +
                    "&size=" + pageSize +
                    "&type=" + type +
                    "&state=" + state;
        } else {
            url += "/public/users/" + userId + "/shows?page=" + page +
                    "&size=" + pageSize +
                    "&type=" + type;
        }

        new DataRequest<List<Room>>(context) {

            @Override
            public List<Room> jsonToBean(JSONObject json) {
                SearchLiveData info = new Gson().fromJson(json.toString(), SearchLiveData.class);
                if (info != null && info.getTotal() != 0 && info.getLiveInfoList() != null) {
                    ArrayList<Room> list = new ArrayList<Room>();
                    for (LiveInfo item : info.getLiveInfoList()) {
                        list.add(item);
                    }
                    return list;
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), page > 1)
                .setCallback(callback);
    }

    /**
     * 获取当前用户的活动直播详情
     *
     * @param showId
     * @param callback
     */
    public void getCurrentUserLiveServiceDetailsInfo(long showId, DataRequest.DataCallback<LiveServiceRoomDetailsInfo> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/activity-shows/" + showId;
        new DataRequest<LiveServiceRoomDetailsInfo>(context) {

            @Override
            public LiveServiceRoomDetailsInfo jsonToBean(JSONObject json) {
                LiveServiceRoomDetailsInfo info = new Gson()
                        .fromJson(json.toString(), LiveServiceRoomDetailsInfo.class);
                return info;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }
}
