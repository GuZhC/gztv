package com.dfsx.logonproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by heyang on  2016/10/29
 */
public class SignChangeFragment extends BaseResultFragment {
    EditText mContent;
    AccountApi mUserApi;

    //有修改 wxl 2016.11.9
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_change_info, container, false);
        mContent = (EditText) view.findViewById(R.id.sign_content_edt);
        Activity act = getActivity();
        if (act instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) act).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String str = mContent.getText().toString().trim();
                            if (str != null && !TextUtils.isEmpty(str)) {
                                if (str.length() > 50) {
                                    Toast.makeText(getActivity(), "个性签名已超出50个长度", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            onFocusChange(false,mContent);
                            finishActivityWithResult("signature", str, 4);
                        }
                    });
        }
        if (getArguments() != null) {
            String sign = getArguments().getString(WhiteTopBarActivity.KEY_PARAM);
            if (sign != null && !TextUtils.isEmpty(sign))
                mContent.setText(sign);
        }
        return view;
    }
}
