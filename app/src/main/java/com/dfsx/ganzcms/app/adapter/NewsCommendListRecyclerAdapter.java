package com.dfsx.ganzcms.app.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseEmptyViewRecyclerAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ICommendReplaylister;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.model.ContentComment;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.openimage.OpenImageUtils;

import java.util.List;

public class NewsCommendListRecyclerAdapter extends BaseEmptyViewRecyclerAdapter<ContentComment> implements View.OnClickListener,
        BaseRecyclerViewHolder.OnRecyclerItemViewClickListener {

    private ICommendReplaylister<ContentComment> callBack;
    private long rootId = -1;

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    public void setCallBack(ICommendReplaylister<ContentComment> callBack) {
        this.callBack = callBack;
    }

    @Override
    public View getItemView(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_replay_item, null);
        return v;
    }

    @Override
    public void onBindItemView(BaseRecyclerViewHolder hodler, int position) {
        //        View itemView = hodler.getView(R.id.replay_item_hor);
        hodler.getConvertView().setTag(list.get(position));
        hodler.setOnRecyclerItemViewClickListener(this);
        //        itemView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if (itemViewClickListener != null) {
        //                    itemViewClickListener.onItemViewClick(v);
        //                }
        //            }
        //        });

        CircleButton logo = hodler.getView(R.id.replay_user_logo);
        TextView nameText = hodler.getView(R.id.replay_user_name);
        TextView titleText = hodler.getView(R.id.replay_title_value);
        TextView timeText = hodler.getView(R.id.replay_time_value);
        TextView numberText = hodler.getView(R.id.replay_count_text);
        ImageView thumb = hodler.getView(R.id.replay_thumb);
        ImageButton replypub = hodler.getView(R.id.disclosure_replay_btn);
        replypub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentComment object = (ContentComment) view.getTag(R.id.tag_replay_cid);
                if (callBack != null) {
                    callBack.OnItemClick(view, object);
                }
            }
        });
        //        View view = (View) logo.getParent();
        //        view.setOnClickListener(this);
        //        view.setTag(info);
        LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
        //            setCountText(itemCountText, position);
        ContentComment info = list.get(position);
        //            GlideImgManager.getInstance().showImg(context, logo, info.getAuthor_avatar_url());
        Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);

        nameText.setText(info.getAuthor_nickname());

        if (info.getReferenceComment() != null) {
            ContentComment bean = info.getReferenceComment();
            if (rootId == 0 || bean.getId() == rootId) {
                titleText.setText(info.getText());
            } else {
                String replyNickName = info.getText();
                String commentNickName = "@" + info.getAuthor_nickname();
                SpannableString ss = null;
                ss = new SpannableString(replyNickName + "//" + commentNickName + ":" + bean.getText());
                if (!TextUtils.isEmpty(replyNickName) && !TextUtils.isEmpty(replyNickName)) {
                    //                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                titleText.setText(ss);
            }
        } else {
            titleText.setText(info.getText());
        }


        replypub.setTag(R.id.tag_replay_cid, info);
        UtilHelp.setTimeDate(timeText,info.getCreation_time());
//        timeText.setText(StringUtil.getTimeAgoText(info.getCreation_time()));

        //            if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
        //                thumb.setVisibility(View.VISIBLE);
        //                String path = info.getMthumImage();
        //                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
        //                Util.LoadThumebImage(thumb, info.getMthumImage(), null);
        //                thumb.setTag(R.id.tag_replay_thumb, info.getMthumImage());
        //            }
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) view.getTag(R.id.tag_replay_thumb);
                if (path != null && !TextUtils.isEmpty(path)) {
                    OpenImageUtils.openImage(view.getContext(), path, 0);
                }
            }
        });
        List<ContentComment> reppls = info.getSubCommentList();
        //            if (reppls != null && !reppls.isEmpty()) {
        //                if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
        //                extendlay.setVisibility(View.VISIBLE);
        //                for (CommendCmsEntry.RefCommentsBean bean : reppls) {
        //                    View view = createChildReplay(bean.getAuthor_nickname(), info.getAuthor_nickname(), bean.getText());
        //                    extendlay.addView(view);
        //                }
        //            } else {
        //                extendlay.removeAllViews();
        //                extendlay.setVisibility(View.GONE);
        //            }
        if (reppls != null && reppls.size() > 0) {
            numberText.setText(info.getSub_comment_count() + "回复");
        } else {
            numberText.setText("回复");
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof CommendCmsEntry) {
            ContentComment contentComment = (ContentComment) tag;
            if (callBack != null) {
                callBack.OnItemClick(v, contentComment);
            }
        }
    }

    private BaseRecyclerViewHolder.OnRecyclerItemViewClickListener itemViewClickListener;

    public void setOnItemViewClickLisenter(BaseRecyclerViewHolder.OnRecyclerItemViewClickListener lisenter) {
        this.itemViewClickListener = lisenter;
    }


    @Override
    public void onItemViewClick(View v) {
        if (itemViewClickListener != null) {
            itemViewClickListener.onItemViewClick(v);
        }
    }
}
