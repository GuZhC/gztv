package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyServiceGridAdapter;
import com.dfsx.ganzcms.app.model.ColumnEntry;
import com.dfsx.ganzcms.app.view.MyGridView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *  Created  by heyang   2018/4/10
 */

public class HandServiceFragment extends HeadLineFragment {

    private MyGridView gridview;
    private MyServiceGridAdapter mMyGridAdapter;
    private long mColumType = -1;
    private int offset = 1;
    private HeadlineListManager dataRequester;


    public static HandServiceFragment newInstance(long type) {
        HandServiceFragment fragment = new HandServiceFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_handser_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mColumType = bundle.getLong("type");
        }
        gridview = (MyGridView) view.findViewById(R.id.gridview);
        mMyGridAdapter = new MyServiceGridAdapter<ContentCmsEntry>(getActivity());
        gridview.setAdapter(mMyGridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isLeagle = position >= 0 && position < mMyGridAdapter.getCount();
                if (isLeagle) {
                    ContentCmsEntry channel = (ContentCmsEntry) mMyGridAdapter.getData(position);
                    if (channel != null) {
                        newsDatailHelper.goDetail(channel);
                    }
                }
            }
        });
        //测试
//        ArrayList<ContentCmsEntry> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ContentCmsEntry eh = new ContentCmsEntry();
//            eh.setTitle("天下" + i);
//            list.add(eh);
//        }
//        mMyGridAdapter.update(list, false);

        //创建数据的管理器
        dataRequester = new HeadlineListManager(getActivity(), 21 + "", mColumType, "bianming");
        dataRequester.setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ContentCmsEntry> data) {
                mMyGridAdapter.update(data, false);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
//        initData();
    }

//    public void initData() {
//        dataRequester.start(false, false, offset);
//    }

    @Override
    public void onResume() {
        super.onResume();
        dataRequester.start(false, false, offset);
    }


}
