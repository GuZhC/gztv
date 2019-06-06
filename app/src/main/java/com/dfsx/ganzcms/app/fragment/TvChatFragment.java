package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.LiveTvChatAdapter;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.model.LiveEntity;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.MessageIntents;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/20.
 * 图文直播的聊天页面
 */
public class TvChatFragment extends Fragment {
    private static final String STATE_LIST = "TvcahtFragment.mlist";

    private LiveTvChatAdapter adapter;
    private Context context;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    //    private LiveServiceBottomBar chatBottomBar;
    private ImageView _sendBtn;
    //    private HttpGetChatMessageHelper getChatMessageHelper;
    private long lastestBeforeMessageId;
    private Handler handler = new Handler();
    private int type = 0;
    protected NewsDatailHelper _newsHelper;
    private final int UPDTAE_LIST_DATE = 0x000023;
    private final int UPDTAE_LIST_FAILED_DATE = 0x000024;
    private Subscription channelItemscription;
    private boolean refurbish = false;


    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (getActivity() == null)
                return false;
            if (message.what == UPDTAE_LIST_DATE) {
                int type = message.arg1;
                ArrayList<CommendCmsEntry> data = (ArrayList<CommendCmsEntry>) message.obj;
                if (type == 0) {
                    adapter.update(data, false);
                } else
                    adapter.addTopDataList(data);
                pullToRefreshListView.onRefreshComplete();
            } else if (message.what == UPDTAE_LIST_FAILED_DATE) {
                adapter.update(null, false);
                pullToRefreshListView.onRefreshComplete();
            }
            return false;
        }
    });


    public static TvChatFragment newInstance(int type) {
        TvChatFragment tabFragment = new TvChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", type);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_tv_service_chat, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        _sendBtn = (ImageView) view.findViewById(R.id.send_btn);
        _sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pubCommendDialog(v, -1);
            }
        });
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.chat_list_view);
//        chatBottomBar = (LiveServiceBottomBar) view.findViewById(R.id.edit_chat_bar);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshListView.getLoadingLayoutProxy().setPullLabel("下拉加载...");
        pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在载入...");
        pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel("放开加载更多");
        listView = pullToRefreshListView.getRefreshableView();
        setListAdapter(listView);
//        if (savedInstanceState != null) {
//            List<CommendCmsEntry> sList;
//            sList = (ArrayList<CommendCmsEntry>) savedInstanceState.getSerializable(STATE_LIST);
//            if (sList != null) {
//                adapter.update(sList, false);
//            }
//        }
        _newsHelper = new NewsDatailHelper(getActivity());
//        getChatMessageHelper = new HttpGetChatMessageHelper(context, getRoomEnterId());
        initAction();
        register();
        getData(1);
    }

    public void register() {
        channelItemscription = RxBus.getInstance().
                toObserverable(MessageData.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageData>() {
                    @Override
                    public void call(MessageData messageData) {
                        if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_TV_ITEM_CREATE)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
//                            if (channel != null) {
//                                playedChannel = channel;
//                                initData(playedChannel);
                            getData(1);
//                            }
                        } else if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_RADIO_ITEM_SELECT)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
                            getData(1);
                        }
                    }
                });
    }

    public void pubCommendDialog(View view, long refId) {
        _newsHelper.showCommendDialog(view, refId, -1,new NewsDatailHelper.ICommendDialogLbtnlister() {
            @Override
            public boolean onParams(long id, long ref_id,final String context) {
                _newsHelper.getmContentCmsApi().createCmsCommend(getChannerId(), id, context, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        LSUtils.toastMsgFunction(getActivity(), "发表评论成功");
                        CommendCmsEntry entry = new CommendCmsEntry();
                        entry.setAuthor_id(App.getInstance().getUser().getUser().getId());
                        entry.setAuthor_avatar_url(App.getInstance().getUser().getUser().getAvatar_url());
                        entry.setText(context);
                        entry.setUser(true);
                        entry.setCreation_time(System.currentTimeMillis());
//                        boolean isCurUser = data.size() == 1 && isCurrentUser(data.get(0).getChatUserId());
//                        boolean isToBottom = isCurUser ||
//                                isListViewLastItemVisible();
                        adapter.addBottomData(entry);
                        setListViewScrollToBottom(true);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        LSUtils.toastMsgFunction(getContext(), JsonCreater.getErrorMsgFromApi(e.toString()));
                        e.printStackTrace();
                    }
                });
                return true;
            }
        });
    }

    private void initAction() {
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                long oldMessageId = 1;
//                boolean isLeagle = adapter.getData() != null && !adapter.getData().isEmpty();
//                CommendCmsEntry chatData = isLeagle ? adapter.getData().get(0) : null;
//                oldMessageId = chatData != null ? chatData.getId() : 0;
//                if (oldMessageId != 0 && oldMessageId != lastestBeforeMessageId) {
                    lastestBeforeMessageId = oldMessageId;
                    getData((int) oldMessageId);
//                } else {
//                    Toast.makeText(context, "没有更多的数据", Toast.LENGTH_SHORT).show();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            pullToRefreshListView.onRefreshComplete();
//                        }
//                    }, 50);
//                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

//        chatBottomBar.setOnViewBtnClickListener(getBottomBarEventListener());
    }

//    private LiveServiceBottomBar.OnViewBtnClickListener getBottomBarEventListener() {
//        Activity activity = getActivity();
//        if (activity != null && activity instanceof LiveServiceBottomBar.OnViewBtnClickListener) {
//            return (LiveServiceBottomBar.OnViewBtnClickListener) activity;
//        }
//        return null;
//    }

    public long getChannerId() {
        long chanId = -1;
        if (getParentFragment() != null) {
            if (type == 0) {
                chanId = ((DzLiveTvFragment) getParentFragment()).getChannalId();
            } else if (type == 1) {
                chanId = ((DzBrodcastFragment) getParentFragment()).getChannalId();
            }
        }
        return chanId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (channelItemscription != null)
            channelItemscription.unsubscribe();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (adapter != null) {
//            outState.putSerializable(STATE_LIST, (Serializable) adapter.getData());
//        }
//    }

    private DataFileCacheManager<ArrayList<CommendCmsEntry>> dataCommendManager = new
            DataFileCacheManager<ArrayList<CommendCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            112+type + "", App.getInstance().getPackageName() + "tvchat_commend.txt") {
                @Override
                public ArrayList<CommendCmsEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<CommendCmsEntry> dlist = null;
                    List<CommendCmsEntry.SubCommentsBean> subLits = null;
                    try {
                        JSONArray result = jsonObject.optJSONArray("data");
                        if (result != null) {
                            dlist = new ArrayList<CommendCmsEntry>();
                            Gson g = new Gson();
                            for (int i = 0; i < result.length(); i++) {
//                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                CommendCmsEntry entry = g.fromJson(item.toString(), CommendCmsEntry.class);
                                JSONArray arr = item.optJSONArray("sub_comments");
                                if (arr != null && arr.length() > 0) {
                                    subLits = new ArrayList<>();
                                    for (int k = 0; k < arr.length(); k++) {
                                        JSONObject ob = (JSONObject) arr.get(k);
                                        CommendCmsEntry.SubCommentsBean subn = new Gson().fromJson(ob.toString(), CommendCmsEntry.SubCommentsBean.class);
                                        subLits.add(subn);
                                    }
                                    entry.setmSubCommendList(subLits);
                                    entry.setUser(false);
                                }
                                dlist.add(entry);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
//                    return _newsHelper.getmContentCmsApi().getRootCommendListFromJson(jsonObject);
                }
            };

    protected void getData(int offset) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/contents/" + getChannerId() + "/root-comments?";
        url += "page=" + offset + "&size20&sub_comment_count=3";
        dataCommendManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<CommendCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<CommendCmsEntry>>() {
        @Override
        public void onSuccess(final boolean isAppend, ArrayList<CommendCmsEntry> data) {
//            if (!isAppend && data!=null) {
//                adapter.update(data, isAppend);
//            } else {
//                adapter.addTopDataList(data);
//            }
            Message msg = myHander.obtainMessage(UPDTAE_LIST_DATE);
            if (!isAppend) {
//                adapter.update(data, isAppend);
                msg.obj = data;
                msg.arg1 = 0;
            } else {
//                adapter.addTopDataList(data);
                msg.obj = data;
                msg.arg1 = 1;
            }
            myHander.sendMessage(msg);

//                pullToRefreshListView.onRefreshComplete();
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            myHander.sendEmptyMessage(UPDTAE_LIST_FAILED_DATE);
//                pullToRefreshListView.onRefreshComplete();
        }
    };

    public void setListAdapter(ListView listView) {
        adapter = new LiveTvChatAdapter(context);
        listView.setAdapter(adapter);
    }

//    public void onProcessMessage(List<UserChatMessage> messageList) {
//        if (messageList != null && messageList.size() > 0) {
//            Observable.just(messageList)
//                    .observeOn(Schedulers.newThread())
//                    .map(new Func1<List<UserChatMessage>, List<IChatData>>() {
//                        @Override
//                        public List<IChatData> call(List<UserChatMessage> userChatMessages) {
//                            List<IChatData> list = new ArrayList<IChatData>();
//                            for (UserChatMessage chatMessage : userChatMessages) {
//                                list.add(chatMessage);
//                            }
//                            return list;
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<List<IChatData>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onNext(List<IChatData> datas) {
//                            boolean isCurUser = datas.size() == 1 && isCurrentUser(datas.get(0).getChatUserId());
//                            boolean isToBottom = isCurUser ||
//                                    isListViewLastItemVisible();
//                            adapter.addBottomDataList(datas);
//                            setListViewScrollToBottom(isToBottom);
//                        }
//                    });
//        }
//    }

    private boolean isCurrentUser(long id) {
        return AppManager.getInstance().getIApp().getLoginUserId() == id;
    }

    protected void setListViewScrollToBottom(boolean isScrollToBottom) {
        if (isScrollToBottom) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(listView.getBottom());
                }
            }, 100);

        }
    }

    private boolean isListViewLastItemVisible() {
        Adapter adapter = listView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            final int lastItemPosition = listView.getCount() - 1;
            final int lastVisiblePosition = listView.getLastVisiblePosition();

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - listView.getFirstVisiblePosition();
                final View lastVisibleChild = listView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() - listView.getBottom() <= 10;
                }
            }
        }
        return false;
    }
}
