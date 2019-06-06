package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.TVSeriesDetailsActivity;
import com.dfsx.ganzcms.app.model.IItemVideo;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

/**
 * 电视剧选集列表
 */
public class TVSeriesVideoItemListFragment extends AbsListFragment {
    private ItemTVVideoAdapter adapter;

    private boolean isCreated = false;

    private List<IItemVideo> videoList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isCreated = true;
        if (videoList != null && adapter != null) {
            adapter.update(videoList, false);
        }
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    public void setListAdapter(ListView listView) {
        listView.setBackgroundResource(R.color.white);
        adapter = new ItemTVVideoAdapter(context);
        listView.setAdapter(adapter);

        View v = LayoutInflater.from(context)
                .inflate(R.layout.header_tv_series_grid_layout, null);
        listView.addHeaderView(v);
        v.findViewById(R.id.close_this)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeFragment();
                    }
                });
    }

    public void update(List<IItemVideo> videoList) {
        this.videoList = videoList;
        if (isCreated && adapter != null) {
            adapter.update(videoList, false);
        }
    }

    private void closeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.frag_bottom_in, R.anim.frag_bottom_out);
        ft.remove(this);
        ft.commitAllowingStateLoss();
    }

    class ItemTVVideoAdapter extends BaseGridListAdapter<IItemVideo> {

        public ItemTVVideoAdapter(Context context) {
            super(context);
        }


        @Override
        protected int getHDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDLeftDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDLeftDivideLineWidth() {
            return PixelUtil.dp2px(context, 15);
        }


        @Override
        protected int getHDRightDivideLineWidth() {
            return PixelUtil.dp2px(context, 15);
        }

        @Override
        protected int getHDRightDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDivideLineWidth() {
            return PixelUtil.dp2px(context, 17);
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_item_tv_grid_layout;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler holder, IItemVideo video) {
            TextView videoTx = holder.getView(R.id.item_video_text);
            View vieoSquareView = holder.getView(R.id.video_square_view);
            vieoSquareView.setTag(video);
            if (video.isPlaying()) {
                videoTx.setText("");
                videoTx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tv_series_play, 0, 0, 0);
            } else {
                videoTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                videoTx.setText(video.getTitle());
            }
            vieoSquareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IItemVideo video = (IItemVideo) v.getTag();
                    if (getActivity() instanceof TVSeriesDetailsActivity) {
                        ((TVSeriesDetailsActivity) getActivity()).onItemSeriesVideoClick(video, list.indexOf(video));
                    }
                }
            });
        }
    }
}
