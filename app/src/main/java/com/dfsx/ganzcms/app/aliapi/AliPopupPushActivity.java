package com.dfsx.ganzcms.app.aliapi;

import com.dfsx.ganzcms.app.App;
import com.dfsx.push.aliyunpush.BasePopupPushActivity;

public class AliPopupPushActivity extends BasePopupPushActivity {
    @Override
    public boolean isAppAlive() {
        try {
            return App.getInstance().getTopActivity() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
