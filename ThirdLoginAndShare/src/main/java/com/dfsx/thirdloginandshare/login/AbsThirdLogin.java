package com.dfsx.thirdloginandshare.login;

import android.content.Context;
import android.content.SharedPreferences;
import com.dfsx.thirdloginandshare.Constants;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/24.
 */
public abstract class AbsThirdLogin {

    public static final String KEY_AUTH_DATA = "key_auth_data";
    public static final String ACTION_WX_AUTH = "com.tixa.plugin.login.thirdLogin_WX";

    //oauthtype 的类型
    public static final int Sinaweibo = 1;
    public static final int Qq = 2;
    public static final int Weixin = 11;

    public static final String SP_THIRD_LOGIN_INFO = "third_login_info";

    protected OnThirdLoginListener thirdLoginListener;
    protected Context context;

    public AbsThirdLogin(Context context, OnThirdLoginListener thirdLoginListener) {
        this.thirdLoginListener = thirdLoginListener;
        this.context = context;
    }

    public abstract void login();

    public void saveOnCompleteString(String openId, String value, int oauthtype) {
        SharedPreferences sp = context.getSharedPreferences(SP_THIRD_LOGIN_INFO, 0);
        if (openId != null && value != null) {
            String SP_KEY = getSPKey(openId, oauthtype);
            sp.edit().putString(SP_KEY, value).commit();
        }
    }

    public static HashMap<String, String> getThirdPartyInfo(Context context, String oauthId,
                                                            int oauthtype) {
        SharedPreferences sp = context.
                getSharedPreferences(SP_THIRD_LOGIN_INFO, 0);
        String res = sp.getString(getSPKey(oauthId, oauthtype), null);
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            if (res != null) {
                JSONObject jsonObject = new JSONObject(res);
                if (oauthtype == Qq) { //QQ
                    result.put("thirdLogo", jsonObject.optString("figureurl_qq_2"));
                    result.put("thirdName", jsonObject.optString("nickname"));
                    result.put("thirdGender", jsonObject.optString("gender"));
                } else if (oauthtype == Weixin) { //微信
                    if (jsonObject.has("headimgurl")) {
                        String logo = jsonObject.optString("headimgurl");
                        try {
                            if (logo.lastIndexOf("/") > -1) {
                                logo = logo.substring(0, logo.lastIndexOf("/")) + "/132.jpg";
                                result.put("thirdLogo", logo);
                            }
                        } catch (Exception e) {
                            result.put("thirdLogo", logo);
                        }
                    }
                    result.put("thirdName", jsonObject.optString("nickname"));

                    if (jsonObject.has("sex")) {
                        int gender = jsonObject.optInt("sex");
                        if (gender != Constants.GENDER_GIRL) {
                            gender = Constants.GENDER_BOY;
                        }
                        result.put("thirdGender", String.valueOf(gender));
                    }
                } else if (oauthtype == Sinaweibo) {//新浪微博
                    result.put("thirdLogo", jsonObject.optString("avatar_hd"));
                    result.put("thirdName", jsonObject.optString("name"));
                    result.put("thirdGender", jsonObject.optString("gender"));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }



    public interface OnThirdLoginListener {
        void onThirdLoginCompelete(String access_token, String openId, int oauthtype);

        void onThirdLoginError(int oauthtype);
    }

    public static String getSPKey(String oauthId, int oauthtype) {
        return oauthId + "_" + oauthtype;
    }

    public abstract void onDestory();
}
