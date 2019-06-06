package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.view.View;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.SharePlatform;

/**
 * Created by liuwb on 2017/6/30.
 */
public class LiveServiceSharePopupwindow extends AbsPopupwindow implements View.OnClickListener {
    private View qqShareView;
    private View wxShareView;
    private View friendShareView;
    private View wbShareView;
    private ShareContent shareContent;

    public void setShareItemClickListener2(OnShareItemClickListener2 shareItemClickListener2) {
        this.shareItemClickListener2 = shareItemClickListener2;
    }

    OnShareItemClickListener2 shareItemClickListener2;

    private OnShareItemClickListener itemClickListener;

    public LiveServiceSharePopupwindow(Context context) {
        super(context);
    }

    public LiveServiceSharePopupwindow(Context context, ShareContent sh) {
        super(context);
        shareContent = sh;
    }

    public void setShareContent(ShareContent shareContent) {
        this.shareContent = shareContent;
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.pop_live_service_share;
    }

    @Override
    protected void onInitWindowView(View popView) {

        qqShareView = popView.findViewById(R.id.share_QQ);
        wxShareView = popView.findViewById(R.id.share_wx);
        friendShareView = popView.findViewById(R.id.share_fiends);
        wbShareView = popView.findViewById(R.id.share_wb);

        qqShareView.setOnClickListener(this);
        wxShareView.setOnClickListener(this);
        friendShareView.setOnClickListener(this);
        wbShareView.setOnClickListener(this);

        popView.setOnClickListener(this);
    }

    @Override
    protected void onInitFinish() {
        super.onInitFinish();
    }

    @Override
    public void onClick(View v) {
        SharePlatform platform = null;
        if (v == qqShareView) {
            platform = SharePlatform.QQ;
        } else if (v == wxShareView) {
            platform = SharePlatform.Wechat;
        } else if (v == friendShareView) {
            platform = SharePlatform.Wechat_FRIENDS;
        } else if (v == wbShareView) {
            platform = SharePlatform.WeiBo;
        }

        if (platform != null && itemClickListener != null) {
            itemClickListener.onShareItemClick(platform);
        }
        if (platform != null && shareItemClickListener2 != null) {
            shareItemClickListener2.onShareItemClick(platform, shareContent);
        }
        dismiss();
    }

    public void setOnShareItemClickListener(OnShareItemClickListener l) {
        this.itemClickListener = l;
    }

    public interface OnShareItemClickListener {
        void onShareItemClick(SharePlatform platform);
    }

    public interface OnShareItemClickListener2 {
        void onShareItemClick(SharePlatform platform, ShareContent sh);
    }
}
