package com.dfsx.thirdloginandshare.share;

import android.content.Context;
import android.content.Intent;
import com.dfsx.core.CoreApp;
import com.dfsx.thirdloginandshare.R;
import com.sina.weibo.sdk.api.BaseMediaObject;

/**
 * Created by Administrator on 2015/12/28.
 */
public class SinaShare extends AbsShare{

    public SinaShare(Context context) {
        super(context);
    }

    @Override
    public void share(ShareContent content) {
        if(content == null) {
            return;
        }
        Intent intent = new Intent(context, WBShareActivity.class);
        WBShareActivity.SinaShareData shareData = new WBShareActivity.SinaShareData();
        if(content.type == ShareContent.UrlType.WebPage) {
            shareData.shareDataType = BaseMediaObject.MEDIA_TYPE_WEBPAGE;
            shareData.actionUrl = content.getUrl();
            shareData.shareImageUrl = content.getPicUrl();
        }else if(content.type == ShareContent.UrlType.Image){
            shareData.shareDataType = BaseMediaObject.MEDIA_TYPE_IMAGE;
            shareData.shareImageUrl = content.getPicUrl();
        }else if(content.type == ShareContent.UrlType.Video) {
            shareData.shareDataType = BaseMediaObject.MEDIA_TYPE_VIDEO;
            shareData.actionUrl = content.getUrl();
            shareData.shareImageUrl = content.getPicUrl();
            shareData.mediaDataHdUrl = content.getUrl();
            shareData.mediaDataUrl = content.getUrl();
        }else {
            shareData.shareDataType = BaseMediaObject.MEDIA_TYPE_TEXT;
        }
        shareData.shareSummary = content.getContent();
        shareData.defaultText = CoreApp.getInstance().
                getResources().getString(R.string.app_name);
        shareData.mediaTitle = content.getTitle();
        shareData.mediaDescription = "";
        intent.putExtra(WBShareActivity.KEY_SHARE_DATA, shareData);
        context.startActivity(intent);
    }
}
