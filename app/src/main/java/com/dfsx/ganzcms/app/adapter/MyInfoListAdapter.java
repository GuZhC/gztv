package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ITabUserView;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;

/**
 * Created by liuwb on 2017/6/16.
 * 我的 页面的adapter
 */
public class MyInfoListAdapter extends BaseListViewAdapter<ITabUserView> {
    public MyInfoListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_my_info;
    }

    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {
        ImageView lableImage = holder.getView(R.id.item_image);
        TextView labelName = holder.getView(R.id.item_tv_name);
        ImageView labelContentImage = holder.getView(R.id.image_content);
        TextView labelContentText = holder.getView(R.id.tv_content);
        View divideView = holder.getView(R.id.line_8dp_divide);
        View messagePointView = holder.getView(R.id.item_message_point);

        ITabUserView data = list.get(position);
        if (TextUtils.isEmpty(data.getTabViewImageUrl())) {
            lableImage.setImageResource(data.getTabViewImageRes());
        } else {
            GlideImgManager.getInstance().showImg(context,
                    lableImage, data.getTabViewImageUrl());
        }
        labelName.setText(data.getTabViewText());
        if (TextUtils.isEmpty(data.getTabImageContent())) {
            labelContentImage.setVisibility(View.GONE);
        } else {
            labelContentImage.setVisibility(View.VISIBLE);
            GlideImgManager.getInstance().showImg(context,
                    labelContentImage, data.getTabImageContent());
        }
        labelContentText.setText(data.getTabTextContent());
        divideView.setVisibility(data.isShowDivideView() ? View.VISIBLE : View.GONE);
        messagePointView.setVisibility(data.isShowRightTopPoint() ? View.VISIBLE : View.GONE);
    }

}
