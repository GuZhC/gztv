package com.dfsx.logonproject.dzfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.fragment.BaseResultFragment;

/**
 * Created by heyang on 2017/10/23
 */

public class RegWelFragment extends Fragment implements View.OnClickListener {
    private ImageView  _backBtn;
    private TextView  _logoBtn;
    private EditText _telephoneEdt;
    private TextView _verifyBtn;
    private ImageView _weixinBtn, _qqBtn, _weiBoBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_reg_wel, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _logoBtn = (TextView) view.findViewById(R.id.reg_logon_btn);
        _logoBtn.setOnClickListener(this);
        _backBtn = (ImageView) view.findViewById(R.id.wel_back_btn);
        _backBtn.setOnClickListener(this);
        _telephoneEdt = (EditText) view.findViewById(R.id.wel_telephone_edt);
        _verifyBtn = (TextView) view.findViewById(R.id.get_verift_btn);
        _verifyBtn.setOnClickListener(this);
        _weixinBtn = (ImageView) view.findViewById(R.id.wel_back_btn);
        _weixinBtn.setOnClickListener(this);
        _qqBtn = (ImageView) view.findViewById(R.id.wel_back_btn);
        _qqBtn.setOnClickListener(this);
        _weiBoBtn = (ImageView) view.findViewById(R.id.wel_back_btn);
        _weiBoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == _logoBtn) {
            DefaultFragmentActivity.start(getActivity(), LogonFragment.class.getName());
        } else if (v == _backBtn) {
            getActivity().finish();
        } else if (v == _verifyBtn) {

        } else if (v == _weixinBtn) {

        } else if (v == _qqBtn) {

        } else if (v == _weiBoBtn) {

        }
    }
}
