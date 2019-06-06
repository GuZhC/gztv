package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.GradeResourceImpl;
import com.dfsx.ganzcms.app.business.IGradeResource;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/14.
 */
public class LiveUserGridRoomAdapter extends LiveRoomAdapter implements View.OnClickListener {

    public static final int COLUMN_NUM = 2;

    private ArrayList<PairRoom> gridList;
    private OnGridItemClickListener itemClickListener;
    private IGradeResource gradeResourceHelper;

    public LiveUserGridRoomAdapter(Context context, ArrayList<Room> list) {
        super(context, list);
        gridList = toGridList(list);
        gradeResourceHelper = new GradeResourceImpl();
    }

    @Override
    public void update(List<Room> data, boolean isAdd) {
        if (isAdd && list != null) {
            list.addAll(data);
            addRoomList(data);
        } else {
            list = data;
            gridList = toGridList(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = gridList == null ? 0 : gridList.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return gridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {//在不是缓存的时候使用.动态设置显示的界面
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.adapter_item_live_user_grid_layout, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.lineContainer = (LinearLayout) convertView.findViewById(R.id.item_room_line_container);
            LinearLayout.LayoutParams params;
            for (int i = 0; i < COLUMN_NUM; i++) {
                FrameLayout itemContainer = new FrameLayout(context);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                itemContainer.setLayoutParams(params);
                itemContainer.setId(i);
                holder.viewMap.put(i, itemContainer);
                holder.lineContainer.addView(itemContainer, params);

                if (i < COLUMN_NUM - 1) {
                    addItemDivideView(holder.lineContainer);
                }

                itemContainer.addView(getItemView());
                tagItemViewHolder(itemContainer);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setViewData(holder, position);
        return convertView;
    }

    protected void addItemDivideView(LinearLayout container) {
        int with = PixelUtil.dp2px(context, 2);
        if (with > 0) {
            View divideView = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            divideView.setLayoutParams(lp);
            divideView.setBackgroundResource(R.color.white);
            container.addView(divideView);
        }
    }

    public void setOnGridItemClickListener(OnGridItemClickListener l) {
        this.itemClickListener = l;
    }

    private void tagItemViewHolder(View v) {
        ItemViewHolder itemHolder = new ItemViewHolder();
        itemHolder.itemContainerView = v.findViewById(R.id.grid_container_layout);
        itemHolder.roomOwnerLogoImageView = (ImageView) v.findViewById(R.id.room_image_view);
        itemHolder.roomTypeImageView = (ImageView) v.findViewById(R.id.room_type_image_view);
        itemHolder.bottomDivideLine = v.findViewById(R.id.bottom_horizontal_line);
        itemHolder.ownerGradeTextView = (ImageView) v.findViewById(R.id.room_owner_grade);
        itemHolder.ownerNameTextView = (TextView) v.findViewById(R.id.room_owner_text);
        itemHolder.roomVisitorNumTextView = (TextView) v.findViewById(R.id.room_num_text);
        itemHolder.roomLabelTextView = (TextView) v.findViewById(R.id.room_label_text_view);
        itemHolder.roomNameTextView = (TextView) v.findViewById(R.id.room_name);
        v.setTag(itemHolder);
    }

    private View getItemView() {
        return LayoutInflater.from(context).
                inflate(R.layout.adapter_live_user_grid_layout, null);
    }

    private void setViewData(ViewHolder holder, final int position) {
        PairRoom pairRoom = gridList.get(position);
        boolean isShowBottomLine = position != gridList.size() - 1;
        for (int i = 0; i < COLUMN_NUM; i++) {
            FrameLayout itemContainer = (FrameLayout) holder.getView(i);
            ItemViewHolder itemHolder = (ItemViewHolder) itemContainer.getTag();
            if (i < pairRoom.pairList.size()) {
                itemHolder.itemContainerView.setVisibility(View.VISIBLE);
                itemHolder.itemContainerView.setTag(pairRoom.pairList.get(i));
                setItemViewData(itemHolder, pairRoom.pairList.get(i), isShowBottomLine);
            } else {
                itemHolder.itemContainerView.setVisibility(View.INVISIBLE);
                itemHolder.itemContainerView.setTag(null);
            }
            itemHolder.itemContainerView.setOnClickListener(this);
        }
    }

    /**
     * @param room
     * @param isShowBottomDivideLine 是否显示分割线
     */
    private void setItemViewData(ItemViewHolder holder, Room room,
                                 boolean isShowBottomDivideLine) {
        holder.bottomDivideLine.setVisibility(isShowBottomDivideLine ? View.VISIBLE : View.GONE);
        GlideImgManager.getInstance().showImg(context, holder.roomOwnerLogoImageView,
                room.getRoomImagePath());
        int flagRes = 0;
        if (room.getRoomFlag() == Room.FLAG_PRIVACY) {
            flagRes = R.drawable.icon_back_play_privacy;
        } else if (room.getRoomFlag() == Room.FLAG_YUGAO) {
            flagRes = R.drawable.icon_live_no_start;
        } else if (room.getRoomFlag() == Room.FLAG_LIVE_ROOM) {
            flagRes = R.drawable.icon_live_living;
        } else {
            flagRes = R.drawable.icon_back_live;
        }
        holder.roomTypeImageView.setImageResource(flagRes);
        UserLevelManager.getInstance().showLevelImage(context, room.getRoomOwnerLevelId(),
                holder.ownerGradeTextView);
        holder.ownerNameTextView.setText(room.getRoomOwnerName());
        String numText = getNumTextString(room.getRoomMessageCount());
        holder.roomVisitorNumTextView.setText(numText);
        if (room.getRoomLabel() != null && room.getRoomLabel().length > 0) {
            holder.roomLabelTextView.setText(room.getRoomLabel()[0]);
            holder.roomLabelTextView.setVisibility(View.VISIBLE);
        } else {
            holder.roomLabelTextView.setVisibility(View.GONE);
        }
        holder.roomNameTextView.setText(room.getRoomTitle());
    }

    private String getNumTextString(long num) {
        if (num / 10000 > 0) {
            float fw = num / 10000f;
            return String.format("%.1f", fw) + "w人";
        } else {
            int bNum = (int) num;
            return bNum + "人";
        }
    }

    private ArrayList<PairRoom> toGridList(List<Room> list) {
        ArrayList<PairRoom> pairRooms = new ArrayList<>();
        if (list != null) {
            PairRoom pairRoom = null;
            for (int i = 0; i < list.size(); i++) {
                if (i % COLUMN_NUM == 0) {
                    pairRoom = new PairRoom();
                    pairRooms.add(pairRoom);
                }
                if (pairRoom != null) {
                    pairRoom.pairList.add(list.get(i));
                }
            }
        }
        return pairRooms;
    }

    private void addRoomList(List<Room> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (gridList.isEmpty()) {
            gridList.addAll(toGridList(list));
        } else {
            PairRoom lastPair = gridList.get(gridList.size() - 1);
            if (lastPair != null && lastPair.pairList.size() < COLUMN_NUM) {
                int count = COLUMN_NUM - lastPair.pairList.size();
                int listSize = list.size();
                count = Math.min(count, listSize);
                for (int i = 0; i < count; i++) {
                    Room r = list.remove(0);//把新来的列表的头几个数据放到上面没有补齐的数据中
                    if (r != null) {
                        lastPair.pairList.add(r);
                    }
                }
            }
            gridList.addAll(toGridList(list));
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Room) {
            Room room = (Room) tag;
            if (itemClickListener != null) {
                itemClickListener.onGridItemClick(room);
            }
        }
    }

    class ViewHolder {
        LinearLayout lineContainer;
        SparseArray<View> viewMap = new SparseArray<View>();

        public View getView(int viewId) {
            View view = viewMap.get(viewId);
            if (view == null) {
                view = lineContainer.findViewById(viewId);
                viewMap.put(viewId, view);
            }
            return view;
        }
    }

    class ItemViewHolder {
        public View itemContainerView;
        public ImageView roomOwnerLogoImageView;
        public ImageView roomTypeImageView;
        public View bottomDivideLine;
        public ImageView ownerGradeTextView;
        public TextView ownerNameTextView;
        public TextView roomVisitorNumTextView;
        public TextView roomLabelTextView;
        public TextView roomNameTextView;
    }

    class PairRoom {
        public ArrayList<Room> pairList;

        public PairRoom() {
            pairList = new ArrayList<>(COLUMN_NUM);
        }

        public PairRoom(Room... rooms) {
            pairList = new ArrayList<>(COLUMN_NUM);
            if (rooms.length > COLUMN_NUM) {
                Log.e("TAG", "数据配对异常");
                return;
            }
            for (Room r : rooms) {
                pairList.add(r);
            }
        }
    }


    public interface OnGridItemClickListener {
        void onGridItemClick(Room room);
    }
}
