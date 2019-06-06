package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.content.Intent;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.model.UserMoneyInfo;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 当前用户的乐币信息管理类
 * Created by liuwb on 2016/12/5.
 */
public class MyMoneyInfoManager {

    private static MyMoneyInfoManager instance = new MyMoneyInfoManager();

    private LiveChannelManager channelManager;

    private UserMoneyInfo myMoneyInfo;

    private MyMoneyInfoManager() {
        initRegister();
    }

    public static MyMoneyInfoManager getInstance() {
        return instance;
    }

    private void initRegister() {
        RxBus.getInstance().toObserverable(Intent.class)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OUT)) {
                            if (myMoneyInfo != null) {
                                myMoneyInfo = null;
                            }
                        }
                    }
                });
    }

    public void refreshMoneyInfo(Context context) {
        getNetData(context, null);
    }

    public void getMoneyInfo(Context context, ICallBack<UserMoneyInfo> callBack) {
        if (myMoneyInfo != null) {
            if (callBack != null) {
                callBack.callBack(myMoneyInfo);
            }
        } else {
            getNetData(context, callBack);
        }
    }

    public UserMoneyInfo getCacheMoneyInfo() {
        return myMoneyInfo;
    }

    /**
     * 支付了乐币
     * <p>
     * 重新计算当前还有多少乐币
     *
     * @param payMoney 消费的金额
     */
    public void payMoney(double payMoney) {
        if (myMoneyInfo != null) {
            myMoneyInfo.setCoins(myMoneyInfo.getCoins() - payMoney);
        }
    }

    /**
     * 直接设置当前还有多少余额
     *
     * @param coin
     */
    public void setMyTotalMoney(double coin) {
        if (myMoneyInfo != null) {
            myMoneyInfo.setCoins(coin);
        }
    }

    private void getNetData(Context context, final ICallBack<UserMoneyInfo> callBack) {
        if (channelManager == null) {
            channelManager = new LiveChannelManager(context);
        }
        channelManager.getMyMoneyInfo(new DataRequest.DataCallback<UserMoneyInfo>() {
            @Override
            public void onSuccess(boolean isAppend, UserMoneyInfo data) {
                myMoneyInfo = data;
                if (callBack != null) {
                    callBack.callBack(data);
                }
            }

            @Override
            public void onFail(ApiException e) {
                myMoneyInfo = null;
                if (callBack != null) {
                    callBack.callBack(null);
                }
            }
        });
    }
}
