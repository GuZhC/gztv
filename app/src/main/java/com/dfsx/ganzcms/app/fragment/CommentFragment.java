package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.openimage.OpenImageUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2017/1/17
 */
public class CommentFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView>, EditChangedLister.EditeTextStatuimpl, GestureDetector.OnGestureListener {
    private final String TAG = "HeadLineFragment";
    public static final int RESULT_OK = -1;
    private PullToRefreshListView pullListview;
    private ListView list;
    private int mScreenWidth;
    private long mCLoumnType = -1;
    private ReplayAdapter adapter;
    private ContentCmsApi mContentCmsApi = null;
    int offset = 1;
    PopupWindow mMorePopupWindow;
    EditText mReplyContent;
    private IsLoginCheck mloginCheck;
    NewsDatailHelper newsHelper;


    public static CommentFragment newInstance(long type) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", type);
        CommentFragment fragment = new CommentFragment();
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
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCLoumnType = bundle.getLong("type");
        }
        newsHelper = new NewsDatailHelper(getActivity());
        mloginCheck = newsHelper.getMloginCheck();
        mContentCmsApi = newsHelper.getmContentCmsApi();
        View view = inflater.inflate(R.layout.news, container, false);
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        list = ((ListView) pullListview.getRefreshableView());
        adapter = new ReplayAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ListViewAdapter.ViewHolder vmHolder = (ListViewAdapter.ViewHolder) view.getTag();
//                if (getVideoPlyer() != null && getVideoPlyer().getTag() != null) {
//                    View child = (View) getVideoPlyer().getTag();
//                    String ip = child.getParent().getParent().getParent().getClass().getName();
//                    if (view == child.getParent().getParent().getParent().getParent()) {
//                        if (getVideoPlyer().isPlay())
//                            return;
//                    }
//                }
//                goDetail(vmHolder.item, vmHolder.pos);
            }
        });
        return view;
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
        if (adapter != null && !adapter.isInit()) {
            getData(offset);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset = 1;
        getData(offset);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset++;
        getData(offset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private DataFileCacheManager<ArrayList<CommendCmsEntry>> dataFileCacheManager = new
            DataFileCacheManager<ArrayList<CommendCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            mCLoumnType + "", App.getInstance().getPackageName() + "commntfr.txt") {
                @Override
                public ArrayList<CommendCmsEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<CommendCmsEntry> dlist = null;
                    Map<Long, CommendCmsEntry> lists = null;
                    try {
                        JSONArray result = jsonObject.optJSONArray("data");
                        if (result != null) {
                            lists = new HashMap<Long, CommendCmsEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                CommendCmsEntry entry = new Gson().fromJson(item.toString(), CommendCmsEntry.class);
                                if (entry.getRef_comments() != null && !entry.getRef_comments().isEmpty()) {
                                    for (CommendCmsEntry.RefCommentsBean bean : entry.getRef_comments()) {
                                        long id = bean.getId();
                                        CommendCmsEntry rp = lists.get(id);
                                        if (rp != null) {
                                            CommendCmsEntry.RefCommentsBean bs = new CommendCmsEntry.RefCommentsBean();
                                            bs.setAuthor_name(entry.getAuthor_name());
                                            bs.setText(entry.getText());
                                            List<CommendCmsEntry.RefCommentsBean> rpss = rp.getRef_comments();
                                            if (rpss == null) rpss = new ArrayList<CommendCmsEntry.RefCommentsBean>();
                                            rpss.add(bs);
                                            rp.setRef_comments(rpss);
                                        }
                                    }
                                } else
                                    lists.put(entry.getId(), entry);
                            }
                            if (lists != null && !lists.isEmpty()) {
                                dlist = new ArrayList<CommendCmsEntry>();
                                for (Long obj : lists.keySet()) {
                                    CommendCmsEntry value = lists.get(obj);
                                    dlist.add(value);
                                }
                                lists.clear();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
                }
            };


    private DataRequest.DataCallback<ArrayList<CommendCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<CommendCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<CommendCmsEntry> data) {
            if (pullListview != null)
                pullListview.onRefreshComplete();
            if (data != null && !data.isEmpty()) {
                adapter.update(data, isAppend);
            }
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            if (pullListview != null)
                pullListview.onRefreshComplete();
        }
    };

    private void getData(int offset) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/contents/" + mCLoumnType + "/root-comments?";
        url += "page=" + offset + "&sub_comment_count=3";
        dataFileCacheManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

    /***
     public class ReplayAdapter extends BaseAdapter {
     private ArrayList<CommendCmsEntry> list;

     public boolean isInit() {
     return init;
     }

     public ArrayList<CommendCmsEntry> getList() {
     return list;
     }

     private boolean init = false;

     public ReplayAdapter() {
     }

     public ReplayAdapter(ArrayList<CommendCmsEntry> list) {
     this.list = list;
     }

     public void update(ArrayList<CommendCmsEntry> data, boolean isAdd) {
     if (list == null || !isAdd) {
     list = data;
     } else {
     list.addAll(data);
     }
     init = true;
     notifyDataSetChanged();
     }

     public ArrayList<CommendCmsEntry> getData() {
     return list;
     }

     @Override public int getCount() {
     return list == null ? 0 : list.size();
     }

     @Override public Object getItem(int position) {
     return list.get(position);
     }

     @Override public long getItemId(int position) {
     return position;
     }

     @Override public View getView(int position, View convertView, ViewGroup parent) {
     BaseViewHodler hodler = BaseViewHodler.get(convertView, parent,
     R.layout.community_replay_item, position);

     setViewData(hodler, position);
     return hodler.getConvertView();
     }

     public void setViewData(BaseViewHodler hodler, int position) {
     CircleButton logo = hodler.getView(R.id.replay_user_logo);
     TextView nameText = hodler.getView(R.id.replay_user_name);
     TextView titleText = hodler.getView(R.id.replay_title_value);
     final TextView timeText = hodler.getView(R.id.disclosure_list_time);
     TextView numberText = hodler.getView(R.id.replay_count_text);
     ImageView thumb = hodler.getView(R.id.replay_thumb);
     ImageButton replypub = hodler.getView(R.id.disclosure_replay_btn);
     replypub.setOnClickListener(new View.OnClickListener() {
     @Override public void onClick(View view) {
     long tId = (long) view.getTag(R.id.tag_replay_cid);
     showMore(view, tId);
     }
     });
     LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
     //            setCountText(itemCountText, position);
     CommendCmsEntry info = list.get(position);
     GlideImgManager.getInstance().showImg(getActivity(), logo, info.getAuthor_avatar_url());
     nameText.setText(info.getAuthor_name());
     titleText.setText(info.getText());
     replypub.setTag(R.id.tag_replay_cid, info.getId());
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//制定日期的显示格式
     String time = sdf.format(new Date(info.getCreation_time() * 1000));
     timeText.setText(time);
     //            if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
     //                thumb.setVisibility(View.VISIBLE);
     //                String path = info.getMthumImage();
     //                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
     //                Util.LoadThumebImage(thumb, info.getMthumImage(), null);
     //                thumb.setTag(R.id.tag_replay_thumb, info.getMthumImage());
     //            }
     thumb.setOnClickListener(new View.OnClickListener() {
     @Override public void onClick(View view) {
     String path = (String) view.getTag(R.id.tag_replay_thumb);
     if (path != null && !TextUtils.isEmpty(path)) {
     OpenImageUtils.openImage(view.getContext(), path, 0);
     }
     }
     });
     List<CommendCmsEntry.RefCommentsBean> reppls = info.getRef_comments();
     if (reppls != null && !reppls.isEmpty()) {
     if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
     extendlay.setVisibility(View.VISIBLE);
     for (CommendCmsEntry.RefCommentsBean bean : reppls) {
     View view = createChildReplay(bean.getAuthor_name(), info.getAuthor_name(), bean.getText());
     extendlay.addView(view);
     }
     }
     //            numberText.setText(info.getre + "回复");
     }
     }   **/


    public class ReplayAdapter extends BaseAdapter {
        private ArrayList<CommendCmsEntry> list;

        public boolean isInit() {
            return init;
        }

        private boolean init = false;

        public ReplayAdapter() {
        }

        public void update(ArrayList<CommendCmsEntry> data, boolean isAdd) {
            if (list == null || !isAdd) {
                if (data != null && !data.isEmpty())
                    Collections.reverse(data);
//                Collections.sort(data,new Comparator(){
//                    public int compare(CommendCmsEntry arg0, CommendCmsEntry arg1) {
//                        int  n1=(int)arg1.getCreation_time();
//                        return n1.compareTo((int)arg0.getCreation_time());
//                    }
//                });
                list = data;
            } else {
                list.addAll(data);
            }
            init = true;
            notifyDataSetChanged();
        }

        public ArrayList<CommendCmsEntry> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            BaseViewHodler hodler = BaseViewHodler.get(convertView, parent,
//                    R.layout.community_replay_item, position);
            BaseViewHodler hodler = BaseViewHodler.get(convertView, parent,
                    R.layout.nc_commend_replay_item, position);
            setViewData(hodler, position);
            return hodler.getConvertView();
        }

        public void setViewData(BaseViewHodler hodler, int position) {
            View bodyView = hodler.getView(R.id.replay_item_hor);
            CircleButton logo = hodler.getView(R.id.replay_user_logo);
            TextView nameText = hodler.getView(R.id.replay_user_name);
            TextView titleText = hodler.getView(R.id.replay_title_value);
            TextView timeText = hodler.getView(R.id.replay_time_value);
            TextView numberText = hodler.getView(R.id.replay_count_text);
            TextView praiseTv = hodler.getView(R.id.comemndg_praise_txt);
            View praiseBtn = hodler.getView(R.id.praise_container);
            praiseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = (long) v.getTag(R.id.tag_replay_cid);
                    newsHelper.praiseforCmsCommend( v, id, new DataRequest.DataCallbackTag() {
                        @Override
                        public void onSuccess(Object object, boolean isAppend, Object data) {
                            if (object != null) {
                                if (object instanceof RelativeLayout) {
                                    View parent = (View) ((RelativeLayout) object).getParent();
                                    TextView txt = (TextView) parent.findViewById(R.id.comemndg_praise_txt);
                                    if (txt != null) {
                                        long num = 0;
                                        String numtxt = txt.getText().toString().trim();
                                        if (numtxt != null && !TextUtils.isEmpty(numtxt)) {
                                            num = Long.parseLong(numtxt);
                                            num++;
                                        }
                                        txt.setText(num + "");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onSuccess(boolean isAppend, Object data) {

                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            ToastUtils.toastApiexceFunction(getActivity(),e);
                        }
                    });
                }
            });
            ImageView thumb = hodler.getView(R.id.replay_thumb);
            ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
            replypub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long tId = (long) view.getTag(R.id.tag_replay_cid);
                    showMore(view, tId);
                }
            });
            LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
            CommendCmsEntry info = list.get(position);
//            GlideImgManager.getInstance().showImg(context, logo, info.getAuthor_avatar_url());
            Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);

            praiseTv.setText(info.getLike_count() + "");
            nameText.setText(info.getAuthor_nickname());
            titleText.setText(info.getText());
            replypub.setTag(R.id.tag_replay_cid, info.getId());
            praiseBtn.setTag(R.id.tag_replay_cid, info.getId());
            timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));

            bodyView.setTag(R.id.tag_replay_item_cid, info);
            bodyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommendCmsEntry entry = (CommendCmsEntry) v.getTag(R.id.tag_replay_item_cid);
                    if (entry.getSub_comment_count() == 0) {
                        ToastUtils.toastNoCommendFunction(v.getContext());
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putLong("itemId", mCLoumnType);
                    bundle.putLong("subId", entry.getId());
                    bundle.putLong("praiseNumer", entry.getLike_count());
                    DefaultFragmentActivity.start(getActivity(), CommendPageFragment.class.getName(), bundle);
                }
            });

            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = (String) view.getTag(R.id.tag_replay_thumb);
                    if (path != null && !TextUtils.isEmpty(path)) {
                        OpenImageUtils.openImage(view.getContext(), path, 0);
                    }
                }
            });
            List<CommendCmsEntry.SubCommentsBean> reppls = info.getmSubCommendList();
            long nSubCommendNum = info.getSub_comment_count();
//            if (reppls != null && !reppls.isEmpty()) {
//                if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
//                extendlay.setVisibility(View.VISIBLE);
//                int i = 0;
//                int count = reppls.size() >= 3 ? 3 : reppls.size();
//                do {
//                    CommendCmsEntry.SubCommentsBean bean = reppls.get(i);
//                    View view = createSubReplay(bean.getAuthor_nickname(), bean.getText());
//                    extendlay.addView(view);
//                    i++;
//                } while (i < count);
//            } else {
//                extendlay.removeAllViews();
//                extendlay.setVisibility(View.GONE);
//            }
            if (nSubCommendNum > 0) {
                numberText.setVisibility(View.VISIBLE);
                numberText.setText(nSubCommendNum + "回复");
//                numberText.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                numberText.setVisibility(View.GONE);
                numberText.setText("回复");
//                numberText.setBackground(null);
            }
        }
    }


    public View createSubReplay(String replyNickName, String replyContentStr) {
        LinearLayout view = new LinearLayout(getActivity());
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView txt = new TextView(getActivity());
        txt.setTextSize(12);
        txt.setMaxLines(2);
        txt.setEllipsize(TextUtils.TruncateAt.END);
        SpannableString ss = null;
        if (replyNickName != null && !TextUtils.isEmpty(replyNickName)) {
            ss = new SpannableString(replyNickName + ":" + replyContentStr);
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt.setText(ss);
        view.addView(txt);
        return view;
    }

    public View createChildReplay(String replyNickName, String commentNickName, String replyContentStr) {
        LinearLayout view = new LinearLayout(getActivity());
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView txt = new TextView(getActivity());
        txt.setTextSize(12);
        txt.setMaxLines(2);
        txt.setEllipsize(TextUtils.TruncateAt.END);
        SpannableString ss = null;
        commentNickName = "";
        if (replyNickName != null && !TextUtils.isEmpty(commentNickName)) {
            ss = new SpannableString(replyNickName + "回复" + commentNickName + ":" + replyContentStr);
        } else {
//            ss = new SpannableString(replyNickName + "评论" + commentNickName + ":" + replyContentStr);
            ss = new SpannableString(replyNickName + ":" + replyContentStr);
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(ss);
        view.addView(txt);
        return view;
    }

    ImageButton mBtnSend;

    private void showMore(View moreBtnView, long resf_cid) {
        if (mMorePopupWindow == null) {
            LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.layout_more, null, false);
            mMorePopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setFocusable(true);
//            mMorePopupWindow.setAnimationStyle(R.style.LiveVideoPopupStyle);
            mMorePopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mMorePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //     mMorePopupWindow.setOutsideTouchable(true);
            //      mMorePopupWindow.setTouchable(true);
            //    content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //     mShowMorePopupWindowWidth = UtilHelp.getScreenWidth(this);
            //      mShowMorePopupWindowHeight = content.getMeasuredHeight();
            View parent = mMorePopupWindow.getContentView();
            mReplyContent = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mBtnSend = (ImageButton) parent.findViewById(R.id.commentButton);
            mReplyContent.addTextChangedListener(new EditChangedLister(this));
//            final TextView comment = (TextView) parent.findViewById(R.id.commentButton);
            mBtnSend.setTag(resf_cid);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mloginCheck.checkLogin()) return;
                    String contnt = mReplyContent.getText().toString();
                    long refId = -1;
                    if (view.getTag() != null) refId = (long) view.getTag();
                    mContentCmsApi.createCmsCommend(mCLoumnType, refId, contnt, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            ToastUtils.toastMsgFunction(getActivity(), "发表评论成功");
                            mMorePopupWindow.dismiss();
                            onFocusChange(false, mReplyContent);
                            mReplyContent.setText("");
                            getData(1);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            ToastUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            mBtnSend.setTag(resf_cid);
            mMorePopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
            onFocusChange(true, mReplyContent);
        }
    }

    private void onFocusChange(boolean hasFocus, final EditText edt) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                if (isFocus) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
            }
        }, 100);
    }

}
