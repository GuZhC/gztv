package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.LiveServiceChatAdapter;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.business.HttpGetChatMessageHelper;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.view.LiveServiceBottomBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/20.
 * 图文直播的聊天页面
 */
public class LiveServiceChatFragment extends Fragment {

    private LiveServiceChatAdapter adapter;
    private Context context;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private LiveServiceBottomBar chatBottomBar;

    private HttpGetChatMessageHelper getChatMessageHelper;

    private long lastestBeforeMessageId;
    private Handler handler = new Handler();

    private Runnable afterCreateRun;

    private boolean isCreatedFragment = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();

        return inflater.inflate(R.layout.frag_live_service_chat, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.chat_list_view);
        chatBottomBar = (LiveServiceBottomBar) view.findViewById(R.id.edit_chat_bar);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshListView.getLoadingLayoutProxy().setPullLabel("下拉加载...");
        pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在载入...");
        pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel("放开加载更多");
        listView = pullToRefreshListView.getRefreshableView();
        setListAdapter(listView);

        getChatMessageHelper = new HttpGetChatMessageHelper(context, getRoomEnterId());
        initAction();
        getData(0);
        isCreatedFragment = true;
        if (afterCreateRun != null) {
            handler.post(afterCreateRun);
        }
    }

    private String getRoomEnterId() {
        String id = "";
        if (getActivity() instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) getActivity()).getRoomEnterId();
        }
        return id;
    }

    public void setCouldChat(boolean isCouldChat) {
        if (!isCouldChat) {
            Runnable setNoTalkRun = new Runnable() {
                @Override
                public void run() {
                    ArrayList<IChatData> noTalkData = new ArrayList<>();
                    noTalkData.add(new NoChatNote());
                    if (adapter != null) {
                        adapter.update(noTalkData, true);
                    }
                }
            };
            if (isCreatedFragment) {
                setNoTalkRun.run();
            } else {
                afterCreateRun = setNoTalkRun;
            }
        } else {
            afterCreateRun = null;
        }
    }

    private void initAction() {
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                long oldMessageId = 0;
                boolean isLeagle = adapter.getData() != null && !adapter.getData().isEmpty();
                IChatData chatData = isLeagle ? adapter.getData().get(0) : null;
                oldMessageId = chatData != null ? chatData.getChatId() : 0;
                if (oldMessageId != 0 && oldMessageId != lastestBeforeMessageId) {
                    lastestBeforeMessageId = oldMessageId;
                    getData(oldMessageId);
                } else {
                    Toast.makeText(context, "没有更多的数据", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }, 50);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        chatBottomBar.setOnViewBtnClickListener(getBottomBarEventListener());
    }

    private LiveServiceBottomBar.OnViewBtnClickListener getBottomBarEventListener() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof LiveServiceBottomBar.OnViewBtnClickListener) {
            return (LiveServiceBottomBar.OnViewBtnClickListener) activity;
        }
        return null;
    }

    private long getRoomId() {
        long rId = 0;
        if (context instanceof AbsChatRoomActivity) {
            rId = ((AbsChatRoomActivity) context).getRoomId();
        }
        return rId;
    }


    private void getData(long beforeMessageId) {
        getChatMessageHelper.getLiveChatMessageList(getRoomId(), beforeMessageId, 5, new DataRequest.DataCallback<List<IChatData>>() {
            @Override
            public void onSuccess(boolean isAppend, List<IChatData> data) {
                if (!isAppend) {
                    adapter.update(data, isAppend);
                } else {
                    adapter.addTopDataList(data);
                }
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    public void setListAdapter(ListView listView) {
        adapter = new LiveServiceChatAdapter(context);
        listView.setAdapter(adapter);
    }

    public void onProcessMessage(List<LiveMessage> messageList) {
        if (messageList != null && messageList.size() > 0) {
            Observable.just(messageList)
                    .observeOn(Schedulers.newThread())
                    .map(new Func1<List<LiveMessage>, List<IChatData>>() {
                        @Override
                        public List<IChatData> call(List<LiveMessage> userChatMessages) {
                            List<IChatData> list = new ArrayList<IChatData>();
                            for (LiveMessage chatMessage : userChatMessages) {
                                list.add(chatMessage);
                            }
                            return list;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<IChatData>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(List<IChatData> datas) {
                            boolean isCurUser = datas.size() == 1 && isCurrentUser(datas.get(0).getChatUserId());
                            boolean isToBottom = isCurUser ||
                                    isListViewLastItemVisible();
                            adapter.addBottomDataList(datas);
                            setListViewScrollToBottom(isToBottom);
                        }
                    });
        }
    }

    public void onReceiveGiftMessage(List<GiftMessage> message) {
        if (message != null) {
            Observable.just(message)
                    .observeOn(Schedulers.io())
                    .map(new Func1<List<GiftMessage>, List<IChatData>>() {
                        @Override
                        public List<IChatData> call(List<GiftMessage> giftMessageList) {
                            ArrayList<IChatData> messages = new ArrayList<IChatData>();
                            for (GiftMessage giftMessage : giftMessageList) {
                                boolean isOk = giftMessage.initGiftContentTextSync(getActivity());
                                if (isOk) {
                                    messages.add(giftMessage);
                                }
                            }
                            return messages;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<IChatData>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(List<IChatData> giftMessages) {
                            if (giftMessages != null) {
                                boolean isCurrentUser = giftMessages.size() == 1 && isCurrentUser(giftMessages.get(0)
                                        .getChatUserId());
                                boolean isToBottom = isCurrentUser ||
                                        isListViewLastItemVisible();
                                adapter.addBottomDataList(giftMessages);
                                setListViewScrollToBottom(isToBottom);
                            }
                        }
                    });
        }
    }

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
