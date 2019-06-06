package com.dfsx.ganzcms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.LiveEntity;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/9/13.
 */
public class LiveVideoAdapter extends BaseRecyclerViewAdapter {

    private ArrayList<LiveEntity.LiveChannel> list;

    public LiveVideoAdapter(ArrayList<LiveEntity.LiveChannel> list) {
        this.list = list;
    }

    public void update(ArrayList<LiveEntity.LiveChannel> data, boolean isAdd) {
        if (isAdd && this.list != null) {
            this.list.addAll(data);
        } else {
            this.list = data;
        }
        notifyDataSetChanged();
    }

    public ArrayList<LiveEntity.LiveChannel> getData() {
        return list;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_live_video_pager, null);
        return new BaseRecyclerViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        TextView videoTitle = holder.getView(R.id.item_live_title);
        ImageView videoImage = holder.getView(R.id.item_live_img);

        LiveEntity.LiveChannel channel = list.get(position);
        videoTitle.setText(channel.channelName);
        Util.LoadThumebImage(videoImage, channel.thumb, null);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
