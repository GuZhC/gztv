package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.Richtext;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.MyCollapseeView;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.searchlibaray.SearchUtil;
import com.dfsx.searchlibaray.adapter.FilterSearchListAdapter;
import com.dfsx.searchlibaray.model.ISearchData;

import java.util.List;

public class AppSearchListAdapter extends FilterSearchListAdapter {
    private IButtonClickListenr _item_back;
    private CommuntyDatailHelper _comnityHelper;
    private int nScreenWidth, nScreenHeight;
    private int group_nItmeWidh, group_nItemHeight;

    public void set_item_back(IButtonClickListenr _item_back) {
        this._item_back = _item_back;
    }


    public AppSearchListAdapter(Context context) {
        super(context);
        _comnityHelper = new CommuntyDatailHelper(context);
        nScreenWidth = UtilHelp.getScreenWidth(context);
        nScreenHeight = UtilHelp.getScreenHeight(context);
//        group_nItmeWidh = (nScreenWidth - 60) / 3;
//        group_nItemHeight = (group_nItmeWidh * 3 / 4);
        group_nItmeWidh = Util.dp2px(context, 112);
        group_nItemHeight = Util.dp2px(context, 70);
    }

    @Override
    public int getItemViewLayoutId(int type) {
        //按自己要求可以增加样式
        //        return super.getItemViewLayoutId(type);
        ISearchData.SearchShowStyle showStyle = ISearchData.SearchShowStyle
                .getShowStyle(type);
        if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD) {
            return R.layout.news_news_list_item;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD_THREE) {
            return R.layout.news_item_multphotos;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_CMS_VIDEO) {
//            return R.layout.news_video_list_hsrocll_item;
            return R.layout.news_news_list_item;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_CMS_ACTIVITY) {
            return R.layout.news_news_list_noimg_item;
        } else {
            return super.getItemViewLayoutId(type);
        }
    }


    @Override
    protected void setItemViewData(int type, BaseViewHodler holder, int position) {
        //按自己要求可以增加样式设置增加的内容
        //        super.setItemViewData(type, holder, position);
        ISearchData.SearchShowStyle showStyle = ISearchData.SearchShowStyle
                .getShowStyle(type);
        if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD) {
            setWordViewData(holder, position);
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD_THREE) {
            setMulityViewData(holder, position);
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_CMS_VIDEO) {
            setVideoViewData(holder, position);
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_CMS_ACTIVITY) {
            setActivtiyViewData(holder, position);
        } else {
            super.setItemViewData(type, holder, position);
        }
    }

    public void setWordViewData(BaseViewHodler holder, int position) {
        TextView titleTextView = (TextView) holder.getView(R.id.news_list_item_title);
        ImageView thumbnailImageView = (ImageView) holder.getView(R.id.news_news_list_item_image);
        TextView ctimeTextView = (TextView) holder.getView(R.id.news_list_item_time);
        TextView commentNumberTextView = (TextView) holder.getView(R.id.news_list_item_command_tx);
        ContentCmsEntry entry = (ContentCmsEntry) list.get(position);
        if (entry != null) {
            String title = SearchUtil.getShowTitleHtmlString(entry.getTitle(),
                    entry.getSearchItemInfo());
            titleTextView.setText(Html.fromHtml(title));
            UtilHelp.setViewCount(commentNumberTextView, entry.getView_count());
//            commentNumberTextView.setText(entry.getView_count() + "浏览");
            ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", entry.getPublish_time() * 1000));
            if (entry.getThumbnail_urls() != null && entry.getThumbnail_urls().size() > 0) {
                thumbnailImageView.setVisibility(View.VISIBLE);
                String imageUrl = entry.getThumbnail_urls().get(0).toString();
                //                imageUrl += "?w=" + dpNews_width + "&h=" + dpNews_height + "&s=1";
                Util.LoadThumebImage(thumbnailImageView, imageUrl, null);
            } else {
//                thumbnailImageView.setVisibility(View.GONE);
            }
        }
    }

    public void setVideoViewData(BaseViewHodler holder, int position) {
        ImageView mark = (ImageView) holder.getView(R.id.play_mark);
        mark.setVisibility(View.VISIBLE);
        ImageView thumbnailImageView = (ImageView) holder.getView(R.id.news_news_list_item_image);
        TextView titleTextView = (TextView) holder.getView(R.id.news_list_item_title);
        TextView ctimeTextView = (TextView) holder.getView(R.id.news_list_item_time);
        TextView commentNumberTextView = (TextView) holder.getView(R.id.news_list_item_command_tx);
        ContentCmsEntry entry = (ContentCmsEntry) list.get(position);
        if (entry != null) {
            String title = SearchUtil.getShowTitleHtmlString(entry.getTitle(),
                    entry.getSearchItemInfo());
            titleTextView.setText(Html.fromHtml(title));
            ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", entry.getPublish_time() * 1000));
            UtilHelp.setViewCount(commentNumberTextView, entry.getView_count());
//            commentNumberTextView.setText(entry.getView_count() + "浏览");
            List<String> thumb = entry.getThumbnail_urls();
            if (thumb != null && !thumb.isEmpty()) {
                String imageUrl = thumb.get(0).toString();
                //                imageUrl += "?w=" + dpNews_width + "&s=1";
                Util.LoadThumebImage(thumbnailImageView, imageUrl, null);
            } else {
                thumbnailImageView.setImageResource(R.drawable.glide_default_image);
            }
        }
    }

    public void setMulityViewData(BaseViewHodler holder, int position) {
        TextView titleTextView = (TextView) holder.getView(R.id.news_list_item_title);
        TextView ctimeTextView = (TextView) holder.getView(R.id.item_create_time);
        TextView commentNumberTextView = (TextView) holder.getView(R.id.news_list_command_time);
        LinearLayout relayoutArea = (LinearLayout) holder.getView(R.id.news_list_iamgelayout);
        LinearLayout mLayout = new LinearLayout(context);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        mLayout.setLayoutParams(params);
        ContentCmsEntry entry = (ContentCmsEntry) list.get(position);
        if (entry != null) {
            String title = SearchUtil.getShowTitleHtmlString(entry.getTitle(),
                    entry.getSearchItemInfo());
            titleTextView.setText(Html.fromHtml(title));
            ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", entry.getPublish_time() * 1000));
            UtilHelp.setViewCount(commentNumberTextView, entry.getView_count());
//            commentNumberTextView.setText(entry.getView_count() + "浏览");
            String url = "";
            for (int i = 0; i < 3; i++) {
                if (i < entry.getThumbnail_urls().size())
                    url = entry.getThumbnail_urls().get(i).toString();
                ImageView img = createImageView();
                //                url+= "?w=" + dpgp_nItmeWidh + "&h=" + dpgp_nItemHeight + "&s=1";
                Util.LoadThumebImage(img, url, null);
                mLayout.addView(img, i);
                url = "";
            }
            relayoutArea.addView(mLayout);
        }
    }

    public void setActivtiyViewData(BaseViewHodler holder, int position) {
        TextView titleTextView = (TextView) holder.getView(R.id.news_list_item_title);
        TextView ctimeTextView = (TextView) holder.getView(R.id.news_list_item_time);
        TextView commentNumberTextView = (TextView) holder.getView(R.id.news_list_item_command_tx);
        ContentCmsEntry entry = (ContentCmsEntry) list.get(position);
        if (entry != null) {
            ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", entry.getPublish_time() * 1000));
            String title = SearchUtil.getShowTitleHtmlString(entry.getTitle(),
                    entry.getSearchItemInfo());
            titleTextView.setText(Html.fromHtml(title));
            UtilHelp.setViewCount(commentNumberTextView, entry.getView_count());
//            commentNumberTextView.setText(entry.getView_count() + "浏览");
        }
    }

    public ImageView createImageView() {
        ImageView bg = new ImageView(context);
        bg.setBackgroundColor(context.getResources().getColor(R.color.img_default_bankgrond_color));
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(group_nItmeWidh, group_nItemHeight);
        //控件距离其右侧控件的距离，此处为60
        lp1.setMargins(0, 0, 10, 0);
        bg.setLayoutParams(lp1);
        //        bg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.main_page_zhibo));
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return bg;
    }

    @Override
    public int getQuanziItemLayoutId() {
        //        return super.getQuanziItemLayoutId();
        //设置圈子的布局
        return R.layout.disclosure_item;
    }

    @Override
    public void setQuanZiData(BaseViewHodler holder, int position) {
        //        super.setQuanZiData(holder, position);
        //设置圈子的数据
        setQuanziViewData(holder, position);
    }

    public void setItemAttion(long aver_id, int role) {
        if (!(list == null || list.isEmpty())) {
            for (ISearchData entry : list) {
                if (entry instanceof TopicalEntry) {
                    if (((TopicalEntry) entry).getAuthor_id() == aver_id) {
                        ((TopicalEntry) entry).setRelationRole(role);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    public void setItemPraise(long tid, boolean isPraise) {
        if (!(list == null || list.isEmpty())) {
            for (ISearchData entry : list) {
                if (entry instanceof TopicalEntry) {
                    if (((TopicalEntry) entry).getId() == tid) {
                        ((TopicalEntry) entry).setAttitude(isPraise ? 1 : 0);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    public void setItemFavority(long id, boolean isFav) {
        if (!(list == null || list.isEmpty())) {
            for (ISearchData entry : list) {
                if (((TopicalEntry) entry).getId() == id) {
                    ((TopicalEntry) entry).setIsFavl(isFav);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setQuanziViewData(BaseViewHodler holder, int position) {
        View body = holder.getView(R.id.communtiy_item_hor);
        TextView usename = (TextView) holder.getView(R.id.replay_user_name);
        ImageView userImg = (ImageView) holder.getView(R.id.head_img);
        ImageView userLevel = (ImageView) holder.getView(R.id.cmy_user_level);
        Richtext title = (Richtext) holder.getView(R.id.disclosure_list_title);
//        title.setCollapsedLines(5);
//        title.setTipsColor(0xff5193EA);
//        title.setCollapsedDrawableRes(R.drawable.expand_shou);
//        title.setExpandedDrawableRes(R.drawable.expand);
//        title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TopicalEntry entry = (TopicalEntry) v.getTag();
//                _item_back.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(null, entry));
//            }
//        });
        ImageView realtRoleTx = (ImageView) holder.getView(R.id.common_guanzhu_tx);
        View praieView = (View) holder.getView(R.id.cmy_praise_view);
        ImageView praiseBtn = (ImageView) holder.getView(R.id.comnity_item_praise);
        TextView animalBtn = (TextView) holder.getView(R.id.comnity_item_praise_animal);
        TextView time = (TextView) holder.getView(R.id.common_time);
        LinearLayout imgs = (LinearLayout) holder.getView(R.id.disclosure_list_iamgelayout);
        LinearLayout bottomView = (LinearLayout) holder.getView(R.id.comunity_botom_view);
        TabGrouplayout tabGroup = (TabGrouplayout) holder.getView(R.id.communt_img_taglay);
        LinearLayout userGroup = (LinearLayout) holder.getView(R.id.userGrouplay);
        LinearLayout commendContainer = (LinearLayout) holder.getView(R.id.commend_container);
        TextView commnedTxt = (TextView) holder.getView(R.id.community_commend_number);
        TextView vistNumberTx = (TextView) holder.getView(R.id.community_vist_number);
        View shareBtn = (View) holder.getView(R.id.comnity_item_share);
        RelativeLayout favirtyBtn = (RelativeLayout) holder.getView(R.id.comnity_item_favority);
        View commendPubBtn = (View) holder.getView(R.id.comnity_item_commend);
        View delBtn = (View) holder.getView(R.id.comnity_del_btn);
        TopicalEntry entry = (TopicalEntry) list.get(position);
        UtilHelp.LoadImageErrorUrl(userImg, entry.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
        userImg.setTag(R.id.cirbutton_user_id, entry.getAuthor_id());
        UserLevelManager.getInstance().showLevelImage(context, entry.getUser_level_id(), userLevel);
        usename.setText(entry.getAuthor_nickname());
        String titlestr = SearchUtil.getShowTitleHtmlString(entry.getContent(),
                entry.getSearchItemInfo());
        title.setText(Html.fromHtml(titlestr));
//        title.setText(entry.getContent());
        time.setText(UtilHelp.getTimeFormatText("HH:mm yyyy/MM/dd", entry.getPost_time() * 1000));
        if (entry.isHome()) {
            delBtn.setVisibility(View.VISIBLE);
            favirtyBtn.setVisibility(View.GONE);
        } else {
            delBtn.setVisibility(View.GONE);
            favirtyBtn.setVisibility(View.VISIBLE);
        }
        int role = entry.getRelationRole();
        _comnityHelper.setAttteonTextStatus(role, realtRoleTx);
        vistNumberTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicalEntry item = (TopicalEntry) view.getTag();
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.VISTI_CLICK, new IButtonClickData(null, item));
            }
        });
        commendPubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicalEntry item = getEntry(view);
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData(null, item));
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicalEntry item = getEntry(view);
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.SHARE_CLICK, new IButtonClickData(null, item));
            }
        });
        praieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicalEntry item = getEntry(view);
                if (item == null) return;
                View parent = (View) view.getParent();
                TextView animal = (TextView) parent.findViewById(R.id.comnity_item_praise_animal);
                _item_back.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(animal, item));
            }
        });

        favirtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TopicalEntry item = getEntry(view);
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.FARVITY_CLICK, new IButtonClickData(null, item));
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TopicalEntry item = getEntry(view);
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.DEL_CLICK, new IButtonClickData(null, item));
            }
        });

        realtRoleTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                TopicalEntry item = (TopicalEntry) view.getTag();
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.ATTION_CLICK, new IButtonClickData(null, item));
            }
        });
        _comnityHelper.setPrsiseStatus(praiseBtn, entry.getAttitude() == 1 ? true : false, false);
        ImageView bg = (ImageView) favirtyBtn.getChildAt(0);
        _comnityHelper.setFavStatus(bg, entry.isFavl(), false);
        List<Attachment> dlist = entry.getAttachmentInfos();
        if (dlist != null && !dlist.isEmpty()) {
            imgs.removeAllViews();
            imgs.setVisibility(View.VISIBLE);
            String urls = "";
            if (dlist != null && !dlist.isEmpty()) {
                for (int i = 0; i < dlist.size(); i++) {
//                    urls += dlist.get(i).getUrl() + "?w=" + nScreenWidth + "&h=" + nScreenHeight + "&s<=1";
                    urls += dlist.get(i).getUrl();
                    if (i < dlist.size() - 1)
                        urls += ",";
                }
                if (entry.getType() == 1) {
                    _comnityHelper.setMulitpImage(imgs, dlist, urls);
                } else {
                    _comnityHelper.createVideoContainer(imgs, dlist.get(0));
                }
            }
        } else {
            imgs.removeAllViews();
            imgs.setVisibility(View.GONE);
        }
        List<ReplyEntry> reppls = entry.getReplyList();
        if (!(reppls == null || reppls.isEmpty())) {
            if (commendContainer.getChildCount() > 0) commendContainer.removeAllViews();
            commendContainer.setVisibility(View.VISIBLE);
            int i = 0;
            int count = reppls.size() >= 3 ? 3 : reppls.size();
            do {
                ReplyEntry bean = reppls.get(i);
                View view = _comnityHelper.createSubReplay(bean.getAuthor_nickname(), bean.getContent());
                commendContainer.addView(view);
                i++;
            } while (i < count);
            commnedTxt.setText("查看全部(" + reppls.size() + ")");
            commnedTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TopicalEntry item = (TopicalEntry) v.getTag();
                    if (item == null) return;
                    _item_back.onLbtClick(IButtonClickType.QUERY_COMMEND, new IButtonClickData(v, item));
                }
            });
        } else {
            commendContainer.removeAllViews();
            View parent = (View) commendContainer.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
        List<TopicalEntry.PraiseBean> visit = entry.getPraiseBeanList();
        if (!(visit == null || visit.isEmpty())) {
            View vparent = (View) vistNumberTx.getParent();
            vparent.setVisibility(View.VISIBLE);
//            vistNumberTx.setTag(R.id.cirbutton_user_id, entry.getId());
            vistNumberTx.setTag(entry);
//            vistNumberTx.setText(visit.size() + "");   //到访记录
            vistNumberTx.setText(visit.size() + "赞");
            _comnityHelper.initUserGroupLayout(userGroup, visit);
        } else {
            View vparent = (View) vistNumberTx.getParent();
            vparent.setVisibility(View.GONE);
        }
        _comnityHelper.initTabGroupLayout(tabGroup, entry.getTags());
        body.setTag(entry);
        bottomView.setTag(entry);
        title.setTag(entry);
        realtRoleTx.setTag(entry);
        commnedTxt.setTag(entry);
        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() == null) return;
                TopicalEntry entry = (TopicalEntry) v.getTag();
                _item_back.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(null, entry));
            }
        });
    }

    public TopicalEntry getEntry(View view) {
        View parent = (View) view.getParent();
        if (parent != null) {
            return (TopicalEntry) parent.getTag();
        }
        return null;
    }

}
