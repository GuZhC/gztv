package com.dfsx.selectedmedia.async;


import java.io.File;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.selectedmedia.R;
import com.dfsx.selectedmedia.async.MediaAsync;
import com.squareup.picasso.Picasso;


public class ImageLoadAsync extends MediaAsync<String,String, String> {

	private ImageView mImageView;
	private Context mContext;
	private int mWidth;

	public ImageLoadAsync(Context context,ImageView imageView, int width) {
		mImageView = imageView;
		mContext   = context;
		mWidth     = width;
	}

	@Override
	protected String doInBackground(String... params) {
		String url = params[0].toString();
		return url;
	}

	@Override
	protected void onPostExecute(String result) {
//		Picasso.with(mContext)
//		.load(new File(result))
//		.resize(mWidth, mWidth)
//		.centerCrop().placeholder(R.drawable.ic_loading)
//		.into(mImageView);

		LoadThumebImage(mImageView,result);
	}

	public static void LoadThumebImage(final ImageView img, String imageUrl) {
		GlideImgManager.getInstance().showImg(img.getContext(), img, imageUrl,
				new RequestListener<String, GlideDrawable>() {

					@Override
					public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
						return false;
					}

					@Override
					public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
						return false;
					}
				});
	}

}
