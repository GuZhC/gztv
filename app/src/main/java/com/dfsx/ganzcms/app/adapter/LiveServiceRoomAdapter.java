package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.fivehundredpx.android.blur.BlurringView;


/**
 * Created by liuwb on 2017/6/14.
 */
public class LiveServiceRoomAdapter extends BaseListViewAdapter<LiveInfo> {

    private android.os.Handler handler = new Handler(Looper.getMainLooper());

    private BlurringView blurringView;

    public LiveServiceRoomAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_item_live_service;
    }

    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {
        ImageView liveImageView = holder.getView(R.id.item_bkg_image);
        TextView liveInfoTextView = holder.getView(R.id.item_live_info_text);
        ImageView liveTypeView = holder.getView(R.id.item_live_type_text);
        TextView liveUserNumTextView = holder.getView(R.id.item_user_num_text);
        ImageView livePassWordImage = holder.getView(R.id.center_password_image);
        blurringView = holder.getView(R.id.room_bkg_blurring);


        LiveInfo info = list.get(position);
        blurringView.setBlurredView(liveImageView);
        blurringView.setVisibility(info.isPassword() ? View.VISIBLE : View.GONE);
        livePassWordImage.setVisibility(info.isPassword() ? View.VISIBLE : View.GONE);
        Glide.with(context)
                .load(info.getCoverUrl())
                .error(R.drawable.glide_default_image)
                .into(new GlideDrawableImageViewTarget(liveImageView) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blurringView.invalidate();
                            }
                        }, 100);
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blurringView.invalidate();
                            }
                        }, 100);
                    }
                });
        liveInfoTextView.setText(info.getTitle());
        liveUserNumTextView.setText(info.getCurrentVisitorCount() + "参与");


        int state = info.getState();
        int liveTypeRes = 0;
        if (state == 1) { //未直播
            liveTypeRes = R.drawable.icon_live_no_start;
        } else if (state == 2) {//正在直播
            liveTypeRes = R.drawable.icon_living_on;
        } else if (state == 3) {//直播已结束
            liveTypeRes = R.drawable.icon_back_live;
        } else {//其他默认
            liveTypeRes = R.drawable.icon_living_on;
        }
        liveTypeView.setImageResource(liveTypeRes);

    }
}
