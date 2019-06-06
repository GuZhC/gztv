package com.dfsx.thirdloginandshare.share;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.thirdloginandshare.login.WXUtil;

/**
 * Created by Administrator on 2015/12/24.
 */
public class WXShare extends AbsShare{

    private boolean isWeChatMoment;

    public WXShare(Context context, boolean isWeChatMoment) {
        super(context);
        this.isWeChatMoment = isWeChatMoment;
    }

    @Override
    public void share(ShareContent content) {
        if(content != null) {
            switch (content.type) {
                case Video:
                    WXUtil.sendVideo(context, content.getUrl(),
                            content.getTitle(), content.getContent(),
                            content.getPicUrl(), isWeChatMoment);
                    break;
                case WebPage:
                    WXUtil.sendImageAndText(context, content.getUrl(),
                            content.getTitle(), content.getContent(),
                            content.getPicUrl(), isWeChatMoment);
                    break;
                case Image:
                    WXUtil.sendNetImg(context, content.getTitle(),
                            content.getPicUrl(), isWeChatMoment);
                    break;

            }
        }
    }
}
