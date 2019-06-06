package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.model.Account;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.fragment.ILiveShowStyleChange;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.Richtext;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by  heyang  on 2017/10/23.
 */
public class CommunityAdapter extends BaseRecyclerViewDataAdapter<TopicalEntry> {
    private final String STATE_LIST = "ListqqDisclsureCommunityAdapter.mlist";
    protected Context context;
    private CommuntyDatailHelper _comnityHelper;
    private int nScreenWidth, nScreenHeight;
    private boolean inInit = false;
    private IButtonClickListenr _item_back;

    public CommunityAdapter(Context c) {
        this.context = c;
        _comnityHelper = new CommuntyDatailHelper(context);
        nScreenWidth = UtilHelp.getScreenWidth(c);
        nScreenHeight = UtilHelp.getScreenHeight(c);
    }

    public void set_item_back(IButtonClickListenr _item_back) {
        this._item_back = _item_back;
    }

    public boolean isInInit() {
        return inInit;
    }

    public void setInInit(boolean inInit) {
        this.inInit = inInit;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_LIST, (Serializable) list);
    }

    public void init(Bundle savedInstanceState) {
        ArrayList<TopicalEntry> sList;
        sList = (ArrayList<TopicalEntry>) savedInstanceState.getSerializable(STATE_LIST);
        if (sList != null) {
            list = sList;
            notifyDataSetChanged();
            inInit = true;
        }
    }

    public void update(List<TopicalEntry> data, boolean isAdd) {
        if (data == null || data.size() <= 0) {
            return;
        }
        inInit = true;
        if (!isAdd) {
            list = data;
            notifyDataSetChanged();
        } else {
            int index = list.size();
            list.addAll(data);
            notifyItemRangeChanged(index, data.size());
        }
    }

    public void setItemAttion(long aver_id, int role) {
        if (!(list == null || list.isEmpty())) {
            for (TopicalEntry entry : list) {
                if (entry.getAuthor_id() == aver_id) {
                    entry.setRelationRole(role);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 设置收藏
     *
     * @param id
     * @param isFav
     */
    public void setItemFavority(long id, boolean isFav) {
        if (!(list == null || list.isEmpty())) {
            for (TopicalEntry entry : list) {
                if (entry.getId() == id) {
                    entry.setIsFavl(isFav);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 设置被赞
     *
     * @param tid
     */
//    public void setItemPraise(long tid, boolean isPraise) {
//        if (!(list == null || list.isEmpty())) {
//            for (TopicalEntry entry : list) {
//                if (entry.getId() == tid) {
//                    entry.setAttitude(isPraise ? 1 : 0);
//                }
//            }
//            notifyDataSetChanged();
//        }
//    }

    public void setItemPraise(long tid, boolean isPraise) {
        if (!(list == null || list.isEmpty())) {
            for (TopicalEntry entry : list) {
                if (entry.getId() == tid) {
                    entry.setAttitude(isPraise ? 1 : 0);
                    List<TopicalEntry.PraiseBean> dlist = entry.getPraiseBeanList();
                    Account account = App.getInstance().getUser();
                    long useId = 0;
                    if (!(account == null || account.getUser() == null))
                        useId = account.getUser().getId();
                    if (isPraise) {
                        //加
                        if (dlist == null)
                            dlist = new ArrayList<>();
                        TopicalEntry.PraiseBean bean = new TopicalEntry.PraiseBean();
                        if (!(account == null || account.getUser() == null)) {
                            Account.UserBean userBean = account.getUser();
                            bean.setAvatar_url(userBean.getAvatar_url());
                            bean.setUser_id(userBean.getId());
                        }
                        if (!dlist.isEmpty()) {
                            dlist.add(0, bean);
                        } else
                            dlist.add(bean);
                        break;
                    } else {
                        //取消
                        if (!(dlist == null || dlist.isEmpty())) {
                            for (int i = 0; i < dlist.size(); i++) {
                                TopicalEntry.PraiseBean bean = dlist.get(i);
                                if (bean.getUser_id() == useId) {
                                    dlist.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    entry.setPraiseBeanList(dlist);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setItemCommend(long tid, String repleContext) {
        if (!(list == null || list.isEmpty())) {
            for (TopicalEntry entry : list) {
                if (entry.getId() == tid) {
                    List<ReplyEntry> dlist = entry.getReplyList();
                    if (dlist == null)
                        dlist = new ArrayList<>();
                    Account account = App.getInstance().getUser();
                    if (!(account == null || account.getUser() == null)) {
                        Account.UserBean userBean = account.getUser();
                        ReplyEntry bean = new ReplyEntry();
                        bean.setContent(repleContext);
                        bean.setAuthor_nickname(userBean.getNickname());
                        bean.setAuthor_id(userBean.getAvatar_id());
                        if (!dlist.isEmpty()) {
                            dlist.add(0, bean);
                        } else
                            dlist.add(bean);
                        entry.setReplyList(dlist);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.disclosure_item, viewGroup, false);
        return new BaseRecyclerViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, int position) {
        View body = holder.getView(R.id.communtiy_item_hor);
        TextView usename = (TextView) holder.getView(R.id.replay_user_name);
        ImageView userImg = (ImageView) holder.getView(R.id.head_img);
        ImageView userLevel = (ImageView) holder.getView(R.id.cmy_user_level);
        Richtext title = (Richtext) holder.getView(R.id.disclosure_list_title);
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
        if (list == null || list.isEmpty()) {
            body.setVisibility(View.GONE);
            return;
        } else {
            body.setVisibility(View.VISIBLE);
        }
        TopicalEntry entry = list.get(position);
        if (entry == null) return;
        UtilHelp.LoadImageErrorUrl(userImg, entry.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
        userImg.setTag(R.id.cirbutton_user_id, entry.getAuthor_id());
        UserLevelManager.getInstance().showLevelImage(context, entry.getUser_level_id(), userLevel);
        usename.setText(entry.getAuthor_nickname());
        String ty = UtilHelp.getDecondeString(entry.getContent().toString());
        title.setText(ty);
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
                ImageView favorty = null;
                if (view != null)
                    favorty = (ImageView) ((RelativeLayout) view).getChildAt(0);
                if (item == null) return;
                _item_back.onLbtClick(IButtonClickType.FARVITY_CLICK, new IButtonClickData(favorty, item));
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
            public void onClick(final View view) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                final TopicalEntry item = (TopicalEntry) view.getTag();
                if (item == null) return;
//                RxView.clicks(view)
//                        .throttleFirst(1000, TimeUnit.MILLISECONDS)
//                        .subscribe(new Action1<Void>() {
//                            @Override
//                            public void call(Void aVoid) {
//                                _item_back.onLbtClick(IButtonClickType.ATTION_CLICK, new IButtonClickData(view, item));
//                            }
//                        });
                _item_back.onLbtClick(IButtonClickType.ATTION_CLICK, new IButtonClickData(view, item));
            }
        });
        _comnityHelper.setPrsiseStatus(praiseBtn, entry.getAttitude() == 1 ? true : false, false);
        ImageView favrot = (ImageView) favirtyBtn.getChildAt(0);
        _comnityHelper.setFavStatus(favrot, entry.isFavl(), false);
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
            View parent = (View) commendContainer.getParent().getParent();
            parent.setVisibility(View.VISIBLE);
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
        if (visit != null) {
            View vparent = (View) vistNumberTx.getParent();
            vparent.setVisibility(View.VISIBLE);
//            vistNumberTx.setTag(R.id.cirbutton_user_id, entry.getId());
            vistNumberTx.setTag(entry);
//            vistNumberTx.setText(visit.size() + "");   //到访记录
            vistNumberTx.setText(visit.size() + "");
            _comnityHelper.initUserGroupLayout(userGroup, visit);
        } else {
            View vparent = (View) vistNumberTx.getParent();
            vparent.setVisibility(View.GONE);
        }
        _comnityHelper.initTabGroupLayout(tabGroup, entry.getTags());
        body.setTag(entry);
        bottomView.setTag(entry);
        realtRoleTx.setTag(entry);
//        commendPubBtn.setTag(entry);
//        shareBtn.setTag(entry);
//        favirtyBtn.setTag(entry);
//        praiseBtn.setTag(entry);
//        imgs.setTag(entry);
        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
