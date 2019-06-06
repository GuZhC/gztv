package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.act.CommunityAct;
import com.dfsx.ganzcms.app.act.CommunityNewAct;
import com.dfsx.ganzcms.app.model.PraistmpType;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ComunityFullVideoActivity;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.LiveServiceSharePopupwindow;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.openimage.OpenImageUtils;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang  on 2017/7/3
 */
public class CommuntyDatailHelper extends NewsDatailHelper {

    private Context mContext;
    TopicalApi mTopicalApi = null;
    SharePopupwindow sharePopupwindow;
    LiveServiceSharePopupwindow shareNewPopupwindow;
    IGetPraistmp mIGetPraistmp = null;
    private IsLoginCheck mloginCheck;
    private android.view.animation.Animation animation;

    public CommuntyDatailHelper(Context context) {
        super(context);
        this.mContext = context;
        mTopicalApi = new TopicalApi(context);
        mIGetPraistmp = IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_TOPIC);
        mloginCheck = new IsLoginCheck(context);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.add_score_anim);

    }

    public TopicalApi getmTopicalApi() {
        return mTopicalApi;
    }

    public IGetPraistmp getmIGetPraistmp() {
        return mIGetPraistmp;
    }

    public IsLoginCheck getMloginCheck() {
        return mloginCheck;
    }

    /**
     * 分享
     */
//    public void shareNewUiWnd(View rootView, final long cId, final String title, final String thumb) {
//        if (shareNewPopupwindow == null) {
//            shareNewPopupwindow = new LiveServiceSharePopupwindow(mContext);
//            shareNewPopupwindow.setOnShareItemClickListener(new LiveServiceSharePopupwindow.OnShareItemClickListener() {
//                @Override
//                public void onShareItemClick(SharePlatform platform) {
//                    if (platform == platform.QQ) {
//                        onSharePlatfrom(SharePlatform.QQ, cId, title, thumb);
//                    } else if (platform == platform.WeiBo) {
//                        onSharePlatfrom(SharePlatform.WeiBo, cId, title, thumb);
//                    } else if (platform == platform.Wechat) {
//                        onSharePlatfrom(SharePlatform.Wechat, cId, title, thumb);
//                    } else if (platform == platform.Wechat_FRIENDS) {
//                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, cId, title, thumb);
//                    }
//                }
//            });
//        }
//        shareNewPopupwindow.show(rootView);
//    }

    /**
     * 跳转到社区详情页
     *
     * @param item
     */
    /**
     * 跳转到社区详情页
     *
     * @param item
     */
    public void gotoComunnityInfo(TopicalEntry item) {
        gotoComunnityInfo(item, false, true);
    }

    public void gotoComunnityInfo(TopicalEntry item, boolean iscroolTop, boolean isCommedflag) {
        if (item == null) return;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong("tid", item.getId());
        bundle.putInt("attion", item.getRelationRole());
        bundle.putLong("aver_id", item.getAuthor_id());
        bundle.putLong("aver_id", item.getAuthor_id());
        bundle.putBoolean("scoll_top", iscroolTop);
        bundle.putBoolean("isCommedflag", isCommedflag);
        bundle.putLong("commeds_count", item.getReplyList() == null ? 0 : item.getReplyList().size());
        bundle.putLong("priase_count", item.getPraiseBeanList() == null ? 0 : item.getPraiseBeanList().size());
        String titles = item.getContent();
        if (!TextUtils.isEmpty(titles)) {
            if (titles.length() > 20) titles = titles.substring(0, 20);
        }
        bundle.putString("title", titles);
        if (item.getAttachmentInfos() != null) {
            if (!item.getAttachmentInfos().isEmpty()) {
                String imgss = "";
                for (int k = 0; k < item.getAttachmentInfos().size(); k++) {
                    if (item.getAttachmentInfos().get(k).getType() == 1) {
                        imgss += item.getAttachmentInfos().get(k).getUrl();
                        if (k < item.getAttachmentInfos().size() - 1)
                            imgss += ',';
                    } else {
                        if (item.getType() == 2)
                            bundle.putString("videoUrl", item.getAttachmentInfos().get(k).getUrl());
                    }
                }
                bundle.putString("imgs", imgss);
            }
        }
        mTopicalApi.addViewCount(item.getId());
//        intent.setClass(mContext, CommunityAct.class);
        intent.setClass(mContext, CommunityNewAct.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    /**
     * 分享
     */
//    public void shareWnd(View rootView, final long cId, final String title, final String thumb) {
//        if (sharePopupwindow == null) {
//            sharePopupwindow = new SharePopupwindow(mContext);
//            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
//                @Override
//                public void onShareClick(View v) {
//                    int vId = v.getId();
//                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
//                        onSharePlatfrom(SharePlatform.QQ, cId, title, thumb);
//                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
//                        onSharePlatfrom(SharePlatform.WeiBo, cId, title, thumb);
//                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
//                        onSharePlatfrom(SharePlatform.Wechat, cId, title, thumb);
//                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
//                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, cId, title, thumb);
//                    }
//                }
//            });
//        }
//        sharePopupwindow.show(rootView);
//    }
//    public void onSharePlatfrom(SharePlatform platform, long cId, String title, String thumb) {
//        ShareContent content = new ShareContent();
//        content.title = title;
//        content.thumb = thumb;
//        content.type = ShareContent.UrlType.WebPage;
//        content.url = App.getInstance().getCommuityShareUrl() + cId;
//        AbsShare share = ShareFactory.createShare(mContext, platform);
//        share.share(content);
//    }

    ////////////////////////////////////  UI  修改  ///////////////////////////////////
//    public  void  addCommunityFollowed(final long uId, final int role)
//    {
//        if (!getMloginCheck().checkLogin()) return;
////        int isAttion = mIGetPraistmp.isAttionUuser(uId, role);
//        mTopicalApi.attentionAuthor(uId, role, )
//    }
    public void setAttteonTextStatus(int falg, ImageView view) {
        if (falg == -1) {
            //如果发布的主题是自己，不显示关注
            view.setVisibility(View.GONE);
        } else if (falg == 1) {
            view.setVisibility(View.VISIBLE);
            view.setBackground(null);
            view.setImageResource(R.drawable.commuity_att_selected);
        } else if (falg == 2) {
            view.setVisibility(View.VISIBLE);
            view.setBackground(null);
        } else {
            view.setVisibility(View.VISIBLE);
//            view.setTextColor(getActivity().getResources().getColor(R.color.ewd_descibr_font));
//            view.setBackground(getActivity().getResources().getDrawable(R.drawable.commonirty_item_attent_bankground));
//            view.setText(
//                    Html.fromHtml("<b>+ </b>" + "关注"));
            view.setImageResource(R.drawable.commuity_att_normal);
        }
    }

    /**
     * 更改关注状态图片
     */
    public void setAttteonMarkStatus(int falg, ImageView view) {
        if (falg == -1) {
            view.setVisibility(View.GONE);
        } else if (falg == 1) {
            view.setVisibility(View.VISIBLE);
            view.setBackground(null);
            view.setImageResource(R.drawable.commuity_att_selected);
        } else if (falg == 2) {
            view.setVisibility(View.VISIBLE);
            view.setBackground(null);
        } else {
            view.setVisibility(View.VISIBLE);
//            view.setTextColor(getActivity().getResources().getColor(R.color.ewd_descibr_font));
//            view.setBackground(getActivity().getResources().getDrawable(R.drawable.commonirty_item_attent_bankground));
//            view.setText(
//                    Html.fromHtml("<b>+ </b>" + "关注"));
            view.setImageResource(R.drawable.commuity_att_normal);
        }
    }

    /**
     * 更改关注状态图片
     */
    public void setAttteonStatus(boolean falg, ImageView view) {
        if (falg) {
            view.setImageResource(R.drawable.commuity_att_selected);
        } else {
            view.setImageResource(R.drawable.commuity_att_normal);
        }
    }

    public void setPrsiseStatus(ImageView tagView, boolean flag, boolean isShowMsg) {
        String msg = "点赞成功";
        if (flag) {
            tagView.setImageResource(R.drawable.communtiy_item_praise_sel);
        } else {
            msg = "取消点赞";
            tagView.setImageResource(R.drawable.commuity_parise_normal);
        }
        if (isShowMsg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
        }
    }

    public void setFavStatus(ImageView tagView, boolean flag, boolean isShowMsg) {
        if (tagView == null) return;
        String msg = "收藏成功";
        if (flag) {
            tagView.setImageResource(R.drawable.communtiy_item_fal_sel);
        } else {
            msg = "取消收藏成功";
            tagView.setImageResource(R.drawable.communtiy_item_falnoral);
        }
        if (isShowMsg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
        }
    }

    ////////////////////////  leshi 2.0  AOPI

    public void praiseforTopicCommend(View view, long commendId, DataRequest.DataCallbackTag callbackTag) {
        if (!mloginCheck.checkLogin()) return;
        mTopicalApi.praiseforCommend(view, commendId, callbackTag);
    }

    /**
     * 添加 关注
     *
     * @param uId
     * @param role
     * @param view
     */
    public void setAttentionUser(final long uId, final int role, final ImageView view) {
        mTopicalApi.attentionAuthor(uId, role, new DataRequest.DataCallbackTag<Boolean>() {
            @Override
            public void onSuccess(Object object, boolean isAppend, Boolean data) {
                if (object == null) return;
                final ImageView bg = (ImageView) object;
                Observable.just((Boolean) data).
                        subscribeOn(Schedulers.io()).
                        observeOn(Schedulers.io()).
                        map(new Func1<Boolean, Integer>() {
                            @Override
                            public Integer call(Boolean aBoolean) {
                                return 0;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                                       @Override
                                       public void onCompleted() {
                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                                           boolean flag = role == 0 ? true : false;
                                           RXBusUtil.sendConcernChangeMessage(flag, 1);
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           e.printStackTrace();
                                       }

                                       @Override
                                       public void onNext(Integer result) {
                                           setAttteonTextStatus(role, bg);
                                       }
                                   }
                        );
            }

            @Override
            public void onSuccess(boolean isAppend, Boolean data) {

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    public void setNewAttentionUser(final long uId, final int role, DataRequest.DataCallback callback) {
        mTopicalApi.attentionAuthor(uId, role, callback);
    }

    /**
     * 收藏
     */
    public void addFavritory(boolean bfalg, long cId, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        mTopicalApi.farityToptic(cId, !bfalg, callback);
    }

    /**
     * 点赞按钮
     *
     * @param id
     * @param aimal
     */

    public void praiseLbtCLick(final long id, final TextView aimal, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
//        final boolean flag = getmIGetPraistmp().isPriseFlag(id);
//        if (flag) {
//            LSUtils.toastMsgFunction(mContext, "已经点赞过了");
//            return;
//        }
        mTopicalApi.pariseToptic(id, callback);
    }

    /**
     * 写评论
     *
     * @param id
     * @param refId
     * @param content
     */
    public void writeCommend(long id, long refId, String content, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
//        if (!App.getInstance().checkIsVerity()) return;
        mTopicalApi.createComment(id, 0, content, null, callback);
    }

    /***
     *   点赞
     * @param cId
     * @param callback
     */
    public void addPraiseNmber(long cId, DataRequest.DataCallback callback) {
        if (!mloginCheck.checkLogin()) return;
        boolean flag = mIGetPraistmp.isPriseFlag(cId);
        if (flag) {
            Toast.makeText(mContext, "已经点赞过了", Toast.LENGTH_SHORT).show();
            return;
        }
        mTopicalApi.pariseToptic(cId, callback);
    }

    /**
     * 设置关注API
     */
    public void upAttentionChanged(long uID, final int role, DataRequest.DataCallback back) {
        mTopicalApi.attentionAuthor(uID, role, back);
    }

    public void setMulitStringArr(LinearLayout mainVew, String[] s, String imgs) {
        if (s != null && s.length > 0) {
            List<Attachment> dlist = new ArrayList<Attachment>();
            for (int i = 0; i < s.length; i++) {
                Attachment at = new Attachment();
                at.setThumbnail_url(s[0]);
                dlist.add(at);
            }
            setMulitpImage(mainVew, dlist, imgs);
        }
    }

    /**
     * 圈子 创建多图   老版本

    public void setMulitpImage(LinearLayout mainVew, List<Attachment> dlist, String imgs) {
        if(mainVew!=null) mainVew.removeAllViews();
        int line = 2;
        int count = 3;
        int pos = 0;
        boolean isSingle = false;
        boolean flag = true;
        if (dlist.size() == 4) {
            count = 2;
        } else if (dlist.size() < 4) {
            line = 1;
            if (dlist.size() == 1) isSingle = true;
        }
        for (int i = 0; i < line; i++) {
            LinearLayout mHorView = createHorvertyLay(i);
            for (int k = 0; k < count; k++) {
                if (pos < dlist.size()) {
                    if (isSingle)
                        flag = dlist.get(pos).getWidth() >= dlist.get(0).getHeight() ? false : true;
                    ImageView bg = createPixImag(pos, dlist.get(pos).getType(), dlist.get(pos).getUrl(), imgs, isSingle, flag, dlist.get(pos).getWidth() == dlist.get(0).getHeight() ? true : false);
                    mHorView.addView(bg);
                    pos++;
                }
            }
            mainVew.addView(mHorView);
        }
    }  */

    public void setMulitpImage(LinearLayout mainVew, List<Attachment> dlist, String imgs) {
        if(mainVew!=null) mainVew.removeAllViews();
        int line = 3;
        int count = 3;
        int pos = 0;
        boolean isSingle = false;
        boolean flag = true;
        if (dlist.size() == 4) {
            count = 2;
        } else if (dlist.size() < 4) {
            line = 1;
            if (dlist.size() == 1) isSingle = true;
        }
        for (int i = 0; i < line; i++) {
            LinearLayout mHorView = createHorvertyLay(i);
            for (int k = 0; k < count; k++) {
                if (pos < dlist.size()) {
                    Attachment attr = dlist.get(pos);
//                    if (isSingle)
//                        flag = dlist.get(pos).getWidth() >= dlist.get(0).getHeight() ? false : true;
//                    ImageView bg = createPixImag(pos, dlist.get(pos).getType(), dlist.get(pos).getUrl(), imgs, isSingle, flag, dlist.get(pos).getWidth() == dlist.get(0).getHeight() ? true : false);
                    ImageView bg = createPixImag(pos, attr, imgs, isSingle);
                    mHorView.addView(bg);
                    pos++;
                }
            }
            mainVew.addView(mHorView);
        }
    }

    public LinearLayout createHorvertyLay(int i) {
        LinearLayout mLayout = new LinearLayout(mContext);
//                    mLayout.setWeightSum(1.0f);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    params.leftMargin = ((int) (5.0F * dm.density));
//                    params.rightMargin = ((int) (5.0F * dm.density));
        params.gravity = Gravity.LEFT;
        if (i >= 1)
            params.topMargin = Util.dp2px(mContext, 6);
        mLayout.setLayoutParams(params);
        return mLayout;
    }

    /**  老版本
    public LinearLayout createHorvertyLay(int i) {
        LinearLayout mLayout = new LinearLayout(mContext);
//                    mLayout.setWeightSum(1.0f);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    params.leftMargin = ((int) (5.0F * dm.density));
//                    params.rightMargin = ((int) (5.0F * dm.density));
        params.gravity = Gravity.LEFT;
        if (i == 1)
            params.topMargin = Util.dp2px(mContext, 8);
        mLayout.setLayoutParams(params);
        return mLayout;
    }  **/

    /***  老版本
    public ImageView createPixImag(int pos, int type, String path, final String urls, boolean isSingle, boolean isVerty, boolean isSame) {
        ImageView bg = new ImageView(mContext);
        bg.setBackgroundColor(mContext.getResources().getColor(R.color.img_default_bankgrond_color));
//        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(nItmeWidh, (int) (nItmeWidh * 3 / 4), 1.0f);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //控件距离其右侧控件的距离，此处为60
        int width = 110; //ppi
        int height = 110;
        if (isSingle) {
            if (!isVerty) {
                if (isSame) {
                    width = 223;
                    height = 223;
                } else {
                    //heng
                    width = 223;
                    height = 167;
                }
            } else {
                width = 167;
                height = 223;
            }
        }
        lp1.width = Util.dp2px(mContext, width);   //dp
        lp1.height = Util.dp2px(mContext, height);
        if (type == 1)
            path += "?w=" + lp1.width + "&h=" + lp1.height + "&s=1";
        lp1.setMargins(0, 0, 10, 0);
        bg.setLayoutParams(lp1);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (type == 1) {
            Glide.with(mContext)
                    .load(path)
                    .asBitmap()
//                .override(width, height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
                    .centerCrop()//对图片进行裁剪
//                    .placeholder(R.drawable.glide_default_image)//加载图片之前显示的图片
                    .error(R.drawable.glide_default_image)//加载图片失败的时候显示的默认图
//                    .crossFade()//让图片显示的时候带有淡出的效果
                    .into(bg);
//        } else {
//            bg.setBackground(mContext.getResources().getDrawable(R.drawable.glide_default_image));
//            myVideoThumbLoader.showThumbByAsynctack(path, bg);
//        }

//        Util.LoadThumebImage(bg, path, null);
        bg.setTag(R.id.commny_bg_position, pos);
        bg.setTag(R.id.commny_bg_urls, urls);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.commny_bg_position);
                String imgs = (String) view.getTag(R.id.commny_bg_urls);
                if (imgs == null || TextUtils.isEmpty(imgs)) {

                } else {
                    OpenImageUtils.openImage(mContext, imgs, position);
//                    context.overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                }
            }
        });
        return bg;
    }
    **/

    public ImageView createPixImag(int pos, Attachment attr, String urls, boolean isSingle) {
        int width = Util.dp2px(mContext, 79); //ppi
        int height = width;
        String path = "";
        ImageView bg = new ImageView(mContext);
        if (attr != null) {
            boolean isVerty = true;
            boolean isSame = false;
            if (isSingle)
                isVerty = attr.getWidth() >= attr.getHeight() ? false : true;
            isSame = attr.getWidth() == attr.getHeight() ? true : false;
            path = attr.getUrl();
            if (attr.getType() == 2 || TextUtils.isEmpty(path))
                path = attr.getThumbnail_url();
//        bg.setBackgroundColor(mContext.getResources().getColor(R.color.img_default_bankgrond_color));
            if (isSingle) {
                if (!isVerty) {
                    //水平放置  高固定
                    if (isSame) {
                        width = Util.dp2px(mContext, 146);
                        height = Util.dp2px(mContext, 146);
                    } else {
//                    width = 223;
//                    height = 167;
                        width = Util.dp2px(mContext, 146);
                        height = width * attr.getHeight() / attr.getWidth();
                    }
                } else {
                    // 垂直放  宽度固定  算高
                    width = Util.dp2px(mContext, 165);
                    height = width * attr.getHeight() / attr.getWidth();
//                height = 223;
                }
            }
        } else {
            width = Util.dp2px(mContext, 146);
            height = Util.dp2px(mContext, 146);
        }
        path += "?w=" + width + "&h=" + height + "&s=0";
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(width, height);
        lp1.setMargins(0, 0, Util.dp2px(mContext, 6), 0);
        bg.setLayoutParams(lp1);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (type == 1) {
        Glide.with(mContext)
                .load(path)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
//                .centerCrop()//对图片进行裁剪
                .placeholder(R.drawable.glide_default_image)//加载图片之前显示的图片
                .error(R.drawable.glide_default_image)//加载图片失败的时候显示的默认图
//                .crossFade()//让图片显示的时候带有淡出的效果
                .into(bg);
//        } else {
//            bg.setBackground(mContext.getResources().getDrawable(R.drawable.glide_default_image));
//        }
        bg.setTag(R.id.commny_bg_position, pos);
        bg.setTag(R.id.commny_bg_urls, urls);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.commny_bg_position);
                String imgs = (String) view.getTag(R.id.commny_bg_urls);
                if (!(imgs == null || TextUtils.isEmpty(imgs))) {
                    OpenImageUtils.openImage(mContext, imgs, position);
//                    String[] arr = imgs.split(",");
//                    List<String> list = Arrays.asList(arr);
//                    OpenImageUtils.open(mContext, list, position);
                }
            }
        });
        return bg;
    }

    /**   老版本
    public void createVideoContainer(LinearLayout mainVew, Attachment att) {
        RelativeLayout mHorView = createVideoHorvertyLay();
        boolean flag = att.getWidth() >= att.getHeight() ? false : true;
        ImageView bg = createPixImag(0, 1, att.getThumbnail_url(), "", true, flag, false);
        mHorView.addView(bg);
        ViewGroup.LayoutParams lp = bg.getLayoutParams();
        View v = createPlayButton(lp, att.getUrl());
        mHorView.addView(v);
        mainVew.addView(mHorView);
    }  **/

    public void createVideoContainer(LinearLayout mainVew, Attachment att) {
        RelativeLayout mHorView = createVideoHorvertyLay();
//        ImageView bg = createPixImag(0, 1, att.getThumbnail_url(), "", true, flag, false);
        ImageView bg = createPixImag(0, att, "", true);
        mHorView.addView(bg);
        ViewGroup.LayoutParams lp = bg.getLayoutParams();
        View v = createPlayButton(lp, att.getUrl());
        mHorView.addView(v);
        mainVew.addView(mHorView);
    }

    public RelativeLayout createVideoHorvertyLay() {
        RelativeLayout mLayout = new RelativeLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = Util.dp2px(mContext, 8);
        mLayout.setLayoutParams(params);
        return mLayout;
    }

    public View createPlayButton(ViewGroup.LayoutParams lp, String url) {
        RelativeLayout mLayout = new RelativeLayout(mContext);
        mLayout.setGravity(Gravity.CENTER);
        mLayout.setLayoutParams(lp);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.dp2px(mContext, 60), Util.dp2px(mContext, 60));
        ImageView img = new ImageView(mContext);
        img.setImageResource(R.drawable.video_head_icon);
        img.setTag(url);
        mLayout.addView(img, params);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = (String) v.getTag();
                if (str != null && !TextUtils.isEmpty(str)) {
                    playVideo(str);
//                    Uri url = Uri.parse(str);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(url, "video/mp4");
//                    try {
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "没有默认播放器", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
                } else {
                    Toast.makeText(mContext, "视频地址无效", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return mLayout;
    }

    public void playVideo(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            Toast.makeText(mContext, "播放失败,视频地址无效!", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (getVideoPlyer() != null) {
//            getVideoPlyer().stop();
//            getVideoPlyer().release();
//            removeVideoPlayer();
//            if (getVideoPlyer().getTag() != null) {
//                View tag = (View) getVideoPlyer().getTag();
//                tag.setVisibility(View.VISIBLE);
//            }
//        }
//        addVideoPlayerToFullScreenContainer(getVideoPlyer());
//        act.addVideoPlayerToFullScreenContainer(getVideoPlyer());
//        playVideoIndex = holder.pos;
//        addVideoPlayerToFullContainer(getVideoPlyer(), fullScreenLayout);
//        holder.forgrondlay.setVisibility(View.GONE);
//        getVideoPlyer().setTag(holder.forgrondlay);
//        getVideoPlyer().start(url);

        Intent intent = new Intent(mContext, ComunityFullVideoActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);
    }

    public void initTabGroupLayout(TabGrouplayout mTagLayout, final List<String> arr) {
        if (arr == null || arr.isEmpty()) {
            mTagLayout.setVisibility(View.GONE);
            return;
        }
        mTagLayout.removeAllViewsInLayout();

        mTagLayout.setVisibility(View.VISIBLE);

        /**
         * 创建 textView数组
         */
        final TextView[] textViews = new TextView[arr.size()];

        for (int i = 0; i < arr.size(); i++) {

            final int pos = i;

            final View view = (View) LayoutInflater.from(mContext).inflate(R.layout.communti_tag_text, mTagLayout, false);

            final TextView text = (TextView) view.findViewById(R.id.txt_item);  //查找  到当前     textView


            // 将     已有标签设置成      可选标签
            text.setText(arr.get(i));
            /**
             * 将当前  textView  赋值给    textView数组
             */
            textViews[i] = text;

            mTagLayout.addView(view);
        }
    }

    public void initUserGroupLayout(LinearLayout mTagLayout, final List<TopicalEntry.PraiseBean> arr) {
        if (arr == null || arr.isEmpty()) {
            mTagLayout.setVisibility(View.GONE);
            return;
        }
        mTagLayout.setVisibility(View.VISIBLE);

        mTagLayout.removeAllViewsInLayout();

        /**
         * 创建 textView数组
         */
//           final CircleButton[] textViews = new CircleButton[arr.size()];
        int i = 0;
        int len = arr.size() >= 5 ? 5 : arr.size();
        do {
            final View view = (View) LayoutInflater.from(mContext).inflate(R.layout.communti_tag_circleimg, mTagLayout, false);
            final ImageView userImg = (ImageView) view.findViewById(R.id.visit_user_imge);  //查找  到当前     textView
//                CircleButton  userImg=new CircleButton(getActivity());
//                LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(25, 25);
//                mParams.setMargins(10, 0, 10, 0);//设置小圆点左右之间的间隔
//                userImg.setLayoutParams(mParams);
            TopicalEntry.PraiseBean bena = arr.get(i);
//            Util.LoadThumebImage(userImg, "http://himg.bdimg.com/sys/portrait/item/f0db1261.jpg", null);
//            Util.LoadThumebImage(userImg, bena.getAvatar_url(), null);
            LSLiveUtils.showUserLogoImage(mContext, userImg, bena.getAvatar_url());
            userImg.setTag(R.id.cirbutton_user_id, bena.getUser_id());
//                textViews[i] = userImg;
            mTagLayout.addView(view);
            i++;
        } while (i < len);
    }

}
