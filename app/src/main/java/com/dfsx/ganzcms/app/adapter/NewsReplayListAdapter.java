package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.openimage.OpenImageUtils;

import java.net.URLDecoder;

/**
 * Created by  heyang on 2017/6/14.
 */
public class NewsReplayListAdapter extends BaseListViewAdapter<CommendCmsEntry> {
    IButtonClickListenr callback;

    public NewsReplayListAdapter(Context context) {
        super(context);
    }

    public boolean isInit() {
        return init;
    }

    private boolean init = false;

    @Override
    public int getCount() {
        return list == null ? 1 : list.size() == 0 ? 1 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public void setCallback(IButtonClickListenr callback) {
        this.callback = callback;
    }

    public boolean isHas() {
        boolean flag = false;
        if (list != null && !list.isEmpty())
            flag = true;
        return flag;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.nc_commend_replay_item;
    }

    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {
        setViewData(holder, position);
    }

    public void setViewData(BaseViewHodler hodler, int position) {
        View bodyView = hodler.getView(R.id.replay_item_hor);
        CircleButton logo = hodler.getView(R.id.replay_user_logo);
        TextView nameText = hodler.getView(R.id.replay_user_name);
        TextView titleText = hodler.getView(R.id.replay_title_value);
        TextView timeText = hodler.getView(R.id.replay_time_value);
        TextView numberText = hodler.getView(R.id.replay_count_text);
        TextView praiseTv = hodler.getView(R.id.comemndg_praise_txt);
        View praiseContain = hodler.getView(R.id.praise_container);
        praiseContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommendCmsEntry id = (long) v.getTag(R.id.tag_replay_cid);
//                _newsHelper.praiseforCommend(v, id, new DataRequest.DataCallbackTag() {
//                    @Override
//                    public void onSuccess(Object object, boolean isAppend, Object data) {
//                        if ((boolean) data) {
//                            getCommendData(pageoffset);
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(boolean isAppend, Object data) {
//
//                    }
//
//                    @Override
//                    public void onFail(ApiException e) {
//                        e.printStackTrace();
//                        ToastUtils.toastApiexceFunction(context, e);
//                    }
//                });
                CommendCmsEntry entry = (CommendCmsEntry) v.getTag(R.id.tag_replay_cid);
                if (callback != null)
                    callback.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData<CommendCmsEntry>(v, entry));

            }
        });
        ImageView thumb = hodler.getView(R.id.replay_thumb);
        ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
        replypub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommendCmsEntry entry = (CommendCmsEntry) view.getTag(R.id.tag_replay_cid);
                if (callback != null)
                    callback.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData<CommendCmsEntry>(view, entry));
            }
        });
        LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
        if (list == null || list.isEmpty()) {
            bodyView.setVisibility(View.GONE);
            return;
        } else {
            bodyView.setVisibility(View.VISIBLE);
        }
        CommendCmsEntry info = list.get(position);
        if (info == null) return;
        praiseTv.setText(StringUtil.getNumKString(info.getLike_count()));
        Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
        nameText.setText(info.getAuthor_nickname());
        String content = UtilHelp.getDecondeString(info.getText());
        titleText.setText(content);
        replypub.setTag(R.id.tag_replay_cid, info);
        praiseContain.setTag(R.id.tag_replay_cid, info);
        UtilHelp.setTimeDate(timeText,info.getCreation_time());
//        timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));
        bodyView.setTag(R.id.tag_replay_cid, info);
        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommendCmsEntry entry = (CommendCmsEntry) v.getTag(R.id.tag_replay_cid);
                if (callback != null)
                    callback.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData<CommendCmsEntry>(v, entry));
            }
        });
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) view.getTag(R.id.tag_replay_thumb);
                if (path != null && !TextUtils.isEmpty(path)) {
                    OpenImageUtils.openImage(view.getContext(), path, 0);
                }
            }
        });
        long nSubCommendNum = info.getSub_comment_count();
        if (nSubCommendNum > 0) {
            numberText.setVisibility(View.VISIBLE);
            numberText.setText(nSubCommendNum + "回复");
        } else {
            numberText.setVisibility(View.GONE);
        }
    }
}
