package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseEmptyViewRecyclerAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.model.IChatData;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;

public class ChatMessageListAdapter extends BaseEmptyViewRecyclerAdapter<IChatData> {
    private static final int TYPE_CURRENT_USER = 1;
    private static final int TYPE_NO_CURRENT_USER = 2;
    private Context context;

    public ChatMessageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getItemView(ViewGroup parent, int viewType) {
        int layoutRes = getItemViewLayoutIdByType(viewType);
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, null);
    }

    @Override
    public void onBindItemView(BaseRecyclerViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_CURRENT_USER) {
            setCurrentUserData(holder, position);
        } else if (viewType == TYPE_NO_CURRENT_USER) {
            setNoCurrentUserData(holder, position);
        }
    }

    @Override
    public int getOtherItemViewType(int position) {
        IChatData.ChatViewType type = list.get(position).getChatViewType();
        if (type == com.dfsx.lzcms.liveroom.model.IChatData.ChatViewType.CURRENT_USER) {
            return TYPE_CURRENT_USER;
        } else if (type == com.dfsx.lzcms.liveroom.model.IChatData.ChatViewType.NO_CURRENT_USER) {
            return TYPE_NO_CURRENT_USER;
        } else {
            return TYPE_NO_CURRENT_USER;
        }
    }

    private int getItemViewLayoutIdByType(int type) {
        if (type == TYPE_NO_CURRENT_USER) {
            return R.layout.adapter_item_message_other_user;
        } else if (type == TYPE_CURRENT_USER) {
            return R.layout.adapter_item_message_me;
        }
        return R.layout.adapter_item_message_other_user;
    }

    private void setCurrentUserData(BaseRecyclerViewHolder holder, int position) {
        TextView chatTime = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_chat_time_text);
        CircleButton userLogo = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_user_logo_image);
        TextView chatContent = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_chat_content_text);
        IChatData data = list.get(position);
        chatTime.setText(data.getChatTimeText());
        LSLiveUtils.showUserLogoImage(context, userLogo, data.getChatUserLogo());
        chatContent.setText(data.getChatContentText());
    }

    private void setNoCurrentUserData(BaseRecyclerViewHolder holder, int position) {
        TextView chatTime = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_chat_time_text);
        TextView userName = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_user_name_text);
        CircleButton userLogo = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_user_logo_image);
        TextView chatContent = holder.getView(com.dfsx.lzcms.liveroom.R.id.item_chat_content_text);

        IChatData data = list.get(position);
        chatTime.setText(data.getChatTimeText());
        userName.setText(data.getChatUserNickName());
        GlideImgManager.getInstance().showImg(context, userLogo, data.getChatUserLogo());
        chatContent.setText(data.getChatContentText());
    }
}
