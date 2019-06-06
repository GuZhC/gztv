package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.TopicCommendListAdapter;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.openimage.OpenImageUtils;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang  on 2017/9/22
 */
public class CmyReplyPageFragment extends CommendPageFragment {

    //    public static final int PAGE_SIZE = 20;
    private TopicCommendListAdapter adapter;
    long tId = -1;
    private ReplyEntry gReplyEntry;
//    private LinearLayout topView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            gReplyEntry = (ReplyEntry) getArguments().getSerializable("obeject");
            tId = getArguments().getLong("itemId");
//            replyThumbPath = getArguments().getString("thumb");
//            adapter.setRootId(mId);
        }
//        newsDatailHelper = new NewsDatailHelper(act);
        getData(1);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new TopicCommendListAdapter(context);
        }
        adapter.setRootId(mId);
        listView.setAdapter(adapter);
        pullToRefreshListView.setBackgroundColor(Color.WHITE);
        adapter.setCallback(new IButtonClickListenr<ReplyEntry>() {
            @Override
            public void onLbtClick(int type, IButtonClickData<ReplyEntry> data) {
                ReplyEntry entry = data.getObject();
                if (entry == null) return;
                switch (type) {
                    case IButtonClickType.ITEM_CLICK:
                        gotoPubFrag(tId, entry.getId());
                        break;
                    case IButtonClickType.PRAISE_CLICK:
                        onPraiseBtn(data.getTag(), entry.getId());
                        break;
                }
            }
        });
    }

    public void gotoPubFrag(long tid, long refid) {
        Intent intent = new Intent();
        intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, tid);
        intent.putExtra("tid", refid);
        DefaultFragmentActivity.start(getActivity(), CommendPubFragment.class.getName(), intent);
    }

    public void onPraiseBtn(View v, long comendId) {
        newsDatailHelper.praiseforTopicCommend(v, comendId, new DataRequest.DataCallbackTag() {
            @Override
            public void onSuccess(Object object, boolean isAppend, Object data) {
//                if (object != null) {
//                    if (object instanceof RelativeLayout) {
//                        View parent = (View) ((RelativeLayout) object).getParent();
//                        TextView txt = (TextView) parent.findViewById(R.id.comemndg_praise_txt);
//                        if (txt != null) {
//                            long num = 0;
//                            String numtxt = txt.getText().toString().trim();
//                            if (numtxt != null && !TextUtils.isEmpty(numtxt)) {
//                                num = Long.parseLong(numtxt);
//                                num++;
//                            }
//                            txt.setText(num + "");
//                        }
//                    }
//                }
                if ((boolean) data) {
                    ToastUtils.toastMsgFunction(act, "点赞成功");
                    getData(pageCount);
                }
            }

            @Override
            public void onSuccess(boolean isAppend, Object data) {

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(context, e);
            }
        });
    }

//    @Override
//    protected PullToRefreshBase.Mode getListViewMode() {
//        return PullToRefreshBase.Mode.BOTH;
//    }
//
//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//        pageCount = 1;
//        getData(1);
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//        pageCount++;
//        getData(pageCount);
//    }

    private DataFileCacheManager<ArrayList<ReplyEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ReplyEntry>>
                    (App.getInstance().getApplicationContext(),
                            mId + "", App.getInstance().getPackageName() + "_cmyfrag.txt") {
                @Override
                public ArrayList<ReplyEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<ReplyEntry> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<ReplyEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ReplyEntry entry = new Gson().fromJson(item.toString(), ReplyEntry.class);
                                JSONArray refarr = item.optJSONArray("ref_replies");
                                List<ReplyEntry.RefRepliesBean> dlist = null;
                                if (refarr != null && refarr.length() > 0) {
                                    int postion = refarr.length() > 1 ? refarr.length() - 1 : 0;
                                    dlist = new ArrayList<ReplyEntry.RefRepliesBean>();
                                    JSONObject ob = (JSONObject) refarr.get(postion);
                                    ReplyEntry.RefRepliesBean refd = new Gson().fromJson(ob.toString(), ReplyEntry.RefRepliesBean.class);
                                    dlist.add(refd);
                                }
                                entry.setRef_replies(dlist);
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };

    private void getData(int page) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/root-replies/" + mId + "/sub-replies?";
        url += "page=" + page + "&size=20";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 1, false).setCallback(callback);
    }

    long nSubCommendNum = 0;
    private DataRequest.DataCallback<ArrayList<ReplyEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ReplyEntry>>() {
        @Override
        public void onSuccess(final boolean isAppend, ArrayList<ReplyEntry> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (!isAppend && data == null) {
                clear();
            } else {
                if (adapter == null) return;
//                topNumText.setText("共有" + gReplyEntry.getSub_reply_count() + "个回复");
//                ReplyEntry entry = data.get(data.size() - 1);
//                adapter.update(data, isAppend);
                if ((data == null || data.isEmpty()) && !isAppend) {
                    topView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                } else {
                    emptyView.setVisibility(View.GONE);
                    topView.setVisibility(View.VISIBLE);
                }
                if (data == null || data.isEmpty()) return;
                Observable.from(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ReplyEntry, ReplyEntry>() {
                            @Override
                            public ReplyEntry call(ReplyEntry topicalEntry) {
                                long id = -1;
                                try {
                                    if (topicalEntry.getAttachments() != null && topicalEntry.getAttachments().size() > 0) {
                                        id = topicalEntry.getAttachments().get(0);
                                    }
                                    if (id == -1) return topicalEntry;
                                    String respone = newsDatailHelper.getmTopicalApi().getAtthmentById(id);
                                    if (!TextUtils.isEmpty(respone.toString().trim())) {
                                        JSONObject json = JsonCreater.jsonParseString(respone);
                                        Gson gson = new Gson();
                                        JSONArray arr = json.optJSONArray("result");
                                        if (arr != null && arr.length() > 0) {
                                            for (int i = 0; i < arr.length(); i++) {
                                                try {
                                                    JSONObject obj = (JSONObject) arr.get(i);
                                                    Attachment cp = gson.fromJson(obj.toString(),
                                                            Attachment.class);
                                                    topicalEntry.setMthumImage(cp.getUrl());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }
                                return topicalEntry;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<ReplyEntry>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<ReplyEntry> data) {
                                if (data != null && data.size() > 0) {
                                    adapter.update(data, isAppend);
                                }
                            }
                        });
                if (!isAppend) {
                    nSubCommendNum = data.size();
                    initData(gReplyEntry);
                }
            }
        }

        @Override
        public void onFail(ApiException e) {
            pullToRefreshListView.onRefreshComplete();
            emptyView.loadOver();
            LogUtils.e("TAG", "error === " + e.getMessage());
        }
    };

    public void initData(ReplyEntry entry) {
        if (entry != null) {
//            ReplyEntry.RefRepliesBean info = entry.getRef_replies().get(0);
            Util.LoadImageErrorUrl(headImage, entry.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
            publicker.setText(entry.getAuthor_nickname());
            String  s=UtilHelp.getDecondeString(entry.getContent());
            content.setText(s);
            subId = entry.getId();
            headImage.setTag(R.id.tag_replay_cid, entry.getAuthor_id());
            createTIme.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", entry.getPost_time() * 1000));
            String path = gReplyEntry.getMthumImage();
            if (path != null && !TextUtils.isEmpty(path)) {
                replayThumb.setVisibility(View.VISIBLE);
//                String path = info.getMthumImage();
//                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
                path += "?w=" + Util.dp2px(getContext(), 120) + "&h=" + Util.dp2px(context, 90) + "&s=1";
                Util.LoadThumebImage(replayThumb, path, null);
                replayThumb.setTag(R.id.tag_replay_thumb, entry.getMthumImage());
            } else {
                replayThumb.setVisibility(View.GONE);
            }
            praiseNumber = entry.getLike_count() != 0 ? entry.getLike_count() : praiseNumber;
            praiseNumberTv.setText(StringUtil.getNumKString(praiseNumber));
            replayThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = (String) view.getTag(R.id.tag_replay_thumb);
                    if (path != null && !TextUtils.isEmpty(path)) {
                        OpenImageUtils.openImage(view.getContext(), path, 0);
                    }
                }
            });
            praiseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsDatailHelper.praiseforTopicCommend(v, mId, new DataRequest.DataCallbackTag() {
                        @Override
                        public void onSuccess(Object object, boolean isAppend, Object data) {
                            if (object != null) {
                                if (object instanceof RelativeLayout) {
                                    View parent = (View) ((RelativeLayout) object).getParent();
                                    TextView txt = (TextView) parent.findViewById(R.id.comemndg_praise_txt);
                                    if (txt != null) {
                                        long num = 0;
                                        String numtxt = txt.getText().toString().trim();
                                        if (numtxt != null && !TextUtils.isEmpty(numtxt)) {
                                            num = Long.parseLong(numtxt);
                                            num++;
                                        }
                                        txt.setText(num + "");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onSuccess(boolean isAppend, Object data) {

                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            ToastUtils.toastApiexceFunction(getActivity(), e);
                        }
                    });
                }
            });
            nSubCommendNum = entry.getSub_reply_count();
            if (nSubCommendNum > 0) {
                commendNum.setText(nSubCommendNum + "回复");
                commendNum.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                commendNum.setText("回复");
                commendNum.setBackground(null);
            }
        }
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        commenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMore(v, subId);
//                DefaultFragmentActivity.start(v.getContext(), CommendPubFragment.class.getName(), tId, mId);
                gotoPubFrag(tId, mId);
            }
        });
    }

}
