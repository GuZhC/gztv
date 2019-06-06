package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.fivehundredpx.android.blur.BlurringView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveRoomAdapter extends BaseAdapter {

    protected Context context;
    protected List<Room> list;

    protected IRoomAdapterMoreSettingClickListener moreSettingClickListener;

    public LiveRoomAdapter(Context context, ArrayList<Room> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<Room> data, boolean isAdd) {
        if (isAdd && list != null) {
            list.addAll(data);
        } else {
            list = data;
        }
        notifyDataSetChanged();
    }

    public void setIRoomAdapterMoreSettingClickListener(IRoomAdapterMoreSettingClickListener l) {
        this.moreSettingClickListener = l;
    }

    public List<Room> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHodler holder = BaseViewHodler.get(convertView, parent,
                R.layout.item_live_room, position);
        setViewData(holder, position);
        return holder.getConvertView();
    }

    private void setViewData(final BaseViewHodler holder, final int position) {
        TextView roomName = holder.getView(R.id.room_name);
        TextView roomOwnerName = holder.getView(R.id.room_owner_text);
        TextView roomLiveState = holder.getView(R.id.room_status);
        TextView roomMsgNum = holder.getView(R.id.room_msg_num);
        TextView roomTime = holder.getView(R.id.room_start_time);

        ImageView roomImage = holder.getView(R.id.room_img);
        BlurringView roomFilter = holder.getView(R.id.room_filter);
        ImageView centerImg = holder.getView(R.id.center_img);
        TextView bottomCenterText = holder.getView(R.id.note_text);
        ImageView backPlayImage = holder.getView(R.id.image_back_play);
        CircleButton logo = holder.getView(R.id.item_owner_logo);

        TextView moreSettingText = holder.getView(R.id.text_more);

        roomFilter.setBlurredView(roomImage);

        Room roomInfo = list.get(position);

        roomOwnerName.setText(roomInfo.getRoomOwnerName());
        roomName.setText(roomInfo.getRoomTitle());
        roomTime.setText(StringUtil.getTimeText(roomInfo.getRoomTime()));
        String stateText = roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY ?
                getTimeAgoText(roomInfo.getRoomTime()) :
                roomInfo.getRoomStatus();
        roomLiveState.setText(stateText);
        String numText = roomInfo.getRoomFlag() == Room.FLAG_BACK_PLAY ? "人观看过" : "人在观看";
        roomMsgNum.setText(roomInfo.getRoomMessageCount() + numText);

        moreSettingText.setVisibility(roomInfo.isOnlyUserRoom() ? View.VISIBLE : View.GONE);

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
        GlideImgManager.getInstance().showImg(context, logo,
                roomInfo.getRoomOwnerLogo());
        int flagRes = 0;
        if (roomInfo.getRoomFlag() == Room.FLAG_PRIVACY) {
            flagRes = R.drawable.icon_back_play_privacy;
        } else if (roomInfo.getRoomFlag() == Room.FLAG_YUGAO) {
            flagRes = R.drawable.icon_live_no_start;
        } else if (roomInfo.getRoomFlag() == Room.FLAG_LIVE_ROOM) {
            flagRes = R.drawable.icon_live_living;
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
        //        if (!TextUtils.isEmpty(roomInfo.getCoverUrl())) {
        //        } else {
        //            roomImage.setBackgroundColor(context.getResources().getColor(R.color.red));
        //        }
    }

    protected String getTimeAgoText(long time) {
        long cTime = new Date().getTime();
        time = time * 1000;
        long dT = cTime - time;

        long seconds = dT / 1000;
        int h = (int) (seconds / (60 * 60));
        int m = (int) (seconds / 60);
        String text = "";
        if (h > 0) {
            text = h + "小时之前";
        } else {
            m = m <= 0 ? m = 1 : m;
            text = m + "分钟之前";
        }
        return text;
    }

}
