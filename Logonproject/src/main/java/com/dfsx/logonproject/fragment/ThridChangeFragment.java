package com.dfsx.logonproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.busniness.ThirdLoginCalbackHelper2;
import com.dfsx.thirdloginandshare.login.*;
import com.tencent.open.t.Weibo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang 2018/8/10
 */

public class ThridChangeFragment extends BaseResultFragment implements AbsThirdLogin.OnThirdLoginListener {
    private View rootView;
    private ImageView topBarLeft;
    private TextView wexinName, qqName, weboName;
    private ImageView weixinBtn, qqBtn, weiBoBtn;

    private AccountApi accountApi;
    private boolean isChangge = false;
    private AbsThirdLogin thirdLogin;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == topBarLeft) {
                onRetrun();
            }
        }
    };

    public void onRetrun() {
        String param = "";
        if (isChangge) {
            param = "122";
            Log.e("TAG", "ischange");
        }
        finishActivityWithResult("ischang", param, 16);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.third_change_fragment, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof WhiteTopBarActivity) {
            topBarLeft = ((WhiteTopBarActivity) getActivity()).getTopBarLeft();
            topBarLeft.setOnClickListener(listener);
        }
        accountApi = new AccountApi(getActivity());
        initViews(view);
        initThirdLogin();
        initParams();
    }

    public void initViews(View view) {
        wexinName = (TextView) view.findViewById(R.id.th_weixin_name);
        weixinBtn = (ImageView) view.findViewById(R.id.th_weixin_oper);
        qqName = (TextView) view.findViewById(R.id.th_qq_name);
        qqBtn = (ImageView) view.findViewById(R.id.th_qq_oper);
        weboName = (TextView) view.findViewById(R.id.th_weibo_name);
        weiBoBtn = (ImageView) view.findViewById(R.id.th_weibo_oper);
        weixinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBind = (int) v.getTag() == 0 ? false : true;
                if (isBind) {
                    //解绑
                    onRemoveAuthor(weixinBtn, wexinName, 1);
                } else {
                    thirdLogin = ThirdLoginFactory.createThirdLogin(getActivity(), AbsThirdLogin.Weixin, ThirdLoginCalbackHelper2.getInstance());
                    thirdLogin.login();
                }
            }
        });
        weiBoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBind = (int) v.getTag() == 0 ? false : true;
                if (isBind) {
                    //解绑
                    onRemoveAuthor(weiBoBtn, weboName, 2);
                } else {
                    thirdLogin = ThirdLoginFactory.createThirdLogin(getActivity(), AbsThirdLogin.Sinaweibo, ThirdLoginCalbackHelper2.getInstance());
                    thirdLogin.login();
                }
            }
        });
        qqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBind = (int) v.getTag() == 0 ? false : true;
                if (isBind) {
                    //解绑
                    onRemoveAuthor(qqBtn, qqName, 3);
                } else {
                    thirdLogin = ThirdLoginFactory.createThirdLogin(getActivity(), AbsThirdLogin.Qq, ThirdLoginCalbackHelper2.getInstance());
                    thirdLogin.login();
                }
            }
        });
    }

    public void onRemoveAuthor(final ImageView button, final TextView txtView, final int type) {
        isChangge = true;
        try {
            accountApi.removeThirdAuthor(type, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    ToastUtils.toastMsgFunction(getActivity(), "解除成功");
                    setButtonStatus(button, txtView, "", false);
                    updateAuthor(type, "", false);
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    ToastUtils.toastApiexceFunction(getActivity(), e);
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    public void setButtonStatus(ImageView button, TextView txtView, String name, boolean isBind) {
        txtView.setText(name);
        if (isBind) {
            button.setImageResource(R.drawable.third_cancel_btn);
            button.setTag(1);
        } else {
            button.setImageResource(R.drawable.third_add_btn);
            button.setTag(0);
        }
    }

    public void initParams() {
        resetValues();
        Account account = CoreApp.getInstance().getUser();
        if (account != null) {
            if (account.getUser() != null) {
                List<Account.AuthorThrid> list = account.getAthorList();
                if (!(list == null || list.isEmpty())) {
                    for (Account.AuthorThrid third : list) {
                        if (third.getType() == 1) {
                            // weixin
                            setButtonStatus(weixinBtn, wexinName, third.getNickName(), true);
                        } else if (third.getType() == 2) {
                            // weibo
                            setButtonStatus(weiBoBtn, weboName, third.getNickName(), true);
                        } else {
                            setButtonStatus(qqBtn, qqName, third.getNickName(), true);
                        }
                    }
                }
            }
        }
    }

    public void resetValues() {
        weixinBtn.setImageResource(R.drawable.third_add_btn);
        weixinBtn.setTag(0);
        weiBoBtn.setImageResource(R.drawable.third_add_btn);
        weiBoBtn.setTag(0);
        qqBtn.setImageResource(R.drawable.third_add_btn);
        qqBtn.setTag(0);
    }

    @Override
    public void onThirdLoginCompelete(String access_token, String openId, int oauthtype) {
        if (thirdLogin != null) {
            thirdLogin.onDestory();
        }//结束的时候调用登录流程完毕，防止内存泄露哦
//        doThirdLogin(access_token, openId, oauthtype);
        getThirdTypeString(oauthtype, access_token);
    }

    @Override
    public void onThirdLoginError(int oauthtype) {
        if (oauthtype == AbsThirdLogin.Qq) {
            ToastUtils.toastMsgFunction(getActivity(), "获取qq授权失败");
        } else if (oauthtype == AbsThirdLogin.Sinaweibo) {
            ToastUtils.toastMsgFunction(getActivity(), "获取微博授权失败");
        } else {
            ToastUtils.toastMsgFunction(getActivity(), "获取微信授权失败");
        }
    }

    public void getThirdTypeString(int type, String token) {
        if (type == AbsThirdLogin.Qq) {
            bind(2, token);
        } else if (type == AbsThirdLogin.Sinaweibo) {
            bind(1, token);
        } else {
            bind(0, token);
        }
    }

    String[] bindOkStr = {"绑定微信成功", "绑定微博成功", "绑定qq成功"};
    String[] bindErrorStr = {"绑定微信失败", "绑定微博失败", "绑定qq失败"};

    public String getResultMsg(boolean isOK, int pos) {
        return isOK ? bindOkStr[pos] : bindErrorStr[pos];
    }

    public void bind(final int type, String token) {
        isChangge = true;
        try {
            accountApi.bindThirdAuthor(type, token, new DataRequest.DataCallback<String>() {
                @Override
                public void onSuccess(boolean isAppend, String data) {
                    if (!TextUtils.isEmpty(data)) {
                        ToastUtils.toastMsgFunction(getActivity(), getResultMsg(true, type));
                        if (type == 0) {
                            setButtonStatus(weixinBtn, wexinName, data, true);
                        } else if (type == 1) {
                            setButtonStatus(weiBoBtn, weboName, data, true);
                        } else
                            setButtonStatus(qqBtn, qqName, data, true);
                        int tagType = type + 1;
                        updateAuthor(tagType, data, true);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    ToastUtils.toastApiexceFunction(getActivity(), e);
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public void updateAuthor(int type, String name, boolean isBind) {
        Account account = CoreApp.getInstance().getUser();
        if (account != null) {
            if (account.getUser() != null) {
                List<Account.AuthorThrid> list = account.getAthorList();
                if (isBind) {
                    Account.AuthorThrid third = new Account.AuthorThrid();
                    third.setType(type);
                    third.setNickName(name);
                    if (list == null) list = new ArrayList<>();
                    list.add(third);
                } else {
                    if (!(list == null || list.isEmpty())) {
                        for (Account.AuthorThrid third : list) {
                            if (third.getType() == type) {
                                list.remove(third);
                                break;
                            }
                        }
                    }
                }
                CoreApp.getInstance().setCurrentAccount(account);
            }
        }
    }

    private void initThirdLogin() {
        ThirdLoginCalbackHelper2.getInstance().addOnThirdLoginListener(this);
        if (ThirdLoginCalbackHelper2.getInstance().isAvailableCallBack()) {
            boolean isError = ThirdLoginCalbackHelper2.getInstance().isError();
            if (isError) {
                ToastUtils.toastMsgFunction(getActivity(), "授权失败");
            } else {
                getThirdTypeString(ThirdLoginCalbackHelper2.getInstance().getThirdType(), ThirdLoginCalbackHelper2.getInstance().getAccessToken());
            }
        }
    }


    @Override
    public void onDestroy() {
        ThirdLoginCalbackHelper2.getInstance().removeOnThirdLoginListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        onRetrun();
        return false;
    }

}
