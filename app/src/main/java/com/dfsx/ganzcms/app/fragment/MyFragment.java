package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;

import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.logonproject.fragment.PersonInforFragment;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;
import com.dfsx.ganzcms.app.act.DeepColorTopbarActivity;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.view.TwoRelyView;

import com.dfsx.lzcms.liveroom.util.ConcernChanegeInfo;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by liuwb on 2016/9/12.
 */
public class MyFragment extends Fragment {

    private CircleButton userLogo;
    private LinearLayout topViewContainer;
    private ImageView noLoginLogoView;
    private TextView loginText;
    private TextView userName, userId;
    private TextView userSignatureText;
    private View myVideoView, myTopicView, myLiveView;

    private View noLoginView;

    private View loginedView;

    private TwoRelyView myWallet, addMoney, payRecord, myStore, myAttention, myFans;

    private View myPersonShareView;
    private View myNotificationView;
    private View myAccountSettings;


    private Subscription loginOkSubscription;
    private Subscription favritySubscription;
    private Subscription concernSubscription;
    boolean isLogin = false;

    private MyDataManager dataManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_my_layout, null);
        dataManager = new MyDataManager(getActivity());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setTopLayout();
        initAction();
        initRegister();
    }

    private void initView(View view) {
        topViewContainer = (LinearLayout) view.findViewById(R.id.my_top_layout);
        noLoginView = LayoutInflater.from(getContext()).
                inflate(R.layout.no_login_layout, null);
        loginedView = LayoutInflater.from(getContext()).
                inflate(R.layout.login_logo_layout, null);
        userName = (TextView) loginedView.findViewById(R.id.user_name);
        userId = (TextView) loginedView.findViewById(R.id.user_id);
        userSignatureText = (TextView) loginedView.findViewById(R.id.user_signature_text);
        userLogo = (CircleButton) loginedView.findViewById(R.id.user_logo);
        myStore = (TwoRelyView) loginedView.findViewById(R.id.my_store);
        myAttention = (TwoRelyView) loginedView.findViewById(R.id.my_attention);
        myFans = (TwoRelyView) loginedView.findViewById(R.id.my_fans);
        noLoginLogoView = (ImageView) noLoginView.findViewById(R.id.user_logo_no_login);
        loginText = (TextView) noLoginView.findViewById(R.id.no_login_text);
        myVideoView = view.findViewById(R.id.video_layout);
        myTopicView = view.findViewById(R.id.topic_layout);
        myLiveView = view.findViewById(R.id.my_live_layout);
        myNotificationView = view.findViewById(R.id.my_notification);
        myPersonShareView = view.findViewById(R.id.my_share_id_card);
        myAccountSettings = view.findViewById(R.id.my_setting);

        myWallet = (TwoRelyView) view.findViewById(R.id.my_wallet);
        addMoney = (TwoRelyView) view.findViewById(R.id.add_money);
        payRecord = (TwoRelyView) view.findViewById(R.id.pay_record);
    }

    private void initRegister() {
        loginOkSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OK)) {
                            setTopLayout();
                        }
                    }
                });

        if (favritySubscription == null) {
            favritySubscription = RxBus.getInstance().
                    toObserverable(Intent.class).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Intent>() {
                        @Override
                        public void call(Intent intent) {
                            if (intent.getAction().equals(IntentUtil.UPDATE_FAVIRITY_MSG)) {
                                setMyStoreTextNum();
                            }
                        }
                    });
        }
        concernSubscription = RxBus.getInstance()
                .toObserverable(ConcernChanegeInfo.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConcernChanegeInfo>() {
                    @Override
                    public void call(ConcernChanegeInfo chanegeInfo) {
                        if (chanegeInfo == null) {
                            return;
                        }
                        String numStr = myAttention.getFirstView().getText().toString();
                        int num = TextUtils.isDigitsOnly(numStr) ? Integer.valueOf(numStr) : 0;
                        num = num + (chanegeInfo.isAdd() ? 1 : -1) * chanegeInfo.getChangeNum();
                        if (num < 0) {
                            num = 0;
                        }
                        myAttention.getFirstView().setText(num + "");
                    }
                });
    }

    private void setTopLayout() {
        isLogin = App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;

        topViewContainer.removeAllViews();
        if (isLogin) {
            topViewContainer.addView(loginedView);
            userId.setText("ID: " + App.getInstance().getUser().getUser().getId());
            String signText = App.getInstance().getUser().getUser().getSignature();
            if (TextUtils.isEmpty(signText)) {
                signText = "";
            }
            userSignatureText.setText(signText);
            userName.setText(App.getInstance().getUser().getUser().getUsername());
            if (!TextUtils.isEmpty(App.getInstance().getUser().
                    getUser().getAvatar_url())) {
                GlideImgManager.getInstance().showImg(getActivity(), userLogo,
                        App.getInstance().getUser().getUser().getAvatar_url());
                userLogo.setTag(R.id.cirbutton_user_id, App.getInstance().getUser().getUser().getId());
            } else {
                userLogo.setImageResource(R.drawable.icon_default_logo);
            }
            setMyFallowTextNum();
            setMyFansTextNum();
            setMyStoreTextNum();
        } else {
            topViewContainer.addView(noLoginView);
        }
    }

    private void initAction() {
        userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.gotoPersonHomeAct(getActivity(), App.getInstance().getUser().getUser().getId());
            }
        });

        noLoginLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        myVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //heyang 2016-11-7
                WhiteTopBarActivity.startAct(getContext(), MineVideoFragment.class.getName(),
                        "我的视频", " ");
                //                WhiteTopBarActivity.startAct(getContext(), MyRecVideoFragment.class.getName(),
                //                        "我的视频", "清除全部");
            }
        });
        myTopicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //heyang  2016-11-7
                WhiteTopBarActivity.startAct(getContext(), CommunityRecycleUpFragment.class.getName(),
                        "我的话题", "清除全部", -1);
            }
        });

        loginedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.gotoPersonHomeAct(getActivity(), App.getInstance().getUser().getUser().getId());
            }
        });

        myLiveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getContext(),
                        UserLiveFragment.class.getName(), "我的直播");
            }
        });

        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                WhiteTopBarActivity.startAct(getContext(), MyWalletFragment.class.getName(),
                //                        "我的钱包");
                DefaultFragmentActivity.start(getContext(), MyWalletFragment.class.getName());
            }
        });

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getContext(), AddMoneyFragment.class.getName(),
                        "充值中心");
            }
        });
        payRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getContext(),
                        PayRecordFragment.class.getName(), "消费记录", "");
            }
        });
        myAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getActivity(), MyAttentionFragment.class.getName(), "我关注的");
            }
        });

        myFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getActivity(), MyFansFragment.class.getName(), "我的粉丝");
            }
        });

        //heyang 2016-11-7
        myStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                WhiteTopBarActivity.startAct(getActivity(), HeadLineFragment.class.getName(), "收藏", "清除全部", 12);
                WhiteTopBarActivity.startAct(getActivity(), MyfavorityFragment.class.getName(), "收藏", " ");
            }
        });
        //wen 2017.3.15
        myPersonShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    DeepColorTopbarActivity.start(getActivity(), ShareCard.class.getName(), "分享名片", null);
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //wen 2017.3.15
        myNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {

                    ArrayList<String> logList = new ArrayList<>();
                    logList.add("我关注的");
                    logList.add("系统消息");
                    DeepColorSwitchTopbarActivity.start(getActivity(), MyMessageFragment.class.getName(),
                            logList, R.drawable.icon_topbar_delete);
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        myAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    DefaultFragmentActivity.start(getActivity(), PersonInforFragment.class.getName());
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //    public void getUserInfo(int uId) {
    //        UserInfoHelper.getUserInfo(getContext(), uId, new DataRequest.DataCallback<Account>() {
    //            @Override
    //            public void onSuccess(boolean isAppend, Account data) {
    //                if (data != null) {
    //                    if (!TextUtils.isEmpty(data.getUser().getAvatar_url())) {
    //                        GlideImgManager.getInstance().showImg(getActivity(), userLogo, data.getUser().getAvatar_url());
    //                    }
    //                    userId.setText(data.getUser().getId() + "");
    //                    userName.setText(data.getUser().getUsername());
    //                }
    //            }
    //
    //            @Override
    //            public void onFail(ApiException e) {
    //
    //            }
    //        });
    //    }

    private void setMyStoreTextNum() {
        dataManager.getMyStoreNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                myStore.getFirstView().setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                myStore.getFirstView().setText("0");
            }
        });
    }

    private void setMyFansTextNum() {
        dataManager.getMyFansNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                myFans.getFirstView().setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                myFans.getFirstView().setText("0");
            }
        });
    }

    private void setMyFallowTextNum() {
        dataManager.getMyFollowNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                myAttention.getFirstView().setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                myAttention.getFirstView().setText("0");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginOkSubscription != null) {
            loginOkSubscription.unsubscribe();
        }
        if (favritySubscription != null) {
            favritySubscription.unsubscribe();
        }
        if (concernSubscription != null) {
            concernSubscription.unsubscribe();
        }


    }
}
