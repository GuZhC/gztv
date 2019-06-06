package com.dfsx.ganzcms.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 我的名片
 * Created by wen on 2017/3/15.
 */

public class ShareCard extends Fragment {
    private View rootView;
    private TextView weibo, weixin, qq, qone;//微博、微信、QQ、朋友圈
    private ImageView cardImg;
    private String localImgPath;
    private Subscription shareSubscription;
    private boolean isCanClick = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_share_card, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initAction();
        initData();
    }

    private void initView() {
        weibo = (TextView) rootView.findViewById(R.id.share_wb);
        weixin = (TextView) rootView.findViewById(R.id.share_wx);
        qq = (TextView) rootView.findViewById(R.id.share_qq);
        qone = (TextView) rootView.findViewById(R.id.share_wxfriends);
        cardImg = (ImageView) rootView.findViewById(R.id.my_card);
    }

    private void initData() {
        if (weibo != null)
            weibo.setOnClickListener(onLister);
        if (weixin != null)
            weixin.setOnClickListener(onLister);
        if (qq != null)
            qq.setOnClickListener(onLister);
        if (qone != null)
            qone.setOnClickListener(onLister);

        Util.LoadThumebImage(cardImg, getCardUrl(), null);
        prepareCardImgFile();
    }

    private View.OnClickListener onLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isCanClick) return;
            shareImsg(view.getId());
        }
    };

    private void initAction() {
        shareSubscription = RxBus.getInstance().toObserverable(ShareCallBackEvent.class)
                .subscribe(new Action1<ShareCallBackEvent>() {
                    @Override
                    public void call(ShareCallBackEvent shareCallBackEvent) {
                        if (shareCallBackEvent != null) {
                            isCanClick = true;
                        }
                    }
                });
    }

    private void prepareCardImgFile() {
        String url = getCardUrl();
        Glide.with(getActivity()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                if (bitmap != null)
                    localImgPath = FileUtil.saveBitmapByAccountId(CoreApp.getInstance().getUser().getUser().getId() + "card.png", bitmap);
            }
        });
    }

    private String getCardUrl() {
        String imgUrl = App.getInstance().getmSession().getBaseMobileWebUrl() +
                "/user_card/" +
                CoreApp.getInstance().getUser().getUser().getId() + ".png";
        return imgUrl;
    }

    public void shareImsg(int resId) {
        isCanClick = false;
        //qq和微博传本地图片路径
        ShareContent content = new ShareContent();
        content.type = ShareContent.UrlType.Image;
        content.setTitle("test");
        //        content.thumb = "http://n.sinaimg.cn/transform/20150816/9iNs-fxfxrav2415982.jpg";
        content.disc = "disc";
        content.title = "title";
        //        content.url =
        AbsShare share;
        switch (resId) {
            case R.id.share_wb:
                if (localImgPath == null) {
                    showShortToast("要分享的图片不在本地");
                    isCanClick = true;
                    return;
                }
                content.thumb = localImgPath;
                share = ShareFactory.createShare(getActivity(), SharePlatform.WeiBo);
                share.share(content);
                break;
            case R.id.share_wx:
                content.thumb = getCardUrl();
                share = ShareFactory.createShare(getActivity(), SharePlatform.Wechat);
                share.share(content);
                break;
            case R.id.share_qq:
                if (localImgPath == null) {
                    showShortToast("要分享的图片不在本地");
                    isCanClick = true;
                    return;
                }
                content.thumb = localImgPath;
                share = ShareFactory.createShare(getActivity(), SharePlatform.QQ);
                share.share(content);
                break;
            case R.id.share_wxfriends:
                content.thumb = getCardUrl();
                share = ShareFactory.createShare(getActivity(), SharePlatform.Wechat_FRIENDS);
                share.share(content);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shareSubscription != null) {
            shareSubscription.unsubscribe();
        }
    }

    private void showShortToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
