package com.dfsx.ganzcms.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.act.*;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by heyang on 2017/6/20
 * 问答  页面
 */
public class QustionAnsFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView>, GestureDetector.OnGestureListener, View.OnClickListener {
    private static final String TAG = "QustionAnsFragment";
    private PullToRefreshListView pullListview;
    private ListView list;
    private final int NETWORK_BUSY = 54;
    private ImageButton mLoadRetryBtn;
    private long mCLoumnType = -1;
    private HeadlineListManager dataRequester;
    private ListViewAdapter adapter;
    private ContentCmsApi mContentCmsApi = null;
    //定时任务
    private int offset = 0;
    private Subscription tabItemSubscription;

    private View mQuestBtn, mAnswerBtn;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NETWORK_BUSY) {
                if (getActivity() != null) {
                    AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            QustionAnsFragment.this.onResume();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            QustionAnsFragment.this.pullListview.setVisibility(View.GONE);
                            QustionAnsFragment.this.list.setVisibility(View.GONE);
                        }
                    }).create();
                    adig.show();
                } else {
                    QustionAnsFragment.this.pullListview.setVisibility(View.GONE);
                    QustionAnsFragment.this.list.setVisibility(View.GONE);
                }
            }
            return false;
        }
    });

    public static QustionAnsFragment newInstance(long type) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", type);
        QustionAnsFragment fragment = new QustionAnsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v2) {
        try {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            //限制必须得划过屏幕的1/4才能算划过
            float x_limit = Util.dp2px(getActivity(), 35);
            float y_limit = Util.dp2px(getActivity(), 35);
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;
            if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling down
                Log.i("MyGesture", "Fling down");
                Toast.makeText(getActivity(), "Fling down", Toast.LENGTH_SHORT).show();
            } else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling up
                Log.i("MyGesture", "Fling up");
                Toast.makeText(getActivity(), "Fling up", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DisplayMetrics dm = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tabItemSubscription != null)
            tabItemSubscription.unsubscribe();
        if (getVideoPlyer() != null) {
            getVideoPlyer().onDestroy();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCLoumnType = bundle.getLong("type");
        }
        mContentCmsApi = new ContentCmsApi(getActivity());
        //创建数据的管理器
        dataRequester = new HeadlineListManager(getActivity(), 1 + "", mCLoumnType,"");
        dataRequester.setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (pullListview != null)
                    pullListview.onRefreshComplete();
                if (adapter != null && data != null && data.size() > 0) {
                    if (data != null && !data.isEmpty()) {
                        adapter.update(data, isAppend);
                    }
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                if (pullListview != null)
                    pullListview.onRefreshComplete();
            }
        });

        View view = inflater.inflate(R.layout.news, container, false);
        mLoadRetryBtn = (ImageButton) view.findViewById(R.id.reload_btn);
        mLoadRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        list = ((ListView) pullListview.getRefreshableView());
        adapter = new ListViewAdapter(this.getActivity());
        list.setAdapter(adapter);
        list.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.news_answer_header_layout, null));
        mQuestBtn = (View) view.findViewById(R.id.quest_view);
        mAnswerBtn = (View) view.findViewById(R.id.anwer_view);
        mQuestBtn.setOnClickListener(this);
        mAnswerBtn.setOnClickListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewAdapter.ViewHolder vmHolder = (ListViewAdapter.ViewHolder) view.getTag();
//                if (getVideoPlyer() != null && getVideoPlyer().getTag() != null) {
//                    View child = (View) getVideoPlyer().getTag();
//                    String ip = child.getParent().getParent().getParent().getClass().getName();
//                    if (view == child.getParent().getParent().getParent().getParent()) {
//                        if (getVideoPlyer().isPlay())
//                            return;
//                    }
//                }
//                if (vmHolder.item.getType() == 2 || vmHolder.item.getType() == 3) return;
                if (vmHolder == null || vmHolder.item == null) return;
                goDetail(vmHolder.item, vmHolder.pos);
            }
        });
        initAction();
        return view;
    }

    private void initAction() {
        tabItemSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_SCROLL_ITEM_OK)) {
//                            int pos = intent.getIntExtra("pos", -1);
//                            if (pos != -1) {
//                                if (pos != defaultIndex && getVideoPlyer().getTag() != null) {
//                                    adapter.stopPlay();
//                                }
//                            }
                        }
                    }
                });
    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (getActivity() instanceof MainTabActivity) {
            return ((MainTabActivity) getActivity()).getVideoPlayer();
        }
        return null;
    }

    public void stopVideo() {
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

    private void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    private void goDetail(ContentCmsEntry channel, int listPos) {
        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
        intent.putExtra("tid", channel.getId());
        startActivity(intent);
    }

    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        if (fragment == null) return null;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !adapter.isInited()) {
            dataRequester.start(false, 1);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset = 1;
        dataRequester.start(false, offset);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset++;
        dataRequester.start(true, offset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (view == mQuestBtn) {
            DefaultFragmentActivity.start(getActivity(), MyquswerTabFragment.class.getName());
//            LSUtils.toastNoFunction(getContext());
        } else if (view == mAnswerBtn) {
//            LSUtils.toastNoFunction(getContext());
            WhiteTopBarActivity.startAct(getActivity(), MyAnswerFragment.class.getName(), "我来回答");
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        private static final int TYPE_NEWS = 0;
        private static final int TYPE_VIDEO = 1;
        private static final int TYPE_GROUP = 3;
        private static final int TYPE_COUNT = 3;

        private final String STATE_LIST = "ListAdapter.mlist";
        private ArrayList<ContentCmsEntry> items = new ArrayList<ContentCmsEntry>();
        private LayoutInflater inflater;
        public boolean bInit;
        Context mContext = null;

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
//            HeadLineFragment.this.pullListview.setVisibility(0);
//            HeadLineFragment.this.list.setVisibility(0);
//            mRelativeLayoutFail.setVisibility(8);
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
                type = TYPE_NEWS;
            } else if (item.getShowType() == 2) {
                type = TYPE_VIDEO;
            } else if (item.getShowType() == 4) {
                type = TYPE_GROUP;
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

        public class LiveHolder extends ViewHolder {
            public CircleButton userImage;
            public TextView username;
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

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View newsView = null;
            View videoView = null;
            View multPic = null;
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
                holder.titleTextView.setText(holder.item.getTitle());
                String timeStr = UtilHelp.getTimeString("yyy-MM-dd", holder.item.getCreation_time());
                holder.ctimeTextView.setText(timeStr);
//                if (IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).isRead((int) holder.item.getId())) {
//                    holder.titleTextView.setTextColor(Color.parseColor("#ff8b8b8b"));
//                } else {
//                    holder.titleTextView.setTextColor(Color.parseColor("#ff000000"));
//                }
                holder.commentNumberTextView.setText(holder.item.getView_count() + "浏览");
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
