package com.dfsx.lzcms.liveroom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.IChatData;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/20.
 */
public class LiveServiceChatAdapter extends BaseListViewAdapter<IChatData> {
    private static final int TYPE_CURRENT_USER = 0;
    private static final int TYPE_NO_CURRENT_USER = 1;
    private static final int TYPE_NOTE_MESSAGE = 2;
    private static final int TYPE_ALL_COUNT = 3;
    private static final int TYPE_UNKNOWN = 1000;

    public LiveServiceChatAdapter(Context context) {
        super(context);
    }

    public void addTopDataList(List<IChatData> datas) {
        if (list == null) {
            list = new ArrayList<IChatData>();
        }
        if (datas != null) {
            list.addAll(0, datas);
        }
        notifyDataSetChanged();
    }

    public void addBottomDataList(List<IChatData> datas) {
        if (list == null) {
            list = new ArrayList<IChatData>();
        }
        if (datas != null) {
            list.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void addBottomData(IChatData data) {
        if (list == null) {
            list = new ArrayList<IChatData>();
        }
        if (data != null) {
            list.add(data);
        }
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == TYPE_CURRENT_USER) {
            BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                    getItemViewLayoutIdByType(type), position);
            setItemViewData(type, holdler, position);
            return holdler.getConvertView();
        } else if (type == TYPE_NO_CURRENT_USER) {
            BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                    getItemViewLayoutIdByType(type), position);
            setItemViewData(type, holdler, position);
            return holdler.getConvertView();
        } else if (type == TYPE_NOTE_MESSAGE) {
            BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                    getItemViewLayoutIdByType(type), position);
            setItemViewData(type, holdler, position);
            return holdler.getConvertView();
        } else {
            Log.e("TAG", "数据配置错误------------------");
        }
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        IChatData.ChatViewType type = list.get(position).getChatViewType();
        if (type == IChatData.ChatViewType.CURRENT_USER) {
            return TYPE_CURRENT_USER;
        } else if (type == IChatData.ChatViewType.NO_CURRENT_USER) {
            return TYPE_NO_CURRENT_USER;
        } else if (type == IChatData.ChatViewType.NOTE_MESSAGE) {
            return TYPE_NOTE_MESSAGE;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ALL_COUNT;
    }

    private int getItemViewLayoutIdByType(int type) {
        if (type == TYPE_NO_CURRENT_USER) {
            return R.layout.adapter_item_chat_no_current_user;
        } else if (type == TYPE_CURRENT_USER) {
            return R.layout.adapter_item_chat_current_user;
        } else if (type == TYPE_NOTE_MESSAGE) {
            return R.layout.adapter_item_chat_note_message;
        }
        return 0;
    }

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {

    }

    private void setItemViewData(int type, BaseViewHodler holder, int position) {
        if (type == TYPE_NO_CURRENT_USER) {
            setNoCurrentUserData(holder, position);
        } else if (type == TYPE_CURRENT_USER) {
            setCurrentUserData(holder, position);
        } else if (type == TYPE_NOTE_MESSAGE) {
            setNoteMessage(holder, position);
        }
    }

    private void setNoteMessage(BaseViewHodler holder, int position) {
        TextView noteText = holder.getView(R.id.note_time_and_message);
        IChatData data = list.get(position);
        String time = TextUtils.isEmpty(data.getChatTimeText()) ? "" : data.getChatTimeText() + " ";
        String text = time +
                data.getChatContentText();
        noteText.setText(text);
    }

    private void setCurrentUserData(BaseViewHodler holder, int position) {
        TextView chatTime = holder.getView(R.id.item_chat_time_text);
        CircleButton userLogo = holder.getView(R.id.item_user_logo_image);
        TextView chatContent = holder.getView(R.id.item_chat_content_text);
        IChatData data = list.get(position);
        chatTime.setText(data.getChatTimeText());
        LSLiveUtils.showUserLogoImage(context, userLogo, data.getChatUserLogo());
        chatContent.setText(data.getChatContentText());
    }

    private void setNoCurrentUserData(BaseViewHodler holder, int position) {
        TextView chatTime = holder.getView(R.id.item_chat_time_text);
        TextView userName = holder.getView(R.id.item_user_name_text);
        CircleButton userLogo = holder.getView(R.id.item_user_logo_image);
        TextView chatContent = holder.getView(R.id.item_chat_content_text);

        IChatData data = list.get(position);
        chatTime.setText(data.getChatTimeText());
        userName.setText(data.getChatUserNickName());
        GlideImgManager.getInstance().showImg(context, userLogo, data.getChatUserLogo());
        chatContent.setText(data.getChatContentText());
    }
}
