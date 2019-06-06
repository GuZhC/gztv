package com.dfsx.lzcms.liveroom.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.img.GlideRoundTransform;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.business.HttpChatRoomHelper;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.IChatRoom;
import com.dfsx.lzcms.liveroom.model.NoTalkUser;
import com.dfsx.lzcms.liveroom.model.RoomPerson;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class RoomMemberControlFragment extends AbsListFragment {
    public static final String KEY_INTENT_SHOW_ID = "RoomMemberControlFragment_show_id";
    public static final String KEY_INTENT_ROOM_ENTER_ID = "RoomMemberControlFragment_room_enter_id";
    private EmptyView emptyView;
    private RoomMemberControlAdapter adapter;

    private long showId;
    private String roomEnterId;

    private IChatRoom chatRoom;

    private CustomeProgressDialog dialog;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            showId = bundle.getLong(KEY_INTENT_SHOW_ID, 0L);
            roomEnterId = bundle.getString(KEY_INTENT_ROOM_ENTER_ID, "");
        }
        super.onViewCreated(view, savedInstanceState);

        chatRoom = new HttpChatRoomHelper(context, showId, roomEnterId);
        getData();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData();
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new RoomMemberControlAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("目前还没有信息");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    private void getData() {
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, List<RoomPerson>>() {
                    @Override
                    public List<RoomPerson> call(Object o) {
                        List<NoTalkUser> noTalkUserList = chatRoom.getNoTalkMember();
                        List<RoomPerson> banUserList = chatRoom.getBanUserListSync();
                        List<RoomPerson> list = new ArrayList<>();
                        if (banUserList != null && banUserList.size() > 0) {
                            list.addAll(banUserList);
                        }
                        if (noTalkUserList != null && !noTalkUserList.isEmpty()) {
                            for (NoTalkUser user : noTalkUserList) {
                                list.add(user);
                            }
                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RoomPerson>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        emptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<RoomPerson> roomPeople) {
                        adapter.update(roomPeople, false);
                        emptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void onItemActionClick(RoomPerson data) {
        boolean isNoTalk = data instanceof NoTalkUser;
        dialog = CustomeProgressDialog.show(context, "加载中...");
        if (isNoTalk) {
            chatRoom.noTalk(data.getUserId(), 0, new ActionCallBack(data));
        } else {
            chatRoom.banUser(data.getUserId(), false, new ActionCallBack(data));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private class ActionCallBack implements ICallBack<Boolean> {
        private RoomPerson person;

        public ActionCallBack(RoomPerson person) {
            this.person = person;
        }

        @Override
        public void callBack(Boolean data) {
            if (data) {
                if (adapter != null && adapter.getData() != null) {
                    boolean remove = adapter.getData().remove(person);
                    Log.e("TAG", "remove ==== " + remove + "size == " + adapter.getData().size());
                    if (remove) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(adapter.getData(), false);
                            }
                        }, 100);
                    }
                }
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    class RoomMemberControlAdapter extends BaseGridListAdapter<RoomPerson> {

        public RoomMemberControlAdapter(Context context) {
            super(context);
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        protected int getHDivideLineWidth() {
            return 0;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_room_member_control_layout;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, RoomPerson data) {
            ImageView userLogoImage = hodler.getView(R.id.user_logo_image);
            TextView userNameText = hodler.getView(R.id.user_name_text);
            TextView userIDText = hodler.getView(R.id.user_id_text);
            ImageView userActionImage = hodler.getView(R.id.item_action_image);

            Glide.with(context)
                    .load(data.getLogo())
                    .transform(new CenterCrop(context), new GlideRoundTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(userLogoImage);
            userNameText.setText(data.getNickName());
            userIDText.setText("ID:" + data.getUserId());
            boolean isNoTlak = data instanceof NoTalkUser;
            int actionRes = isNoTlak ? R.drawable.icon_text_cancel_no_talk :
                    R.drawable.icon_text_cancel_let_out;
            userActionImage.setImageResource(actionRes);
            userActionImage.setTag(data);
            userActionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemActionClick((RoomPerson) v.getTag());
                }
            });

        }
    }
}
