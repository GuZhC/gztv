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
 * Created by heyang on  2016/10/28
 */
public class PassChangeFragment extends Fragment {
    EditText mOldTx;
    EditText mNewTx;
    EditText mNewAginTx;
    AccountApi mUserApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pass_change_info, container, false);
        mOldTx = (EditText) view.findViewById(R.id.pass_old_edt);
        mNewTx = (EditText) view.findViewById(R.id.pass_new_edt);
        mNewAginTx = (EditText) view.findViewById(R.id.pass_newagin_edt);
        mUserApi = new AccountApi(getActivity());
        Activity  act=getActivity();
         if (act instanceof WhiteTopBarActivity) {
           ((WhiteTopBarActivity) act).getTopBarRightText().
                     setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          try {
                              mUserApi.changePassword(mOldTx.getText().toString(), mNewTx.getText().toString(), new DataRequest.DataCallback() {
                                  @Override
                                  public void onSuccess(boolean isAppend, Object data) {
                                         boolean  isOk=(Boolean)data;
                                         String  succMsg="修改密码成功";
                                         if(isOk)
                                         {
                                             succMsg="修改密码失败";
                                         }
                                         Toast.makeText(getActivity(),succMsg,Toast.LENGTH_SHORT).show();
                                  }

                                  @Override
                                  public void onFail(ApiException e) {
                                      Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                                  }
                              });
                          } catch (ApiException e) {
                              e.printStackTrace();
                          }
                      }
                     });
            }
        return view;
    }
}
