package com.dfsx.selectedmedia.adapter;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.LoadingVideoThumbImageUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.R;
import com.dfsx.selectedmedia.async.ImageLoadAsync;
import com.dfsx.selectedmedia.async.MediaAsync;
import com.dfsx.selectedmedia.async.VideoLoadAsync;
import com.dfsx.selectedmedia.fragment.VideoFragment;


public class GridViewAdapter extends ArrayAdapter<MediaModel> {
    public Fragment videoFragment;

    private Context mContext;
    private List<MediaModel> mGalleryModelList;
    private int mWidth;
    private boolean mIsFromVideo;
    LayoutInflater viewInflater;


    public GridViewAdapter(Context context, int resource, List<MediaModel> categories, boolean isFromVideo) {
        super(context, resource, categories);
        mGalleryModelList = categories;
        mContext = context;
        mIsFromVideo = isFromVideo;
        viewInflater = LayoutInflater.from(mContext);
    }

    public List<MediaModel> getData() {
        return mGalleryModelList;
    }

    public int getCount() {
        return mGalleryModelList.size();
    }

    @Override
    public MediaModel getItem(int position) {
        return mGalleryModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            mWidth = mContext.getResources().getDisplayMetrics().widthPixels;

            convertView = viewInflater.inflate(R.layout.view_grid_item_media_chooser, parent, false);

            holder = new ViewHolder();
            holder.checkBoxTextView = (CheckedTextView) convertView.findViewById(R.id.checkTextViewFromMediaChooserGridItemRowView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromMediaChooserGridItemRowView);
            holder.durTxt = (TextView) convertView.findViewById(R.id.duration_time);
            holder.mVideoIcon = (ImageView) convertView.findViewById(R.id.video_iocon);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LayoutParams imageParams = (LayoutParams) holder.imageView.getLayoutParams();
        imageParams.width = mWidth / 3;
        imageParams.height = mWidth / 3;

        holder.imageView.setLayoutParams(imageParams);

        // set the status according to this Category item

        if (mIsFromVideo) {

            holder.mVideoIcon.setVisibility(View.VISIBLE);
            holder.durTxt.setVisibility(View.VISIBLE);
            holder.durTxt.setText(stringForTime(mGalleryModelList.get(position).duration));
//            new LoadingVideoThumbImageUtil(holder.imageView, mGalleryModelList.get(position).url, R.drawable.glide_default_image).load();
            new VideoLoadAsync(mContext,this, holder.imageView, false, mWidth / 2).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url.toString());
//            Util.LoadImageErrorUrl(holder.imageView,mGalleryModelList.get(position).getThumb(),null,R.drawable.ic_loading);
        } else {
            holder.mVideoIcon.setVisibility(View.GONE);
            holder.durTxt.setVisibility(View.GONE);

            Glide.with(mContext)
                    .load(mGalleryModelList.get(position).url)
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .placeholder(R.drawable.glide_default_image)
                    .into(holder.imageView);


//            ImageLoadAsync loadAsync = new ImageLoadAsync(mContext, holder.imageView, mWidth / 2);
//            loadAsync.executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url);
        }

        holder.checkBoxTextView.setChecked(mGalleryModelList.get(position).status);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        CheckedTextView checkBoxTextView;
        TextView durTxt;
        ImageView mVideoIcon;
    }

    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;


        int minutes = (totalSeconds / 60) % 60;


        int hours = totalSeconds / 3600;

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        }
    }

}
