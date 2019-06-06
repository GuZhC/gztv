package com.dfsx.core.common.view.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.CoreApp;
import com.dfsx.core.R;
import com.dfsx.core.img.GlideImgManager;

public class SimpleImageBanner extends BaseIndicatorBanner<BannerItem, SimpleImageBanner> {
    private ColorDrawable colorDrawable;
    private Context context;

    public SimpleImageBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        colorDrawable = new ColorDrawable(context.getResources().getColor(R.color.COLOR_f7f9));
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
        final BannerItem item = mDatas.get(position);
        tv.setText(item.title);
        if (item.titleResource != 0) {
            tv.setCompoundDrawablePadding(10);
            tv.setCompoundDrawablesWithIntrinsicBounds(item.titleResource, 0, 0, 0);
        }
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = inflate(mContext, R.layout.item_pager_image, null);
        ImageView iv = (ImageView) inflate.findViewById(R.id.image);
        ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.loading);

        BannerItem item = mDatas.get(position);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String imgUrl = item.imgUrl;

        if (!TextUtils.isEmpty(imgUrl)) {
            //            Glide.with(mContext)
            //                    .load(imgUrl)
            //                    .centerCrop()
            //                    .placeholder(colorDrawable)
            //                    .crossFade()
            //                    .into(iv);
            LoadImageFormUrl(iv, imgUrl, progressBar);
            //            GlideImgManager.getInstance().showImg(mContext, iv, imgUrl);
        } else {
            iv.setImageDrawable(colorDrawable);
        }

        return inflate;
    }

    public void LoadImageFormUrl(final ImageView img, String imageUrl, final ProgressBar spinner) {
        if (spinner != null) {
            spinner.setVisibility(VISIBLE);
        }
        Context cn = context != null ? context : img.getContext();
        if(cn==null) return;
        Glide.with(CoreApp.getInstance().getApplicationContext())
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.glide_default_image)
                .error(R.drawable.glide_default_image)
                .centerCrop()
                .into(new BitmapImageViewTarget(img) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        if (spinner != null) {
                            spinner.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (spinner != null) {
                            spinner.setVisibility(GONE);
                        }
                    }
                });
    }
}
