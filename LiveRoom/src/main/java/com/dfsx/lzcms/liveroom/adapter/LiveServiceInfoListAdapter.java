package com.dfsx.lzcms.liveroom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.ImageTextMessage;
import com.dfsx.lzcms.liveroom.view.ImageListGroupView;
import com.dfsx.openimage.OpenImageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwb on 2017/6/20.
 */
public class LiveServiceInfoListAdapter extends BaseListViewAdapter<ImageTextMessage> {
    public LiveServiceInfoListAdapter(Context context) {
        super(context);
    }

    public void addTopData(ImageTextMessage message) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(0, message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_item_live_service_info_layout;
    }

    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {
        View userInfoView = holder.getView(R.id.user_info_view);
        TextView itemTimeJustTextView = holder.getView(R.id.just_time_text);
        TextView dateTextView = holder.getView(R.id.date_time_text);
        TextView itemInfoTextView = holder.getView(R.id.item_info_text);
        ImageView userLogoImageView = holder.getView(R.id.item_logo_image_View);
        TextView userNickNameTextView = holder.getView(R.id.item_user_name_text);
        ImageListGroupView imageListGroupView = holder.getView(R.id.item_image_list_view);

        ImageTextMessage message = list.get(position);
        if (message.getUserId() == 0) {
            userInfoView.setVisibility(View.GONE);
        } else {
            userInfoView.setVisibility(View.VISIBLE);
            GlideImgManager.getInstance().showImg(context,
                    userLogoImageView, message.getUserAvatarUrl());
            userNickNameTextView.setText("主持人");
        }
        if (TextUtils.isEmpty(message.getText())) {
            itemInfoTextView.setVisibility(View.GONE);
        } else {
            itemInfoTextView.setText(message.getText());
            itemInfoTextView.setVisibility(View.VISIBLE);
        }
        List<String> imageList = message.getImageList();
        if (imageList == null || imageList.isEmpty()) {
            imageListGroupView.setVisibility(View.GONE);
        } else {
            imageListGroupView.setVisibility(View.VISIBLE);
            imageListGroupView.setImageList(imageList.toArray(new String[0]));
            imageListGroupView.setTag(message);
        }
        long time = message.getTimestamp() * 1000;
        itemTimeJustTextView.setText(getTimeShowText(time));
        dateTextView.setText(getTimeDateText(time));
        imageListGroupView.setOnItemImageClickListener(new ImageListGroupView.OnItemImageClickListener() {
            @Override
            public void onItemImageClick(View imageListView, int position) {
                Object tag = imageListView.getTag();
                if (tag instanceof ImageTextMessage) {
                    //图片的跳转
                    ImageTextMessage imageTextMessage = (ImageTextMessage) tag;
                    List<String> imageList = imageTextMessage.getImageList();
                    if (imageList != null && position >= 0 && position < imageList.size()) {
                        OpenImageUtils.openImage(context, position,
                                imageList.toArray(new String[0]));
                    }
                }
            }
        });
    }

    private String getTimeShowText(long time) {
        long currentTime = new Date().getTime();
        long dT = currentTime - time;

        long seconds = dT / 1000;
        int h = (int) (seconds / (60 * 60));
        int m = (int) (seconds / 60);
        String text = "";
        if (h == 0 && m > 0) {
            text = m + "分钟之前";
        } else if (m <= 0) {
            text = "刚刚";
        } else {
            text = getTimeHourText(time);
        }
        return text;
    }

    private String getTimeHourText(long time) {
        String pp = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(time));
    }

    private String getTimeDateText(long time) {
        String pp = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(time));
    }
}
