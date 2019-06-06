package com.dfsx.ganzcms.app.fragment;

import android.widget.TextView;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/19.
 */
public class TaskFragment extends AbsSwitchContentFragment implements DeepColorSwitchTopbarActivity.ISwitchTopBarActionListener {

    private ArrayList<String> showFragmentNameList;

    @Override
    List<String> getFragmentNameList() {
        if (showFragmentNameList == null) {
            showFragmentNameList = new ArrayList<>();
            showFragmentNameList.add(DayTaskListFragment.class.getName());
            showFragmentNameList.add(OnceTaskListFragment.class.getName());
        }
        return showFragmentNameList;
    }

    @Override
    public void onActFinish() {

    }

    @Override
    public void onRightViewClick(TextView textView) {

    }

    @Override
    public void onCheckChange(int position, String optionString) {
        setShowFragmentIndex(position);
    }
}
