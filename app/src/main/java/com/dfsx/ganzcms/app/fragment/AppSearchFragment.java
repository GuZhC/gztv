package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.adapter.AppSearchListAdapter;
import com.dfsx.ganzcms.app.business.AppSearchHelper;
import com.dfsx.ganzcms.app.business.CommuntyDatailHelper;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.searchlibaray.FilterSearchFragment;
import com.dfsx.searchlibaray.adapter.FilterSearchListAdapter;
import com.dfsx.searchlibaray.businness.SearchHelper;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.thirdloginandshare.share.ShareContent;

public class AppSearchFragment extends FilterSearchFragment implements IButtonClickListenr, NewsDatailHelper.ICommendDialogLbtnlister {

    private CommuntyDatailHelper _comnityHelper;
    private AppSearchListAdapter adapter;
    private android.view.animation.Animation animation;

    @Override
    protected SearchHelper createSearchHelper() {
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_score_anim);
        if (_comnityHelper == null)
            _comnityHelper = new CommuntyDatailHelper(getActivity());
        AppSearchHelper searchHelper = new AppSearchHelper(getActivity());
        return searchHelper;
    }

    @Override
    protected FilterSearchListAdapter createListViewAdapter() {
        adapter = new AppSearchListAdapter(activity);
//        AppSearchListAdapter adapter = new AppSearchListAdapter(activity);
        adapter.set_item_back(this);
        return adapter;
    }

    @Override
    protected void onSearchItemClick(ISearchData searchData) {
        //实现点击事件
//        super.onSearchItemClick(searchData);
        if (searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_WORD
                || searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_CMS_VIDEO
                || searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_WORD_THREE
                || searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_CMS_ACTIVITY) {
            ContentCmsEntry entry = (ContentCmsEntry) searchData.getContentData();
            if (entry != null)
                _comnityHelper.goDetail(entry);
        } else if (searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_QUANZI) {
            TopicalEntry entry = (TopicalEntry) searchData.getContentData();
            if (entry != null)
                _comnityHelper.gotoComunnityInfo(entry);
        } else {
            super.onSearchItemClick(searchData);
        }
    }

    @Override
    public void onLbtClick(int tyep, IButtonClickData data) {
        final IButtonClickData<TopicalEntry> mydata = data;
        final TopicalEntry entry = mydata.getObject();

        switch (tyep) {
            case IButtonClickType.VISTI_CLICK:
                long userId = entry.getAuthor_id();
                WhiteTopBarActivity.startAct(getActivity(), VisitRcordFragment.class.getName(), "浏览记录", "", userId);
                break;
            case IButtonClickType.ATTION_CLICK: {
                ImageView bg = (ImageView) mydata.getTag();
//                _comnityHelper.setAttentionUser(entry.getAuthor_id(), entry.getRelationRole(), bg);
//                refreshData();
                setAttentionUser(entry.getAuthor_id(), entry.getRelationRole());
            }
            break;
            case IButtonClickType.ITEM_CLICK:
                _comnityHelper.gotoComunnityInfo(entry);
                break;
            case IButtonClickType.PRAISE_CLICK:
                final TextView anmal = (TextView) mydata.getTag();
                if (entry.getAttitude() == 1) {
                    //已点赞
                    _comnityHelper.getmTopicalApi().cancelPariseToptic(entry.getId(), new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            if (!(boolean) data) return;
                            if (anmal != null) {
                                anmal.setVisibility(View.VISIBLE);
                                anmal.startAnimation(animation);
                                anmal.setText("-1");
                            }
                            ToastUtils.toastMsgFunction(getActivity(), "取消点赞");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
                                }
                            }, 50);
                            //选中
                            adapter.setItemPraise(entry.getId(), false);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            ToastUtils.toastApiexceFunction(getActivity(), e);
                        }
                    });
                } else {
                    _comnityHelper.praiseLbtCLick(entry.getId(), anmal, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            if (!(boolean) data) return;
                            if (anmal != null) {
                                anmal.setVisibility(View.VISIBLE);
                                anmal.startAnimation(animation);
                                anmal.setText("+1");
                            }
                            ToastUtils.toastMsgFunction(getActivity(), "点赞成功");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
//                                    adapter.setItemPraise(entry.getId(), true);
                                }
                            }, 50);
                            adapter.setItemPraise(entry.getId(), true);
                            //选中
//                            dataRequester.start(mClounType, false, offset, false);
//                            _comnityHelper.getmIGetPraistmp().updateValuse(entry.getId(), true, false, false);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            ToastUtils.toastApiexceFunction(getActivity(), e);
                        }
                    });
                }
                break;
            case IButtonClickType.COMMEND_CLICK:
                _comnityHelper.showCommendDialog(rootView, entry.getId(), -1, this);
                break;
            case IButtonClickType.SHARE_CLICK: {
                String url = App.getInstance().getCommuityShareUrl() + entry.getId();
                String thumb = "";
                if (entry.getAttachmentInfos() != null && entry.getAttachmentInfos().size() > 0)
                    thumb = entry.getAttachmentInfos().get(0).getThumbnail_url();
                ShareContent shareContent = _comnityHelper.ObtainShareContent(entry.getId(), entry.getContent(),
                        url, thumb, false);
                _comnityHelper.shareNewUiWnd(rootView, shareContent);
            }
            break;
            case IButtonClickType.FARVITY_CLICK: {
//                final boolean isfal = entry.isFavl();
//                _comnityHelper.addFavritory(entry.isFavl(), entry.getId(), new DataRequest.DataCallback() {
//                    @Override
//                    public void onSuccess(boolean isAppend, Object data) {
//                        if ((boolean) data) {
//                            String msg = "收藏成功";
//                            if (isfal)
//                                msg = "取消收藏";
//                            LSUtils.toastMsgFunction(getActivity(), msg);
////                            dataRequester.start(mClounType, false, offset);
////                            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
////                            ImageView anmal = (ImageView) mydata.getTag();
////                            _comnityHelper.setFavStatus(anmal, isfal, false);
//                            refreshData();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(ApiException e) {
//                        e.printStackTrace();
//                        LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
//                    }
//                });

                final boolean isfal = entry.isFavl();
                _comnityHelper.addFavritory(isfal, entry.getId(), new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        if ((boolean) data) {
                            boolean blag = isfal ? false : true;
                            String msg = "收藏成功";
                            if (!blag) {
                                msg = "取消收藏";
                            }
                            ToastUtils.toastMsgFunction(getActivity(), msg);
                            adapter.setItemFavority(entry.getId(), blag);
//                            dataRequester.start(mClounType, false, offset);
                            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
                        } else {
                            ToastUtils.toastMsgFunction(getActivity(), "收藏失败");
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastApiexceFunction(getActivity(), e);
                    }
                });
            }
            break;
        }
    }

    public void setAttentionUser(final long uId, final int role) {
        if (!_comnityHelper.getMloginCheck().checkLogin()) return;
        _comnityHelper.getmTopicalApi().attentionAuthor(uId, role, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    boolean flag = role == 0 ? true : false;
                    String msg = "关注成功";
                    if (!flag) {
                        msg = "取消关注";
                    }
                    ToastUtils.toastMsgFunction(getActivity(), msg);
                    adapter.setItemAttion(uId, role == 0 ? 1 : 0);
//                    RXBusUtil.sendConcernChangeMessage(flag, 1);
                    RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                } else {
                    ToastUtils.toastMsgFunction(getActivity(), "关注失败");
                }
            }

            @Override
            public void onFail(ApiException e) {
                ToastUtils.toastApiexceFunction(getActivity(), e);
            }
        });
    }

    @Override
    public boolean onParams(long id, long resf_id, String context) {
        _comnityHelper.writeCommend(id, -1, context, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                long id = (Long) data;
                if (id != -1) {
                    LSUtils.toastMsgFunction(getActivity(), "评论发表成功");
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
        return true;
    }
}
