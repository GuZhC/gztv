package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;

public class NewsTwoListFragment extends Fragment {

    public static final String KEY_CITY_COLUMN_DATA = "NewsTwoListFragment_column_data";

    private Fragment leftFrag;
    private Fragment rightFrag;

    private int leftFragContentId;
    private int rightFragContentId;


    public static NewsTwoListFragment newInstance(ColumnCmsEntry columnMenu) {
        NewsTwoListFragment twoListFragment = new NewsTwoListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewsTwoListFragment.KEY_CITY_COLUMN_DATA, columnMenu);
        twoListFragment.setArguments(bundle);
        return twoListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_news_two_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leftFragContentId = R.id.left_frame;
        rightFragContentId = R.id.right_frame;

        //设置子内容
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        leftFrag = new NewsCityColumnListFragment();
        leftFrag.setArguments(getArguments());
        rightFrag = new NewsCityParentColumnContentListFagment();
        rightFrag.setArguments(getArguments());
        transaction.add(leftFragContentId, leftFrag);
        transaction.add(rightFragContentId, rightFrag);
        transaction.commitAllowingStateLoss();
    }
}
