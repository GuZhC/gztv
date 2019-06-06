package com.dfsx.ganzcms.app.push;

import android.content.Context;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;

/**
 * Created by liuwb on 2017/5/5.
 */
public class NotificationMessageStartManager {

    private static NotificationMessageStartManager instance = new NotificationMessageStartManager();

    private OnMessageClickEvent messageClickEvent;
    private Message message;
    private boolean isMainTabActivityIsLive;

    private NotificationMessageStartManager() {

    }

    public static NotificationMessageStartManager getInstance() {
        return instance;
    }

    public void startApp(Message message) {
        this.message = message;
        if (isMainTabActivityIsLive) {//标记主页是否是活着的
            //如果存在主页就立马启动消息页面。如果不存在主頁。那等待主頁啟動完毕之后启动
            startMessageAct(App.getInstance().getApplicationContext());
        }
        //这里本来要启动APP的.单由于这个广播是从APP里面操作的。启动的Application。自然就启动了首页
    }

    public boolean isMainTabLive() {
        return isMainTabActivityIsLive;
    }

    public void startMessageAct(Context context) {
        if (message != null) {
            if (messageClickEvent == null) {
                messageClickEvent = new OnMessageClickEvent(context,
                        new IPushMessageClickEvent.DefaultPushMessageClickEvent());
            }
            messageClickEvent.onMessageClick(message);
        }
        message = null;
    }

    public void setMainTabActivityIsLive(boolean isLive) {
        this.isMainTabActivityIsLive = isLive;
    }

}