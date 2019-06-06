package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.*;

import com.dfsx.ganzcms.app.util.MessageIntents;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.Serializable;
import java.util.*;

/**
 * Created by heyang on 2016/10/11.
 */
public class TvChannelFragment extends Fragment {
    private LiveEntity.LiveChannel playedChannel;
    private long colId = -1;
    ContentCmsApi mContentCmsApi = null;
    private int pageOffset = 1;
    private LinearLayout _channelContainer;
    private List<ContentCmsInfoEntry> _listData;
    private static final String STATE_LIST = "TvChannelFragment.mlist";

    public static TvChannelFragment newInstance(long type) {
        TvChannelFragment fragment = new TvChannelFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_tv_channel, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            colId = getArguments().getLong("type");
        }
        mContentCmsApi = new ContentCmsApi(getActivity());
        _channelContainer = (LinearLayout) view.findViewById(R.id.tv_channle_body);
        if (savedInstanceState != null) {
            List<ContentCmsInfoEntry> sList;
            sList = (ArrayList<ContentCmsInfoEntry>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                initData(sList);
            }
        }
        getTvlistData(false);
    }

    public LiveEntity.LiveChannel getCurentPlayChannel() {
        return playedChannel;
    }

    public View createChannelView(LinearLayout container, LiveEntity.LiveChannel fr, boolean isFirst) {
        ImageView imag = new ImageView(getActivity());
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        if (isFirst) {
            lpp.setMargins(0, 0, 5, 0);
        }
        imag.setLayoutParams(lpp);
        imag.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imag.setBackgroundResource(R.drawable.glide_default_image);
//        imag.setBackgroundColor(R.color.img_default_bankgrond_color);
        if (fr == null) {
            imag.setVisibility(View.INVISIBLE);
        } else {
            Util.LoadThumebImage(imag, fr.thumb, null);
            imag.setTag(fr);
        }
        imag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveEntity.LiveChannel entry = (LiveEntity.LiveChannel) v.getTag();
                if (entry != null) {
                    playedChannel = entry;
                    RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_TV_ITEM_SELECT, entry));
                }
            }
        });
        container.addView(imag);
        return container;
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataTvRequest =
            new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                    getFileId() + "." + colId, App.getInstance().getPackageName() + getFileName()) {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject json) {
                    ArrayList<ContentCmsEntry> dlist = null;
                    try {
                        if (json != null && !TextUtils.isEmpty(json.toString())) {
                            JSONArray attr = json.optJSONArray("data");
                            if (attr != null && attr.length() > 0) {
                                dlist = new ArrayList<ContentCmsEntry>();
                                for (int i = 0; i < attr.length(); i++) {
                                    JSONObject obj = (JSONObject) attr.get(i);
                                    ContentCmsEntry entry = new Gson().fromJson(obj.toString(), ContentCmsEntry.class);
                                    if (!TextUtils.equals(entry.getType(), "live")) continue;
                                    dlist.add(entry);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
                }
            };

    private void getTvlistData(boolean bAddTail) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + colId + "/contents?";
        url += "page=" + pageOffset;
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build();
        dataTvRequest.getData(httpParams, bAddTail).
                setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final ArrayList<ContentCmsEntry> liveChannelsAarry) {
                        if (liveChannelsAarry != null && !liveChannelsAarry.isEmpty()) {
                            Observable.from(liveChannelsAarry)
                                    .subscribeOn(Schedulers.io())
                                    .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                                        @Override
                                        public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                                            ContentCmsInfoEntry info = mContentCmsApi.getEnteyFromJson(topicalEntry.getId());
                                            return info;
                                        }
                                    })
                                    .toList()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onNext(List<ContentCmsInfoEntry> data) {
                                            initData(data);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void initData(List<ContentCmsInfoEntry> data) {
        if (data != null && !data.isEmpty()) {
            if (_channelContainer != null && _channelContainer.getChildCount() > 0)
                _channelContainer.removeAllViews();
            _listData = data;
            int i = 1;
            for (; i <= data.size(); i++) {
                ContentCmsInfoEntry enytry = data.get(i - 1);
                String thummb = "";
                if (enytry.getThumbnail_urls() != null && !enytry.getThumbnail_urls().isEmpty())
                    thummb = enytry.getThumbnail_urls().get(0).toString();
                LiveEntity.LiveChannel channel = new LiveEntity.LiveChannel(enytry.getLiveId(),enytry.getId(), enytry.getTitle(), enytry.getSummary(), enytry.getUrl(), thummb);
                if (i == 1 && channel != null) {
                    playedChannel = channel;
                    RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_TV_ITEM_CREATE, channel));
                }
                if (i % 2 != 0) {
                    View v = createHor();
                    createChannelView((LinearLayout) v, channel, true);
                    _channelContainer.addView(v);
                } else {
                    int count = _channelContainer.getChildCount();
                    View v = _channelContainer.getChildAt(count >= 0 ? count - 1 : 0);
                    createChannelView((LinearLayout) v, channel, false);
                }
            }
            if ((i - 1) % 2 != 0) {
                int count = _channelContainer.getChildCount();
                View v = _channelContainer.getChildAt(count >= 0 ? count - 1 : 0);
                createChannelView((LinearLayout) v, null, true);
            }
        }
    }

    public View createHor() {
        LinearLayout container = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dp2px(getActivity(), 75));
        lp1.setMargins(0, 10, 0, 0);
        container.setLayoutParams(lp1);
        container.setOrientation(0);
        return container;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String getFileId() {
        String defaultId = "livetv_";
        if (playedChannel != null)
            defaultId += playedChannel.channelID;
        return defaultId;
    }

    private String getFileName() {
        return "tvchannalFragment.txt";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (_listData != null && !_listData.isEmpty()) {
            outState.putSerializable(STATE_LIST, (Serializable) _listData);
        }
    }


}
