package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.openimage.OpenImageUtils;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by  heyang on 2017/6/14.
 */
public class TopicCommendListAdapter extends BaseListViewAdapter<ReplyEntry> {

    private long rootId = -1;
    IButtonClickListenr callback;

    public TopicCommendListAdapter(Context context) {
        super(context);
    }

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

//    @Override
//    public int getCount() {
//        return list == null ? 1 : list.size() == 0 ? 1 : list.size();
//    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.nc_commend_replay_item;
    }

    public void setCallback(IButtonClickListenr callback) {
        this.callback = callback;
    }

    @Override
    public void setItemViewData(final BaseViewHodler hodler, final int position) {
        final CircleButton logo = hodler.getView(R.id.replay_user_logo);
        TextView nameText = hodler.getView(R.id.replay_user_name);
        TextView titleText = hodler.getView(R.id.replay_title_value);
        TextView timeText = hodler.getView(R.id.replay_time_value);
        TextView numberText = hodler.getView(R.id.replay_count_text);
        TextView praiseTxt = hodler.getView(R.id.comemndg_praise_txt);
        View praiseBtn = hodler.getView(R.id.praise_container);
        praiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyEntry object = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                if (callback != null) {
                    callback.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(v, object));
                }
            }
        });
        ImageView thumb = hodler.getView(R.id.replay_thumb);
        ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
        replypub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplyEntry object = (ReplyEntry) view.getTag(R.id.tag_replay_cid);
                if (callback != null) {
                    callback.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(view, object));
                }
            }
        });
        View view = (View) logo.getParent();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyEntry object = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                if (callback != null) {
                    callback.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(v, object));
                }
            }
        });
        LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
        if (list == null || list.isEmpty()) {
            view.setVisibility(View.GONE);
            return;
        } else {
            view.setVisibility(View.VISIBLE);
        }
        ReplyEntry info = list.get(position);
        if (info == null || info.getId() == 0) return;
        Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
        praiseTxt.setText(StringUtil.getNumKString(info.getLike_count()));

        nameText.setText(info.getAuthor_nickname());

        List<ReplyEntry.RefRepliesBean> list = info.getRef_replies();
        if (list != null) {
            int pos = list.size() > 1 ? list.size() - 1 : 0;
            ReplyEntry.RefRepliesBean bean = info.getRef_replies().get(pos);
            if (bean.getId() == rootId) {
                String  s=UtilHelp.getDecondeString(info.getContent());
                titleText.setText(s);
            } else {
                String  s=UtilHelp.getDecondeString(bean.getContent());

//                String replyNickName = info.getContent();
                String replyNickName = UtilHelp.getDecondeString(info.getContent());   // 新版本

                String commentNickName = "@" + bean.getAuthor_nickname();
                SpannableString ss = null;
//                ss = new SpannableString(replyNickName + "//" + commentNickName + ":" + bean.getContent());
                ss = new SpannableString(replyNickName + "//" + commentNickName + ":" + s);   //兼容emoj表情
                if (!TextUtils.isEmpty(replyNickName) && !TextUtils.isEmpty(replyNickName)) {
//                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                titleText.setText(ss);
            }
        }else {
            titleText.setText(info.getContent());
        }
//        if (info.getSub_reply_count() > 0) {
//            ReplyEntry.RefRepliesBean bean = info.getRef_replies().get(0);
//            if (bean.getId() == rootId) {
//                titleText.setText(info.getContent());
//            } else {
//                String replyNickName = info.getAuthor_nickname();
//                String commentNickName = "@" + info.getAuthor_nickname();
//                SpannableString ss = null;
//                ss = new SpannableString(replyNickName + "//" + commentNickName + ":" + bean.getAuthor_nickname());
//                if (!TextUtils.isEmpty(replyNickName) && !TextUtils.isEmpty(replyNickName)) {
////                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                }
//                titleText.setText(ss);
//            }
//        } else {
//            titleText.setText(info.getContent());
//        }
//        }
        view.setTag(R.id.tag_replay_cid, info);
        replypub.setTag(R.id.tag_replay_cid, info);
        praiseBtn.setTag(R.id.tag_replay_cid, info);
        timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getPost_time() * 1000));
        if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
            thumb.setVisibility(View.VISIBLE);
            String path = info.getMthumImage();
//                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
            path += "?w=" + 120 + "&h=" + 90 + "&s=1";
            Util.LoadThumebImage(thumb, info.getMthumImage(), null);
            thumb.setTag(R.id.tag_replay_thumb, info.getMthumImage());
        } else {
            thumb.setVisibility(View.GONE);
        }
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) view.getTag(R.id.tag_replay_thumb);
                if (path != null && !TextUtils.isEmpty(path)) {
                    OpenImageUtils.openImage(view.getContext(), path, 0);
                }
            }
        });
//        List<CommendCmsEntry.SubCommentsBean> reppls = info.getmSubCommendList();
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
//        if (reppls != null && reppls.size() > 0) {
//            numberText.setVisibility(View.VISIBLE);
//            numberText.setText(info.getSub_comment_count() + "回复");
//        } else {
//            numberText.setVisibility(View.GONE);
//            numberText.setText("回复");
//        }
//        if (rootId == -1 && info.getSub_reply_count() > 0) {
//            numberText.setVisibility(View.VISIBLE);
//            numberText.setText(info.getSub_reply_count() + "回复");
//        } else {
//            numberText.setVisibility(View.GONE);
//        }
    }
}
