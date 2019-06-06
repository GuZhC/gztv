package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.LiveRoomInfo;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;

/**
 * Created by liuwb on 2016/10/27.
 */
public class MyVideoFragment extends AbsListFragment {
    private MyMorePopupwindow popupwindow;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupwindow = new MyMorePopupwindow(context);
    }

    @Override
    public void setListAdapter(ListView listView) {

    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
//       EmptyView  emptyView= (EmptyView) LayoutInflater.from(context).inflate(R.layout.no_video_layout, container);
        EmptyView emptyView = new EmptyView(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);
        container.addView(emptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_video_layout, null);
        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        Button btnStart = (Button) emptyLayout.findViewById(R.id.btn_start_live);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    protected class MyLiveAdapter extends BaseListViewAdapter<LiveRoomInfo> {

        public MyLiveAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_my_live_channel;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            TextView title = holder.getView(R.id.title);
            TextView more = holder.getView(R.id.text_more);
            ImageView btnPlay = holder.getView(R.id.btn_play);
            TextView userType = holder.getView(R.id.user_type_text);
            TextView numText = holder.getView(R.id.num_text);
            TextView timeText = holder.getView(R.id.time_text);
            ImageView roomImage = holder.getView(R.id.channel_img);

            LiveRoomInfo roomInfo = list.get(position);
            title.setText(roomInfo.getTitle());
            userType.setText(roomInfo.getCategoryName());
            numText.setText(roomInfo.getMaxVisitorCount() + "");
            timeText.setText(StringUtil.getTimeText(roomInfo.getCreationTime()));

            if (TextUtils.isEmpty(roomInfo.getCoverUrl())) {
                GlideImgManager.getInstance().
                        showImg(context, roomImage, roomInfo.getCoverUrl());
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupwindow != null) {
                        popupwindow.setTag(list.get(position));
                        popupwindow.show(v);
                    }
                }
            });
        }
    }
}
