package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.act.CvideoPlayAct;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.IGetPriseManager;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.CenterAlignImageSpan;
import com.dfsx.ganzcms.app.view.RadiusBackgroundSpan;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * heyang  2016-9-16
 */

public class ListViewAdapter extends BaseAdapter {
    private static final int TYPE_NEWS = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_ACTIVIRY = 2;
    private static final int TYPE_GROUP = 3;
    private static final int TYPE_AD = 4;
    private static final int TYPE_LIVE = 5;
    private static final int TYPE_SCROLL = 6;
    private static final int TYPE_SPEICAL = 7;
    private static final int TYPE_DYNSIC = 8;
    private static final int TYPE_EMERGENCY = 9;    //应急消息类型
    private static final int TYPE_COUNT = 13;
    private final String STATE_LIST = "ListAdapter.mlist";
    private ArrayList<ContentCmsEntry> items = new ArrayList<ContentCmsEntry>();
    public LayoutInflater inflater;
    public boolean bInit;
    Context mContext = null;
    private int dpNews_width, dpNews_height;
    private int dpgp_nItmeWidh, dpgp_nItemHeight;
    private int screen;
    private int dpVideo_width, dpVideo_heigth;
    private int dpAd_width, dpad_heigth;


    public int getPlayVideoIndex() {
        return playVideoIndex;
    }

    public void setPlayVideoIndex(int playVideoIndex) {
        this.playVideoIndex = playVideoIndex;
    }

    int playVideoIndex = -1;   //记录播放器的位置

    public ListViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        bInit = false;
        this.mContext = context;
        screen = UtilHelp.getScreenWidth(mContext);
        dpVideo_width = screen - Util.dp2px(mContext, 26);
        dpVideo_heigth = Util.dp2px(mContext, 204);
        dpNews_width = Util.dp2px(mContext, 107);
        dpNews_height = Util.dp2px(mContext, 70);
        dpgp_nItmeWidh = Util.dp2px(mContext, 112);
        dpgp_nItemHeight = Util.dp2px(mContext, 70);
        dpAd_width = screen - Util.dp2px(mContext, 26);
        dpad_heigth = Util.dp2px(mContext, 118);
    }

    public void init(Bundle savedInstanceState) {
        ArrayList<ContentCmsEntry> sList;
        sList = (ArrayList<ContentCmsEntry>) savedInstanceState.getSerializable(STATE_LIST);
        if (sList != null) {
            items = sList;
            notifyDataSetChanged();
            bInit = true;
        }
    }


    public void SetInitStatus(boolean flag) {
        bInit = flag;
    }

    public void saveInstanceState(Bundle outState) {
        if (bInit) {
            outState.putSerializable(STATE_LIST, items);
        }
    }

    public boolean isInited() {
        return bInit;
    }

    public void update(ArrayList<ContentCmsEntry> data, boolean bAddTail) {
        if (data != null && !data.isEmpty()) {
            boolean noData = false;
            if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                if (items.size() >= data.size()
                        && items.get(items.size() - 1).getId() == data.get(data.size() - 1).getId())
                    noData = true;
                else
                    items.addAll(data);
            else {
                if (items != null) {
                    if (/*items.size() == data.size() && */
                            items.size() > 0 &&
                                    items.get(0).getId() == data.get(0).getId())
                        noData = false;
                }
                if (!noData) {
                    items = data;
                }
            }
            bInit = true;
            if (!noData)
                notifyDataSetChanged();
        }
    }

    public void stopPlay() {
        if (getVideoPlyer() != null) {
            getVideoPlyer().stop();
            getVideoPlyer().release();
            removeVideoPlayer();
            if (getVideoPlyer().getTag() != null) {
                View tag = (View) getVideoPlyer().getTag();
                tag.setVisibility(View.VISIBLE);
            }
        }
    }

    public List<ContentCmsEntry> getData() {
        return items;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void insertAd(int position, ContentCmsEntry tag) {
        if (tag == null) return;
        int pos = 0;
        if (!(items == null || items.isEmpty())) {
//            position--;
            if (position < items.size()) {
                try {
                    int i = 0;
                    //抛开广告所占位置
                    do {
                        ContentCmsEntry item = items.get(i);
                        if (pos == position - 1) {
//                        if(pos!=0)  pos++;
                            pos = i;
                            break;
                        }
                        if (!TextUtils.equals(item.getType(), "adtype")) {
                            pos++;
                        }
                        i++;
                    } while (pos < position);
                    if (pos < items.size()) {
                        ContentCmsEntry entry = items.get(pos);
                        if (entry != null) {
                            if (entry.getId() != tag.getId() && !TextUtils.equals(entry.getType(), tag.getType()))
                                items.add(pos, tag);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
//        int type = super.getItemViewType(position);
        int type = 0;
        ContentCmsEntry item = items.get(position);
        if (item.getShowType() == 1) {
//            if (item.getThumbnail_urls() == null || item.getThumbnail_urls().isEmpty()) {
//                type = TYPE_ACTIVIRY;
//            } else {
            type = TYPE_NEWS;
//            }
        } else if (item.getShowType() == 2) {
            type = TYPE_VIDEO;
        } else if (item.getShowType() == 3 || item.getShowType() == 9) {
            type = TYPE_LIVE;
        } else if (item.getShowType() == 5) {
            type = TYPE_ACTIVIRY;
        } else if (item.getShowType() == 4 || item.getShowType() == 8) {
            type = TYPE_GROUP;
        } else if (item.getShowType() == 6 || item.getShowType() == 7) {
            type = TYPE_AD;
        } else if (item.getShowType() == 10) {
            type = TYPE_SCROLL;
        } else if (item.getShowType() == 11) {
            type = TYPE_SPEICAL;
        } else if (item.getShowType() == 12) {
            type = TYPE_DYNSIC;
        }else if (item.getShowType() == 13){//应急消息类型
            type = TYPE_EMERGENCY;
        }
//        else if (item.typeId == 4) {
//            type = TYPE_AD;
        else
            type = TYPE_NEWS;
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    public class ViewHolder {
        public ContentCmsEntry item;
        public int pos;
        public TextView titleTextView;
        public ImageView thumbnailImageView;
        public TextView sourceView;
        public TextView ctimeTextView;
        public TextView commentNumberTextView;
        public ImageView mark;
        public ImageView adMark;
    }

    public class Multphotoholder extends ViewHolder {
        public LinearLayout relayoutArea;
        public TextView groupConttxt;
    }

    public class LiveHolder extends ViewHolder {
        public CircleButton userImage;
        public TextView username;
        public ImageView statusImg;
    }

    public class Videoholder extends ViewHolder {
        public FrameLayout videoContainer;
        public RelativeLayout forgrondlay;
        public ImageButton btnplay;
    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (mContext instanceof MainTabActivity) {
            return ((MainTabActivity) mContext).getVideoPlayer();
        }
        return null;
    }

    private void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer, FrameLayout videoContainer) {
        videoContainer.setVisibility(View.VISIBLE);
//        fullContainer.setVisibility(View.GONE);
        if (videoContainer != null) {
            if (!(videoContainer.getChildAt(0) != null &&
                    videoContainer.getChildAt(0) instanceof VideoAdwarePlayView)) {
                removeVideoPlayer();
                videoContainer.addView(videoPlayer, 0);
            }
        }
    }

    private void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    /*
    * 检查是不是显示广告
    */
    public void checkAdware(ContentCmsEntry entry, ViewHolder holder) {
        if (entry == null) return;
        if (TextUtils.equals(entry.getType(), "ad") ||
                TextUtils.equals(entry.getType(), "adtype")) {
            holder.commentNumberTextView.setVisibility(View.GONE);
            holder.adMark.setVisibility(View.VISIBLE);
            holder.ctimeTextView.setVisibility(View.GONE);
        } else {
            holder.commentNumberTextView.setVisibility(View.VISIBLE);
            holder.adMark.setVisibility(View.GONE);
            holder.ctimeTextView.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 检查是不是横幅
     */
    public void checkBanner(ContentCmsEntry entry, TextView timeTxt) {
        if (entry == null) return;
        View parent = (View) timeTxt.getParent();
        if (TextUtils.equals(entry.getType(), "banner")) {
            parent.setVisibility(View.GONE);
        } else {   //ad
            timeTxt.setVisibility(View.GONE);
        }
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View newsView = null;
        View videoView = null;
        View actvdView = null;
        View multPic = null;
        int currentType = getItemViewType(position);
        if (currentType == TYPE_NEWS) {
            ViewHolder viewHolder = null;
            if (view == null) {
                newsView = inflater.inflate(R.layout.news_news_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.news_list_item_title);
                viewHolder.thumbnailImageView = (ImageView) newsView.findViewById(R.id.news_news_list_item_image);
//                viewHolder.contentTextView = (TextView) newsView.findViewById(R.id.news_list_item_content);
                viewHolder.sourceView = (TextView) newsView.findViewById(R.id.news_list_item_source_tx);
                viewHolder.ctimeTextView = (TextView) newsView.findViewById(R.id.news_list_item_time);
                viewHolder.commentNumberTextView = (TextView) newsView.findViewById(R.id.news_list_item_command_tx);
                viewHolder.mark = (ImageView) newsView.findViewById(R.id.play_mark);
                viewHolder.adMark = (ImageView) newsView.findViewById(R.id.ad_icon);
                viewHolder.item = items.get(position);
                newsView.setTag(viewHolder);
                view = newsView;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
            try {
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));
                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) viewHolder.item.getId())) {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                } else {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                }
                checkAdware(viewHolder.item, viewHolder);
//            String source = viewHolder.item.getSource();
//            if (source != null && !TextUtils.isEmpty(source)) {
//                viewHolder.sourceView.setVisibility(View.VISIBLE);
//                viewHolder.sourceView.setText(source);
//            } else {
//                viewHolder.sourceView.setVisibility(View.GONE);
//            }
                if (TextUtils.equals(viewHolder.item.getType(), "video")) {
                    viewHolder.mark.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mark.setVisibility(View.GONE);
                }
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                UtilHelp.setViewCount(viewHolder.commentNumberTextView, viewHolder.item.getView_count());
                String imageUrl = "";
                if (viewHolder.item.getThumbnail_urls() != null && viewHolder.item.getThumbnail_urls().size() > 0) {
                    viewHolder.thumbnailImageView.setVisibility(View.VISIBLE);
                    imageUrl = viewHolder.item.getThumbnail_urls().get(0).toString();
                    imageUrl += "?w=" + dpNews_width + "&h=" + dpNews_height + "&s=0";
//                Util.LoadThumebImage(viewHolder.thumbnailImageView, imageUrl, null);
                }
                Glide.with(mContext)
                        .load(imageUrl)
                        .asBitmap()
                        .error(R.drawable.glide_default_image)
                        .placeholder(R.drawable.glide_default_image)
                        .into(viewHolder.thumbnailImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentType == TYPE_VIDEO) {
            Videoholder holder = null;
            if (view == null) {
                holder = new Videoholder();
                videoView = inflater.inflate(R.layout.news_video_list_hsrocll_item, null);
                holder.thumbnailImageView = (ImageView) videoView.findViewById(R.id.item_img);
                holder.titleTextView = (TextView) videoView.findViewById(R.id.news_list_item_title);
                holder.ctimeTextView = (TextView) videoView.findViewById(R.id.item_create_time);
                holder.videoContainer = (FrameLayout) videoView.findViewById(R.id.video_container);
                holder.forgrondlay = (RelativeLayout) videoView.findViewById(R.id.forground_bank);
                holder.commentNumberTextView = (TextView) videoView.findViewById(R.id.item_commeanuder_tx);
                holder.btnplay = (ImageButton) videoView.findViewById(R.id.item_video_mark);
                holder.adMark = (ImageView) videoView.findViewById(R.id.ad_icon);
                holder.btnplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final Videoholder holder = (Videoholder) view.getTag();
                        if (holder != null) {
                            //获取url
                            Observable.just(holder.item.getId())
                                    .subscribeOn(Schedulers.io())
                                    .map(new Func1<Long, ContentCmsInfoEntry>() {
                                        @Override
                                        public ContentCmsInfoEntry call(Long id) {
                                            ContentCmsInfoEntry info = new ContentCmsApi(view.getContext()).getEnteyFromJson(id);
                                            return info;
                                        }
                                    }).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<ContentCmsInfoEntry>() {
                                                   @Override
                                                   public void onCompleted() {
                                                   }

                                                   @Override
                                                   public void onError(Throwable e) {
                                                       if (e != null) e.printStackTrace();
                                                   }

                                                   @Override
                                                   public void onNext(ContentCmsInfoEntry result) {
                                                       if (result == null) return;
                                                       playVideo(result.getUrl(), holder);
                                                   }
                                               }
                                    );
                        }
                    }
                });
                videoView.setTag(holder);
                holder.btnplay.setTag(holder);
                holder.forgrondlay.setTag(holder);
                view = videoView;
            } else {
                holder = (Videoholder) view.getTag();
            }
            holder.item = items.get(position);
            holder.pos = position;
            try {
                holder.titleTextView.setText(holder.item.getTitle());
                holder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", holder.item.getPublish_time() * 1000));

                checkAdware(holder.item, holder);

                //大图模式下隐藏video按钮.
                if (TextUtils.equals(holder.item.getType(), "video")) {
                    holder.btnplay.setVisibility(View.VISIBLE);
                } else {
                    holder.btnplay.setVisibility(View.GONE);
                }

//            holder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", holder.item.getPublish_time() * 1000));
                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) holder.item.getId())) {
                    holder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                } else {
                    holder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                }
                if (holder.item.getModeType() == 2) {
                    holder.btnplay.setVisibility(View.VISIBLE);
                } else {
                    holder.btnplay.setVisibility(View.GONE);
                }
                UtilHelp.setViewCount(holder.commentNumberTextView, holder.item.getView_count());
                List<String> thumb = holder.item.getThumbnail_urls();
                String imageUrl = "";
                if (!(thumb == null || thumb.isEmpty())) {
                    imageUrl = thumb.get(0).toString();
                    imageUrl += "?w=" + dpVideo_width + "&h=" + dpVideo_heigth + "&s=0";
                }
//            Util.LoadThumebImage(holder.thumbnailImageView, imageUrl, null);
                Glide.with(mContext)
                        .load(imageUrl)
                        .asBitmap()
                        .error(R.drawable.glide_default_image)
                        .placeholder(R.drawable.glide_default_image)
                        .into(holder.thumbnailImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentType == TYPE_GROUP) {
            Multphotoholder viewHolder = null;
            if (view == null) {
                multPic = inflater.inflate(R.layout.news_item_multphotos, null);
                viewHolder = new Multphotoholder();
                viewHolder.titleTextView = (TextView) multPic.findViewById(R.id.news_list_item_title);
                viewHolder.thumbnailImageView = (ImageView) multPic.findViewById(R.id.news_news_list_item_image);
//                viewHolder.contentTextView = (TextView) multPic.findViewById(R.id.news_list_item_content);
                viewHolder.ctimeTextView = (TextView) multPic.findViewById(R.id.item_create_time);
                viewHolder.commentNumberTextView = (TextView) multPic.findViewById(R.id.news_list_command_time);
                viewHolder.relayoutArea = (LinearLayout) multPic.findViewById(R.id.news_list_iamgelayout);
                viewHolder.adMark = (ImageView) multPic.findViewById(R.id.ad_icon);
//                viewHolder.groupConttxt = (TextView) multPic.findViewById(R.id.news_group_count);
//                int screen = UtilHelp.getScreenWidth(mContext);

                //获取屏幕的宽度，每行3个Button，间隙为10共300，除4为每个控件的宽度
//                int nItmeWidh = (screen - 60) / 3;

                LinearLayout mLayout = new LinearLayout(mContext);
//                    mLayout.setWeightSum(1.0f);
                mLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
//                    params.leftMargin = ((int) (5.0F * dm.density));
//                    params.rightMargin = ((int) (5.0F * dm.density));
                mLayout.setLayoutParams(params);
                for (int i = 0; i < 3; i++) {
                    ImageView bg = new ImageView(mContext);
//                    bg.setBackgroundColor(mContext.getResources().getColor(R.color.img_default_bankgrond_color));
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(dpgp_nItmeWidh, dpgp_nItemHeight);
                    //控件距离其右侧控件的距离，此处为60
                    lp1.setMargins(0, 0, 10, 0);
                    bg.setLayoutParams(lp1);
                    //        bg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.main_page_zhibo));
                    bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                     mLayout.setBackgroundColor(Color.parseColor("#D9DEDF"));
                    mLayout.addView(bg, i);
                }
                viewHolder.relayoutArea.addView(mLayout);
                viewHolder.item = items.get(position);
                multPic.setTag(viewHolder);
                view = multPic;
            } else {
                viewHolder = (Multphotoholder) view.getTag();
            }
            //update
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
            try {
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) viewHolder.item.getId())) {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                } else {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                }

                checkAdware(viewHolder.item, viewHolder);

                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));
                UtilHelp.setViewCount(viewHolder.commentNumberTextView, viewHolder.item.getView_count());
                LinearLayout linp = (LinearLayout) viewHolder.relayoutArea.getChildAt(0);
                int ncount = 0;
//            int ncount = viewHolder.item.getThumbnail_urls().size();
                String[] item = {"", "", ""};
                String pathls = "";
                if (!(viewHolder.item.getThumbnail_urls() == null
                        || viewHolder.item.getThumbnail_urls().isEmpty())) {
                    ncount = viewHolder.item.getThumbnail_urls().size();
                    for (int i = 0; i < 3; i++) {
                        if (i < ncount) {
                            item[i] = viewHolder.item.getThumbnail_urls().get(i).toString();
                            pathls += item[i];
                            if (i != ncount - 1) pathls += ",";
                        }
                    }
                }
//            viewHolder.groupConttxt.setText(a.length + "图");
                for (int i = 0; i < linp.getChildCount(); i++) {
//                if (i < a.length) {
//                    String[] item = a[i].split(",");
                    ImageView img = (ImageView) linp.getChildAt(i);

                    img.setTag(R.id.tag_hedlinegroup_path, pathls);
//                    img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.op));
                    String url = item[i] + "?w=" + dpgp_nItmeWidh + "&h=" + dpgp_nItemHeight + "&s=1";

                Util.LoadThumebImage(img, url, null);
                    if (TextUtils.isEmpty(item[i].toString())) {
                        img.setTag(R.id.tag_hedlinegroup_pos, -1);
                    } else {
                        img.setTag(R.id.tag_hedlinegroup_pos, i);
                    }
//                }
//                img.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int position = (int) view.getTag(R.id.tag_hedlinegroup_pos);
//                        if (position < 0) return;
//                        String imgs = (String) view.getTag(R.id.tag_hedlinegroup_path);
//                        OpenImageUtils.openImage(mContext, imgs, position);
//                    }
//                });
//                .overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentType == TYPE_AD) {
            ViewHolder viewHolder = null;
            if (view == null) {
                newsView = inflater.inflate(R.layout.news_list_big_ad_item, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.news_list_item_title);
                viewHolder.thumbnailImageView = (ImageView) newsView.findViewById(R.id.ad_item_img);
                viewHolder.sourceView = (TextView) newsView.findViewById(R.id.item_souyce_tx);
                viewHolder.ctimeTextView = (TextView) newsView.findViewById(R.id.item_create_time);
                viewHolder.item = items.get(position);
                newsView.setTag(viewHolder);
                view = newsView;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
//                viewHolder.mSpeicallayout.setVisibility(View.GONE);
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
            viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));
            viewHolder.sourceView.setText(viewHolder.item.getSummary());
            viewHolder.titleTextView.setText(viewHolder.item.getTitle());
            viewHolder.sourceView.setText(viewHolder.item.getSource());
            String thumb = "";
            checkBanner(viewHolder.item, viewHolder.ctimeTextView);
            if (!(viewHolder.item.getThumbnail_urls() == null ||
                    viewHolder.item.getThumbnail_urls().isEmpty())) {
                thumb = viewHolder.item.getThumbnail_urls().get(0);
                thumb += "?w=" + dpAd_width + "&h=" + dpad_heigth + "&s=0";
            }
            Util.LoadThumebImage(viewHolder.thumbnailImageView, thumb, null);
        } else if (currentType == TYPE_LIVE) {
            LiveHolder holder = null;
            if (view == null) {
                actvdView = inflater.inflate(R.layout.news_video_live_item, null);
                holder = new LiveHolder();
                holder.statusImg = (ImageView) actvdView.findViewById(R.id.item_live_type_text);
                holder.thumbnailImageView = (ImageView) actvdView.findViewById(R.id.item_img);
                holder.titleTextView = (TextView) actvdView.findViewById(R.id.news_list_item_title);
                holder.commentNumberTextView = (TextView) actvdView.findViewById(R.id.news_live_viewcont_txt);
                holder.item = items.get(position);
                actvdView.setTag(holder);
                view = actvdView;
            } else {
                holder = (LiveHolder) view.getTag();
            }
            holder.item = items.get(position);
            holder.pos = position;
            try {
                LiveInfo showExtends = holder.item.getLiveInfo();
                if (showExtends != null) {
//                holder.username.setText(showExtends.getOwner_nickname());
                    holder.titleTextView.setText(showExtends.getTitle());
                    if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) holder.item.getId())) {
                        holder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                    } else {
                        holder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                    }
//                int flagRes = 0;
//                String stauseDescirbe = "未直播";
//                flagRes = R.drawable.icon_service_no_start_live;
//                if (showExtends.getState() == 2) {
//                    stauseDescirbe = "正在直播";
//                    flagRes = R.drawable.icon_live_living;
//                } else if (showExtends.getState() == 3) {
//                    stauseDescirbe = "直播已结束";
//                    flagRes = R.drawable.icon_back_play;
//                }
                    int state = showExtends.getState();
                    int liveTypeRes = 0;
                    if (state == 1) { //未直播
                        liveTypeRes = R.drawable.icon_live_no_start;
                    } else if (state == 2) {//正在直播
                        liveTypeRes = R.drawable.icon_living_on;
                    } else if (state == 3) {//直播已结束
                        liveTypeRes = R.drawable.icon_back_live;
                    } else {//其他默认
                        liveTypeRes = R.drawable.icon_living_on;
                    }
                    holder.statusImg.setImageResource(liveTypeRes);
//                holder.commentNumberTextView.setText(showExtends.getCurrentVisitorCount() + "人观看");
                    UtilHelp.setViewCount(holder.commentNumberTextView, holder.item.getView_count());
                    String imageUrl = "";
                    if (!(holder.item.getThumbnail_urls() == null ||
                            holder.item.getThumbnail_urls().isEmpty())) {
                        imageUrl = holder.item.getThumbnail_urls().get(0).toString();
                    } else {
                        imageUrl = showExtends.getCoverUrl();
                    }
                    imageUrl += "?w=" + screen + "&s=1";
                    Util.LoadThumebImage(holder.thumbnailImageView, imageUrl, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentType == TYPE_ACTIVIRY) {
            ViewHolder viewHolder = null;
            if (view == null) {
                actvdView = inflater.inflate(R.layout.news_news_list_noimg_item, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) actvdView.findViewById(R.id.news_list_item_title);
                viewHolder.ctimeTextView = (TextView) actvdView.findViewById(R.id.news_list_item_time);
                viewHolder.commentNumberTextView = (TextView) actvdView.findViewById(R.id.news_list_item_command_tx);
                viewHolder.item = items.get(position);
                actvdView.setTag(viewHolder);
                view = actvdView;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            //update
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
//            if (App.getInstance().getmSession().isRead((int) viewHolder.item.getId())) {
//                viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
//            } else {
//                viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
//            }
            try {
                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) viewHolder.item.getId())) {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                } else {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                }
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));
                UtilHelp.setViewCount(viewHolder.commentNumberTextView, viewHolder.item.getView_count());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (currentType == TYPE_EMERGENCY){
            ViewHolder viewHolder = null;
            if (view == null){
                viewHolder = new Videoholder();
                actvdView = inflater.inflate(R.layout.news_emergency_item,null);
                viewHolder.titleTextView = (TextView) actvdView.findViewById(R.id.text_title);
                viewHolder.ctimeTextView = (TextView) actvdView.findViewById(R.id.text_time);
                actvdView.setTag(viewHolder);
                view = actvdView;
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
            final String title = viewHolder.item.getEmergencyType() + viewHolder.item.getTitle();
            final ViewHolder finalViewHolder = viewHolder;
            RadiusBackgroundSpan radiusBackgroundSpan = new RadiusBackgroundSpan(Color.WHITE,Color.parseColor("#ff4d55"),10);
            SpannableString spannableString = new SpannableString(title);
            int start = 0;
            int end = viewHolder.item.getEmergencyType().length();
            //稍微设置标签文字小一点
            spannableString.setSpan(new RelativeSizeSpan(0.9f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置圆角背景
            spannableString.setSpan(radiusBackgroundSpan,start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            finalViewHolder.titleTextView.setText(spannableString);
            viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));

        }else if (currentType == TYPE_SPEICAL) {
            ViewHolder viewHolder = null;
            if (view == null) {
                newsView = inflater.inflate(R.layout.adapter_special_topic_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.item_title_tv);
                viewHolder.thumbnailImageView = (ImageView) newsView.findViewById(R.id.item_thumb_iamge);
//                viewHolder.mark = (ImageView) newsView.findViewById(R.id.news_items_thumb);
                viewHolder.ctimeTextView = (TextView) newsView.findViewById(R.id.item_time_tv);
//                viewHolder.commentNumberTextView = (TextView) newsView.findViewById(R.id.news_list_item_command_tx);
                viewHolder.item = items.get(position);
                newsView.setTag(viewHolder);
                view = newsView;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.item = items.get(position);
            viewHolder.pos = position;
            try {
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getPublish_time() * 1000));
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
//                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "浏览");
                String thumb = "";
//                if (!(viewHolder.item.getThumbnail_urls() == null
//                        || viewHolder.item.getThumbnail_urls().isEmpty())) {
//                    thumb = viewHolder.item.getThumbnail_urls().get(0).toString();
//                    thumb += "?w=" + dpVideo_width + "&h=" + dpVideo_heigth + "&s=0";
//                }
//                Util.LoadThumebImage(viewHolder.mark, thumb, null);
                Util.LoadThumebImage(viewHolder.thumbnailImageView, viewHolder.item.getThumbnail_urls().get(0), null);
                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) viewHolder.item.getId())) {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
                } else {
                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }


    public void playVideo(String url, Videoholder holder) {
        if (getVideoPlyer() != null) {
            getVideoPlyer().stop();
            getVideoPlyer().release();
            removeVideoPlayer();
            if (getVideoPlyer().getTag() != null) {
                View tag = (View) getVideoPlyer().getTag();
                tag.setVisibility(View.VISIBLE);
            }
        }
        playVideoIndex = holder.pos;
        addVideoPlayerToContainer(getVideoPlyer(), holder.videoContainer);
        holder.forgrondlay.setVisibility(View.GONE);
//        String url = holder.item.getUrl();
        getVideoPlyer().setTag(holder.forgrondlay);
        if (url != null && !TextUtils.isEmpty(url))
            getVideoPlyer().start(url);
    }


    private void goDetail(ContentCmsInfoEntry channel) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong("index", channel.getId());
        if (channel.getThumbnail_urls() != null && channel.getThumbnail_urls().size() > 0)
            bundle.putString("posterPath", channel.getThumbnail_urls().get(0));
        intent.setClass(mContext, CvideoPlayAct.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public boolean remove(int position) {
        if (position < items.size()) {
            items.remove(position);
            return true;
        }
        return false;
    }
}