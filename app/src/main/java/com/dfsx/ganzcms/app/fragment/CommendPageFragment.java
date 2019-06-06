package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.dfsx.ganzcms.app.adapter.NewsCommendListAdapter;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.business.ICommendReplaylister;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang  on 2017/9/22
 */
public class CommendPageFragment extends AbsListFragment implements EditChangedLister.EditeTextStatuimpl {
    public NewsCommendListAdapter adapter;
    public int pageCount = 1;
    public EmptyView emptyView;
    public LinearLayout topView;
    public long mId = -1;
    public ImageView headImage, topPraiseBtn;
    public TextView publicker, createTIme, commendNum, content, topNumText, praiseNumberTv;
    public ImageView commenBtn;
    PopupWindow mMorePopupWindow;
    ImageButton mBtnSend;
    EditText mReplyContent;
    ImageView replayThumb;
    CommuntyDatailHelper newsDatailHelper;
    long subId = -1;
    long itemId = -1;
    long praiseNumber = 0;
    protected View bottomLinespce, praiseContainer;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mId = getArguments().getLong(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, -1);
            if (mId == -1) {
                mId = getArguments().getLong("subId");
                itemId = getArguments().getLong("itemId");
                praiseNumber = getArguments().getLong("praiseNumer");
            }
        }
        super.onViewCreated(view, savedInstanceState);
        newsDatailHelper = new CommuntyDatailHelper(act);
        getData(1);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new NewsCommendListAdapter(context);
        }
        adapter.setRootId(mId);
        listView.setAdapter(adapter);
        adapter.setCallBack(new IButtonClickListenr<CommendCmsEntry>() {
            @Override
            public void onLbtClick(int type, IButtonClickData<CommendCmsEntry> data) {
                CommendCmsEntry entry = data.getObject();
                if (entry == null) return;
                switch (type) {
                    case IButtonClickType.COMMEND_CLICK:
                        showMore(data.getTag(), entry.getId());
                        break;
                    case IButtonClickType.PRAISE_CLICK:
                        addPraiseBtn(data.getTag(), entry.getId());
                        break;
                }
            }
        });
        pullToRefreshListView.setBackgroundColor(Color.WHITE);
//        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position -= listView.getHeaderViewsCount();
//                onListViewItemClick(position);
//                int a=0;
//            }
//        });
    }

    public void addPraiseBtn(View v, long id) {
        newsDatailHelper.praiseforCmsCommend(v, id, new DataRequest.DataCallbackTag() {
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
                    ToastUtils.toastPraiseMsgFunction(act);
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

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageCount = 1;
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageCount++;
        getData(pageCount);
    }

    private DataFileCacheManager<ArrayList<CommendCmsEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<CommendCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            mId + "", this.getClass().getName() + "_frag.txt") {
                @Override
                public ArrayList<CommendCmsEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<CommendCmsEntry> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<CommendCmsEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                CommendCmsEntry entry = new Gson().fromJson(item.toString(), CommendCmsEntry.class);
                                JSONObject ref = item.optJSONObject("ref_comment");
                                List<CommendCmsEntry.RefCommentsBean> dlist = null;
                                if (ref != null) {
                                    dlist = new ArrayList<CommendCmsEntry.RefCommentsBean>();
                                    CommendCmsEntry.RefCommentsBean refd = new Gson().fromJson(ref.toString(), CommendCmsEntry.RefCommentsBean.class);
                                    dlist.add(refd);
                                }
                                entry.setRef_comments(dlist);
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
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/contents/" + mId + "/sub-comments?";
        url += "&page=" + page + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 1).setCallback(callback);
    }

    long nSubCommendNum = 0;
    private DataRequest.DataCallback<ArrayList<CommendCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<CommendCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<CommendCmsEntry> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (!isAppend && data == null) {
                clear();
            } else {
                if (adapter == null) return;
                adapter.update(data, isAppend);
                if ((data == null || data.isEmpty()) && !isAppend) {
                    topView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                } else {
                    emptyView.setVisibility(View.GONE);
                    topView.setVisibility(View.VISIBLE);
                }
                if (!isAppend) {
                    nSubCommendNum = data.size();
//                topNumText.setText("共有" + nSubCommendNum + "个回复");
                    CommendCmsEntry entry = data.get(data.size() - 1);
                    initData(entry);
                }
            }
        }

        @Override
        public void onFail(ApiException e) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            LogUtils.e("TAG", "error === " + e.getMessage());
        }
    };

    public void initData(CommendCmsEntry entry) {
        if (entry != null && entry.getRef_comments() != null) {
            CommendCmsEntry.RefCommentsBean info = entry.getRef_comments().get(0);
            Util.LoadImageErrorUrl(headImage, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
            publicker.setText(info.getAuthor_nickname());
            content.setText(info.getText());
            praiseNumber = entry.getLike_count() != 0 ? entry.getLike_count() : praiseNumber;
            praiseNumberTv.setText(StringUtil.getNumKString(praiseNumber));
            subId = info.getId();
            headImage.setTag(R.id.tag_replay_cid, info.getId());
            createTIme.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));
            String  replay=getActivity().getResources().getString(R.string.news_act_replay_item_hit);
            if (nSubCommendNum > 0) {
                commendNum.setText(nSubCommendNum + replay);
//                commendNum.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                commendNum.setText(replay);
//                commendNum.setBackground(null);
            }
        }
    }

    public void clear() {
        topView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText(getActivity().getResources().getString(R.string.no_data_hit));
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView, p);
//        topView.setBackgroundResource(R.drawable.exchange_top_bankground);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.frag_commend_page_header, null);
        View leftBack = (View) topLayout.findViewById(R.id.back_finish_view);
        replayThumb = (ImageView) topLayout.findViewById(R.id.replay_thumb);
        topNumText = (TextView) topLayout.findViewById(R.id.title);
        headImage = (ImageView) topLayout.findViewById(R.id.replay_user_logo);
//        topPraiseBtn = (ImageView) topLayout.findViewById(R.id.replay_praise_child_btn);
        publicker = (TextView) topLayout.findViewById(R.id.replay_user_name);
        commenBtn = (ImageView) topLayout.findViewById(R.id.disclosure_replay_btn);
        createTIme = (TextView) topLayout.findViewById(R.id.replay_time_value);
        commendNum = (TextView) topLayout.findViewById(R.id.replay_count_text);
        content = (TextView) topLayout.findViewById(R.id.replay_title_value);
        praiseNumberTv = (TextView) topLayout.findViewById(R.id.comemndg_praise_txt);
        bottomLinespce = (View) topLayout.findViewById(R.id.bottom_line_space);
        bottomLinespce.setVisibility(View.GONE);
        praiseContainer = (View) topLayout.findViewById(R.id.praise_container);
        commenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore(v, subId);
            }
        });
        leftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        praiseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsDatailHelper.praiseforCmsCommend(v, mId, new DataRequest.DataCallbackTag() {
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
        topView.addView(topLayout);
    }

    @Override
    protected void setBottomView(FrameLayout bottomListViewContainer) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        View bottomLayout = LayoutInflater.from(context).
                inflate(R.layout.news_commend_bottom_custom, null);
        View commenBottomBtn = (View) bottomLayout.findViewById(R.id.cvideo_bottom_commend);
        ImageView bottomFavImag = (ImageView) bottomLayout.findViewById(R.id.cvideo_isfav_img);
        ImageView shareBtn = (ImageView) bottomLayout.findViewById(R.id.cvido_share_img);
        bottomFavImag.setVisibility(View.GONE);
        shareBtn.setVisibility(View.GONE);
        commenBottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore(v, subId);
            }
        });
        bottomFavImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bottomListViewContainer.addView(bottomLayout);
    }

    public void showMore(View moreBtnView, long resf_cid) {
        if (mMorePopupWindow == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.layout_more, null, false);
            mMorePopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setFocusable(true);
            mMorePopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mMorePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            View parent = mMorePopupWindow.getContentView();
            mReplyContent = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mBtnSend = (ImageButton) parent.findViewById(R.id.commentButton);
            mReplyContent.addTextChangedListener(new EditChangedLister(this));
            mBtnSend.setTag(resf_cid);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contnt = mReplyContent.getText().toString();
                    if (TextUtils.isEmpty(contnt)) {
                        ToastUtils.toastNoContentCommendFunction(getActivity());
                        return;
                    }
                    long refId = -1;
                    if (view.getTag() != null) refId = (long) view.getTag();
                    newsDatailHelper.pubCommnedReplay(itemId, refId, contnt, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            ToastUtils.toastReplayOk(context);
                            mMorePopupWindow.dismiss();
                            newsDatailHelper.onFocusChange(false, mReplyContent);
                            mReplyContent.setText("");
                            getData(1);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            ToastUtils.toastMsgFunction(context, JsonCreater.getErrorMsgFromApi(e.toString()));
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            mBtnSend.setTag(resf_cid);
            mMorePopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
            newsDatailHelper.onFocusChange(true, mReplyContent);
        }
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }
}
