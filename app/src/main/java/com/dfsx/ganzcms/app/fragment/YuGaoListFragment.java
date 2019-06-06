package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.UserLiveDataHelper;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.model.RecordRoomIntentData;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import me.lake.librestreaming.sample.LiveRecordStreamingActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwb on 2017/7/12.
 */
public class YuGaoListFragment extends AbsListFragment {
    private Button btnStartServiceLive;
    private YuGaoListAdapter adapter;

    private EmptyView emptyView;
    private UserLiveDataHelper dataHelper;
    private int currentPage;
    private LiveInfo selectedLiveInfo;

    private Handler handler = new Handler();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataHelper = new UserLiveDataHelper(context);
        getData(1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                if (adapter.getData() != null && position >= 0 && position < adapter.getData().size()) {
                    List<Room> list = adapter.getData();
                    LiveInfo info = (LiveInfo) list.get(position);
                    if (!info.isSelected()) {
                        info.setSelected(true);
                        selectedLiveInfo = info;
                        btnStartServiceLive.setEnabled(true);
                        for (int i = 0; i < list.size(); i++) {
                            if (i != position) {
                                LiveInfo live = (LiveInfo) list.get(i);
                                live.setSelected(false);
                            }
                        }
                    } else {
                        info.setSelected(false);
                        selectedLiveInfo = null;
                        btnStartServiceLive.setEnabled(false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new YuGaoListAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        getData(currentPage + 1);
    }

    private void getData(int page) {
        currentPage = page;
        dataHelper.getUserLiveRoomDataList(0, page, 20, 1, 1, new DataRequest.DataCallback<List<Room>>() {
            @Override
            public void onSuccess(boolean isAppend, List<Room> data) {
                if (data == null || data.isEmpty()) {
                    adapter.update(data, isAppend);
                } else {
                    ArrayList<Room> list = new ArrayList<Room>();
                    for (Room r : data) {
                        if (r instanceof LiveInfo) {
                            LiveInfo info = (LiveInfo) r;
                            if (isBiggerTime(info.getPlanStartTime())) {
                                list.add(r);
                            }
                        }
                    }
                    adapter.update(list, isAppend);
                }
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }
        });
    }

    private boolean isBiggerTime(long timeStamp) {
        long curTime = new Date().getTime() / 1000;
        return timeStamp > curTime;
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("暂时未有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    @Override
    protected void setBottomView(FrameLayout bottomListViewContainer) {
        btnStartServiceLive = new Button(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, 45));
        btnStartServiceLive.setLayoutParams(params);
        btnStartServiceLive.setBackgroundResource(R.drawable.btn_start_yugao_live);
        btnStartServiceLive.setText("开始直播");
        btnStartServiceLive.setTextSize(15);
        btnStartServiceLive.setEnabled(false);
        btnStartServiceLive.setTextColor(context.getResources()
                .getColorStateList(R.color.btn_start_yugao_live_text_color));
        bottomListViewContainer.addView(btnStartServiceLive);
        btnStartServiceLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartLiveBtnClick();
            }
        });
    }

    void onStartLiveBtnClick() {
        if (selectedLiveInfo != null) {
            if (isBiggerTime(selectedLiveInfo.getPlanStartTime())) {
                Intent intent = new Intent(context, LiveRecordStreamingActivity.class);
                RecordRoomIntentData intentData = new RecordRoomIntentData();
                intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
                intentData.setScreenPortrait(selectedLiveInfo.getScreenMode() == 2);
                intentData.setAutoJoinRoomAtOnce(true);
                intentData.setRoomId(selectedLiveInfo.getRoomId());
                intentData.setRoomOwnerId(selectedLiveInfo.getOwnerId());
                intentData.setLiveRTMPURL("");
                intentData.setCoverImagePath(selectedLiveInfo.getRoomImagePath());
                intentData.setSubject(selectedLiveInfo.getTitle());

                startActivity(intent);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        act.finish();
                    }
                }, 10);

            } else {
                Toast.makeText(context, "预告已过期", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class YuGaoListAdapter extends BaseListViewAdapter<Room> {

        public YuGaoListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_yugao_list;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView selectedImage = holder.getView(R.id.item_selected_image);
            TextView titleText = holder.getView(R.id.item_title_text);
            TextView timeText = holder.getView(R.id.item_time_text);

            Room room = list.get(position);
            if (room != null && room instanceof LiveInfo) {
                LiveInfo info = (LiveInfo) room;
                selectedImage.setImageResource(info.isSelected() ?
                        R.drawable.icon_yugao_selected :
                        R.drawable.icon_yugao_unselected);
                titleText.setText(info.getTitle());
                timeText.setText(StringUtil.getTimeFurtureText(info.getPlanStartTime()));
            }
        }
    }
}
