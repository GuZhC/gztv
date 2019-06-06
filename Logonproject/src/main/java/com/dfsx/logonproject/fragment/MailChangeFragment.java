package com.dfsx.logonproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.logonproject.R;

/**
 * Created by wxl on 2016/11/8.
 */

public class MailChangeFragment extends BaseResultFragment {
    private View rootView;
    private EditText editText;
    private TextView topBarRight;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == topBarRight) {
                onFocusChange(false,editText);
                String str = editText.getText().toString().trim();
                if (Util.isEmail(str))
                    finishActivityWithResult("mail", str, 1);
                else
                    showLongToast("这不是一个正确的邮件地址");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mail_change_fragment, null);
        editText = (EditText) rootView.findViewById(R.id.mail_edittext);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof WhiteTopBarActivity) {
            topBarRight = ((WhiteTopBarActivity) getActivity()).getTopBarRightText();
            topBarRight.setOnClickListener(listener);
        }
    }
}
