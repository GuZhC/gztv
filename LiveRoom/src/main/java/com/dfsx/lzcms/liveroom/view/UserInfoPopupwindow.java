package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.model.ChatMember;
import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;

/**
 * 之间的人物信息的展示页面
 * Created by liuwb on 2017/3/10.
 */
public class UserInfoPopupwindow extends AbsPopupwindow implements View.OnClickListener {

    private View _allView;

    private CircleButton _userLogo;
    private TextView _userName;
    private TextView _userId;
    private TextView _userSign;
    private TextView _userConcernNum;
    private TextView _userLoverNum;
    private Button _btnAddConcern;

    private View _noTalkView;
    private View _letOutView;
    private View _userPageView;
    private View _userReportView;

    private View _closeView;

    private IOnEventClickListener _listener;

    private DefaultUserInfoEventAction _eventAction;

    private long _roomId;
    private String _roomEnterId;

    public UserInfoPopupwindow(Context context) {
        super(context);
        _eventAction = new DefaultUserInfoEventAction();
    }

    @Override
    public int getPopupwindowLayoutId() {
        return R.layout.user_info_layout;
    }

    @Override
    public void onInitWindowView(View popView) {
        _allView = popView.findViewById(R.id.all_view);
        _userLogo = (CircleButton) popView.findViewById(R.id.user_logo);
        _userName = (TextView) popView.findViewById(R.id.user_name);
        _userId = (TextView) popView.findViewById(R.id.user_id);
        _userSign = (TextView) popView.findViewById(R.id.user_signature);
        _userConcernNum = (TextView) popView.findViewById(R.id.user_concern_num);
        _userLoverNum = (TextView) popView.findViewById(R.id.user_lover_num);
        _btnAddConcern = (Button) popView.findViewById(R.id.btn_user_concern);
        _noTalkView = popView.findViewById(R.id.no_talk_view);
        _letOutView = popView.findViewById(R.id.let_out_view);
        _userPageView = popView.findViewById(R.id.user_page_view);
        _closeView = popView.findViewById(R.id.close_pop);
        _userReportView = popView.findViewById(R.id.user_report_view);

        _noTalkView.setOnClickListener(this);
        _letOutView.setOnClickListener(this);
        _userPageView.setOnClickListener(this);
        _btnAddConcern.setOnClickListener(this);
        _userReportView.setOnClickListener(this);
        _closeView.setOnClickListener(this);
        _allView.setOnClickListener(this);

        setVisitorView(false);
    }

    public void setUserRoomId(long roomId, String roomEnterId) {
        this._roomId = roomId;
        this._roomEnterId = roomEnterId;
    }

    public void setUserInfo(ChatMember chatMember) {
        setTag(chatMember);
        setViewData(chatMember);
    }

    private void setConcernVisiable(long userId) {
        if (userId == AppManager.getInstance().getIApp().getLoginUserId()) {
            _btnAddConcern.setVisibility(View.GONE);
            return;
        }
        new LiveChannelManager(context)
                .isConcern(userId, new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        _btnAddConcern.setVisibility(data ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        _btnAddConcern.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setViewData(final ChatMember chatMember) {
        if (chatMember != null) {
            LSLiveUtils.showUserLogoImage(context, _userLogo, chatMember.getLogo());
            _userName.setText(chatMember.getNickName());
            _userId.setText("ID: " + chatMember.getUserId());
            if (chatMember.getUserDetailsInfo() == null) {
                new UserInfoHttpHelper().getUserDetailsAsync(chatMember.getUserId(), new ICallBack<UserDetailsInfo>() {
                    @Override
                    public void callBack(UserDetailsInfo data) {
                        chatMember.setUserDetailsInfo(data);
                        setUserDetailsDataView(data);
                    }
                });
            } else {
                setUserDetailsDataView(chatMember.getUserDetailsInfo());
            }

            setConcernVisiable(chatMember.getUserId());
        }
    }

    private void setUserDetailsDataView(UserDetailsInfo detailsData) {
        if (detailsData != null && (detailsData.getId() != 0 ||
                !TextUtils.isEmpty(detailsData.getUsername()))) {
            String showSignText = TextUtils.isEmpty(detailsData.getSignature()) ?
                    "这人很懒，什么都没留下~" :
                    detailsData.getSignature();
            _userSign.setText(showSignText);

            _userConcernNum.setText(detailsData.getFollow_count() + "");
            _userLoverNum.setText(detailsData.getFan_count() + "");
        }
    }

    public void setVisitorView(boolean isVisitor) {
        if (isVisitor) {
            _letOutView.setEnabled(false);
            _noTalkView.setEnabled(false);
            _userReportView.setEnabled(true);
        } else {
            _letOutView.setEnabled(true);
            _noTalkView.setEnabled(true);
            _userReportView.setEnabled(false);
        }
    }

    private ViewGroup getParentView(View v) {
        return (ViewGroup) v.getParent();
    }

    public void setOnEventClickListener(IOnEventClickListener l) {
        _listener = l;
    }

    @Override
    public void onClick(View v) {
        IOnEventClickListener listener = _listener == null ?
                _eventAction :
                _listener;
        if (listener != null) {
            if (v == _noTalkView) {
                listener.noTalk(tag);
            } else if (v == _letOutView) {
                listener.letOut(tag);
            } else if (v == _userPageView) {
                listener.goUserWebPage(tag);
            } else if (v == _btnAddConcern) {
                listener.addConcern(tag);
            } else if (v == _userReportView) {
                listener.report(tag);
            }

        }
        dismiss();
    }

    public interface IOnEventClickListener {
        void noTalk(Object tag);

        void letOut(Object tag);

        void goUserWebPage(Object tag);

        void addConcern(Object tag);

        void report(Object tag);
    }

    public class DefaultUserInfoEventAction implements IOnEventClickListener {

        private IChatRoom chatRoom;

        @Override
        public void noTalk(Object tag) {
            if (_roomId != 0) {
                if (tag instanceof ChatMember) {
                    ChatMember user = (ChatMember) tag;
                    Log.e("TAG", "user name === " + user.getNickName());
                    if (chatRoom == null) {
                        chatRoom = new HttpChatRoomHelper(context, _roomId, _roomEnterId);
                    }
                    chatRoom.noTalk(user.getUserId(), 10, new ICallBack<Boolean>() {
                        @Override
                        public void callBack(Boolean data) {
                            String text = data ? "已禁言他10分钟" : "禁言失败";
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        @Override
        public void letOut(Object tag) {
            if (_roomId != 0) {
                if (tag instanceof ChatMember) {
                    ChatMember user = (ChatMember) tag;
                    if (chatRoom == null) {
                        chatRoom = new HttpChatRoomHelper(context, _roomId, _roomEnterId);
                    }
                    chatRoom.banUser(user.getUserId(), true, new ICallBack<Boolean>() {
                        @Override
                        public void callBack(Boolean data) {
                            String text = data ? "踢出成功" : "踢出失败";
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        @Override
        public void goUserWebPage(Object tag) {
            if (tag instanceof ChatMember) {
                ChatMember member = (ChatMember) tag;
                IntentUtil.gotoPersonHomeAct(context, member.getUserId());
            }
        }

        @Override
        public void addConcern(Object tag) {
            if (tag instanceof ChatMember) {
                ChatMember chatMember = (ChatMember) tag;
                new LiveChannelManager(context).addConcern(chatMember.getUserId(), new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        _btnAddConcern.setVisibility(data ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "关注失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void report(Object tag) {
            IntentUtil.goReport(context);
        }
    }
}