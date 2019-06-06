package com.dfsx.logonproject.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.logonproject.act.LogonContancts;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by wxl on 2016/11/9.
 */
public class BaseResultFragment extends Fragment implements FragmentBackHandler {


    protected void showShortToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * 使用Fragment的startActivityForResult方法跳转到WhiteTopBarActivity
     *
     * @param context
     * @param fragName
     * @param title
     * @param rightTitle
     * @param requestCode
     */
    protected void frgStartWhiteTopBarActivityForResult(Context context, String fragName, String title, String rightTitle, int requestCode) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, title);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        startActivityForResult(intent, requestCode);
    }

    protected void frgStartWhiteTopBarActivityForResult(Context context, String fragName, String title, String rightTitle, int requestCode, String content) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, title);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        intent.putExtra(WhiteTopBarActivity.KEY_PARAM, content);
        startActivityForResult(intent, requestCode);
    }

    /*
* 显示或隐藏输入法
*/
    protected void onFocusChange(boolean hasFocus, final View edt) {
        if (edt == null) return;
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * finish并返回resultCode,与frgStartWhiteTopBarActivityForResult方法搭配使用
     *
     * @param key
     * @param value
     * @param resultCode
     */
    protected void finishActivityWithResult(String key, String value, int resultCode) {
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }

    protected void saveLoginRequestData(String username, String password) {
        if (getActivity() == null) return;
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(LogonContancts.KEY_IS_SAVE_ACCOUNT, true);
        editor.putString(LogonContancts.KEY_USER_NAME, username);
        editor.putString(LogonContancts.KEY_PASSWORD, password);
        editor.commit();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
