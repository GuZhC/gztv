package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.model.ContributionInfo;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 贡献榜
 * Created by liuwb on 2016/10/17.
 */
public class ContributionListFrag extends Fragment {

    public static final String KEY_ROOM_SHOW_ID = "com.dfsx.lzcms.liveroom.fragment.ContributionListFrag.room-id";

    private Context context;
    private Activity activity;
    private ContributionAdapter adapter;
    private LiveChannelManager channelManager;
    private long roomId;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private ImageView actFinishImage;

    private View headerView;
    private View grade1View;
    private View grade2View;
    private View grade3View;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        activity = getActivity();
        return inflater.inflate(R.layout.frag_contribution, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actFinishImage = (ImageView) view.findViewById(R.id.image_finish_act);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.rank_list_view);
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        setHeader();
        setListAdapter(listView);

        channelManager = new LiveChannelManager(context);
        roomId = getArguments().getLong(KEY_ROOM_SHOW_ID, -1L);

        getData(roomId);

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(roomId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        actFinishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    private void setHeader() {
        headerView = LayoutInflater.from(context)
                .inflate(R.layout.rank_list_header, null);
        listView.addHeaderView(headerView);
        grade1View = headerView.findViewById(R.id.grade_1_layout);
        grade2View = headerView.findViewById(R.id.grade_2_layout);
        grade3View = headerView.findViewById(R.id.grade_3_layout);
    }

    private void updateHeaderView(ContributionInfo... datas) {
        ContributionInfo[] dataArr = new ContributionInfo[3];
        for (int i = 0; i < 3; i++) {
            int dataCount = datas != null ? datas.length : 0;
            if (i < dataCount) {
                dataArr[i] = datas[i];
            } else {
                dataArr[i] = null;
            }
        }
        updateHeaderItemView(grade1View, dataArr[0], 0);
        updateHeaderItemView(grade2View, dataArr[1], 1);
        updateHeaderItemView(grade3View, dataArr[2], 2);
    }

    private void updateHeaderItemView(View container, ContributionInfo data, int pos) {
        ImageView noUserImage = (ImageView) container.findViewById(R.id.item_no_user_image);
        CircleButton userLogo = (CircleButton) container.findViewById(R.id.item_logo);
        ImageView userGradeImage = (ImageView) container.findViewById(R.id.user_grade_image);
        TextView userNameText = (TextView) container.findViewById(R.id.user_name);
        TextView userIdText = (TextView) container.findViewById(R.id.user_id);
        TextView valueText = (TextView) container.findViewById(R.id.user_send_coins);
        if (data == null || data.getUserId() == 0) {
            noUserImage.setVisibility(View.VISIBLE);
            userLogo.setVisibility(View.GONE);
            userNameText.setText("");
            userIdText.setText("");
            valueText.setText("");
        } else {
            noUserImage.setVisibility(View.GONE);
            userLogo.setVisibility(View.VISIBLE);
            LSLiveUtils.showUserLogoImage(context, userLogo, data.getUserLogo());
            userNameText.setText(data.getName());
            userIdText.setText("ID:" + data.getUserId());
            valueText.setText(data.getContributionValue() + "");
        }
        int gradeRes = R.drawable.icon_num_3;
        if (pos == 0) {
            gradeRes = R.drawable.icon_num_1;
        } else if (pos == 1) {
            gradeRes = R.drawable.icon_num_2;
        }
        userGradeImage.setImageResource(gradeRes);
    }

    private void getData(long roomId) {
        if (roomId == -1) {
            return;
        }
        channelManager.getRoomContributionList(50, roomId, new DataRequest.DataCallback<ArrayList<ContributionInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<ContributionInfo> data) {
                if (adapter != null && data != null) {
                    Collections.sort(data);
                    ContributionInfo[] dataArr = new ContributionInfo[3];
                    int dataSize = data.size();
                    for (int i = 0; i < 3; i++) {
                        if (i < dataSize) {
                            dataArr[i] = data.remove(0);
                        } else {
                            dataArr[i] = null;
                        }
                    }
                    updateHeaderView(dataArr);
                    adapter.update(data, isAppend);
                } else {
                    updateHeaderView();
                }
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                e.printStackTrace();
            }
        });
    }


    public void setListAdapter(ListView listView) {
        adapter = new ContributionAdapter(null);
        listView.setAdapter(adapter);
    }


    private ArrayList<ContributionInfo> createTestData() {
        ArrayList<ContributionInfo> list = new ArrayList<>();
        String testLogo = "http://n1.itc.cn/img8/wb/recom/2016/05/23/146396862823854425.JPEG";
        ContributionInfo info = new ContributionInfo("吧娜娜0", testLogo, 111024, 1);
        list.add(info);
        info = new ContributionInfo("吧娜娜01", testLogo, 11024, 2);
        list.add(info);
        info = new ContributionInfo("吧娜娜02", testLogo, 1024, 3);
        list.add(info);
        info = new ContributionInfo("吧娜娜03", testLogo, 124, 4);
        list.add(info);
        info = new ContributionInfo("吧娜娜04", testLogo, 114, 5);
        list.add(info);

        return list;
    }

    public class ContributionAdapter extends BaseAdapter {
        private ArrayList<ContributionInfo> list;

        public ContributionAdapter(ArrayList<ContributionInfo> list) {
            this.list = list;
        }

        public void update(ArrayList<ContributionInfo> data, boolean isAdd) {
            if (list == null || !isAdd) {
                list = data;
            } else {
                list.addAll(data);
            }

            notifyDataSetChanged();
        }

        public ArrayList<ContributionInfo> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHodler hodler = BaseViewHodler.get(convertView, parent,
                    R.layout.item_contribution, position);

            setViewData(hodler, position);
            return hodler.getConvertView();
        }

        public void setViewData(BaseViewHodler hodler, int position) {
            TextView itemCountText = hodler.getView(R.id.item_count_text);
            CircleButton logo = hodler.getView(R.id.user_logo);
            TextView nameText = hodler.getView(R.id.item_user_name);
            TextView nameIdText = hodler.getView(R.id.item_user_id);
            TextView valueText = hodler.getView(R.id.item_contribution_value);

            setCountText(itemCountText, 3 + position);
            ContributionInfo info = list.get(position);
            nameIdText.setText("ID:" + info.getUserId());
            LSLiveUtils.showUserLogoImage(context, logo, info.getUserLogo());
            nameText.setText(info.getName());
            valueText.setText("" + info.getContributionValue());
        }

        private void setCountText(TextView tv, int pos) {
            Drawable left = null;
            String text = "";
            if (pos < 3) {
                if (0 == pos) {
                    left = context.getResources().
                            getDrawable(R.drawable.icon_num_1);
                } else if (1 == pos) {
                    left = context.getResources().
                            getDrawable(R.drawable.icon_num_2);
                } else {
                    left = context.getResources().
                            getDrawable(R.drawable.icon_num_3);
                }
                left.setBounds(0, 0, left.getMinimumWidth(),
                        left.getMinimumHeight());
            } else {
                text = (pos + 1) + "";
            }
            tv.setCompoundDrawables(left, null, null, null);
            tv.setText(text);
        }
    }


}
