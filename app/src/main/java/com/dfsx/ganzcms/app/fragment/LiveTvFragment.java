package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.MessageIntents;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by heyang on 2016/10/11.
 */
public class LiveTvFragment extends Fragment implements View.OnClickListener {
    private static final int BAR_TEXT_SIZE_SP = 14;
    private static final int ITEM_MIN_SPACE_DP = 10;
    private Context context;
    private Activity act;
    private int oldSelectedPosition;
    private int currentPosition = 0;
    private RadioGroup radioGroup;
    private FrameLayout videoContainer;
    private FrameLayout fullContainer;
    private TextView liveChannelText;
    private ListView listView;
    private LinearLayout hsvContent;
    private HorizontalScrollView horizontalScrollView;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    ArrayList<LiveEntity.LiveChannel> columlist = new ArrayList<>();
    boolean isSelectflag = false;
    private final static int tabItemIndex = 2;
    private CustomeProgressDialog mLoading;
    private LiveEntity.LiveChannel playedChannel;
    private boolean isResumeState;
    boolean isCreated = false;
    boolean isUserVisible = false;
    private Subscription videoSubscription;
    private Subscription addvideoSubscription;
    private Subscription tabItemSubscription;
    private Subscription channelSelectscription;
    private long colId = -1;
    ContentCmsApi mContentCmsApi = null;
    MyAdapter adapter;
    int pageOffset = 1;
    private CustomeProgressDialog dataLoading;
    LinearLayout mWeeks;
    Map<String, String> mMapWeeks;
    Map<String, ArrayList<LiveEntity.LiveChannel>> findTvlist;
    int weekIndex = 0;
    ArrayList<String> ww;
    private final static int defaultIndex = 1;
    ImageView _videoPlayBtn;
    private TextView channelSelBtn;
    private long channelId;
    private boolean isPlaying = false;
    private int deafultChanlIndex = 0;
    private LiveEntity.LiveChannel defaultChannal;
    private Map<String, Integer> weekSelectPosMap = new HashMap<>();  //每周被点击选中的那条


    //  星期
//    private static String[] weekDatas = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public static LiveTvFragment newInstance(long type) {
        LiveTvFragment fragment = new LiveTvFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_live_video, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            colId = getArguments().getLong("type");
        }
        if (colId == -1 || colId == 0) {
            colId = ColumnBasicListManager.getInstance().findIdByName("tv");
        }
        context = getContext();
        act = getActivity();
        mContentCmsApi = new ContentCmsApi(getActivity());
        initView(view);
//        createWeekTextView();
        getTvlistData(false);
        initAction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            case 2:
                if (data == null) return;
                LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) data.getSerializableExtra("key");
                resetInit(channel);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void resetInit(LiveEntity.LiveChannel channel) {
        if (channel != null) {
            if (playedChannel != null) {
                if (!TextUtils.isEmpty(playedChannel.channelName)) {
                    if (TextUtils.equals(playedChannel.channelName, channel.channelName))
                        return;
                }
            }
            defaultChannal = channel;
            playedChannel = channel;
            channelId = playedChannel.channelID;
            liveChannelText.setText(playedChannel.channelName);
            //                if (getVideoPlyer() != null && getVideoPlyer().isPlay())
            //                    getVideoPlyer().stop();
            killPlayer();
            if (weekSelectPosMap != null)
                weekSelectPosMap.clear();
            if (adapter != null)
                adapter.setSelectPostion(-1);   //清除播放记录
//            restartPlayer();
            //                getData(false, mMapWeeks.get("今天"), false);
            getData(false, getQuerytime(), true);
        }
        setSelectedTextColor(weekIndex);
    }

    public void restWeekItem(String date, int position) {
        if (weekSelectPosMap == null) return;
        boolean isExist = false;
        if (weekSelectPosMap.size() > 0) {
            Iterator<Map.Entry<String, Integer>> it = weekSelectPosMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                if (TextUtils.equals(entry.getKey(), date)) {
                    isExist = true;
                    if (position != -1)
                        entry.setValue(position);
                } else {
                    if (position != -1)
                        entry.setValue(-1);
                }
            }
        } else {
            weekSelectPosMap.put(date, position);
        }
        if (!isExist) {
            weekSelectPosMap.put(date, position);
        }
    }

    private void initView(View v) {
        _videoPlayBtn = (ImageView) v.findViewById(R.id.live_yv_play_btn);
        _videoPlayBtn.setOnClickListener(this);
        channelSelBtn = (TextView) v.findViewById(R.id.live_channel_select_btn);
        channelSelBtn.setOnClickListener(this);
        mWeeks = (LinearLayout) v.findViewById(R.id.tv_week_bofy);
        videoContainer = (FrameLayout) v.findViewById(R.id.video_container);
        fullContainer = (FrameLayout) v.findViewById(R.id.full_screen_video_containerss);
        liveChannelText = (TextView) v.findViewById(R.id.live_channel_name);
        listView = (ListView) v.findViewById(R.id.tv_list);
        hsvContent = (LinearLayout) v.findViewById(R.id.hsv_content);
        horizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.hsv_view);
//        radioGroup = (RadioGroup) v.findViewById(R.id.live_radio_bar);
//        radioGroup.setOnCheckedChangeListener(onRadlister);
        initWeeksc();
    }

    public void initWeeksc() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        String dates = sDateFormat.format(ca.getTime());
//        String ds = onDateSet(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        mMapWeeks = new HashMap<String, String>();
        ww = new ArrayList<String>();
        String week = getWeek(ca.getTime());
//        mMapWeeks.put("今天", dates);
//        ww.add("今天");
        mMapWeeks.put(week, dates);
        mMapWeeks.put("今天", dates);
        ww.add("今天");
        fillWeeks(week, ca);
        createWeekTextView();
    }

    public void fillWeeks(String tody, Calendar ca) {
        String week = "";
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dates = "";
        Calendar tag = ca;
        for (int i = 0; i < 6; i++) {
            tag.add(Calendar.DATE, -1);
            dates = sDateFormat.format(tag.getTime());
            //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
            week = getWeek(tag.getTime());
            ww.add(week);
            mMapWeeks.put(week, dates);
        }
    }

//    public void fillWeeks(String tody, Calendar ca) {
//        String week = "";
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String dates = "";
//        Calendar tag = ca;
//        int preDate = queryPreDateSpane(tody);
//        if (preDate > 0) {
//            for (int i = 0; i < preDate; i++) {
//                tag.add(Calendar.DATE, -1);
//                dates = sDateFormat.format(tag.getTime());
//                //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
//                week = getWeek(tag.getTime());
//                ww.add(week);
//                mMapWeeks.put(week, dates);
//            }
//            int reMainCOnut = 7 - preDate;
//            if (reMainCOnut > 0) {
//                tag = ca;
//                for (int i = 0; i < reMainCOnut; i++) {
//                    tag.add(Calendar.DATE, +1);
//                    dates = sDateFormat.format(tag.getTime());
//                    //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
//                    week = getWeek(tag.getTime());
//                    ww.add(week);
//                    mMapWeeks.put(week, dates);
//                }
//            }
//        } else {
//            for (int i = 0; i < 6; i++) {
//                tag.add(Calendar.DATE, +1);
//                dates = sDateFormat.format(tag.getTime());
//                //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
//                week = getWeek(tag.getTime());
//                ww.add(week);
//                mMapWeeks.put(week, dates);
//            }
//        }
//    }


    public void createWeekTextView() {
        itemWidthList.clear();
        for (int i = ww.size() - 1; i >= 0; i--) {
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(ww.get(i));
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(context.getResources().getColor(R.color.COLOR_WHITE_NORMAL));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(context, ww.get(i).length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
            itemWidthList.add(itemWidth);
            hsvContent.addView(layout, itemWidth, Util.dp2px(context, 40));
            layout.setOnClickListener(new WeekDateClickListener(i));
            layout.setTag(i);
        }
//        weekIndex = queryPreDateSpane(ww.get(0));
        weekIndex = 0;
//        if (weekIndex < 0 || weekIndex >= weekDatas.length)
//            weekIndex = 0;
        if (weekIndex < 0 || weekIndex >= ww.size())
            weekIndex = 0;
        setSelectedTextColor(weekIndex);
    }

    public static String getWeek(Date date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "Radio_____onResume");
        isResumeState = true;
        if (isSelectflag) {
            LiveEntity.LiveChannel channel = ColumnBasicListManager.getInstance().getSelectliveTvChanne();
            if (channel != null) {
                isSelectflag = false;
                if (playedChannel == null || !TextUtils.equals(channel.channelName, playedChannel.channelName)) {
                    resetInit(channel);
                } else {
                    if (isPlaying)
                        restartPlayer();
                }
            }
        }
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataTvRequest =
            new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                    getFileId() + "." + colId, App.getInstance().getPackageName() + getFileName()) {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject json) {
                    ArrayList<ContentCmsEntry> dlist = null;
                    try {
                        if (json != null && !TextUtils.isEmpty(json.toString())) {
                            JSONArray attr = json.optJSONArray("data");
                            if (attr != null && attr.length() > 0) {
                                dlist = new ArrayList<ContentCmsEntry>();
                                for (int i = 0; i < attr.length(); i++) {
                                    JSONObject obj = (JSONObject) attr.get(i);
                                    ContentCmsEntry entry = new Gson().fromJson(obj.toString(), ContentCmsEntry.class);
                                    if (!TextUtils.equals(entry.getType(), "live")) continue;
//                                    if (entry.getShowType() != 3) continue;
                                    dlist.add(entry);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
                }
            };

    private void getTvlistData(boolean bAddTail) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + colId + "/contents?";
        url += "page=" + pageOffset;
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build();
        dataTvRequest.getData(httpParams, bAddTail).
                setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final ArrayList<ContentCmsEntry> liveChannelsAarry) {
                        if (liveChannelsAarry != null && !liveChannelsAarry.isEmpty()) {
                            Observable.from(liveChannelsAarry)
                                    .subscribeOn(Schedulers.io())
                                    .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                                        @Override
                                        public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                                            ContentCmsInfoEntry info = mContentCmsApi.getEnteyFromJson(topicalEntry.getId());
                                            return info;
                                        }
                                    })
                                    .toList()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onNext(List<ContentCmsInfoEntry> data) {
                                            if (data != null && !data.isEmpty()) {
                                                columlist.clear();
//                                                hsvContent.removeAllViews();
                                                for (int i = 0; i < data.size(); i++) {
                                                    ContentCmsInfoEntry enytry = data.get(i);
                                                    if (enytry == null) continue;
                                                    String thummb = "";
                                                    if (enytry.getThumbnail_urls() != null && !enytry.getThumbnail_urls().isEmpty())
                                                        thummb = enytry.getThumbnail_urls().get(0).toString();
                                                    LiveEntity.LiveChannel channel = new LiveEntity.LiveChannel(enytry.getLiveId(), enytry.getTitle(), enytry.getSummary(), enytry.getUrl(), thummb, "rfequency");
                                                    columlist.add(channel);
                                                }
                                                initTopWeekData(columlist);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void initTopWeekData(ArrayList<LiveEntity.LiveChannel> list) {
        if (list != null && list.size() > 0) {
            ColumnBasicListManager.getInstance().setLiveTvChannelMap(list);
            //         addVideoPlayerToContainer(getVideoPlyer());
            playedChannel = list.get(0);
            defaultChannal = playedChannel;
            if (playedChannel != null) {
                liveChannelText.setText(playedChannel.channelName);
                channelId = playedChannel.channelID;
            }
//            playedChannel.url="http://218.6.224.15:8011/vms/videos/channellive/channel1/playlist.m3u8";
            //    getVideoPlyer().start(list.get(0).url);
            if (adapter != null) {
                if (!adapter.isInit()) {
                    //获取过去7天的所有节目单
                    getData(false, getQuerytime(), true);
                    //       getData(false, mMapWeeks.get("今天"), true);
                }
            }
        }
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    private void initAction() {
//        radioGroup.check(R.id.bar_today);
        adapter = new MyAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                position = position - listView.getHeaderViewsCount();
                try {
                    boolean isLeagle = position >= 0 && position < adapter.getCount();
                    if (isLeagle) {
                        String week = "";
                        if (ww != null && currentPosition < ww.size()) {
                            week = ww.get(currentPosition);
                        }
                        restWeekItem(week, position);
                        LiveEntity.LiveChannel channel = adapter.getData().get(position);
                        if (channel != null) {
                            if (channel.isLive) {
                                adapter.setSelectPostion(-1);
                                if (!(defaultChannal == null || TextUtils.isEmpty(defaultChannal.url))) {
                                    playedChannel = defaultChannal;
                                    killPlayer();
                                    restartPlayer();
                                } else {
                                    ToastUtils.toastMsgFunction(getActivity(), "视频源无效");
                                }
                            } else {
                                if (channel.url != null && !TextUtils.isEmpty(channel.url)) {
                                    adapter.setSelectPostion(position);
                                    playedChannel = new LiveEntity.LiveChannel(channel.url);
                                    killPlayer();
                                    restartPlayer();
                                } else {
                                    ToastUtils.toastMsgFunction(getActivity(), "视频源无效");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tabItemSubscription = RxBus.getInstance().
                toObserverable(TabItem.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TabItem>() {
                    @Override
                    public void call(TabItem intent) {
                        if (intent.getMsg().equals(TabItem.BOTTOM_TAB_TV)) {
                            pos = 2;
                            if (getCurrentIndex() == 0 && pos == tabItemIndex && playedChannel != null) {
//                                addVideoPlayerToContainer(getVideoPlyer());
//                                restartPlayer();
                            } else
                                killPlayer();
                        } else {
                            killPlayer();
//                            if (backfrag != null)
//                                backfrag.clearPlay();
                        }
                    }
                });

        channelSelectscription = RxBus.getInstance().
                toObserverable(MessageData.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageData>() {
                    @Override
                    public void call(MessageData messageData) {
                        if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_TV_CLOSED)) {
                            killPlayer();
//                            if (backfrag != null)
//                                backfrag.clearPlay();
                        }
                    }
                });

    }

    public int getCurrentIndex() {
        int position = -1;
        if (getParentFragment() != null) {
            position = ((TvRadioFragment) getParentFragment()).getCurrentPos();
        }
        return position;
    }

    int pos = -1;

    public VideoAdwarePlayView getVideoPlyer() {
        if (act instanceof MainTabActivity) {
            return ((MainTabActivity) act).getVideoPlayer();
        }
        return null;
    }

    /**
     * 设置video播放数据
     *
     * @param item
     */
    private void setVideoPlay(LiveEntity.LiveChannel item) {
//        if (item != null && pos == defaultIndex) {
        if (item != null) {
            playedChannel = item;
            if (item.url == null || TextUtils.isEmpty(item.url)) {
//                killPlayer();
                Toast.makeText(getContext(), "播放失败,地址无效!!", Toast.LENGTH_SHORT).show();
            } else {
                if (!getVideoPlyer().isPlay()) {
                    addVideoPlayerToContainer(getVideoPlyer());    //heyang  2017-2-15
                    getVideoPlyer().start(item.url, true,null);
                    _videoPlayBtn.setVisibility(View.GONE);
                    isPlaying = true;
                    getVideoPlyer().setErrorListener(new VideoAdwarePlayView.OnErrorListener() {
                        @Override
                        public void errorListener(IMediaPlayer mp) {
                            isPlaying = false;
                            _videoPlayBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
//                liveChannelText.setText(item.channelName);
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

    public void killPlayer() {

        if (getVideoPlyer() != null && getVideoPlyer().isPlay())
            getVideoPlyer().stop();
        if (getVideoPlyer() != null)
            getVideoPlyer().release();
        removeVideoPlayer();
        _videoPlayBtn.setVisibility(View.VISIBLE);
    }

    public void restartPlayer() {
        if (playedChannel != null) {
            setVideoPlay(playedChannel);
        } else {
            ToastUtils.toastMsgFunction(getActivity(), "视频地址无效！");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        killPlayer();
        isResumeState = false;
    }

    @Override
    public void onStart() {
        super.onStart();
//        addVideoPlayerToContainer(getVideoPlyer());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isCreated) {
            return;
        }
        //        if (isVisibleToUser) {
        //            isUserVisible = true;
        //            addVideoPlayerToContainer(getVideoPlyer());
        //            getVideoPlyer().start("http://live.ysxtv.cn:8081/Ch1/playlist.m3u8");
        //        } else {
        //            isUserVisible = false;
        //            killPlayer();
        //        }
    }

    private void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer) {
        videoContainer.setVisibility(View.VISIBLE);
        fullContainer.setVisibility(View.GONE);
        if (videoContainer != null) {
            if (!(videoContainer.getChildAt(0) != null &&
                    videoContainer.getChildAt(0) instanceof VideoAdwarePlayView)) {
                removeVideoPlayer();
                videoContainer.addView(videoPlayer, 0);
            }
        }
    }

    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        fullContainer.setVisibility(View.VISIBLE);
        videoContainer.setVisibility(View.GONE);
        if (fullContainer.getChildCount() <= 0 ||
                !(fullContainer.getChildAt(0) instanceof VideoPlayView)) {
            fullContainer.addView(videoPlayer, 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoSubscription != null) {
            videoSubscription.unsubscribe();
        }
        if (addvideoSubscription != null) {
            addvideoSubscription.unsubscribe();
        }
        if (tabItemSubscription != null)
            tabItemSubscription.unsubscribe();

        if (channelSelectscription != null)
            channelSelectscription.unsubscribe();

        if (getVideoPlyer() != null) {
            killPlayer();
            getVideoPlyer().onDestroy();
        }
    }

    private String getFileId() {
        String defaultId = "livetv_";
        if (playedChannel != null)
            defaultId += playedChannel.channelID;
        return defaultId;
    }

//    private String getQuerytime() {
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar ca = Calendar.getInstance();
//        String dates = "";
//        int preDate = 0;
//        if (!(ww == null || ww.isEmpty()))
//            preDate = queryPreDateSpane(ww.get(0));
//        if (preDate == 0) {
//            if (mMapWeeks != null)
//                dates = mMapWeeks.get("今天");
//        } else {
//            ca.add(Calendar.DATE, -preDate);
//            dates = sDateFormat.format(ca.getTime());
//        }
//        return dates;
//    }

    /**
     * 获取过去6天的节目单
     *
     * @return
     */
    private String getQuerytime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        String dates = "";
        ca.add(Calendar.DATE, -6);
        dates = sDateFormat.format(ca.getTime());
        return dates;
    }

    public int queryPreDateSpane(String today) {
        int preDate = 0;
        switch (today) {
            case "周二":
                preDate = 1;
                break;
            case "周三":
                preDate = 2;
                break;
            case "周四":
                preDate = 3;
                break;
            case "周五":
                preDate = 4;
                break;
            case "周六":
                preDate = 5;
                break;
            case "周日":
                preDate = 6;
                break;
        }
        return preDate;
    }

    private String getFileName() {
        return "LiveTxFragment.txt";
    }

    private DataFileCacheManager<ArrayList<LiveEntity.LiveChannel>> dataRequest =
            new DataFileCacheManager<ArrayList<LiveEntity.LiveChannel>>(App.getInstance().getApplicationContext(),
                    getFileId(), getQuerytime()) {
                @Override
                public ArrayList<LiveEntity.LiveChannel> jsonToBean(JSONObject json) {
                    //                    ArrayList<LiveEntity.LiveChannel> dlist = null;
                    if (json != null && !TextUtils.isEmpty(json.toString())) {
                        Iterator iterator = json.keys();
                        while (iterator.hasNext()) {
                            if (findTvlist == null) findTvlist = new HashMap<>();
                            String key = (String) iterator.next();
                            JSONArray arr = json.optJSONArray(key);
                            if (arr != null && arr.length() > 0) {
                                ArrayList<LiveEntity.LiveChannel> dlist = new ArrayList<LiveEntity.LiveChannel>();
                                boolean isflag = false;
                                try {
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = (JSONObject) arr.get(i);
                                        LiveProgramEntity.LiveProgram entity = new Gson().fromJson(obj.toString(), LiveProgramEntity.LiveProgram.class);
                                        String date = Util.getTimeString("yyyy-MM-dd", entity.getStart_time());
                                        String time = Util.getTimeString("HH:mm:ss", entity.getStart_time());
                                        String endtime = Util.getTimeString("HH:mm:ss", entity.getStop_time());
                                        if (!TextUtils.equals(key, date)) continue;
                                        LiveEntity.LiveChannel chanl = new LiveEntity.LiveChannel(entity.getM3u8_url(), entity.getName(), entity.getDescription(), time, endtime);
                                        //判断停止时间D大于当前时间
                                        //             SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
                                        Date curDate = new Date();//获取当前时间
                                        if (!isflag) {
                                            String liveUrl = chanl.url;
                                            long current = curDate.getTime();
//                                            if (entity.getStop_time() * 1000 <= current) {
//                                                chanl.isPlayback = true;
//                                            } else {
//                                                String liveUrl = chanl.url;
//                                                if (liveUrl == null || TextUtils.isEmpty(liveUrl)) {
//                                                    if (columlist != null && columlist.size() > 0)
//                                                        liveUrl = columlist.get(0).url;
//                                                }
//                                                //判断到没有到播放时间
//                                                if (entity.getStart_time() * 1000 < current) {
//                                                    int pos = i > 0 ? --i : 0;
//                                                    if (pos < dlist.size()) {
//                                                        LiveEntity.LiveChannel tasg = dlist.get(pos);
//                                                        tasg.isLive = true;
//                                                        tasg.isPlayback = false;
//                                                        tasg.url = liveUrl;
//                                                    }
//                                                } else {
//                                                    chanl.isLive = true;
//                                                    isflag = true;
//                                                }
//                                            }

                                            ///  方法二
//                                            if (entity.getM3u8_url() == null || TextUtils.isEmpty(entity.getM3u8_url())) {
//                                                if (columlist != null && columlist.size() > 0) {
//                                                    if (currentPosition < columlist.size()) {
//                                                        liveUrl = columlist.get(currentPosition).url;
//                                                    }
//                                                }
//                                                if (entity.getStart_time() * 1000 < current) {
//                                                    chanl.isLive = true;
//                                                    chanl.isPlayback = false;
//                                                    isflag = true;
//                                                    if (liveUrl == null || TextUtils.isEmpty(liveUrl))
//                                                        chanl.isValid = false;
//                                                } else {
//                                                    int pos = i > 0 ? --i : 0;
//                                                    if (!dlist.isEmpty() && pos < dlist.size()) {
//                                                        LiveEntity.LiveChannel tasg = dlist.get(pos);
//                                                        tasg.isLive = true;
//                                                        tasg.isPlayback = false;
//                                                        tasg.url = liveUrl;
//                                                        if (liveUrl == null || TextUtils.isEmpty(liveUrl))
//                                                            tasg.isValid = false;
//                                                    }
//                                                }

                                            if (entity.getStop_time() * 1000 <= current) {
                                                chanl.isLive = false;
                                                chanl.isPlayback = true;
                                                if (liveUrl == null || TextUtils.isEmpty(liveUrl))
                                                    chanl.isValid = false;
                                            } else {
                                                chanl.isLive = true;
                                                chanl.isPlayback = false;
                                                isflag = true;
                                                if (liveUrl == null || TextUtils.isEmpty(liveUrl))
                                                    chanl.isValid = false;
                                            }
//                                                isflag = true;
//                                            } else {
//                                                chanl.isPlayback = true;
//                                            }
                                        }
//                                        String totoday = mMapWeeks.get("今天");
//                                        if (!TextUtils.equals(key, totoday)) {
//                                            chanl.isPlayback = true;
//                                        }
                                        dlist.add(chanl);
                                    }
                                    findTvlist.put(key, dlist);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    return null;
                }
            };

    private void getData(boolean bAddTail, String date, boolean showProcessBar) {
        if (playedChannel == null) return;
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/lives/" + playedChannel.channelID + "/playlists?";
        url += "start=" + date + "&limit=7";
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).
                build();
        dataRequest.getData(httpParams, bAddTail).setCallback(new DataRequest.DataCallback<ArrayList<LiveEntity.LiveChannel>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<LiveEntity.LiveChannel> liveChannelsAarry) {
                if (findTvlist != null && findTvlist.size() > 0) {
                    String totoday = mMapWeeks.get("今天");
                    ArrayList<LiveEntity.LiveChannel> list = findTvlist.get(totoday);
                    adapter.update(list);
                    if (list != null && !list.isEmpty()) {
                        if (playedChannel == null || playedChannel.url == null || TextUtils.isEmpty(playedChannel.url)) {
                            playedChannel = list.get(0);
                        }
                    }
//                    if (pos == defaultIndex && playedChannel != null && !TextUtils.isEmpty(playedChannel.url))
//                        setVideoPlay(playedChannel);
                }
                //                if (liveChannelsAarry != null) {
                //                    adapter.update(liveChannelsAarry);
                //                }
                if ((mLoading != null) && (mLoading.isShowing()))
                    mLoading.dismiss();
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("LiveVideoSubFragment", "getData fail");
                e.printStackTrace();
                if ((mLoading != null) && (mLoading.isShowing()))
                    mLoading.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == _videoPlayBtn) {
            restartPlayer();
        } else if (v == channelSelBtn) {
            if (playedChannel == null ||
                    ColumnBasicListManager.getInstance().getLiveTvChannelMap() == null) {
                ToastUtils.toastMsgFunction(getActivity(), "没有频道");
                return;
            }
            isSelectflag = true;
            Intent intent = new Intent(getActivity(), DefaultFragmentActivity.class);
            intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_NAME, TvChannerSelectFragment.class.getName());
            intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, channelId);
//            getActivity().startActivityForResult(intent,2);
            getRootFragment().getActivity().startActivityForResult(intent, 2);
//            DefaultFragmentActivity.start(getActivity(), TvChannerSelectFragment.class.getName(), playedChannel.channelID);
        }
    }

    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        if (fragment == null) return null;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }

//    class MyOnClickListener implements View.OnClickListener {
//        int position;
//
//        public MyOnClickListener(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void onClick(View v) {
//            horizontalScrollView.smoothScrollTo(getItemLeftXPosition(position - 1), 0);
//            if (columlist != null && position < columlist.size()) {
//                playedChannel = columlist.get(position);
//                //                if (getVideoPlyer() != null && getVideoPlyer().isPlay())
//                //                    getVideoPlyer().stop();
//                killPlayer();
//                restartPlayer();
//                //                getData(false, mMapWeeks.get("今天"), false);
//                getData(false, getQuerytime(), true);
//            }
//            setSelectedTextColor(position);
////            setSelectedWeekColor(0);
//        }
//    }

    class WeekDateClickListener implements View.OnClickListener {
        int position;

        public WeekDateClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            horizontalScrollView.smoothScrollTo(getItemLeftXPosition(position - 1), 0);
            if (ww == null) return;
            if (position < ww.size()) {
                String data = ww.get(position);
                restWeekItem(data, -1);
                if (findTvlist != null && findTvlist.size() > 0) {
                    String totoday = mMapWeeks.get(data);
                    ArrayList<LiveEntity.LiveChannel> list = findTvlist.get(totoday);
                    adapter.update(list);
                    if (list != null && !list.isEmpty()) {
                        if (playedChannel == null || playedChannel.url == null || TextUtils.isEmpty(playedChannel.url)) {
                            playedChannel = list.get(0);
                        }
                    }
                }
            }
            setSelectedTextColor(position);
//            setSelectedWeekColor(0);
        }
    }

//    private void setSelectedWeekColor(int selectedPosition) {
//        TextView oldSelectedText = (TextView) mWeeks.findViewWithTag(weekIndex).
//                findViewById(R.id.scroll_item_week_id);
//        if (oldSelectedText != null && getActivity() != null)
//            oldSelectedText.setTextColor(getResources().getColor(R.color.tv_week_name));
//        if (selectedPosition != -1) {
//            TextView selectedText = (TextView) mWeeks.findViewWithTag(selectedPosition).
//                    findViewById(R.id.scroll_item_week_id);
//            if (getActivity() != null)
//                selectedText.setTextColor(getResources().getColor(R.color.tv_week_selected));
//            weekIndex = selectedPosition;
//        }
//    }

    private void setSelectedTextColor(int selectedPosition) {
        currentPosition = selectedPosition;
        TextView oldSelectedText = (TextView) hsvContent.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) hsvContent.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        if (getActivity() != null) {
            oldSelectedText.setTextColor(getResources().getColor(R.color.COLOR_WHITE_NORMAL));
            selectedText.setTextColor(getResources().getColor(R.color.COLOR_WHITE));
        }
        oldSelectedPosition = selectedPosition;
    }

    class MyAdapter extends BaseListViewAdapter<LiveEntity.LiveChannel> {
        private int selectedPosition = -1;// 选中的位置
        private boolean isInit = false;

        public MyAdapter(Context context) {
            super(context);
        }

        public void setSelectPostion(int position) {
            this.selectedPosition = position;
            notifyDataSetInvalidated();
        }

        public void update(ArrayList<LiveEntity.LiveChannel> data) {
            list = data;
            isInit = true;
            notifyDataSetChanged();
        }

        public boolean isInit() {
            return isInit;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.live_program_item_list;
        }

        @Override
        public void setItemViewData(BaseViewHodler viewHodler, int position) {
            View body = viewHodler.getView(R.id.tv_body_layout);
            View headSpace = viewHodler.getView(R.id.radio_sel_line);
            TextView timeView = viewHodler.getView(R.id.tv_item_time);
            TextView titleView = viewHodler.getView(R.id.tv_item_title_tx);
            ImageView status = viewHodler.getView(R.id.proram_status_mask);
            ImageView operartxr = viewHodler.getView(R.id.radio_play_mask);
            LiveEntity.LiveChannel channe = list.get(position);
            titleView.setTextColor(getResources().getColor(R.color.tv_name_selected));
            operartxr.setVisibility(View.GONE);

            if (channe.url == null || TextUtils.isEmpty(channe.url)) {
                status.setImageResource(R.drawable.tv_program_normal);
            } else {
                status.setImageResource(R.drawable.tv_program_select);
            }
            if (!channe.isPlayback) {
                if (channe.isLive && currentPosition == weekIndex) {
                    status.setImageResource(R.drawable.radio_program_select);
                    operartxr.setVisibility(View.VISIBLE);
                } else {
                    operartxr.setVisibility(View.GONE);
                }
            }

            String week = "";
            if (ww != null && currentPosition < ww.size()) {
                week = ww.get(currentPosition);
            }
            int pos = -1;
            if (!weekSelectPosMap.isEmpty()) {
                pos = weekSelectPosMap.get(week);
            }
            selectedPosition = pos;
            if (selectedPosition == position) {
                body.setBackgroundColor(getResources().getColor(R.color.line_color));
                headSpace.setVisibility(View.VISIBLE);
            } else {
                body.setBackgroundColor(getResources().getColor(R.color.white));
                headSpace.setVisibility(View.INVISIBLE);
            }

//            if (selectedPosition == position && currentPosition == weekIndex) {
//                body.setBackgroundColor(getResources().getColor(R.color.line_color));
//                headSpace.setVisibility(View.VISIBLE);
//            } else {
//                body.setBackgroundColor(getResources().getColor(R.color.white));
//                headSpace.setVisibility(View.INVISIBLE);
//            }
            timeView.setText(list.get(position).creaTime);
            titleView.setText(list.get(position).channelName);
        }
    }
}
