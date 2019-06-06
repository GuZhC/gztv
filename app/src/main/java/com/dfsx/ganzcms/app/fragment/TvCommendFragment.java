package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.EditChangedLister;
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
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang  on 2017/9/22
 */
public class TvCommendFragment extends AbsListFragment implements EditChangedLister.EditeTextStatuimpl {

    public NewsCommendListAdapter adapter;
    public int pageCount = 1;
    public EmptyView emptyView;
    public LinearLayout topView;
    public long mId;
    public ImageView headImage;
    public TextView publicker, createTIme, commendNum, content, topNumText;
    public ImageButton commenBtn;
    PopupWindow mMorePopupWindow;
    ImageButton mBtnSend;
    EditText mReplyContent;
    ImageView replayThumb;
    NewsDatailHelper newsDatailHelper;
    long subId = -1;


    public static TvCommendFragment newInstance(long type) {
        TvCommendFragment fragment = new TvCommendFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(DefaultFragmentActivity.KEY_FRAGMENT_PARAM);
        }
        super.onViewCreated(view, savedInstanceState);
        newsDatailHelper = new NewsDatailHelper(act);
        getData(1);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new NewsCommendListAdapter(context);
        }
        adapter.setRootId(mId);
        listView.setAdapter(adapter);
        pullToRefreshListView.setBackgroundColor(Color.WHITE);
        View tailView = getActivity().getLayoutInflater().inflate(R.layout.video_detail_header, null);
        listView.addFooterView(tailView);
        adapter.setCallBack(new IButtonClickListenr() {
            @Override
            public void onLbtClick(int type, IButtonClickData data) {
                CommendCmsEntry entry = (CommendCmsEntry) data.getObject();
                if (entry == null) return;
                if (type == IButtonClickType.COMMEND_CLICK) {
                    showMore(data.getTag(), entry.getId());
                }
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
                if (data == null || data.isEmpty()) return;
                nSubCommendNum = data.size();
                topNumText.setText("共有" + nSubCommendNum + "个回复");
                CommendCmsEntry entry = data.get(data.size() - 1);
                initData(entry);
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
            subId = info.getId();
            headImage.setTag(R.id.tag_replay_cid, info.getId());
            createTIme.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));
            if (nSubCommendNum > 0) {
                commendNum.setText(nSubCommendNum + "回复");
                commendNum.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                commendNum.setText("回复");
                commendNum.setBackground(null);
            }
        }
    }

    public void clear() {

    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        View eV = LayoutInflater.from(context).
                inflate(R.layout.no_default_frg_layout, null);
        emptyView.setLoadOverView(eV);
        emptyView.loading();
        container.addView(emptyView);
        ImageView btnStart = (ImageView) eV.findViewById(R.id.retyr_btn);
        View parent = (View) btnStart.getParent();
        parent.setVisibility(View.GONE);
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView, p);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.frag_commend_page_header, null);
        View leftBack = (View) topLayout.findViewById(R.id.back_finish_view);
        replayThumb = (ImageView) topLayout.findViewById(R.id.replay_thumb);
        topNumText = (TextView) topLayout.findViewById(R.id.title);
        headImage = (ImageView) topLayout.findViewById(R.id.replay_user_logo);
        publicker = (TextView) topLayout.findViewById(R.id.replay_user_name);
        commenBtn = (ImageButton) topLayout.findViewById(R.id.disclosure_replay_btn);
        createTIme = (TextView) topLayout.findViewById(R.id.replay_time_value);
        commendNum = (TextView) topLayout.findViewById(R.id.replay_count_text);
        content = (TextView) topLayout.findViewById(R.id.replay_title_value);
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
        topView.addView(topLayout);
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
                    long refId = -1;
                    if (view.getTag() != null) refId = (long) view.getTag();
                    newsDatailHelper.pubCommnedReplay(mId, refId, contnt, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            LSUtils.toastMsgFunction(context, "发表评论成功");
                            mMorePopupWindow.dismiss();
                            newsDatailHelper.onFocusChange(false, mReplyContent);
                            mReplyContent.setText("");
                            getData(1);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            LSUtils.toastMsgFunction(context, "发表评论失败:" + e.toString());
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
