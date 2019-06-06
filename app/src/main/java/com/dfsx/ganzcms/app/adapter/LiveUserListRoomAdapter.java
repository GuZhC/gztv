package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.GradeResourceImpl;
import com.dfsx.ganzcms.app.business.IGradeResource;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.ganzcms.app.view.CustomLabelLayout;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.fivehundredpx.android.blur.BlurringView;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/6/14.
 */
public class LiveUserListRoomAdapter extends LiveRoomAdapter {
    private IGradeResource gradeResourceHelper;
    private Handler handler = new Handler();

    public LiveUserListRoomAdapter(Context context, ArrayList<Room> list) {
        super(context, list);
        gradeResourceHelper = new GradeResourceImpl();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHodler holder = BaseViewHodler.get(convertView, parent,
                R.layout.adapter_item_user_list_layout, position);
        setItemViewData(holder, position);
        return holder.getConvertView();
    }

    private void setItemViewData(final BaseViewHodler holder, final int position) {
        TextView roomName = holder.getView(R.id.room_name);
        TextView roomOwnerName = holder.getView(R.id.room_owner_text);
        ImageView roomOwnerGrade = holder.getView(R.id.room_owner_grade);
        TextView roomLiveState = holder.getView(R.id.room_status);
        TextView roomMsgNum = holder.getView(R.id.room_msg_num);
        TextView roomTime = holder.getView(R.id.room_start_time);

        ImageView roomImage;
        roomImage = holder.getView(R.id.room_img);
        BlurringView roomFilter = holder.getView(R.id.room_filter);
        ImageView centerImg = holder.getView(R.id.center_img);
        TextView bottomCenterText = holder.getView(R.id.note_text);
        ImageView backPlayImage = holder.getView(R.id.image_back_play);
        CircleButton logo = holder.getView(R.id.item_owner_logo);
        View labelConatiner = holder.getView(R.id.label_container_view);
        CustomLabelLayout roomLableLayout = holder.getView(R.id.room_label_view);
        roomLableLayout.setCouldClickBody(false);
        roomLableLayout.setAddFlagNeedShown(false);
        int lableTextColor = getColorByResourceId(R.color.label_text_color);
        int lableBgk = R.drawable.shape_label_box;
        roomLableLayout.changeThemeForTextColor(lableTextColor, lableTextColor,
                lableBgk, lableBgk);
        roomLableLayout.destroy();

        TextView moreSettingText = holder.getView(R.id.text_more);

        roomFilter.setBlurredView(roomImage);

        Room roomInfo = list.get(position);

        roomOwnerName.setText(roomInfo.getRoomOwnerName());
        roomName.setText(roomInfo.getRoomTitle());
        roomTime.setText(StringUtil.getTimeAgoText(roomInfo.getRoomTime()));
        String stateText = roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY ?
                StringUtil.getTimeAgoText(roomInfo.getRoomTime()) :
                roomInfo.getRoomStatus();
        roomLiveState.setText(stateText);
        String numText = roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY ? "人观看过" : "人在观看";
        roomMsgNum.setText(roomInfo.getRoomMessageCount() + numText);

        //设置等级
        UserLevelManager.getInstance().showLevelImage(context,
                roomInfo.getRoomOwnerLevelId(), roomOwnerGrade);
        //        moreSettingText.setVisibility(roomInfo.isOnlyUserRoom() ? View.VISIBLE : View.GONE);
        if (roomInfo.getRoomLabel() != null && roomInfo.getRoomLabel().length > 0) {
            roomLableLayout.destroy();
            roomLableLayout.addAllBody(roomInfo.getRoomLabel());
            handler.post(new LabelRequestLayoutRunnable(roomLableLayout));
            Log.e("TAG", "label == ");
            labelConatiner.setVisibility(View.VISIBLE);
        } else {
            labelConatiner.setVisibility(View.GONE);
        }

        moreSettingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreSettingClickListener != null) {
                    moreSettingClickListener.onMoreSettingClick(v, list.get(position));
                }
            }
        });
        //加载房间的图片
        GlideImgManager.getInstance().showImg(context, roomImage,
                roomInfo.getRoomImagePath(), new RequestListener() {
                    @Override
                    public boolean onException(Exception e, Object o, Target target, boolean b) {
                        if (position >= 0 && position < list.size()) {
                            if (list.get(position).isNeedRoomPassword()) {
                                BlurringView blurringView = holder.getView(R.id.room_filter);
                                blurringView.invalidate();
                            }
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object o, Object o2, Target target, boolean b, boolean b1) {
                        if (position >= 0 && position < list.size()) {
                            if (list.get(position).isNeedRoomPassword()) {
                                BlurringView blurringView = holder.getView(R.id.room_filter);
                                blurringView.invalidate();
                            }
                        }
                        return false;
                    }
                });
        LSLiveUtils.showUserLogoImage(context, logo,
                roomInfo.getRoomOwnerLogo());
        int flagRes = 0;
        if (roomInfo.getRoomFlag() == Room.FLAG_PRIVACY) {
            flagRes = R.drawable.icon_back_play_privacy;
        } else if (roomInfo.getRoomFlag() == Room.FLAG_YUGAO) {
            flagRes = R.drawable.icon_live_no_start;
        } else if (roomInfo.getRoomFlag() == Room.FLAG_LIVE_ROOM) {
            flagRes = R.drawable.icon_living_on;
        } else {
            flagRes = R.drawable.icon_back_live;
        }
        backPlayImage.setImageResource(flagRes);
        if (roomInfo.isNeedRoomPassword()) {
            centerImg.setImageResource(R.drawable.icon_live_room_password);
            bottomCenterText.setText("输入密码即可观看");
            bottomCenterText.setVisibility(View.VISIBLE);
            roomFilter.setVisibility(View.VISIBLE);
        } else {
            centerImg.setImageResource(R.drawable.icon_list_play);
            bottomCenterText.setVisibility(View.GONE);
            roomFilter.setVisibility(View.GONE);
        }
    }

    class LabelRequestLayoutRunnable implements Runnable {

        private CustomLabelLayout labelLayout;

        public LabelRequestLayoutRunnable(CustomLabelLayout labelLayout) {
            this.labelLayout = labelLayout;
        }

        @Override
        public void run() {
            if (labelLayout != null) {
                labelLayout.requestLayout();
            }
        }
    }

    private int getColorByResourceId(int colorId) {
        return context.getResources().getColor(colorId);
    }
}
