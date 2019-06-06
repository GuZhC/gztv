package com.dfsx.lzcms.liveroom.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveServiceRoomActivity;
import com.dfsx.lzcms.liveroom.adapter.LiveServiceInfoListAdapter;
import com.dfsx.lzcms.liveroom.business.IGetImageTextMessage;
import com.dfsx.lzcms.liveroom.business.ImageTextMessageDataHelper;
import com.dfsx.lzcms.liveroom.model.ImageTextMessage;
import com.dfsx.lzcms.liveroom.model.LiveServiceDetailsInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/20.
 * 图文直播的直播信息页面
 */
public class LiveServiceInfoFragment extends AbsListFragment {

    public static final int PAGE_MAX = 20;

    private LiveServiceInfoListAdapter adapter;

    private IGetImageTextMessage getImageTextMessage;

    private long lastestLastMessageId;

    private Handler handler = new Handler();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getImageTextMessage = new ImageTextMessageDataHelper(act, getRoomEnterId());
        getData(0);
    }

    private String getRoomEnterId() {
        String id = "";
        if (act instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) act).getRoomEnterId();
        }
        return id;
    }

    private long getRoomId() {
        long rId = 0;
        if (act instanceof AbsChatRoomActivity) {
            rId = ((AbsChatRoomActivity) act).getRoomId();
        }
        return rId;
    }

    private void getData(long beforeId) {
        getImageTextMessage.getImageTextMessageList(getRoomId(),
                beforeId, PAGE_MAX, new DataRequest.DataCallback<List<ImageTextMessage>>() {
                    @Override
                    public void onSuccess(boolean isAppend, List<ImageTextMessage> data) {
                        if (data != null && !data.isEmpty()) {
                            for (ImageTextMessage message : data) {
                                initImageTextMessageUserInfo(message);
                            }
                        }
                        adapter.update(data, isAppend);
                        pullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new LiveServiceInfoListAdapter(context);
        listView.setAdapter(adapter);
    }

    /**
     * 在获取详情成功的时候调用
     */
    public void onGetEventDetailsSuccess() {
        if (adapter != null && adapter.getData() != null &&
                !adapter.getData().isEmpty()) {
            ImageTextMessage message = adapter.getData().get(0);
            if (message.getUserId() != 0) {
                //说明已经设置过了，就不需要再去重新设置图文的发送者
                return;
            }
            for (ImageTextMessage m : adapter.getData()) {
                initImageTextMessageUserInfo(m);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        long lastMessageId = 0;
        if (adapter.getData() != null && !adapter.getData().isEmpty()) {
            ImageTextMessage lastedMessage = adapter.getData().get(adapter.getData().size() - 1);
            lastMessageId = lastedMessage != null ? lastedMessage.getId() : 0;
        }
        if (lastMessageId != 0 && lastMessageId != lastestLastMessageId) {
            lastestLastMessageId = lastMessageId;
            getData(lastMessageId);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefreshListView.onRefreshComplete();
                }
            }, 500);
            Toast.makeText(context, "没有数据了", Toast.LENGTH_SHORT).show();
        }
    }

    private void initImageTextMessageUserInfo(ImageTextMessage message) {
        if (act instanceof LiveServiceRoomActivity) {
            LiveServiceDetailsInfo detailsInfo = ((LiveServiceRoomActivity) act).getServiceDetailsInfo();
            if (message != null && detailsInfo != null) {
                message.setUserId(detailsInfo.getOwnerId());
                message.setUserAvatarUrl(detailsInfo.getOwnerAvatarUrl());
                message.setUserName(detailsInfo.getOwnerUserName());
                message.setUserNickName(detailsInfo.getOwnerNickName());
            }
        }
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    /**
     * 消息
     *
     * @param imageTextMessage
     */
    public void processTextImageMessage(ImageTextMessage imageTextMessage) {
        initImageTextMessageUserInfo(imageTextMessage);
        adapter.addTopData(imageTextMessage);
    }
}
