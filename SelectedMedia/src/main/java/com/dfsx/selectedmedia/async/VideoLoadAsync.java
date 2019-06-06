package com.dfsx.selectedmedia.async;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.dfsx.selectedmedia.GalleryCache;
import com.dfsx.selectedmedia.GalleryRetainCache;
import com.dfsx.selectedmedia.adapter.GridViewAdapter;

public class VideoLoadAsync extends MediaAsync<String,String, String>{

	public Fragment fragment;
	public ArrayAdapter  adapter;

	private ImageView mImageView;
	private static GalleryCache mCache;
	private boolean mIsScrolling;
	private int mWidth;


	public VideoLoadAsync(Context  context, ArrayAdapter fragment, ImageView imageView, boolean isScrolling, int width) {
		mImageView    = imageView;
		this.adapter = fragment;
		mWidth        = width;
		mIsScrolling  = isScrolling;

		final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		final int size = 1024 * 1024 * memClass / 8;

		// Handle orientation change.
		GalleryRetainCache c = GalleryRetainCache.getOrCreateRetainableCache();
		mCache = c.mRetainedCache;

		if (mCache == null) {
			// The maximum bitmap pixels allowed in respective direction.
			// If exceeding, the cache will automatically scale the
			// bitmaps.
			/*	final int MAX_PIXELS_WIDTH  = 100;
			final int MAX_PIXELS_HEIGHT = 100;*/
			mCache = new GalleryCache(size, mWidth, mWidth);
			c.mRetainedCache = mCache;
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String url = params[0].toString();
		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		mCache.loadBitmap(adapter, result, mImageView, mIsScrolling);
	}

}
