package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ITaskData;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;

import java.util.ArrayList;

/**
 * 首次任务列表
 * Created by liuwb on 2017/7/19.
 */
public class OnceTaskListFragment extends AbsListFragment {

    private OnceTaskAdapter adapter;

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new OnceTaskAdapter(context);
        listView.setAdapter(adapter);

        ArrayList<ITaskData> testList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ITaskData data = new ITaskData() {
                @Override
                public long getTaskId() {
                    return 0;
                }

                @Override
                public String getTaskName() {
                    return "实名认证";
                }

                @Override
                public String getTaskFinishProgressText() {
                    return "0/10";
                }

                @Override
                public double getTaskWage() {
                    return 10;
                }

                @Override
                public boolean isTaskFinish() {
                    return false;
                }

                @Override
                public String getTaskBkgImagePath() {
                    return null;
                }

                @Override
                public int getTaskBkgImageRes() {
                    return R.drawable.bg_task_comment;
                }
            };
            testList.add(data);
        }
        adapter.update(testList, false);
    }

    class OnceTaskAdapter extends BaseListViewAdapter<ITaskData> {

        public OnceTaskAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_once_task;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView taskName = holder.getView(R.id.item_task_name);
            TextView taskClickText = holder.getView(R.id.item_task_click_text);
            TextView rightGoldText = holder.getView(R.id.task_gold_text);
            ImageView rightTaskFinishImage = holder.getView(R.id.item_task_ok_image);

            ITaskData data = list.get(position);
            taskName.setText(data.getTaskName());
            taskClickText.setText("去" + data.getTaskName());
            rightGoldText.setText("+" + data.getTaskWage());
            rightTaskFinishImage.setVisibility(data.isTaskFinish() ? View.VISIBLE : View.GONE);
        }
    }
}
