package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by heyang on 2016/9/15.
 * 加载页面
 */
public class LoadFragment extends HeadLineFragment {

    protected Context context;
    CustomeProgressDialog _progressDialog;
    private long _contentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _contentId = bundle.getLong(DefaultFragmentActivity.KEY_FRAGMENT_PARAM);
        }
        context = getContext();
        _progressDialog = CustomeProgressDialog.show(getActivity(), "加载中...");
        if (_contentId != -1) {
            _progressDialog.show();
            reqBackGroundImage();
        }
    }

    public void reqBackGroundImage() {
        rx.Observable.just(_contentId).
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                map(new Func1<Long, ContentCmsInfoEntry>() {
                    @Override
                    public ContentCmsInfoEntry call(Long id) {
                        ContentCmsInfoEntry entry = newsDatailHelper.getmContentCmsApi().getContentCmsInfo(id);
                        entry.setShowType(newsDatailHelper.getmContentCmsApi().getModeType(entry.getType(), 0));
                        return entry;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContentCmsInfoEntry>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                               }

                               @Override
                               public void onNext(ContentCmsInfoEntry entry) {
                                   if (_progressDialog != null && _progressDialog.isShowing())
                                       _progressDialog.dismiss();
                                   if (entry != null) {
                                       String thumb = "";
                                       if (entry.getThumbnail_urls() != null && entry.getThumbnail_urls().size() > 0)
                                           thumb = entry.getThumbnail_urls().get(0);
                                       newsDatailHelper.preDetail(entry.getId(), entry.getShowType(), entry.getTitle(), thumb, 0, entry.getUrl());
                                   }else {
                                       ToastUtils.toastMsgFunction(getActivity(),"没找到页面!!");
                                   }
                                   getActivity().finish();
                               }
                           }
                );

    }


}