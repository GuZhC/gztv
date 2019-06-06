package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/11/6
 *
 *  电视聊天界面
 */
public class LiveTvChatAdapter extends BaseListViewAdapter<CommendCmsEntry> {
    private static final int TYPE_CURRENT_USER = 0;
    private static final int TYPE_NO_CURRENT_USER = 1;
    private static final int TYPE_ALL_COUNT = 2;
    private static final int TYPE_UNKNOWN = 1000;

    public LiveTvChatAdapter(Context context) {
        super(context);
    }

    public void addTopDataList(List<CommendCmsEntry> datas) {
        if (list == null) {
            list = new ArrayList<CommendCmsEntry>();
        }
        if (datas != null) {
            list.addAll(0, datas);
        }
        notifyDataSetChanged();
    }

    public void addBottomDataList(List<CommendCmsEntry> datas) {
        if (list == null) {
            list = new ArrayList<CommendCmsEntry>();
        }
        if (datas != null) {
            list.addAll(datas);
        }
        notifyDataSetChanged();
    }



    public void addBottomData(CommendCmsEntry data) {
        if (list == null) {
            list = new ArrayList<CommendCmsEntry>();
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
        } else {
            Log.e("TAG", "数据配置错误------------------");
        }
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        boolean isUser = list.get(position).isUser();
        if (isUser) {
            return TYPE_CURRENT_USER;
        } else {
            return TYPE_NO_CURRENT_USER;
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
            return R.layout.adapter_item_chat_current_tv_user;
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
        }
    }

    private void setCurrentUserData(BaseViewHodler holder, int position) {
        TextView chatTime = holder.getView(R.id.item_chat_time_text);
        CircleButton userLogo = holder.getView(R.id.item_user_logo_image);
        TextView chatContent = holder.getView(R.id.item_chat_content_text);
        CommendCmsEntry data = list.get(position);
        chatTime.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", data.getCreation_time() * 1000));
        LSLiveUtils.showUserLogoImage(context, userLogo, data.getAuthor_avatar_url());
        chatContent.setText(data.getText());
    }

    private void setNoCurrentUserData(BaseViewHodler holder, int position) {
        TextView chatTime = holder.getView(R.id.item_chat_time_text);
        TextView userName = holder.getView(R.id.item_user_name_text);
        CircleButton userLogo = holder.getView(R.id.item_user_logo_image);
        TextView chatContent = holder.getView(R.id.item_chat_content_text);
        userName.setTextColor(0xff7068f5);

        CommendCmsEntry data = list.get(position);
        chatTime.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", data.getCreation_time() * 1000));
        userName.setText(data.getAuthor_nickname());
        GlideImgManager.getInstance().showImg(context, userLogo, data.getAuthor_avatar_url());
        chatContent.setText(data.getText());
    }
}
