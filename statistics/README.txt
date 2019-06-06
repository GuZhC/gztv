统计库

此库采用腾讯开放平台统计，当前版本是 mta -3.4.3。修改版本需注意和第三方分享和登录的mta库冲突。

修改配置： 直接修改values/strings.xml

例如:
<string name="app_authorities">com.dfsx.lscms.TENCENT.MID.V3</string> <!--你的包名.TENCENT.MID.V3 -->
<string name="app_key">APSM11BE2Z1M</string>
<string name="app_channel">dfsx</string>