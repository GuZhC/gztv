这个module是基于Core module开发的。功能是封装常用的第三方库。目前集成了微信，微博，
QQ这三个平台。

集成需要做一下几点：
1.修改Constant文件的平台ID等信息
2.写一个微信的回调Activity，他要集成BaseWXEntryActivity
3.注册一个Activity接受微信回调启动页面。发送方式为
Intent intentLogin = new Intent(Intent.ACTION_VIEW);
                    String url = "app://third_login/";
                    intentLogin.setData(Uri.parse(url));
例如像如下方式注册。 原因是： 由于在微信登录的时候，会强制关闭启动微信的页面，当微信完成功能的时候需要回到启动的页面
<activity
                android:name="com.dfsx.honghecms.app.act.LoginActivity"
                android:screenOrientation="portrait"
                android:launchMode="singleTop"
                android:configChanges="keyboardHidden|orientation|screenSize|screenLayout">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>

                <category android:name="android.intent.category.BROWSABLE"/>
                <category android:name="android.intent.category.DEFAULT"/>

                <data
                        android:scheme="app"
                        android:host="third_login"/>
            </intent-filter>
        </activity>
4.修改manifest

分享：
1、使用默认的分享界面分享
ShareUtil.share(Context context, ShareContent content);
2、单个平台分享代码
AbsShare share = ShareFactory.createShare(Context context, SharePlatform platform);
share.share(ShareContent content);

登录：
ThirdLoginFactory.createThirdLogin(this, AbsThirdLogin.Weixin, new AbsThirdLogin.OnThirdLoginListener() {
            @Override
            public void onThirdLoginCompelete(String openId, int oauthtype) {
                授权成功的回调
            }

            @Override
            public void onThirdLoginError(int oauthtype) {
                授权失败的回调
            }
        }).login();

授权之后，会获取用户信息并存储到SharePerference，可以使用
AbsThirdLogin.getThirdPartyInfo(Context context, String oauthId,int oauthtype)
来获取之前登录者的用户信息.