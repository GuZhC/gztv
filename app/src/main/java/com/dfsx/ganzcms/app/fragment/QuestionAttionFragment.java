package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.CvideoPlayAct;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.act.QuestionInfoAct;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.CustomeProgressDialog;
import com.dfsx.ganzcms.app.view.MyMorePopupwindow;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2017/2/22.
 * 提问  关注   跳转到 QuestionInfoAct
 */
public class QuestionAttionFragment extends AbsListFragment {
    private MyLiveAdapter adapter;
    private MyDataManager dataManager;
    private ContentCmsApi mContentCmsApi;
    private MyMorePopupwindow popupwindow;
    private EmptyView emptyView;
    private LinearLayout topView;
    private CustomeProgressDialog loading;
    private int page = 1;
    private long mUserId = -1;

    public static QuestionAttionFragment newInstance(long cId) {
        Bundle bundle = new Bundle();
        bundle.putLong("uID", cId);
        QuestionAttionFragment fragment = new QuestionAttionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getLong("uID");
        }
        popupwindow = new MyMorePopupwindow(context);
        emptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
        mContentCmsApi = new ContentCmsApi(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                List<ContentCmsEntry> list = adapter.getData();
                if (position >= 0 && position < list.size()) {
                    ContentCmsEntry info = list.get(position);
                    if (info != null) {
                        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
                        intent.putExtra("tid", info.getId());
                        startActivity(intent);
                    }
                }
            }
        });
        getData(1);
    }

    private void onSharePlatfrom(SharePlatform platform, PlayBackInfo backInfo) {
        ShareContent content = new ShareContent();
        content.title = StringUtil.getLiveShareTitle();
        content.disc = StringUtil.getLiveShareContent(backInfo.getOwnerNickname(), backInfo.getRoomTitle());
        content.thumb = backInfo.getThumbUrl();
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getmSession().getBaseShareBackplayUrl() + backInfo.getId();
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }

    private void goBackPlayRoom(PlayBackInfo backInfo) {
        Intent intent = new Intent();
        intent.setClass(context, LiveBackPlayFullScreenRoomActivity.class);
        BackPlayIntentData intentData = new BackPlayIntentData();
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        intentData.setAutoJoinRoomAtOnce(true);
        intentData.setRoomId(backInfo.getChannelId());
        intentData.setRoomTitle(backInfo.getRoomTitle());
        intentData.setFullScreenVideoImagePath(backInfo.getRoomImagePath());
        intentData.setBackPlayId(backInfo.getId());
        intentData.setRoomOwnerId(backInfo.getOwnerId());
        intentData.setRoomOwnerAccountName(backInfo.getOwnerUsername());
        intentData.setRoomOwnerNickName(backInfo.getOwnerNickname());
        intentData.setRoomOwnerLogo(backInfo.getOwnerAvatarUrl());
        intentData.setRoomTotalCoins(backInfo.getTotalCoins());
        intentData.setMemberSize((int) backInfo.getPlayTimes());
        intentData.setLiveType(backInfo.getRoomLivetype() == LiveType.EventLive ? LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_GUESS :
                LiveBackPlayFullScreenRoomActivity.TYPE_LIVE_SHOW);
        intentData.setShowId(backInfo.getShowId());

        startActivity(intent);
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new MyLiveAdapter(context);
        }
        listView.setAdapter(adapter);
    }

    @Override
    protected void setEmptyLayout(final LinearLayout container) {
        emptyView = new EmptyView(context);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);

        container.addView(emptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_question_attion_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        Button btnStart = (Button) emptyLayout.findViewById(R.id.btn_start_live);
        btnStart.setText("开始上传视频");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                DefaultFragmentActivity.start(getActivity(), FileFragment.class.getName());
//                Intent intent = new Intent(context, PrepareLiveActivity.class);
//                startActivity(intent);
            }
        });
    }


    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
//        super.setTopView(topListViewContainer);
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.top_question_attion_layout, null);
        topView.addView(topLayout);
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<ContentCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            1 + "", App.getInstance().getPackageName() + "MineVideoragment.txt") {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<ContentCmsEntry> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<ContentCmsEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ContentCmsEntry entry = new Gson().fromJson(item.toString(), ContentCmsEntry.class);
//                               //判断是不是多图
                                entry.setShowType(entry.getShowType());
                                if (entry.getShowType() == 1) {
                                    if (entry.getThumbnail_urls().size() >= 3) entry.setShowType(4);
                                    if (entry.getThumbnail_urls() == null || entry.getThumbnail_urls().size() == 0)
                                        entry.setShowType(5);
                                }
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };


    protected void getMoreData() {
        page++;
        getData(page);
    }

    private void getData(int page) {
//        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/users/" + mUserId + "/videos?";
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/29/contents?";
        url += "page=" + page + "";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), page > 0).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<ContentCmsEntry>> callback = new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            if (adapter != null) {
                adapter.update(data, isAppend);
            }
        }

        @Override
        public void onFail(ApiException e) {
            emptyView.loadOver();
            pullToRefreshListView.onRefreshComplete();
            Log.e("TAG", "error == " + e.getMessage());
        }
    };

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
        //   pullToRefreshListView.onRefreshComplete();
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getMoreData();
        //   pullToRefreshListView.onRefreshComplete();
    }

//    private void getBackList() {
//        dataManager.getMyBackPlay(page, PAGE_SIZE, new DataRequest.DataCallback<PlayBackListInfo>() {
//            @Override
//            public void onSuccess(boolean isAppend, PlayBackListInfo data) {
//                emptyView.loadOver();
//                pullToRefreshListView.onRefreshComplete();
//                if (data != null) {
//                    List<PlayBackInfo> list = data.getData();
//                    if (list == null) {
//                        list = new ArrayList<PlayBackInfo>();
//                    }
//                    adapter.update(list, isAppend);
//                }
//            }
//
//            @Override
//            public void onFail(ApiException e) {
//                emptyView.loadOver();
//                pullToRefreshListView.onRefreshComplete();
//                e.printStackTrace();
//            }
//        });
//    }

    public class MyLiveAdapter extends BaseAdapter {
        private static final int TYPE_NEWS = 0;
        private static final int TYPE_VIDEO = 1;
        private static final int TYPE_GROUP = 3;
        private static final int TYPE_ACTIVIRY = 2;
        private static final int TYPE_COUNT = 4;

        private final String STATE_LIST = "ListAdapter.mlist";
        private ArrayList<ContentCmsEntry> items = new ArrayList<ContentCmsEntry>();
        private LayoutInflater inflater;
        public boolean bInit;
        Context mContext = null;

        int playVideoIndex = -1;   //记录播放器的位置

        public MyLiveAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            bInit = false;
            this.mContext = context;
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

        public ArrayList<ContentCmsEntry> getData() {
            return items;
        }

        public void SetInitStatus(boolean flag) {
            bInit = flag;
        }

        public long getMinId() {
            return items.isEmpty() ? -1 : items.get(items.size() - 1).getId();
        }

        public long getMaxId() {
            return items.isEmpty() ? -1 : items.get(0).getId();
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
            int type = 0;
            ContentCmsEntry item = items.get(position);
            if (item.getShowType() == 1) {
                type = TYPE_NEWS;
            } else if (item.getShowType() == 2) {
                type = TYPE_VIDEO;
            } else if (item.getShowType() == 4) {
                type = TYPE_GROUP;
            } else if (item.getShowType() == 5) {
                type = TYPE_ACTIVIRY;
            }
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
        }

        public class Multphotoholder extends ViewHolder {
            public LinearLayout relayoutArea;
            public TextView answeNumView;
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

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View newsView = null;
            View videoView = null;
            View multPic = null;
            View actvdView = null;
            int currentType = getItemViewType(position);
            if (currentType == TYPE_NEWS) {
                ViewHolder viewHolder = null;
                if (view == null) {
                    newsView = inflater.inflate(R.layout.news_answer_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.news_list_item_title);
                    viewHolder.thumbnailImageView = (ImageView) newsView.findViewById(R.id.news_news_list_item_image);
                    viewHolder.commentNumberTextView = (TextView) newsView.findViewById(R.id.news_list_item_answernum_tx);
                    viewHolder.item = items.get(position);
                    newsView.setTag(viewHolder);
                    view = newsView;
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.item = items.get(position);
                viewHolder.pos = position;
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                viewHolder.commentNumberTextView.setText(viewHolder.item.getComment_count() + "评论");
                if (viewHolder.item.getThumbnail_urls() != null && viewHolder.item.getThumbnail_urls().size() > 0) {
                    viewHolder.thumbnailImageView.setVisibility(View.VISIBLE);
                    String imageUrl = viewHolder.item.getThumbnail_urls().get(0).toString();
                    Util.LoadThumebImage(viewHolder.thumbnailImageView, imageUrl, null);
                } else {
                    viewHolder.thumbnailImageView.setVisibility(View.GONE);
                }
            } else if (currentType == TYPE_VIDEO) {
                Videoholder holder = null;
                if (view == null) {
                    holder = new Videoholder();
                    videoView = inflater.inflate(R.layout.news_video_list_hsrocll_item, null);
                    holder.thumbnailImageView = (ImageView) videoView.findViewById(R.id.item_img);
                    holder.titleTextView = (TextView) videoView.findViewById(R.id.item_title);
                    holder.ctimeTextView = (TextView) videoView.findViewById(R.id.item_create_time);
                    holder.videoContainer = (FrameLayout) videoView.findViewById(R.id.video_container);
                    holder.forgrondlay = (RelativeLayout) videoView.findViewById(R.id.forground_bank);
                    holder.commentNumberTextView = (TextView) videoView.findViewById(R.id.item_commeanuder_tx);
                    holder.btnplay = (ImageButton) videoView.findViewById(R.id.item_video_mark);
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
                                                ContentCmsInfoEntry info = new ContentCmsApi(view.getContext()).getContentCmsInfo(id);
                                                return info;
                                            }
                                        }).observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<ContentCmsInfoEntry>() {
                                                       @Override
                                                       public void onCompleted() {
                                                       }

                                                       @Override
                                                       public void onError(Throwable e) {

                                                       }

                                                       @Override
                                                       public void onNext(ContentCmsInfoEntry result) {
                                                           if (result == null) return;
//                                                           playVideo(result.getUrl(), holder);
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
                holder.titleTextView.setText(holder.item.getTitle());
                String timeStr = UtilHelp.getTimeString("yyy-MM-dd", holder.item.getCreation_time());
                holder.ctimeTextView.setText(timeStr);
//                if (App.getInstance().getmSession().isRead((int) holder.item.getId())) {
//                    holder.titleTextView.setTextColor(Color.parseColor("#ff8b8b8b"));
//                } else {
//                    holder.titleTextView.setTextColor(Color.parseColor("#ff000000"));
//                }
                holder.commentNumberTextView.setText(holder.item.getView_count() + "");
                if (holder.item.getThumbnail_urls() != null && holder.item.getThumbnail_urls().size() > 0) {
                    String imageUrl = holder.item.getThumbnail_urls().get(0).toString();
                    Util.LoadThumebImage(holder.thumbnailImageView, imageUrl, null);
                } else {
                    Util.LoadThumebImage(holder.thumbnailImageView, "", null);
                }
            } else if (currentType == TYPE_GROUP) {
                Multphotoholder viewHolder = null;
                if (view == null) {
                    multPic = inflater.inflate(R.layout.news_answer_item_multphotos, null);
                    viewHolder = new Multphotoholder();
                    viewHolder.titleTextView = (TextView) multPic.findViewById(R.id.group_title_txt);
                    viewHolder.relayoutArea = (LinearLayout) multPic.findViewById(R.id.news_list_iamgelayout);
                    viewHolder.answeNumView = (TextView) multPic.findViewById(R.id.news_list_item_answernum_tx);
                    int screen = UtilHelp.getScreenWidth(mContext);

                    //获取屏幕的宽度，每行3个Button，间隙为10共300，除4为每个控件的宽度
                    int nItmeWidh = (screen - 60) / 3;

                    LinearLayout mLayout = new LinearLayout(mContext);
                    mLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
                    mLayout.setLayoutParams(params);
                    for (int i = 0; i < 3; i++) {
                        ImageView bg = new ImageView(mContext);
                        bg.setBackgroundColor(mContext.getResources().getColor(R.color.img_default_bankgrond_color));
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(nItmeWidh, (int) (nItmeWidh * 3 / 4), 1.0f);
                        //控件距离其右侧控件的距离，此处为60
                        lp1.setMargins(0, 0, 10, 0);
                        bg.setLayoutParams(lp1);
                        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                LinearLayout linp = (LinearLayout) viewHolder.relayoutArea.getChildAt(0);
                int ncount = viewHolder.item.getThumbnail_urls().size();
                String[] item = {"", "", ""};
                String pathls = "";
                if (viewHolder.item.getThumbnail_urls() != null && ncount > 0) {
                    for (int i = 0; i < 3; i++) {
                        if (i < ncount) {
                            item[i] = viewHolder.item.getThumbnail_urls().get(i).toString();
                            pathls += item[i];
                            if (i != ncount - 1) pathls += ",";
                        }
                    }
                }
                for (int i = 0; i < linp.getChildCount(); i++) {
                    ImageView img = (ImageView) linp.getChildAt(i);
                    img.setTag(R.id.tag_hedlinegroup_path, pathls);
                    Util.LoadThumebImage(img, item[i], null);
                    if (TextUtils.isEmpty(item[i].toString())) {
                        img.setTag(R.id.tag_hedlinegroup_pos, -1);
                    } else {
                        img.setTag(R.id.tag_hedlinegroup_pos, i);
                    }
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
//                if (App.getInstance().getmSession().isRead((int) viewHolder.item.getId())) {
//                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ffadadad"));
//                } else {
//                    viewHolder.titleTextView.setTextColor(Color.parseColor("#ff212121"));
//                }
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
//                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getCreation_time() * 1000));
                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "评论");
                viewHolder.commentNumberTextView.setTextColor(getResources().getColor(R.color.public_red_bkg));
            }
            return view;
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

        /**
         * 添加列表项
         *
         * @param item
         */
        public void addItem(ContentCmsEntry item) {
            items.add(item);
        }

        /**
         * 添加指定列
         *
         * @param item
         */
        public void addItemByIndex(ContentCmsEntry item, int index) {
            items.add(index, item);
        }
    }
}
