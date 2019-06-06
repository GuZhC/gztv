package com.dfsx.ganzcms.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.act.CommunityAct;
import com.dfsx.ganzcms.app.act.CommunityNewAct;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.Richtext;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.selectedmedia.MyVideoThumbLoader;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heyang on 2015/12/5.
 */
public class MyEnshrineFragment extends CommunityLocalFragment implements IButtonClickListenr, PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final int TYPE_NEWS = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_LIVE = 3;
    private static final int TYPE_GROUP = 4;
    private static final int TYPE_AD = 7;
    private static final int TYPE_CIRCLE_IMG = 5;
    private static final int TYPE_CIRCLE_NO_IMG = 6;
    private static final int TYPE_COUNT = 11;
    private static final int TYPE_ACTIVIRY = 8;
    public static final int RESULT_OK = -1;
    private PullToRefreshListView pullListview;
    private ListView list;
    private ImageView mLoadRetryBtn;
    private EnshrineListManager dataRequester;
    private ListViewAdapter adapter;
    //定时任务
    private RelativeLayout mRelativeLayoutFail;
    MyVideoThumbLoader myVideoThumbLoader;
    private int offset = 0;
    private TopicalApi mTopicalApi = null;
    ContentCmsApi mContentCmsApi = null;
    NewsDatailHelper _newsHelper;
    private Subscription favritySubscription;
    boolean isRersh = false;
    private CommuntyDatailHelper _comnityHelper;
    private IButtonClickListenr _item_back;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public static MyEnshrineFragment newInstance(long type) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", type);
        MyEnshrineFragment fragment = new MyEnshrineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        DisplayMetrics dm = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_enrahcer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) getActivity()).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clearAllItem();
                        }
                    });
        }
        dataRequester = new EnshrineListManager(getActivity(), 256 + "", 9);
        dataRequester.setCallback(
                new DataRequest.DataCallback<ArrayList<CollectEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, ArrayList<CollectEntry> topicalies) {
                        if (pullListview != null)
                            pullListview.onRefreshComplete();
                        if (!isAppend && (topicalies == null || topicalies.isEmpty())) {
                            LoadFailLayout(true);
                            adapter.update(topicalies, isAppend);
                            return;
                        }
                        //  批量获取cms
//                        long a[] = new long[];
//                        int len = 0;
//                        String pl = "";
//                        for (CollectEntry coll : topicalies) {
//                            if (TextUtils.equals(coll.getItem_source(), "cms")) {
//                                len++;
//                                pl += coll.getItem_id() + ",";
//                            }
//                        }
//
//                        Observable.just(pl)
//                                .subscribeOn(Schedulers.io())
//                                .map(new Func1<String, List<ContentCmsEntry>>() {
//                                    @Override
//                                    public List<ContentCmsEntry> call(String params) {
//                                        return mContentCmsApi.getMulitiEntry(params);
//                                    }
//                                })
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Observer<List<ContentCmsEntry>>() {
//                                    @Override
//                                    public void onCompleted() {
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    @Override
//                                    public void onNext(List<ContentCmsEntry> data) {
//                                        if (adapter != null && !data.isEmpty()) {
//                                            LoadFailLayout(false);
//
//
//                                        }
//                                    }
//                                });


                        Observable.from(topicalies)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<CollectEntry, CollectEntry>() {
                                    @Override
                                    public CollectEntry call(CollectEntry topicalEntry) {
                                        if (topicalEntry == null) {
                                            return null;
                                        }
                                        List<Attachment> list = new ArrayList<Attachment>();
                                        String item_source = topicalEntry.getItem_source();
                                        if (TextUtils.equals(item_source, "community")) {
                                            topicalEntry.setId(topicalEntry.getItem_id());
                                            TopicalEntry entry = mTopicalApi.getTopicTopicalInfo(topicalEntry);
//                                            boolean isFavl = mTopicalApi.isFav(topicalEntry.getItem_id());
                                            if (entry != null) {
                                                getTopicInf(topicalEntry, entry);
                                                if (App.getInstance().getUser() != null) {
                                                    if (topicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                                                        int res = mTopicalApi.isAttentionOther(topicalEntry.getAuthor_id());
                                                        topicalEntry.setRelationRole(res);
                                                    } else
                                                        topicalEntry.setRelationRole(-1);
                                                } else
                                                    topicalEntry.setRelationRole(-1);
//                                            topicalEntry.setIsFavl(isFavl);
                                                topicalEntry.setShowType(10);
                                            }
                                        } else if (TextUtils.equals(item_source, "cms")) {
//                                            ContentCmsInfoEntry info = null;
                                            List<ContentCmsEntry> lists = mContentCmsApi.getMulitiEntry(topicalEntry.getItem_id());
//                                            info = mContentCmsApi.getEnteyFromJson(topicalEntry.getItem_id());
//                                            info = mContentCmsApi.getEnteyFromJson(topicalEntry.getItem_id());
                                            if (lists != null && lists.size() > 0) {
                                                getContentInf(topicalEntry, lists.get(0));
//                                                if (topicalEntry.getType() == 1) {
//                                                    if (topicalEntry.getThumbnail_urls().size() >= 3)
//                                                        topicalEntry.setShowType(4);
//                                                    if (topicalEntry.getThumbnail_urls() == null || topicalEntry.getThumbnail_urls().size() == 0)
//                                                        topicalEntry.setShowType(7);
//                                                }
                                            }
                                        } else if (TextUtils.equals(item_source, "live")) {
                                            List<ContentCmsEntry> lists = mContentCmsApi.getMulitiEntry(topicalEntry.getItem_id());
                                            if (lists != null && lists.size() > 0) {
                                                getContentInf(topicalEntry, lists.get(0));
//                                                topicalEntry.setShowType(3);
                                            }
                                        }
                                        return topicalEntry;
                                    }
                                })
                                .toList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<List<CollectEntry>>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(List<CollectEntry> data) {
                                        if (adapter != null && !data.isEmpty()) {
                                            LoadFailLayout(false);
                                            adapter.update((ArrayList<CollectEntry>) data, isAppend);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (pullListview != null)
                            pullListview.onRefreshComplete();
                    }
                });

        mRelativeLayoutFail = (RelativeLayout) view.findViewById(R.id.load_fail_layout);
        mLoadRetryBtn = (ImageButton) mRelativeLayoutFail.findViewById(R.id.reload_btn);
        mLoadRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataRequester.start(false, 1);
            }
        });
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        list = ((ListView) pullListview.getRefreshableView());
        adapter = new ListViewAdapter(this.getActivity());
        list.setAdapter(adapter);
        _newsHelper = new NewsDatailHelper(getContext());
        _comnityHelper = new CommuntyDatailHelper(getContext());
        mTopicalApi = _comnityHelper.getmTopicalApi();
        mContentCmsApi = _newsHelper.getmContentCmsApi();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewHolder vmHolder = (ViewHolder) view.getTag();
                CollectEntry entry = vmHolder.item;
                if (entry != null) {
                    gotoCommunAct(entry);
                }
            }
        });
        myVideoThumbLoader = new MyVideoThumbLoader();
        initRegister();
        _item_back = this;
    }

    @Override
    public void onLbtClick(int tyep, IButtonClickData data) {
        IButtonClickData<CollectEntry> mydata = data;
        CollectEntry entry = mydata.getObject();
        TextView anmal = (TextView) mydata.getTag();
        TopicalEntry tag = new TopicalEntry();
        tag.setId(entry.getId());
        tag.setAuthor_id(entry.getAuthor_id());
        tag.setRelationRole(entry.getRelationRole());
        tag.setAttachmentss(entry.getAttachmentInfos());
        tag.setTitle(entry.getTitle());
        IButtonClickData<TopicalEntry> newData = new IButtonClickData<TopicalEntry>(anmal, tag);
        super.onLbtClick(tyep, newData);
    }

    private void initRegister() {
        if (favritySubscription == null) {
            favritySubscription = RxBus.getInstance().
                    toObserverable(Intent.class).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Intent>() {
                        @Override
                        public void call(Intent intent) {
                            if (intent.getAction().equals(IntentUtil.UPDATE_FAVIRITY_MSG)) {
//                                isRersh = true;
//                                dataRequester.start(false, 1);
                            }
                        }
                    });
        }
    }

    public void LoadFailLayout(boolean isFail) {
        if (isFail) {
            if (list.isActivated()) list.setVisibility(View.GONE);
            mRelativeLayoutFail.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayoutFail.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (favritySubscription != null) {
            favritySubscription.unsubscribe();
        }
    }

    public void gotoCommunAct(CollectEntry item) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("content", item.getContent());
        bundle.putLong("tid", item.getItem_id());
        bundle.putInt("attion", item.getRelationRole());
        bundle.putLong("aver_id", item.getAuthor_id());
        if (item.getAttachmentInfos() != null && !item.getAttachmentInfos().isEmpty())
            bundle.putString("imagpath", item.getAttachmentInfos().get(0).getUrl());
        bundle.putString("title", item.getTitle());
        if (item.getAttachmentInfos() != null) {
            if (!item.getAttachmentInfos().isEmpty()) {
                String imgss = "";
                for (int k = 0; k < item.getAttachmentInfos().size(); k++) {
                    if (item.getAttachmentInfos().get(k).getType() == 1) {
                        imgss += item.getAttachmentInfos().get(k).getUrl();
                        if (k < item.getAttachmentInfos().size() - 1)
                            imgss += ',';
                    } else {
                        bundle.putString("videoUrl", item.getAttachmentInfos().get(k).getUrl());
                    }
                }
                bundle.putString("imgs", imgss);
            }
        }
        if (TextUtils.equals(item.getItem_source(), "community")) {
            mTopicalApi.addViewCount(item.getId());
            intent.setClass(getActivity(), CommunityNewAct.class);
        } else if (TextUtils.equals(item.getItem_source(), "cms")) {
            String url = item.getUrl();
            if (item.getModeType() == 6) {
                if (url == null || TextUtils.isEmpty(url)) {
                    url = item.getItem_source();
                }
            }
            _newsHelper.preDetail(item.getItem_id(), item.getModeType(), item.getTitle(), item.getThumbImg(), item.getCommendNumber(),
                    item.getUrl());
            return;
        } else if (TextUtils.equals(item.getItem_source(), "live")) {

        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void clearAllItem() {
        AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("是否全部删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                adapter.deleteAllItem();
                dataRequester.deleteCacheData();
                LoadFailLayout(true);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            }
        }).create();
        adig.show();
    }

    public void getTopicInf(CollectEntry coll, TopicalEntry entry) {
        coll.setTitle(entry.getTitle());
        coll.setAuthor_name(entry.getAuthor_nickname());
        coll.setAuthor_nickname(entry.getAuthor_nickname());
        coll.setAuthor_id(entry.getAuthor_id());
        coll.setAuthor_avatar_url(entry.getAuthor_avatar_url());
        coll.setColumn_name(entry.getColumn_name());
        coll.setUser_level_id(entry.getUser_level_id());
        coll.setReply_count(entry.getReply_count());
        coll.setView_count(entry.getView_count());
        coll.setContent(entry.getContent());
        coll.setType(entry.getType());
        if (App.getInstance().getUser() != null) {
            if (entry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                int res = mTopicalApi.isAttentionOther(entry.getAuthor_id());
                coll.setRelationRole(res);
            } else
                coll.setRelationRole(-1);
        } else
            coll.setRelationRole(-1);
        String respone = mTopicalApi.getSyncAtthmentById(entry.getAttachments());
        if (!TextUtils.isEmpty(respone.toString().trim())) {
            JSONObject json = null;
            try {
                json = JsonCreater.jsonParseString(respone);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            JSONArray arr = json.optJSONArray("result");
            if (arr != null && arr.length() > 0) {
                List<Attachment> list = new ArrayList<Attachment>();
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject obj = (JSONObject) arr.get(i);
                        Attachment cp = gson.fromJson(obj.toString(),
                                Attachment.class);
                        list.add(cp);
                        coll.setAttachmentss(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else
                coll.setType(10);
        }
    }

    public void getContentInf(CollectEntry coll, ContentCmsEntry entry) {
        coll.setTitle(entry.getTitle());
        coll.setAuthor_name(entry.getAuthor_nickname());
//        coll.setAuthor_avatar_url(entry.getAuthor_avatar_url());
        coll.setColumn_name(entry.getColumn_name());
        coll.setView_count(entry.getView_count());
        coll.setContent(entry.getTitle());
        coll.setCreation_time(entry.getCreation_time());
        coll.setType(entry.getShowType());
        coll.setId(entry.getId());
        coll.setUrl(entry.getUrl());
//        coll.setThumbImg(entry.getVideoThumb());
        coll.setThumbnail_urls(entry.getThumbnail_urls());
        coll.setReply_count(entry.getComment_count());
        coll.setShowType(entry.getShowType());
        coll.setModeType(entry.getModeType());
        coll.setCommendNumber(entry.getComment_count());
        coll.setThumbnail_urls(entry.getThumbnail_urls());
//        if (entry.getGroupimgs() != null)
//            coll.setGroupimgs(entry.getGroupimgs());
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
        if (isRersh) {
            isRersh = false;
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

    public class ListViewAdapter extends BaseAdapter {
        private final String STATE_LIST = "ListAddddapter.mlidst";
        private ArrayList<CollectEntry> items = new ArrayList<CollectEntry>();
        private LayoutInflater inflater;
        public boolean bInit;
        Context mContext = null;

        public ListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            bInit = false;
            this.mContext = context;
        }

        public void SetInitStatus(boolean flag) {
            bInit = flag;
        }

        public boolean isInited() {
            return bInit;
        }

        public void update(ArrayList<CollectEntry> data, boolean bAddTail) {
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
                        noData = false;
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
            } else {
                items.clear();
                notifyDataSetChanged();
            }
        }

        public void deleteAllItem() {
            if (items != null && !items.isEmpty()) {
                for (CollectEntry entry : items) {
                    if (TextUtils.equals(entry.getItem_source(), "community")) {
                        mTopicalApi.farityToptic(entry.getItem_id(), false, null);
                    } else if (TextUtils.equals(entry.getItem_source(), "live")) {

                    }
                }
                items.clear();
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
            int type = items.get(position).getShowType();
            if (type == 1) {
                type = TYPE_NEWS;
            } else if (type == 2) {
                type = TYPE_VIDEO;
            } else if (type == 3 || type == 9) {
                type = TYPE_LIVE;
            } else if (type == 5) {
                type = TYPE_ACTIVIRY;
            } else if (type == 4 || type == 8) {
                type = TYPE_GROUP;
            } else if (type == 6 || type == 7) {
                type = TYPE_AD;
            } else
                type = TYPE_CIRCLE_IMG;
// else if (type == 1) {
//                type = TYPE_VIDEO;
//            } else if (type == 4) {
//                type = TYPE_GROUP;
//            } else if (type == 3) {
//                type = TYPE_LIVE;
//            } else if (type == 7) {
//                type = TYPE_AD;
//            } else if (type == 5) {
//                type = TYPE_CIRCLE_IMG;
//            } else if (type == 6) {
//                type = TYPE_CIRCLE_NO_IMG;
//            }
            return type;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }


        public class Multphotoholder extends ViewHolder {
            public LinearLayout relayoutArea;
            public TextView groupConttxt;
        }

        public class LiveHolder extends ViewHolder {
            public CircleButton userImage;
            public TextView username;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View newsView = null;
            View videoView = null;
            View actvdView = null;
            View multPic = null;
            View cireImg = null;
            View cirleTxt = null;
            int currentType = getItemViewType(position);
            if (currentType == TYPE_NEWS) {
                ViewHolder viewHolder = null;
                if (view == null) {
                    newsView = inflater.inflate(R.layout.news_news_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.news_list_item_title);
                    viewHolder.thumbnailImageView = (ImageView) newsView.findViewById(R.id.news_news_list_item_image);
                    viewHolder.contentTextView = (TextView) newsView.findViewById(R.id.news_list_item_content);
                    viewHolder.ctimeTextView = (TextView) newsView.findViewById(R.id.news_list_item_time);
                    viewHolder.mark = (ImageView) newsView.findViewById(R.id.play_mark);
                    viewHolder.commentNumberTextView = (TextView) newsView.findViewById(R.id.news_list_item_command_tx);
                    viewHolder.item = items.get(position);
                    newsView.setTag(viewHolder);
                    view = newsView;
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.item = items.get(position);
                viewHolder.pos = position;
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getCreation_time() * 1000));
                viewHolder.titleTextView.setText(viewHolder.item.getTitle());
                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "浏览");
//                if (TextUtils.equals(viewHolder.item.getItem_type(), "video")) {
                if (viewHolder.item.getModeType()==1) {
                    viewHolder.mark.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mark.setVisibility(View.GONE);
                }
                if (viewHolder.item.getThumbnail_urls() != null && viewHolder.item.getThumbnail_urls().size() > 0) {
                    viewHolder.thumbnailImageView.setVisibility(View.VISIBLE);
                    String imageUrl = viewHolder.item.getThumbnail_urls().get(0).toString();
                    Util.LoadThumebImage(viewHolder.thumbnailImageView, imageUrl, null);
                } else {
                    viewHolder.thumbnailImageView.setVisibility(View.GONE);
                }
            } else if (currentType == TYPE_VIDEO) {
                ViewHolder holder = null;
                if (view == null) {
                    holder = new ViewHolder();
                    videoView = inflater.inflate(R.layout.news_video_list_hsrocll_item, null);
                    holder.thumbnailImageView = (ImageView) videoView.findViewById(R.id.item_img);
                    holder.titleTextView = (TextView) videoView.findViewById(R.id.news_list_item_title);
                    holder.contentTextView = (TextView) videoView.findViewById(R.id.item_autor);
                    holder.ctimeTextView = (TextView) videoView.findViewById(R.id.item_create_time);
                    holder.commentNumberTextView = (TextView) videoView.findViewById(R.id.item_commeanuder_tx);
                    videoView.setTag(holder);
                    view = videoView;
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                holder.item = items.get(position);
                holder.pos = position;
                holder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", holder.item.getCreation_time() * 1000));
                holder.titleTextView.setText(holder.item.getTitle());
                holder.commentNumberTextView.setText(holder.item.getView_count() + "浏览");
                String thumb = holder.item.getThumbImg();
                if (thumb != null && !TextUtils.isEmpty(thumb)) {
//                String imageUrl = holder.item.getThumbnail_urls().get(0).toString();
//                Util.LoadImageErrorUrl(holder.thumbnailImageView, imageUrl, null, R.drawable.main_page_zhibo);
                    Util.LoadThumebImage(holder.thumbnailImageView, thumb, null);
                } else {
                    holder.thumbnailImageView.setImageResource(R.drawable.glide_default_image);
                }
            } else if (currentType == TYPE_GROUP) {
                Multphotoholder viewHolder = null;
                if (view == null) {
                    multPic = inflater.inflate(R.layout.news_item_multphotos, null);
                    viewHolder = new Multphotoholder();
                    viewHolder.titleTextView = (TextView) multPic.findViewById(R.id.news_list_item_title);
                    viewHolder.thumbnailImageView = (ImageView) multPic.findViewById(R.id.news_news_list_item_image);
                    viewHolder.contentTextView = (TextView) multPic.findViewById(R.id.news_list_item_content);
                    viewHolder.ctimeTextView = (TextView) multPic.findViewById(R.id.item_create_time);
                    viewHolder.commentNumberTextView = (TextView) multPic.findViewById(R.id.news_list_command_time);
                    viewHolder.relayoutArea = (LinearLayout) multPic.findViewById(R.id.news_list_iamgelayout);
                    int screen = UtilHelp.getScreenWidth(mContext);
                    //获取屏幕的宽度，每行3个Button，间隙为10共300，除4为每个控件的宽度
//                    int nItmeWidh = (screen - 60) / 3;
                    int nItmeWidh = Util.dp2px(getActivity(),112);
                    int nItemHeight=Util.dp2px(getActivity(),70);
                    LinearLayout mLayout = new LinearLayout(mContext);
//                    mLayout.setWeightSum(1.0f);
                    mLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
//                    params.leftMargin = ((int) (5.0F * dm.density));
//                    params.rightMargin = ((int) (5.0F * dm.density));
                    mLayout.setLayoutParams(params);
                    for (int i = 0; i < 3; i++) {
                        ImageView bg = new ImageView(mContext);
//                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(nItmeWidh, (int) (nItmeWidh * 3 / 4), 1.0f);
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(nItmeWidh,nItemHeight);
                        //控件距离其右侧控件的距离，此处为60
                        lp1.setMargins(0, 0, 10, 0);
                        bg.setLayoutParams(lp1);
                        bg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.glide_default_image));
                        bg.setScaleType(ImageView.ScaleType.FIT_XY);
//                    bg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.op));
//                        mLayout.setBackgroundColor(Color.parseColor("#D9DEDF"));
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
//                String timeStr = UtilHelp.getTimeString("yyy-MM-dd", viewHolder.item.getCreation_time());
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getCreation_time() * 1000));
                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "浏览");
                LinearLayout linp = (LinearLayout) viewHolder.relayoutArea.getChildAt(0);
                int ncount = viewHolder.item.getThumbnail_urls().size();
                String[] item = {"", "", ""};
                String pathls = "";
                if (viewHolder.item.getThumbnail_urls() != null
                        && viewHolder.item.getThumbnail_urls().size() > 0) {
                    ncount = viewHolder.item.getThumbnail_urls().size();
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
            } else if (currentType == TYPE_LIVE) {
                LiveHolder holder = null;
                if (view == null) {
                    actvdView = inflater.inflate(R.layout.news_live_list_item, null);
                    holder = new LiveHolder();
                    holder.userImage = (CircleButton) actvdView.findViewById(R.id.news_live_user_imge);
                    holder.username = (TextView) actvdView.findViewById(R.id.news_live_user_name);
                    holder.thumbnailImageView = (ImageView) actvdView.findViewById(R.id.item_img);
                    holder.titleTextView = (TextView) actvdView.findViewById(R.id.item_clumn_groups);
                    holder.commentNumberTextView = (TextView) actvdView.findViewById(R.id.news_live_viewcont_txt);
                    holder.item = items.get(position);
                    actvdView.setTag(holder);
                    view = actvdView;
                } else {
                    holder = (LiveHolder) view.getTag();
                }
                holder.item = items.get(position);
                holder.pos = position;
                Util.LoadThumebImage(holder.userImage, holder.item.getAuthor_avatar_url(), null);
                holder.username.setText(holder.item.getAuthor_nickname());
                holder.titleTextView.setText(holder.item.getTitle());
//            holder.titleTextView.setText("cdcscdscdcd");
                holder.commentNumberTextView.setText(holder.item.getView_count() + "浏览");
                if (holder.item.getThumbnail_urls() != null && holder.item.getThumbnail_urls().size() > 0) {
                    String imageUrl = holder.item.getThumbnail_urls().get(0).toString();
                    Util.LoadThumebImage(holder.thumbnailImageView, imageUrl, null);
                }
            } else if (currentType == TYPE_AD) {
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
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getCreation_time() * 1000));
                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "浏览");
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
                viewHolder.ctimeTextView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", viewHolder.item.getCreation_time() * 1000));
                viewHolder.commentNumberTextView.setText(viewHolder.item.getView_count() + "浏览");
            } else if (currentType == TYPE_CIRCLE_IMG) {
                view = createCircleImg(view, items, position);
            } else if (currentType == TYPE_CIRCLE_NO_IMG) {
//                view = createCirleText(view, items, position);
            }
            return view;
        }

        public boolean remove(int position) {
            if (position < items.size()) {
                items.remove(position);
                return true;
            }
            return false;
        }
    }

    public class ViewHolder {
        public CollectEntry item;
        public int pos;
        public TextView titleTextView;
        public ImageView thumbnailImageView;
        public TextView contentTextView;
        public TextView ctimeTextView;
        public TextView commentNumberTextView;
        public ImageView   mark;
    }

    class ImageViewHolder extends ViewHolder {
        public ImageView userImg;
        public TextView usename;
        public LinearLayout imgs;
        public ImageView realtRoleTx;
        public ImageView userLevel;
        public View bottomView;
    }

    class ComnityViewHolder extends ImageViewHolder {
        public Richtext descivbr;
        public TabGrouplayout tabGroup;
        public LinearLayout userGroup;
        public TextView vistNumberTx;
        View prisebtn;
        View commendBtn;
        View sharebtn;
        View favortyBtn;
        View deletefavBtn;
    }

    /**   **/
    public View createCircleImg(View view, ArrayList<CollectEntry> items, int position) {
        ComnityViewHolder viewHolder = null;
        View imgeView = null;
        if (view == null) {
            imgeView = getActivity().getLayoutInflater().inflate(R.layout.disclosure_item, null, false);
            viewHolder = new ComnityViewHolder();
            viewHolder.usename = (TextView) imgeView.findViewById(R.id.replay_user_name);
            viewHolder.userImg = (ImageView) imgeView.findViewById(R.id.head_img);
            viewHolder.userLevel = (ImageView) imgeView.findViewById(R.id.cmy_user_level);
            viewHolder.descivbr = (Richtext) imgeView.findViewById(R.id.disclosure_list_title);
            View   bottomLine=(View)imgeView.findViewById(R.id.bottom_line9);
            bottomLine.setVisibility(View.GONE);
            viewHolder.realtRoleTx = (ImageView) imgeView.findViewById(R.id.common_guanzhu_tx);
            viewHolder.ctimeTextView = (TextView) imgeView.findViewById(R.id.common_time);
            viewHolder.imgs = (LinearLayout) imgeView.findViewById(R.id.disclosure_list_iamgelayout);
            viewHolder.tabGroup = (TabGrouplayout) imgeView.findViewById(R.id.communt_img_taglay);
            viewHolder.userGroup = (LinearLayout) imgeView.findViewById(R.id.userGrouplay);
            viewHolder.prisebtn = (View) imgeView.findViewById(R.id.comnity_item_praise);
            viewHolder.commendBtn = (View) imgeView.findViewById(R.id.comnity_item_commend);
            viewHolder.sharebtn = (View) imgeView.findViewById(R.id.comnity_item_share);
            viewHolder.favortyBtn = (View) imgeView.findViewById(R.id.comnity_item_favority);
            viewHolder.deletefavBtn = (View) imgeView.findViewById(R.id.comnity_del_btn);
            viewHolder.prisebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent().getParent();
                    if (parent == null) return;
                    TopicalEntry item = (TopicalEntry) parent.getTag();
                    if (item == null) return;
                    TextView animal = (TextView) parent.findViewById(R.id.comnity_item_praise_animal);
                    _item_back.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(animal, item));
                }
            });
            viewHolder.commendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent();
                    if (parent == null) return;
                    ;
                    TopicalEntry item = (TopicalEntry) parent.getTag();
                    _item_back.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData(null, item));
                }
            });
            viewHolder.sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent();
                    if (parent == null) return;
                    ;
                    TopicalEntry item = (TopicalEntry) parent.getTag();
                    if (item == null) return;
                    _item_back.onLbtClick(IButtonClickType.SHARE_CLICK, new IButtonClickData(null, item));
                }
            });
            viewHolder.favortyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent();
                    if (parent == null) return;
                    ;
                    TopicalEntry item = (TopicalEntry) parent.getTag();
                    if (item == null) return;
                    _item_back.onLbtClick(IButtonClickType.FARVITY_CLICK, new IButtonClickData(null, item));
                }
            });
            viewHolder.deletefavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent();
                    if (parent == null) return;
                    ;
                    TopicalEntry item = (TopicalEntry) parent.getTag();
                    if (item == null) return;
                    _item_back.onLbtClick(IButtonClickType.DEL_CLICK, new IButtonClickData(null, item));
                }
            });
            viewHolder.vistNumberTx = (TextView) imgeView.findViewById(R.id.community_vist_number);
            viewHolder.vistNumberTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long id = (long) view.getTag(R.id.cirbutton_user_id);
                    WhiteTopBarActivity.startAct(getActivity(), VisitRcordFragment.class.getName(), "到访记录", "", id);
                }
            });
            viewHolder.bottomView = (View) imgeView.findViewById(R.id.comunity_botom_view);
            viewHolder.item = items.get(position);
            imgeView.setTag(viewHolder);
            viewHolder.realtRoleTx.setTag(viewHolder);
            viewHolder.imgs.setTag(viewHolder);
        } else {
            imgeView = view;
            viewHolder = (ComnityViewHolder) view.getTag();
        }
        viewHolder.item = items.get(position);
        viewHolder.pos = position;
        UtilHelp.LoadImageErrorUrl(viewHolder.userImg, viewHolder.item.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
        viewHolder.userImg.setTag(R.id.cirbutton_user_id, viewHolder.item.getAuthor_id());
//        Util.LoadImageErrorUrl(viewHolder.userLevel, viewHolder.item.getUser_level_img(), null, R.drawable.grade_vip1);
        UserLevelManager.getInstance().showLevelImage(getContext(), viewHolder.item.getUser_level_id(), viewHolder.userLevel);
        viewHolder.usename.setText(viewHolder.item.getAuthor_nickname());
        viewHolder.bottomView.setVisibility(View.GONE);   // 隐藏底部关注 收藏 删除
        viewHolder.descivbr.setText(viewHolder.item.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm yyyy-MM-dd");//制定日期的显示格式
        String time = sdf.format(new Date(viewHolder.item.getCreation_time() * 1000));
        viewHolder.ctimeTextView.setText(time);
        int role = viewHolder.item.getRelationRole();
        setAttteonTextStatus(role, viewHolder.realtRoleTx);
        viewHolder.realtRoleTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                ImageViewHolder holder = (ImageViewHolder) view.getTag();
                setAttentionUser(holder.item.getAuthor_id(), holder.item.getRelationRole(), holder.realtRoleTx);
            }
        });
        int nCount = viewHolder.imgs.getChildCount();
        List<Attachment> dlist = viewHolder.item.getAttachmentInfos();
        int count = viewHolder.imgs.getChildCount();
        if (count == 0 && dlist != null && !dlist.isEmpty()) {
        } else
            viewHolder.imgs.removeAllViews();
        String urls = "";
        if (dlist != null && !dlist.isEmpty()) {
            for (int i = 0; i < dlist.size(); i++) {
                urls += dlist.get(i).getUrl();
                if (i < dlist.size() - 1)
                    urls += ",";
            }
            if (viewHolder.item.getType() == 1) {
                _comnityHelper.setMulitpImage(viewHolder.imgs, dlist, urls);
            } else {
                _comnityHelper.createVideoContainer(viewHolder.imgs, dlist.get(0));
            }
        }
        List<TopicalEntry.PraiseBean> visit = viewHolder.item.getPraiseBeanList();
        if (visit != null) {
            viewHolder.vistNumberTx.setTag(R.id.cirbutton_user_id, viewHolder.item.getId());
            viewHolder.vistNumberTx.setText(visit.size() + "");
        } else {
            View vparent = (View) viewHolder.vistNumberTx.getParent();
            vparent.setVisibility(View.GONE);
        }
//        viewHolder.bottomView.setTag(viewHolder.item);
        _comnityHelper.initTabGroupLayout(viewHolder.tabGroup, viewHolder.item.getTags());
        _comnityHelper.initUserGroupLayout(viewHolder.userGroup, visit);
        return imgeView;
    }

    public void setAttentionUser(final long uId, int role, final ImageView view) {
        mTopicalApi.attentionAuthor(uId, role, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
//                dataRequester.start(mClounType, false, offset);
                Observable.just((Boolean) data).
                        subscribeOn(Schedulers.io()).
                        observeOn(Schedulers.io()).
                        map(new Func1<Boolean, Integer>() {
                            @Override
                            public Integer call(Boolean aBoolean) {
//                                int res = mTopicalApi.isAttentionOther(uId);
                                return 0;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                                       @Override
                                       public void onCompleted() {
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           e.printStackTrace();
                                       }

                                       @Override
                                       public void onNext(Integer result) {
                                           setAttteonTextStatus(result, view);
                                           dataRequester.start(false, 1);
                                       }
                                   }
                        );
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    public void setAttteonTextStatus(int falg, ImageView view) {
        if (falg == -1) {
            view.setVisibility(View.GONE);
        } else if (falg == 1) {
            view.setVisibility(View.VISIBLE);
//            view.setBackground(null);
//            view.setTextColor(getResources().getColor(R.color.conmminty_time_color));
//            view.setText("√已关注");  // V    √
            view.setImageResource(R.drawable.commuity_att_selected);
        } else if (falg == 2) {
            view.setVisibility(View.VISIBLE);
            view.setBackground(null);
//            view.setText("相互关注");
        } else {
            view.setVisibility(View.VISIBLE);
//            view.setTextColor(getResources().getColor(R.color.ewd_descibr_font));
//            view.setBackground(getResources().getDrawable(R.drawable.commonirty_item_attent_bankground));
//            view.setText(
//                    Html.fromHtml("<b>+ </b>" + "关注"));
            view.setImageResource(R.drawable.commuity_att_normal);
        }
    }

}
