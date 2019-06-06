package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleImageView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.HostPersonInfoActivity;
import com.dfsx.ganzcms.app.business.HostPersonListHelper;
import com.dfsx.ganzcms.app.model.HostInfo;
import com.dfsx.ganzcms.app.model.IHostData;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

public class HostListFragment extends AbsListFragment {

    public static final String KEY_HOST_COLUMN_ID = "HostListFragment_column_id";

    private HostLisAdapter adapter;
    private HostPersonListHelper hostPersonListHelper;
    private long hostColumnId;

    private int pageSize = 20;
    private int page;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        doBundle();
        super.onViewCreated(view, savedInstanceState);
        pullToRefreshListView.setBackgroundResource(R.color.white);
        hostPersonListHelper = new HostPersonListHelper(context);

        getData(1);
    }

    private void doBundle() {
        Bundle b = getArguments();
        if (b != null) {
            hostColumnId = b.getLong(KEY_HOST_COLUMN_ID, 0);
        }
    }

    @Override
    public void setListAdapter(final ListView listView) {
        adapter = new HostLisAdapter(context);
        listView.setAdapter(adapter);

        adapter.setOnGridItemClickListener(new BaseGridListAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemClick(int linePosition, int column) {
                int pos = linePosition * adapter.getColumnCount() + column;
                List<IHostData> list = adapter.getData();
                if (list != null && pos >= 0 && pos < list.size()) {
                    Intent intent = new Intent(act, HostPersonInfoActivity.class);
                    IHostData data = list.get(pos);
                    intent.putExtra(HostPersonInfoActivity.KEY_HOST_ID, data.getHostId());
                    startActivity(intent);
                }
            }
        });

        /*ArrayList<IHostData> data = new ArrayList<>();
        data.add(new TestHost());
        data.add(new TestHost());
        data.add(new TestHost());
        data.add(new TestHost());
        data.add(new TestHost());
        data.add(new TestHost());
        adapter.update(data, false);*/
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getData(page);
    }

    private void getData(int page) {
        this.page = page;
        hostPersonListHelper.getHostList(hostColumnId, page, pageSize, new DataRequest.DataCallback<ArrayList<HostInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<HostInfo> data) {
                ArrayList<IHostData> list = new ArrayList<>();
                if (data != null) {
                    for (HostInfo info : data) {
                        list.add(info);
                    }
                }
                pullToRefreshListView.onRefreshComplete();
                adapter.update(list, isAppend);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    class TestHost implements IHostData {

        @Override
        public long getHostId() {
            return 0;
        }

        @Override
        public String getHostLogo() {
            return "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2542528435,1174189446&fm=173&s=6D120B99467C03924588153503003060&w=500&h=281&img.JPEG";
        }

        @Override
        public String getHostName() {
            return "威尔逊";
        }
    }

    class HostLisAdapter extends BaseGridListAdapter<IHostData> {

        public HostLisAdapter(Context context) {
            super(context);
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_host_list_item;
        }

        @Override
        protected int getHDivideLineWidth() {
            return 0;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, IHostData data) {
            CircleImageView logo = hodler.getView(R.id.img_host_logo);
            TextView hostName = hodler.getView(R.id.tv_host_name);

            LSLiveUtils.showUserLogoImage(act, logo, data.getHostLogo());
            hostName.setText(data.getHostName());
        }
    }
}
