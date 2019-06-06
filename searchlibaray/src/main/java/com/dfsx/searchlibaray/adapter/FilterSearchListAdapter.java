package com.dfsx.searchlibaray.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleImageView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.searchlibaray.R;
import com.dfsx.searchlibaray.SearchUtil;
import com.dfsx.searchlibaray.businness.UserInfo;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.LiveInfo;
import com.dfsx.searchlibaray.model.SearchItemInfo;
import com.dfsx.searchlibaray.view.CustomLabelLayout;

public class FilterSearchListAdapter extends BaseListViewAdapter<ISearchData> {
    private LiveChannelManager channelManager;

    public FilterSearchListAdapter(Context context) {
        super(context);
    }

    @Override
    public final int getItemViewLayoutId() {
        return 0;
    }

    @Override
    public final void setItemViewData(BaseViewHodler holder, int position) {

    }

    @Override
    public final int getViewTypeCount() {
        return ISearchData.SearchShowStyle.getAllShowTypeCount();
    }

    @Override
    public final int getItemViewType(int position) {
        ISearchData data = list.get(position);
        return data.getShowStyle().getTypeCount();
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                getItemViewLayoutId(type), position);
        setItemViewData(type, holdler, position);
        return holdler.getConvertView();
    }

    public int getItemViewLayoutId(int type) {
        ISearchData.SearchShowStyle showStyle = ISearchData.SearchShowStyle
                .getShowStyle(type);
        if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD) {
            return R.layout.adpter_word_layout;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_WORD_THREE) {
            return R.layout.adapter_word_three_layout;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_LIVE_SHOW) {
            return R.layout.adapter_live_show_layout;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_LIVE_SERVICE) {
            return R.layout.adapter_live_service_layout;
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_QUANZI) {
            return getQuanziItemLayoutId();
        } else if (showStyle == ISearchData.SearchShowStyle.STYLE_USER) {
            return R.layout.adapter_user_search_layout;
        } else {
            return R.layout.adpter_word_layout;
        }
    }

    public int getQuanziItemLayoutId() {
        return 0;
    }

    protected void setItemViewData(int type, BaseViewHodler holder, int position) {
        ISearchData.SearchShowStyle style = ISearchData.SearchShowStyle.getShowStyle(type);
        if (style == ISearchData.SearchShowStyle.STYLE_WORD) {
            setWordData(holder, position);
        } else if (style == ISearchData.SearchShowStyle.STYLE_WORD_THREE) {
            setWordThreeData(holder, position);
        } else if (style == ISearchData.SearchShowStyle.STYLE_LIVE_SHOW) {
            setLiveShowData(holder, position);
        } else if (style == ISearchData.SearchShowStyle.STYLE_LIVE_SERVICE) {
            setLiveServiceData(holder, position);
        } else if (style == ISearchData.SearchShowStyle.STYLE_QUANZI) {
            setQuanZiData(holder, position);
        } else if (style == ISearchData.SearchShowStyle.STYLE_USER) {
            setUserData(holder, position);
        } else {
            setWordData(holder, position);
        }
    }

    protected void setWordData(BaseViewHodler holder, int position) {
    }

    protected void setWordThreeData(BaseViewHodler holder, int position) {

    }

    protected void setLiveServiceData(BaseViewHodler holder, int position) {
        ImageView liveImageView = holder.getView(R.id.item_bkg_image);
        TextView liveInfoTextView = holder.getView(R.id.item_live_info_text);
        ImageView liveTypeView = holder.getView(R.id.item_live_type_text);
        TextView liveUserNumTextView = holder.getView(R.id.item_user_num_text);

        ISearchData<LiveInfo> searchData = list.get(position);
        LiveInfo info = searchData.getContentData();
        Glide.with(context)
                .load(info.getCover_url())
                .error(R.drawable.glide_default_image)
                .into(new GlideDrawableImageViewTarget(liveImageView));
        //        String titleHtml = SearchUtil.getShowTitleHtmlString(info.getTitle(), searchData.getSearchItemInfo());
        liveInfoTextView.setText(info.getTitle());
        liveUserNumTextView.setText(info.getCurrent_visitor_count() + "参与");
        int state = info.getState();
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
        liveTypeView.setImageResource(liveTypeRes);
    }

    protected void setLiveShowData(BaseViewHodler holder, int position) {
        TextView roomName = holder.getView(R.id.room_name);
        TextView roomOwnerName = holder.getView(R.id.room_owner_text);
        ImageView roomOwnerGrade = holder.getView(R.id.room_owner_grade);
        TextView roomLiveState = holder.getView(R.id.room_status);
        TextView roomMsgNum = holder.getView(R.id.room_msg_num);
        TextView roomTime = holder.getView(R.id.room_start_time);

        ImageView roomImage;
        roomImage = holder.getView(R.id.room_img);
        ImageView backPlayImage = holder.getView(R.id.image_back_play);
        ImageView logo = holder.getView(R.id.item_owner_logo);
        View labelConatiner = holder.getView(R.id.label_container_view);
        CustomLabelLayout roomLableLayout = holder.getView(R.id.room_label_view);
        roomLableLayout.setCouldClickBody(false);
        roomLableLayout.setAddFlagNeedShown(false);
        int lableTextColor = getColorByResourceId(R.color.label_text_color);
        int lableBgk = R.drawable.shape_label_box;
        roomLableLayout.changeThemeForTextColor(lableTextColor, lableTextColor,
                lableBgk, lableBgk);
        roomLableLayout.destroy();

        ISearchData<LiveInfo> searchData = list.get(position);
        LiveInfo roomInfo = searchData.getContentData();

        roomOwnerName.setText(roomInfo.getOwner_nickname());
        //        String roomTitleHtml = SearchUtil.getShowTitleHtmlString(roomInfo.getTitle(),
        //                searchData.getSearchItemInfo());
        //不用标亮
        roomName.setText(roomInfo.getTitle());
        roomTime.setText(StringUtil.getTimeAgoText(roomInfo.getCreation_time()));
        String stateText = roomInfo.getState() == 3 ?
                StringUtil.getTimeAgoText(roomInfo.getCreation_time()) :
                roomInfo.getRoomStatus();
        roomLiveState.setText(stateText);
        String numText = roomInfo.getState() == 3 ? "人观看过" : "人在观看";
        roomMsgNum.setText(roomInfo.getCurrent_visitor_count() + numText);

        //设置等级
        UserLevelManager.getInstance().showLevelImage(context,
                roomInfo.getOwner_level_id(), roomOwnerGrade);
        //        moreSettingText.setVisibility(roomInfo.isOnlyUserRoom() ? View.VISIBLE : View.GONE);
        if (roomInfo.getTags() != null && roomInfo.getTags().size() > 0) {
            roomLableLayout.destroy();
            roomLableLayout.addAllBody(roomInfo.getTags().toArray(new String[0]));
            labelConatiner.setVisibility(View.VISIBLE);
        } else {
            labelConatiner.setVisibility(View.GONE);
        }
        //加载房间的图片
        GlideImgManager.getInstance().showImg(context, roomImage,
                roomInfo.getCover_url());
        LSLiveUtils.showUserLogoImage(context, logo,
                roomInfo.getOwner_avatar_url());
        int flagRes = 0;
        if (roomInfo.isPrivacy()) {
            flagRes = R.drawable.icon_back_play_privacy;
        } else if (roomInfo.getState() == 1) {
            flagRes = R.drawable.icon_live_no_start;
        } else if (roomInfo.getState() == 2) {
            flagRes = R.drawable.icon_living_on;
        } else {
            flagRes = R.drawable.icon_back_live;
        }
        backPlayImage.setImageResource(flagRes);
    }

    public void setQuanZiData(BaseViewHodler holder, int position) {

    }

    protected void setUserData(BaseViewHodler holder, int position) {
        CircleImageView circleImageView = holder.getView(R.id.user_logo);
        TextView nameText = holder.getView(R.id.user_nick_name);
        TextView signText = holder.getView(R.id.user_signature_text);
        TextView concernStateText = holder.getView(R.id.concern_state_btn);

        ISearchData<UserInfo> userInfoISearchData = list.get(position);
        SearchItemInfo searchItemInfo = userInfoISearchData.getSearchItemInfo();
        UserInfo userInfo = userInfoISearchData.getContentData();

        //        String htmlTitle = SearchUtil.getShowTitleHtmlString(userInfo.getNickname(),
        //                searchItemInfo);
        //不用标亮
        nameText.setText(userInfo.getNickname());

        LSLiveUtils.showUserLogoImage(context, circleImageView,
                userInfo.getAvatar_url());
        String sign = TextUtils.isEmpty(userInfo.getSignature()) ? "" : userInfo.getSignature();
        signText.setText(sign);

        if (userInfo.isFanned() && userInfo.isFollowed()) {
            //相互关注
            concernStateText.setBackgroundResource(R.drawable.shape_btn_concern_gray);
            concernStateText.setTextColor(context.getResources().getColor(R.color.follow_fan_text_color));
            concernStateText.setText("相互关注");
        } else {
            concernStateText.setBackgroundResource(R.drawable.shape_btn_concern_blue);
            concernStateText.setTextColor(context.getResources().getColor(R.color.white));
            if (userInfo.isFollowed()) {
                //已关注
                concernStateText.setText("已关注");
            } else {
                concernStateText.setText("关注");
            }
        }
        concernStateText.setTag(userInfo);
        concernStateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                final UserInfo user = (UserInfo) v.getTag();
                String text = tv.getText().toString();
                if (TextUtils.equals("关注", text)) {
                    if (channelManager == null) {
                        channelManager = new LiveChannelManager(context);
                    }
                    channelManager.addConcern(user.getId(), new DataRequest.DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(boolean isAppend, Boolean data) {
                            user.setFollowed(data);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private int getColorByResourceId(int colorId) {
        return context.getResources().getColor(colorId);
    }
}
