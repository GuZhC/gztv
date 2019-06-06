package com.dfsx.procamera.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.procamera.R;
import com.dfsx.procamera.model.CommendEntry;

import java.util.List;

/**
 * Created by  heyang on 2017/6/14.
 */
public class CommendReplayAdapter extends BaseListViewAdapter<CommendEntry> implements View.OnClickListener {


    IButtonClickListenr callBack;
    private long rootId = -1;

    public CommendReplayAdapter(Context context) {
        super(context);
    }

    public void setCallBack(IButtonClickListenr callBack) {
        this.callBack = callBack;
    }

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    @Override
    public int getItemViewLayoutId() {
//        return R.layout.community_replay_item;
        return R.layout.commend_item;
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
                CommendEntry object = (CommendEntry) v.getTag(R.id.tag_replay_cid);
                if (callBack != null) {
                    callBack.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(v, object));
                }
//                long id = (long) v.getTag(R.id.tag_replay_cid);
//                newsDatailHelper.praiseforCmsCommend(v, id, new DataRequest.DataCallbackTag() {
//                    @Override
//                    public void onSuccess(Object object, boolean isAppend, Object data) {
//                        if (object != null) {
//                            if (object instanceof RelativeLayout) {
//                                View parent = (View) ((RelativeLayout) object).getParent();
//                                TextView txt = (TextView) parent.findViewById(R.id.comemndg_praise_txt);
//                                if (txt != null) {
//                                    long num = 0;
//                                    String numtxt = txt.getText().toString().trim();
//                                    if (numtxt != null && !TextUtils.isEmpty(numtxt)) {
//                                        num = Long.parseLong(numtxt);
//                                        num++;
//                                    }
//                                    txt.setText(num + "");
//                                }
//                            }
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
            }
        });
        ImageView thumb = hodler.getView(R.id.replay_thumb);
        ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
        replypub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommendEntry object = (CommendEntry) view.getTag(R.id.tag_replay_cid);
                if (callBack != null) {
                    callBack.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData(view, object));
                }
            }
        });
        View view = (View) logo.getParent();
        view.setOnClickListener(this);
        LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
        CommendEntry info = list.get(position);
        view.setTag(info);
//            GlideImgManager.getInstance().showImg(context, logo, info.getAuthor_avatar_url());
        Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
        praiseTxt.setText(StringUtil.getNumKString(info.getLike_count()));

        nameText.setText(info.getAuthor_nickname());

        if (info.getRef_comments() != null) {
            int  size=info.getRef_comments().size()<=1?0:info.getRef_comments().size()-1;
            CommendEntry.RefCommentsBean bean = info.getRef_comments().get(size);
            if (bean.getId() == rootId) {
                titleText.setText(info.getText());
            } else {
                String replyNickName = info.getText();
                String commentNickName = "@" + bean.getAuthor_nickname();
                SpannableString ss = null;
                ss = new SpannableString(replyNickName + "//" + commentNickName + ":" + bean.getText());
                if (!TextUtils.isEmpty(replyNickName) && !TextUtils.isEmpty(replyNickName)) {
//                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                titleText.setText(ss);
            }
        }

        replypub.setTag(R.id.tag_replay_cid, info);
        praiseBtn.setTag(R.id.tag_replay_cid, info);
//        timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));
        timeText.setText(StringUtil.getDayTimeText(info.getCreation_time()));
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) view.getTag(R.id.tag_replay_thumb);
                if (path != null && !TextUtils.isEmpty(path)) {
                    OpenImageUtils.openImage(view.getContext(), path, 0);
                }
            }
        });
        List<CommendEntry.SubCommentsBean> reppls = info.getmSubCommendList();
        if (reppls != null && reppls.size() > 0) {
            numberText.setVisibility(View.VISIBLE);
            numberText.setText(info.getSub_comment_count() + "回复");
        } else {
            numberText.setVisibility(View.GONE);
            numberText.setText("回复");
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof CommendEntry) {
            CommendEntry room = (CommendEntry) tag;
            if (callBack != null) {
                callBack.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData(v, room));
            }
        }
    }

    public interface OnCommendReplaylister {
        public void OnItemClick(View v, CommendEntry object);
    }


}
