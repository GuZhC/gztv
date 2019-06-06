package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.ganzcms.app.R;

/**
 * Created by heyang on 2017/6/23.
 * 回答的详情
 */
public class AnswerInfoFragment extends Fragment implements View.OnClickListener {

    private TextView mHuidaBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_info_custom, container, false);
        mHuidaBtn = (TextView) view.findViewById(R.id.answer_huida_btn);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == mHuidaBtn) {

        }
    }
}



