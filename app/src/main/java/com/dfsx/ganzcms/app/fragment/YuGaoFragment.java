package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;

/**
 * Created by liuwb on 2017/7/12.
 */
public class YuGaoFragment extends AbsLoginRequestFragment implements DeepColorSwitchTopbarActivity.ISwitchTopBarActionListener {

    public static final String TAG_CREATE_YUGAO = "YuGaoFragment_CREATE_YUGAO ";
    public static final String TAG_YUGAO_LIST = "YuGaoFragment_list_YUGAO ";

    private YuGaoCreateFragment createFragment;
    private YuGaoListFragment yuGaoListFragment;
    private FragmentManager manager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_yugao_layout, null);
    }

    @Override
    public void onBeforeCheckLogin(View view, @Nullable Bundle savedInstanceState) {
        manager = getChildFragmentManager();
    }

    @Override
    public void onLogined() {
        setFragment(true);
    }

    private void setFragment(boolean isCreateFragShow) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (isCreateFragShow) {
            if (yuGaoListFragment != null) {
                transaction.hide(yuGaoListFragment);
            }
            createFragment = (YuGaoCreateFragment) manager.
                    findFragmentByTag(TAG_CREATE_YUGAO);
            if (createFragment == null) {
                createFragment = new YuGaoCreateFragment();
                transaction.add(R.id.frag_container, createFragment, TAG_CREATE_YUGAO);
            } else {
                transaction.show(createFragment);
            }
        } else {
            if (createFragment != null) {
                transaction.hide(createFragment);
            }
            yuGaoListFragment = (YuGaoListFragment) manager.
                    findFragmentByTag(TAG_YUGAO_LIST);
            if (yuGaoListFragment == null) {
                yuGaoListFragment = new YuGaoListFragment();
                transaction.add(R.id.frag_container, yuGaoListFragment, TAG_YUGAO_LIST);
            } else {
                transaction.show(yuGaoListFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onActFinish() {

    }

    @Override
    public void onRightViewClick(TextView textView) {

    }

    @Override
    public void onCheckChange(int position, String optionString) {
        setFragment(position == 0);
    }
}
