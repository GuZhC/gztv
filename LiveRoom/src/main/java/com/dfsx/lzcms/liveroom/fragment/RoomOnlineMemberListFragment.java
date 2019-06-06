package com.dfsx.lzcms.liveroom.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.model.ChatMember;
import com.dfsx.lzcms.liveroom.model.NoTalkUser;
import com.dfsx.lzcms.liveroom.model.OnLineMember;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class RoomOnlineMemberListFragment extends AbsListFragment {

    public static final String KEY_INTENT_SHOW_ID = "RoomOnlineMemberListFragment_show_id";
    public static final String KEY_INTENT_ROOM_ENTER_ID = "RoomOnlineMemberListFragment_room_enter_id";
    public static final String KEY_INTENT_IS_LIVE_USER = "RoomOnlineMemberListFragment_is_live_user";

    private static final int PAGE_SIZE = 10;

    private EmptyView emptyView;

    private OnlineMemberAdapter adapter;
    private int page;

    private IChatRoom chatRoom;

    private long showId;
    private String roomEnterId;

    /**
     * 是主播么
     */
    private boolean isLiveUer;
    protected IsLoginCheck loginCheck;
    private LiveChannelManager channelManager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            showId = bundle.getLong(KEY_INTENT_SHOW_ID, 0L);
            roomEnterId = bundle.getString(KEY_INTENT_ROOM_ENTER_ID, "");
            isLiveUer = bundle.getBoolean(KEY_INTENT_IS_LIVE_USER, false);
        }
        super.onViewCreated(view, savedInstanceState);
        chatRoom = new HttpChatRoomHelper(context, showId, roomEnterId);
        loginCheck = new IsLoginCheck(act);
        getData(1);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getData(page);
    }

    private void getData(final int page) {
        this.page = page;
        Observable.just(page)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<OnLineMember>>() {
                    @Override
                    public Observable<OnLineMember> call(Integer page) {
                        return Observable.just(chatRoom.getRoomMember(page, PAGE_SIZE));
                    }
                })
                .map(new Func1<OnLineMember, List<OnlineMember>>() {
                    @Override
                    public List<OnlineMember> call(OnLineMember onLineMember) {
                        if (onLineMember != null && onLineMember.getChatMemberList() != null) {
                            ArrayList<OnlineMember> list = new ArrayList<>();
                            for (ChatMember member : onLineMember.getChatMemberList()) {
                                OnlineMember onlineMember = new OnlineStateMember(member);
                                list.add(onlineMember);
                            }
                            if (isLiveUer) {
                                List<NoTalkUser> noTalkUserList = chatRoom.getNoTalkMember();
                                if (noTalkUserList != null && noTalkUserList.size() > 0) {
                                    for (NoTalkUser noTalkUser : noTalkUserList) {
                                        for (OnlineMember member : list) {
                                            if (member.getMemberId() == noTalkUser.getUserId()) {
                                                member.setNoTalk(true);
                                                continue;
                                            }
                                        }
                                    }
                                }
                            } else {
                                setConcernInfo(list);
                            }
                            return list;
                        }
                        return new ArrayList<OnlineMember>();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OnlineMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        emptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<OnlineMember> onlineMembers) {
                        if (adapter != null) {
                            adapter.update(onlineMembers, page > 1);
                        }
                        emptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void setConcernInfo(ArrayList<OnlineMember> list) {
        if (AppManager.getInstance().getIApp().isLogin()) {
            String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                    "/public/users/current/followed/";
            String ids = "";
            StringBuffer sb = new StringBuffer();
            for (OnlineMember member : list) {
                sb.append(member.getMemberId() + ",");
            }
            ids = sb.toString();
            ids = ids.substring(0, ids.length() - 1);
            Log.e("TAG", "ids == " + ids);
            url += ids;

            String res = HttpUtil.executeGet(url, new HttpParameters(),
                    AppManager.getInstance().getIApp().getCurrentToken());
            try {
                JSONObject json = new JSONObject(res);
                for (OnlineMember member : list) {
                    JSONObject concernJson = json.optJSONObject("" + member.getMemberId());
                    boolean isConcern = concernJson != null ?
                            concernJson.optBoolean("followed") : false;
                    member.setConcern(isConcern);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new OnlineMemberAdapter(context);
        listView.setAdapter(adapter);
    }

    private void onNoTalkClick(int position) {
        final OnlineMember member = getOnlineMember(position);
        if (member != null && !member.isNoTalk()) {
            chatRoom.noTalk(member.getMemberId(), 10, new ICallBack<Boolean>() {
                @Override
                public void callBack(Boolean data) {
                    member.setNoTalk(data);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void onLetOutClick(int position) {
        final OnlineMember member = getOnlineMember(position);
        if (member != null) {
            chatRoom.banUser(member.getMemberId(), true,
                    new ICallBack<Boolean>() {
                        @Override
                        public void callBack(Boolean data) {
                            if (data) {
                                adapter.getData().remove(member);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void onAddConcernClick(int position) {
        if (!loginCheck.checkLogin()) {
            return;
        }
        final OnlineMember member = getOnlineMember(position);
        if (member != null) {
            if (channelManager == null) {
                channelManager = new LiveChannelManager(act);
            }
            channelManager.addConcern(member.getMemberId(), new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    member.setConcern(true);
                    RXBusUtil.sendConcernChangeMessage(true, 1);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFail(ApiException e) {
                    Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private OnlineMember getOnlineMember(int position) {
        List<OnlineMember> list = adapter.getData();
        if (list != null && position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("目前还没有人");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }


    class OnlineMemberAdapter extends BaseListViewAdapter<OnlineMember> {

        public OnlineMemberAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_online_member_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            CircleButton logo = holder.getView(R.id.user_logo);
            TextView userName = holder.getView(R.id.user_name);
            TextView userId = holder.getView(R.id.user_id);
            ImageView addConcern = holder.getView(R.id.concern_image);
            ImageView noTalkView = holder.getView(R.id.item_no_talk);
            ImageView letOutView = holder.getView(R.id.item_let_out);
            View letOutLayout = holder.getView(R.id.no_talk_and_let_out_layout);

            if (isLiveUer) {
                letOutLayout.setVisibility(View.VISIBLE);
                addConcern.setVisibility(View.GONE);
            } else {
                letOutLayout.setVisibility(View.GONE);
                addConcern.setVisibility(View.VISIBLE);
            }
            OnlineMember onlineMember = list.get(position);
            LSLiveUtils.showUserLogoImage(context, logo, onlineMember.getMemberLogo());
            userName.setText(onlineMember.getMemberShowName());
            userId.setText("ID:" + onlineMember.getMemberId());
            int noTalkRes = onlineMember.isNoTalk() ? R.drawable.icon_no_talk_no :
                    R.drawable.icon_no_talk;
            noTalkView.setImageResource(noTalkRes);

            noTalkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNoTalkClick(position);
                }
            });

            letOutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLetOutClick(position);
                }
            });

            int concernRes = onlineMember.isConcern() ? R.drawable.icon_live_member_add_concern_ok :
                    R.drawable.icon_live_member_add_concern;
            addConcern.setImageResource(concernRes);
            addConcern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddConcernClick(position);
                }
            });
        }
    }

    interface OnlineMember {
        String getMemberShowName();

        long getMemberId();

        String getMemberLogo();

        boolean isNoTalk();

        void setNoTalk(boolean isNoTalk);

        boolean isConcern();

        void setConcern(boolean isConcern);
    }

    class OnlineStateMember implements OnlineMember {

        private ChatMember chatMember;

        private boolean isNoTalk;
        private boolean isConcern;

        public OnlineStateMember() {

        }

        public OnlineStateMember(ChatMember chatMember) {
            this.chatMember = chatMember;
        }

        @Override
        public String getMemberShowName() {
            return chatMember != null ? chatMember.getNickName() : "";
        }

        @Override
        public long getMemberId() {
            return chatMember != null ? chatMember.getUserId() : 0;
        }

        @Override
        public String getMemberLogo() {
            return chatMember != null ? chatMember.getLogo() : "";
        }

        @Override
        public boolean isNoTalk() {
            return isNoTalk;
        }

        @Override
        public void setNoTalk(boolean isNoTalk) {
            this.isNoTalk = isNoTalk;
        }

        @Override
        public boolean isConcern() {
            return isConcern;
        }

        @Override
        public void setConcern(boolean isConcern) {
            this.isConcern = isConcern;
        }
    }
}
