package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideRoundTransform;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.DayTask;
import com.dfsx.ganzcms.app.model.ITaskData;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/19.
 */
public class DayTaskListFragment extends AbsListFragment {

    private DayTaskAdapter adapter;

    private MyDataManager dataManager;

    private EmptyView emptyView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MyDataManager(context);
        getData();
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new DayTaskAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        super.setEmptyLayout(container);
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("目前还没有任务");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData();
    }

    private void getData() {
        dataManager.getMyDayTask(new DataRequest.DataCallback<List<DayTask>>() {
            @Override
            public void onSuccess(boolean isAppend, List<DayTask> data) {
                List<ITaskData> list = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (DayTask task : data) {
                        list.add(task);
                    }
                }
                if (adapter != null) {
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


    class DayTaskAdapter extends BaseListViewAdapter<ITaskData> {

        public DayTaskAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_day_task;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView taskBkg = holder.getView(R.id.task_bkg_image);
            TextView taskNameText = holder.getView(R.id.item_task_name);
            TextView taskFinishProgressText = holder.getView(R.id.item_task_ok_num);
            ImageView taskFinishImage = holder.getView(R.id.item_task_ok_image);

            ITaskData taskData = list.get(position);
            if (taskData.getTaskBkgImageRes() != 0) {
                taskBkg.setImageResource(taskData.getTaskBkgImageRes());
            } else {
                Glide.with(context)
                        .load(taskData.getTaskBkgImagePath())
                        .placeholder(R.drawable.bg_task_comment)
                        .error(R.drawable.bg_task_comment)
                        .transform(new GlideRoundTransform(context))
                        .crossFade()
                        .into(taskBkg);
                //                GlideImgManager.getInstance().showImg(context, taskBkg,
                //                        taskData.getTaskBkgImagePath());
            }
            taskNameText.setText(taskData.getTaskName());
            taskFinishProgressText.setText(taskData.getTaskFinishProgressText());
            taskFinishImage.setVisibility(taskData.isTaskFinish() ? View.VISIBLE : View.GONE);
        }
    }
}
