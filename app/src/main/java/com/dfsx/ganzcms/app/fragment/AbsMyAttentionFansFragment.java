package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.view.TwoRelyView;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Subscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenxiaolong on 2016/10/31.
 * 我的关注 我的粉丝 抽象类
 */
public abstract class AbsMyAttentionFansFragment extends AbsListFragment {
    //已关注
    public static final int MODE_ATTENTION = 1;
    //没关注
    public static final int MODE_NO_ATTENTION = 2;
    //相互关注
    public static final int MODE_MUTUAL_ATTENTION = 3;

    protected int clicedPosition = -1;
    protected TwoRelyView clickedTworelyView;
    protected Subscription subscription;
    protected EmptyView emptyView;

    //已经获取到前几页的数据
    protected int pageIndex = 1;
    //每页多少条数据
    protected static int numberPerPage = 50;

    protected MyAdapter adapter = new MyAdapter(context);

    //    ProgressDialog progressDialog;

    private List<IConcernData> data = new ArrayList<>();
    //记录我与各用户的关注状态
    private List<Integer> array = new ArrayList<>();

    protected TopicalApi topicalApi;
    protected long userId;

    protected abstract void getDataFromNetWork(List<IConcernData> data, MyAdapter adapter, boolean isAppend, int pageIndex);


    protected IConcernData getUserbean(int position) {
        return data.get(position);
    }

    protected Integer getState(int position) {
        return data.get(position).getModeType();
    }

    protected void setState(int position, int state) {
        data.get(position).setModeType(state);
    }

    protected void clearState() {
        data.clear();
        array.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        pageIndex = 1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicalApi = new TopicalApi(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscription != null)
            subscription.unsubscribe();
    }

    protected List<IConcernData> getAdapterData() {
        return data;
    }

    protected TwoRelyView getClickedTworelyView() {
        return clickedTworelyView;
    }

    protected int getClicedPosition() {
        return clicedPosition;
    }

    protected void setAttentionView(TwoRelyView twoRelyView) {
        twoRelyView.changeFirstViewDrawableRight(R.drawable.already_attent);
        twoRelyView.setPadding(twoRelyView.getPaddingLeft(), twoRelyView.getPaddingTop(),
                40, twoRelyView.getPaddingBottom());
        twoRelyView.changeSecondText("已关注");
    }

    protected void setNoAttentionView(TwoRelyView twoRelyView) {
        twoRelyView.changeFirstViewDrawableRight(R.drawable.add_attention);
        twoRelyView.setPadding(twoRelyView.getPaddingLeft(), twoRelyView.getPaddingTop(),
                40, twoRelyView.getPaddingBottom());
        twoRelyView.changeSecondText("加关注");
    }

    protected void setMutualAttentionView(TwoRelyView twoRelyView) {
        twoRelyView.changeFirstViewDrawableRight(R.drawable.mutual_attention);
        twoRelyView.changeSecondText("相互关注");
    }


    protected void showShortToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public MyAdapter getListAdapter() {
        return adapter;
    }

    @Override
    public void setListAdapter(final ListView listView) {
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                if (data != null && position >= 0 && position < data.size()) {
                    IConcernData itemData = data.get(position);
                    if (itemData != null) {
                        IntentUtil.gotoPersonHomeAct(context, itemData.getUserId());
                    }
                }
            }
        });
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        pageIndex = 1;
        getDataFromNetWork(data, adapter, false, pageIndex);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);

        int size = data.size();
        if (size % numberPerPage > 0) {
            pageIndex = size / numberPerPage + 2;
        } else {
            pageIndex = size / numberPerPage + 1;
        }
        getDataFromNetWork(data, adapter, true, pageIndex);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getLong(WhiteTopBarActivity.KEY_PARAM, 0L);
        }
        if (userId == 0) {
            if (App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null) {
                userId = App.getInstance().getUser().getUser().getId();
            } else {
                userId = 0;
            }
        }
        super.onViewCreated(view, savedInstanceState);
        //清除上次加载的数据
        clearState();
        //重新获取数据
        //        progressDialog = new ProgressDialog(context);
        //        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //        progressDialog.setMessage("正在请求数据");
        pageIndex = 1;
        getDataFromNetWork(data, adapter, false, 1);
    }


    @Override
    protected void setEmptyLayout(LinearLayout container) {
        super.setEmptyLayout(container);
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    protected void doWithAddaAttentionView(TwoRelyView twoRelyView, int position) {
        if (data.get(position).getModeType() == MODE_ATTENTION)
            setAttentionView(twoRelyView);
        else if (data.get(position).getModeType() == MODE_MUTUAL_ATTENTION)
            setMutualAttentionView(twoRelyView);
        if (data.get(position).getModeType() == MODE_NO_ATTENTION)
            setNoAttentionView(twoRelyView);
    }

    /**
     * 已关注 未关注 状态反转
     */
    protected abstract void reverseState(TwoRelyView twoRelyView, int position);

    protected abstract void onAttentionViewClick(TwoRelyView twoRelyView, int position);


    class MyAttentionViewListener implements View.OnClickListener {
        private int pos;

        public MyAttentionViewListener(int position) {
            this.pos = position;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof TwoRelyView) {
                clicedPosition = pos;
                clickedTworelyView = (TwoRelyView) v;
                onAttentionViewClick((TwoRelyView) v, pos);
            }
        }
    }


    public class MyAdapter extends BaseAdapter {


        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        public void update(ArrayList<IConcernData> dataarray, boolean addtrail) {
            if (addtrail) {
                data.addAll(dataarray);
            } else {
                data = dataarray;
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHodler viewHodler = BaseViewHodler.get(convertView,
                    parent,
                    R.layout.frag_my_attention_list_item,
                    position);
            setViewData(viewHodler, position);
            return viewHodler.getConvertView();
        }

        private void setViewData(BaseViewHodler viewHodler, int Position) {
            CircleButton headView = viewHodler.getView(R.id.head_view);
            TextView userName = viewHodler.getView(R.id.user_name);
            TextView userSignature = viewHodler.getView(R.id.user_signature);
            TwoRelyView attentionView = viewHodler.getView(R.id.add_attention);
            attentionView.setOnClickListener(new MyAttentionViewListener(Position));
            if (data.get(Position).getNickName() != null)
                userName.setText(data.get(Position).getNickName());
            else
                userName.setText(data.get(Position).getUserName());
            userSignature.setText(data.get(Position).getSignature());
            if (data.get(Position).getLogoUrl() != null)
                LSLiveUtils.showUserLogoImage(getActivity(), headView, data.get(Position).getLogoUrl());
            doWithAddaAttentionView(attentionView, Position);
        }
    }

    /**
     * 关注和粉丝的显示数据类型
     */
    public interface IConcernData extends Serializable {
        String getNickName();

        String getUserName();

        String getLogoUrl();

        String getSignature();

        long getUserId();

        /**
         * #MODE_ATTENTION
         * #MODE_NO_ATTENTION
         * MODE_MUTUAL_ATTENTION
         *
         * @return
         */
        int getModeType();

        void setModeType(int modeType);
    }
}
