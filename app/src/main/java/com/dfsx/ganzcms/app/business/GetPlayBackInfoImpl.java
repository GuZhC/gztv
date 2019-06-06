package com.dfsx.ganzcms.app.business;

import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.LiveType;
import com.dfsx.ganzcms.app.model.PlayBackInfo;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.IGetPlayBackInfo;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;

/**
 * Created by liuwb on 2017/3/27.
 */
public class GetPlayBackInfoImpl implements IGetPlayBackInfo {


    public GetPlayBackInfoImpl() {
    }

    @Override
    public String getPlayBackInfo(long playBackId) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/playbacks/" + playBackId;
        return HttpUtil.executeGet(url, new HttpParameters(), null);
    }

    @Override
    public void initBackPlayIntentData(long playBackId, final BackPlayIntentData backPlayIntentData, final ICallBack<BackPlayIntentData> callBack) {
        ChannelManager channelManager = new ChannelManager(App.getInstance().getApplicationContext());
        channelManager.getPlayBackInfo(playBackId, new DataRequest.DataCallback<PlayBackInfo>() {
            @Override
            public void onSuccess(boolean isAppend, PlayBackInfo data) {
                BackPlayIntentData intentData = backPlayIntentData;
                if (data != null) {
                    if (intentData == null) {
                        intentData = new BackPlayIntentData();
                    }
                    intentData.setAutoJoinRoomAtOnce(true);
                    intentData.setRoomId(data.getChannelId());
                    intentData.setRoomTitle(data.getRoomTitle());
                    intentData.setFullScreenVideoImagePath(data.getRoomImagePath());
                    intentData.setBackPlayId(data.getId());
                    intentData.setRoomOwnerId(data.getOwnerId());
                    intentData.setRoomOwnerAccountName(data.getOwnerUsername());
                    intentData.setRoomOwnerNickName(data.getOwnerNickname());
                    intentData.setRoomOwnerLogo(data.getOwnerAvatarUrl());
                    intentData.setRoomTotalCoins(data.getTotalCoins());
                    intentData.setMemberSize((int) data.getPlayTimes());
                    intentData.setLiveType(data.getRoomLivetype() == LiveType.EventLive ? LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_GUESS :
                            LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_SHOW);
                    intentData.setShowId(data.getShowId());
                }
                callBack.callBack(intentData);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                callBack.callBack(null);
            }
        });
    }
}
