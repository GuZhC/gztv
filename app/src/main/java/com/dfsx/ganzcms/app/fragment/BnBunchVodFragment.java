package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.*;

/**
 * Created by heyang on 2018/3/1
 */
public class BnBunchVodFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ScrollView> {
    protected Context context;
    protected PullToRefreshScrollView pullToRefreshScrollView;
    protected ListView listView;
    //    private ListImagesAdapter adapter;
    private long mType = -1;
    private LinearLayout mMainGroupView;
    private NewsDatailHelper _newsHelper;
    private int colorIndex = 0;

    public int[] imgs = {R.drawable.col_vod_fr_bankground, R.drawable.col_vod_se_bankground, R.drawable.col_vod_th_bankground,
            R.drawable.col_vod_tou_bankground};

    public static BnBunchVodFragment newInstance(long type) {
        BnBunchVodFragment fragment = new BnBunchVodFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_video_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mType = bundle.getLong("type");
        }
        context = getContext();
        _newsHelper = new NewsDatailHelper(getActivity());
        mMainGroupView = new LinearLayout(getActivity());
        mMainGroupView.setOrientation(1);
        mMainGroupView.setPadding(11, 9, 11, 0);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMainGroupView.setLayoutParams(lp);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.news_scroll_layout);
        pullToRefreshScrollView.setOnRefreshListener(this);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.addView(mMainGroupView);
        initData();
    }

    public void initData() {
        if (mMainGroupView != null && mMainGroupView.getChildCount() > 0)
            mMainGroupView.removeAllViews();
        if (mType == -1)
            mType = ColumnBasicListManager.getInstance().findIdByName("vod");
        List<ColumnCmsEntry> sort = null;
        List<ColumnCmsEntry> dlist = ColumnBasicListManager.getInstance().get_columnList();
        if (dlist != null && !dlist.isEmpty()) {
            for (ColumnCmsEntry entry : dlist) {
                if (mType == entry.getId()) {
                    sort = entry.getDlist();
                    break;
                }
            }
        }

//        sort = new LinkedHashSet<>();
//        测试
//        for (int i = 0; i < 3; i++) {
//            ColumnCmsEntry entry = new ColumnCmsEntry();
//            entry.setName("上海" + i);
//            entry.setDescription("梅艳芳说，她每开出一张支票，就少掉一个朋友。生活中有太多的塑料情谊，经不起这样的折腾");
//            sort.add(entry);
//        }

        if (sort != null && !sort.isEmpty()) {
            for (ColumnCmsEntry ch : sort) {
                createColumnView(ch.getName(), ch.getDescription(), ch.getId());
            }
        }
        pullToRefreshScrollView.onRefreshComplete();
    }

    public void createColumnView(final String title, String content, final long cid) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.bn_col_vod_item, null);
        if (view != null) {
            LinearLayout body = (LinearLayout) view.findViewById(R.id.item_body);
            if (colorIndex > 3) colorIndex = 0;
            body.setBackgroundResource(imgs[colorIndex]);
            colorIndex++;
            TextView titles = (TextView) view.findViewById(R.id.item_title);
            TextView describe = (TextView) view.findViewById(R.id.content_tv);
            ImageView moreBnt = (ImageView) view.findViewById(R.id.item_extned_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    Bundle bundle = GanZiTopBarActivity.getTitleBundle(0, title);
                    bundle.putLong("id", cid);
                    intent.putExtras(bundle);
                    GanZiTopBarActivity.start(getActivity(), HeadLineFragment.class.getName(), intent);
                }
            });
            titles.setText(title);
            describe.setText(content);
            mMainGroupView.addView(view);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        colorIndex = 0;
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

    }

}
