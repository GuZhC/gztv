package com.dfsx.core.file.processWnd;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.R;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.processWnd.ProgressView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProcessDialog extends Activity implements View.OnClickListener {
    TextView mClosed;
    private Context context;
    PopupWindow pop;
    Button mCancelBtn;
    ProgressView mProcessView;
    OnCancelBtnLister _onCloseLister;

    public ProcessDialog(Context context) {
        this.context = context;
    }

    public void showDialog(View tagView) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.process_popupwindows, null);
//        if (pop == null) {
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        pop.setBackgroundDrawable(dw);
//        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
//        }
        mProcessView = (ProgressView) view.findViewById(R.id.commit_btn);
        mProcessView.setState(ProgressView.DOWNLOADING);
        mCancelBtn = (Button) view.findViewById(R.id.popupwindows_cancel_btn);
        mCancelBtn.setOnClickListener(this);
//        updateValues(12);
//        Window dialogWindow = pop.
//        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = WindowManager.LayoutParams.FILL_PARENT;
//        lp.height = WindowManager.LayoutParams.FILL_PARENT;
//        lp.flags = (WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        dialogWindow.setAttributes(lp);
//        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);//动画
//        mClosed = (TextView) view.findViewById(R.id.search_cancel_btn);
//        mClosed.setOnClickListener(this);
//        dialog.show();
//        ArrayList<SocirtyNewsChannel> dlist = new ArrayList<SocirtyNewsChannel>();
//        for (int i = 0; i < 5; i++) {
//            SocirtyNewsChannel chnel = new SocirtyNewsChannel();
//            chnel.newsTitle = "受期待的Xbox One游安全自拍功能";
//            dlist.add(chnel);
//        }
//        listAdpter.update(dlist, false);
        pop.showAtLocation(tagView, Gravity.CENTER, 0, 0);
    }

    public OnCancelBtnLister get_onCloseLister() {
        return _onCloseLister;
    }

    public void set_onCloseLister(OnCancelBtnLister _onCloseLister) {
        this._onCloseLister = _onCloseLister;
    }

    public boolean isShowActivty() {
        return pop!=null?pop.isShowing():false;
    }

    public void updateValues(float fal) {
        mProcessView.setProgressText("", fal);
    }

    public void updateValues(String des, float fal) {
        mProcessView.setProgressText(des, fal);
    }

    public void initPop() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.5f;
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    public void dismissDialog() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    /*
    public void goToResult(String key) {
        String url = App.getInstance().getmSession().makeUrl("services/operations/retrieve.json?keys=" + key + "&simple=1");
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();

        new DataRequest<ArrayList<SocirtyNewsChannel>>(context) {
            @Override
            public ArrayList<SocirtyNewsChannel> jsonToBean(JSONObject json) {
                if (json != null) {
//                    return JsonToBean(json);
                }
                return null;
            }
        }
                .getData(httpParams, false)
                .setCallback(new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        if (data != null) {
//                          d  ArrayList<SocirtyNewsChannel> list = (ArrayList<SocirtyNewsChannel>) data;
//                            listAdpter.update(dlist, false);
                        } else {
                            Toast.makeText(context, "没有相关新闻", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }  */

    @Override
    public void onClick(View view) {
        if (view == mClosed) {
            dismissDialog();
        } else if (view == mCancelBtn) {
            dismissDialog();
            if (_onCloseLister != null) {
                _onCloseLister.onClosed();
            }
        }
    }

    public interface OnCancelBtnLister {
        public void onClosed();
    }
}