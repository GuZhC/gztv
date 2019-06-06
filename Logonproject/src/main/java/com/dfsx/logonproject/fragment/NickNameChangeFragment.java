package com.dfsx.logonproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.logonproject.R;

/**
 * Created by Administrator on 2016/11/8.
 */

public class NickNameChangeFragment extends BaseResultFragment {
    private View rootView;
    private EditText editText;
    private TextView topBarRight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nickname_change_fragment, null);
        editText = (EditText) rootView.findViewById(R.id.nickname_edittext);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof WhiteTopBarActivity) {
            topBarRight = ((WhiteTopBarActivity) getActivity()).getTopBarRightText();
            topBarRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFocusChange(false,editText);
                    Toast.makeText(getActivity(), "保存", Toast.LENGTH_SHORT).show();
                    finishActivityWithResult("nickname", editText.getText().toString().trim(), 2);
                }
            });
        }
    }
}
