package com.dfsx.logonproject.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.login.AbsThirdLogin;
import com.dfsx.thirdloginandshare.login.ThirdLoginFactory;


/**
 * Created by heyang on 2016/10-27
 */
public class LogonContancts  {
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IS_SAVE_ACCOUNT = "isSaveAccount";
    public static final String KEY_IS_TP_ACCOUNT_SAVED = "isTPAccountSaved";
    public static final String KEY_ACCESS_TOKEN = "token";
    public static final String KEY_OPEN_ID = "openid";
    public static final String KEY_TP_TYPE = "type";
    public static final String KEY_TP_APP_ID = "appid";
    public static final String KEY_ACCOUNT_INFO = "Account_Info";
}
