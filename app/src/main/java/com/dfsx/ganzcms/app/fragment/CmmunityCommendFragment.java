package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ReplyListManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.fragment.AbsPullRecyclerViewFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.openimage.OpenImageUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang 2018/5/20  frag_my_attention_list_item
 */
public class CmmunityCommendFragment extends AbsPullRecyclerViewFragment {
    private RecycleAdapter adapter;
    private int page = 1;
    private long mId = 0;
    private ReportPopupWindow reportPopWindow = null;
    private TopicalApi mTopicalApi;
    private ReplyListManager replyListManager;
    private Subscription commendUpdateSubscription;


    public static CmmunityCommendFragment newInstance(long userId) {
        CmmunityCommendFragment fragment = new CmmunityCommendFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("mId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mId = getArguments().getLong("mId");
        }
        super.onViewCreated(view, savedInstanceState);
        page = 1;
        reportPopWindow = new ReportPopupWindow(getActivity());
        mTopicalApi = new TopicalApi(getActivity());
        if (getActivity() != null && getActivity() instanceof PullToRefreshRecyclerView.PullRecyclerHelper)
            pullToRefreshRecyclerView.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) getActivity());
        replyListManager = new ReplyListManager(getActivity(), 13 + "", mId);
        replyListManager.setCallback(new DataRequest.DataCallback<ArrayList<ReplyEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ReplyEntry> data) {
                if (pullToRefreshRecyclerView != null)
                    pullToRefreshRecyclerView.onRefreshComplete();

                //test
//                data = new ArrayList<>();
//                for (int i = 0; i < 15; i++) {
//                    ReplyEntry entry = new ReplyEntry();
//                    entry.setContent("天下" + i);
//                    entry.setAuthor_nickname("李四");
//                    data.add(entry);
//                }

                if (data == null) {
                    if (adapter.getHeaderView() == null)
                        setHeader(pullToRefreshRecyclerView.getRefreshableView());
//                    if (!isAppend)
//                        tailView.setVisibility(View.GONE);
//                    if (!mReplayAdapter.isHas() && isAppend)
//                        tailView.setVisibility(View.GONE);
                    return;
                } else {
//                    if (data.isEmpty() && !isAppend) {
//                        tailView.setVisibility(View.GONE);
//                    } else {
//                        tailView.setVisibility(View.VISIBLE);
//                    }
                }
                Observable.from(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ReplyEntry, ReplyEntry>() {
                            @Override
                            public ReplyEntry call(ReplyEntry topicalEntry) {
                                try {
                                    String respone = mTopicalApi.getAtthmentById(topicalEntry.getAttmentId());
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
                                if (!(data == null || data.isEmpty())) {
                                    adapter.setHeaderView(null);
                                    adapter.update((ArrayList<ReplyEntry>) data, isAppend);
                                } else {
                                    setHeader(pullToRefreshRecyclerView.getRefreshableView());
                                }
                            }
                        });

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                if (adapter.getHeaderView() == null)
                    setHeader(pullToRefreshRecyclerView.getRefreshableView());
                if (pullToRefreshRecyclerView != null)
                    pullToRefreshRecyclerView.onRefreshComplete();
//                mCommendNumberTx.setText("评论0");
                Log.e("MyDocxFragment", "get data fail");
            }
        });
        getDatas(1);
    }

    public void initRegister() {
        commendUpdateSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_COMNEND_OK)) {
                            getDatas(1);
                        }
                    }
                });

    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.communvity_no_replay_layout, view, false);
        adapter.setHeaderView(header);
    }


    protected void getDatas(int offset) {
        replyListManager.start(mId, offset, offset > 1 ? true : false);
    }

    public void resrashData() {
        page = 1;
        getDatas(page);
    }

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        if (adapter == null) {
            RecycleAdapter adapter1 = new RecycleAdapter();
            adapter1.setOnItemViewClickListener(new BaseRecyclerViewHolder.OnRecyclerItemViewClickListener() {
                @Override
                public void onItemViewClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        List<ReplyEntry> list = adapter.getData();
                        if (list != null && position >= 0 && position < list.size()) {
                            ReplyEntry itemData = list.get(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            adapter = adapter1;
//            setEmptyLayout();
        }
        return adapter;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        page = 1;
        getDatas(page);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getDatas(page);
    }

    public class RecycleAdapter extends BaseRecyclerViewDataAdapter<ReplyEntry> implements BaseRecyclerViewHolder.OnRecyclerItemViewClickListener {
        public boolean bInit;
        private final String STATE_LIST = "ListTleantAdapter.mlist";
        private BaseRecyclerViewHolder.OnRecyclerItemViewClickListener itemViewClickListener;
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;
        private View mHeaderView;

        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putSerializable(STATE_LIST, (Serializable) list);
            }
        }

        private IButtonClickListenr callback;

        public void setCallback(IButtonClickListenr callback) {
            this.callback = callback;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<ReplyEntry> sList;
            sList = (ArrayList<ReplyEntry>) savedInstanceState.getSerializable(STATE_LIST);
            if (sList != null) {
                list = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public void setHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }


        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            return TYPE_NORMAL;
        }


        @Override
        public MyBaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER)
                return new RecycleAdapter.MyBaseRecyclerViewHolder(mHeaderView, viewType);
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.nc_commend_replay_item, parent, false);
            return new RecycleAdapter.MyBaseRecyclerViewHolder(layout, viewType);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder hodler, int position) {
            if (getItemViewType(position) == TYPE_HEADER) return;
            int pos = getRealPosition(hodler);
            View bodyView = hodler.getView(R.id.replay_item_hor);
            CircleButton logo = hodler.getView(R.id.replay_user_logo);
            TextView nameText = hodler.getView(R.id.replay_user_name);
            TextView titleText = hodler.getView(R.id.replay_title_value);
            TextView timeText = hodler.getView(R.id.replay_time_value);
            TextView numberText = hodler.getView(R.id.replay_count_text);
            TextView praiseText = hodler.getView(R.id.comemndg_praise_txt);
            ImageView thumb = hodler.getView(R.id.replay_thumb);
            ImageView praiseBtn = hodler.getView(R.id.replay_praise_child_btn);
            ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
            replypub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReplyEntry entry = (ReplyEntry) view.getTag(R.id.tag_replay_cid);
                    if (entry == null) return;
                    Intent intent = new Intent();
                    intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, mId);
                    intent.putExtra("tid", entry.getId());
                    DefaultFragmentActivity.start(context, CommendPubFragment.class.getName(), intent);
                }
            });
            praiseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyEntry object = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                    if (callback != null) {
                        callback.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(v, object));
                    }
                }
            });
            LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
            if (list == null || list.isEmpty()) {
                bodyView.setVisibility(View.GONE);
                return;
            } else {
                bodyView.setVisibility(View.VISIBLE);
            }
            ReplyEntry info = list.get(pos);
            if (info == null) return;
            Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
            praiseText.setText(StringUtil.getNumKString(info.getLike_count()));

            bodyView.setTag(R.id.tag_replay_cid, info);
            praiseBtn.setTag(R.id.tag_replay_cid, info);
            bodyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyEntry entry = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                    if (entry == null) return;
                    Intent intent = new Intent();
                    if (entry.getSub_reply_count() == 0) {
                        ToastUtils.toastNoCommendFunction(context);
                        return;
                    }
                    intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, entry.getId());
                    intent.putExtra("itemId", mId);
                    intent.putExtra("obeject", entry);
                    intent.putExtra("praiseNumer", entry.getLike_count());
                    DefaultFragmentActivity.start(context, CmyReplyPageFragment.class.getName(), intent);
                }
            });
            nameText.setText(info.getAuthor_nickname());
            titleText.setText(info.getContent());
            replypub.setTag(R.id.tag_replay_cid, info);
            timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getPost_time() * 1000));
            if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
                thumb.setVisibility(View.VISIBLE);
                String path = info.getMthumImage();
//                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
                path += "?w=" + 120 + "&h=" + 90 + "&s=1";
                Util.LoadThumebImage(thumb, info.getMthumImage(), null);
                thumb.setTag(R.id.tag_replay_thumb, info.getMthumImage());
            } else {
                thumb.setVisibility(View.GONE);
            }
            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = (String) view.getTag(R.id.tag_replay_thumb);
                    if (path != null && !TextUtils.isEmpty(path)) {
                        OpenImageUtils.openImage(view.getContext(), path, 0);
                    }
                }
            });
            long nSubCommendNum = info.getSub_reply_count();
            if (nSubCommendNum > 0) {
                numberText.setVisibility(View.VISIBLE);
                numberText.setText(nSubCommendNum + "回复");
//                numberText.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                numberText.setVisibility(View.GONE);
                numberText.setText("回复");
//                numberText.setBackground(null);
            }
            View horItem = hodler.getView(R.id.replay_item_hor);
            horItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (reportPopWindow != null)
                        reportPopWindow.show(view);
                    return false;
                }
            });
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getLayoutPosition();
            return mHeaderView == null ? position : position - 1;
        }

        @Override
        public int getItemCount() {
//            return mHeaderView == null ? list == null ? 0 : list.size() : list == null ? 0 : list.size() + 1;
            if (mHeaderView == null) {
                return list == null ? 0 : list.size();
            } else {
                return list == null ? 1 : list.size() + 1;
            }
        }


        public void setOnItemViewClickListener(BaseRecyclerViewHolder.OnRecyclerItemViewClickListener listener) {
            itemViewClickListener = listener;
        }

        @Override
        public void onItemViewClick(View v) {
            if (itemViewClickListener != null) {
                itemViewClickListener.onItemViewClick(v);
            }
        }

        class MyBaseRecyclerViewHolder extends BaseRecyclerViewHolder {
            public MyBaseRecyclerViewHolder(View itemView, int viewType) {
                super(itemView, viewType);
                if (itemView == mHeaderView) return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commendUpdateSubscription != null)
            commendUpdateSubscription.unsubscribe();
    }


}
