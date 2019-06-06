package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.*;

import com.dfsx.ganzcms.app.util.MessageIntents;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by heyang on 2016/10/11.
 */
public class RadioTablesFragment extends Fragment {
    private static final int BAR_TEXT_SIZE_SP = 17;
    private Context context;
    private ListView listView;
    private LiveEntity.LiveChannel playedChannel;
    private Subscription channelItemscription;
    private long colId = -1;
    ContentCmsApi mContentCmsApi = null;
    MyAdapter adapter;
    LinearLayout mWeeks;
    Map<String, String> mMapWeeks;
    Map<String, ArrayList<LiveEntity.LiveChannel>> findTvlist;
    int weekIndex = 0;
    ArrayList<String> ww;
    private final static int defaultIndex = 1;

    public static RadioTablesFragment newInstance(long type) {
        RadioTablesFragment fragment = new RadioTablesFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_tv_back_video, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            colId = getArguments().getLong("type");
        }
        context = getContext();
        mContentCmsApi = new ContentCmsApi(getActivity());
        initView(view);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        initAction();
        initData();
    }

    private void initView(View v) {
        mWeeks = (LinearLayout) v.findViewById(R.id.tv_week_bofy);
        listView = (ListView) v.findViewById(R.id.tv_list);
        initWeeksc();
    }

    public void initWeeksc() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        String dates = sDateFormat.format(ca.getTime());
        String ds = onDateSet(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        mMapWeeks = new HashMap<String, String>();
        ww = new ArrayList<String>();
        mMapWeeks.put("今天", dates);
        ww.add("今天");
        //        fillWeeks(ca, weekkoffset[weekIndex], 6 - weekIndex, ds);
        fillWeeks(ca);
        for (int i = 0; i < ww.size(); i++) {
            createWeekTextView(i, ww.get(i));
        }
        weekIndex = 0;
    }

    View.OnClickListener myWeekIteclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //            int pos = (Integer) view.getTag(R.id.tag_weekIndex_cid);
            int pos = (Integer) view.getTag();
            if (pos >= 0) {
                setSelectedWeekColor(pos);
                String key = ww.get(pos);
                String date = mMapWeeks.get(key);
                if (findTvlist != null && findTvlist.size() > 0) {
                    ArrayList<LiveEntity.LiveChannel> list = findTvlist.get(date);
                    adapter.update(list);
                    adapter.setSelectPostion(-1);
                } else
                    adapter.update(null);
                //                getData(false, date, false);
            }
        }
    };

    public void createWeekTextView(int pos, String title) {
        LinearLayout lay = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dp2px(getActivity(), 45));
        lay.setBackgroundColor(0xefefef);
        lay.setLayoutParams(lp1);
        TextView tx = new TextView(getActivity());
        tx.setGravity(Gravity.CENTER);
        tx.setTag(R.id.tag_weekIndex_cid, pos);
        tx.setId(R.id.scroll_item_week_id);
        //        tx.setOnClickListener(myWeekIteclick);
        if (pos == 0) title = "今天";
        tx.setText(title);
        tx.setTextSize(BAR_TEXT_SIZE_SP);
        tx.setTextColor(getResources().getColor(R.color.tv_name));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        lay.addView(tx, params);
        mWeeks.addView(lay);
        lay.setOnClickListener(myWeekIteclick);
        lay.setTag(pos);
    }


    public void fillWeeks(Calendar ca) {
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

    public void fillWeeks(Calendar ca, int pre, int back, String curwek) {
        String week = "";
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dates = "";
        Calendar tag = ca;
        int i = pre;
        do {
            if (i == pre) {
                tag.add(Calendar.DATE, -i);
            } else {
                tag.add(Calendar.DATE, +1);
            }
            i--;
            dates = sDateFormat.format(tag.getTime());
            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
            ww.add(week);
            mMapWeeks.put(week, dates);
            break;
        } while (i > 0);
        //        for (; i > 0; i--) {
        //            tag.add(Calendar.DATE, -i);
        //            dates = sDateFormat.format(tag.getTime());
        //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
        //            ww.add(week);
        //            mMapWeeks.put(week, dates);
        //        }
        tag.add(Calendar.DATE, +pre);
        i = back;
        int k = 6;
        do {
            if (i == back) {
                tag.add(Calendar.DATE, +i);
            } else {
                tag.add(Calendar.DATE, -1);
            }
            i--;
            k--;
            dates = sDateFormat.format(tag.getTime());
            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
            if (!TextUtils.equals(week, curwek)) {
                ww.add(week);
                mMapWeeks.put(week, dates);
            }
        } while (k > 0);
        //       for(;i>0; i--) {
        //            tag.add(Calendar.DATE, +1);
        //            dates = sDateFormat.format(tag.getTime());
        //            week = onDateSet(tag.get(Calendar.YEAR), tag.get(Calendar.MONTH), tag.get(Calendar.DAY_OF_MONTH));
        //            ww.add(week);
        //            mMapWeeks.put(week, dates);
        //        }
    }

    public String onDateSet(int year, int monthOfYear,
                            int dayOfMonth) {
        int y = year - 1;
        int m = monthOfYear + 1;
        int c = 20;
        int d = dayOfMonth + 12;
        int w = (y + (y / 4) + (c / 4) - 2 * c + (26 * (m + 1) / 10) + d - 1) % 7;
        String myWeek = null;
        switch (w) {
            case 0:
                myWeek = "周日";
                break;
            case 1:
                myWeek = "周一";
                break;
            case 2:
                myWeek = "周二";
                break;
            case 3:
                myWeek = "周三";
                break;
            case 4:
                myWeek = "周四";
                break;
            case 5:
                myWeek = "周五";
                break;
            case 6:
                myWeek = "周六";
                break;
            default:
                break;
        }
        weekIndex = w;
        return myWeek;
        //        myText.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日"+"周"+myWeek);
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

//    public void initData(LiveEntity.LiveChannel channel) {
//        setSelectedWeekColor(weekIndex);
//        if (channel != null) {
//            playedChannel = channel;
//            if (adapter != null) {
//                if (!adapter.isInit()) {
//                    //获取过去7天的所有节目单
//                    getData(false, getQuerytime(), true);
//                    //       getData(false, mMapWeeks.get("今天"), true);
//                }
//            }
//        }
//    }

    public void initData() {
        if (adapter != null) {
//            if (!adapter.isInit()) {
            //获取过去7天的所有节目单
            getData(false, getQuerytime(), true);
            //       getData(false, mMapWeeks.get("今天"), true);
//            }
        }
    }

    private void initAction() {
//        radioGroup.check(R.id.bar_today);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                position = position - listView.getHeaderViewsCount();
                try {
                    boolean isLeagle = position >= 0 && position < adapter.getCount();
                    if (isLeagle) {
                        LiveEntity.LiveChannel channel = adapter.getData().get(position);
                        if (channel != null) {
                            if (channel.url != null && !TextUtils.isEmpty(channel.url)) {
                                adapter.setSelectPostion(position);
                                playedChannel = channel;
                                RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_RADIO_BACK_SELECT, playedChannel));
//                                killPlayer();
//                                restartPlayer();
                            } else {
                                Toast.makeText(getActivity(), "音频地址无效", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        channelItemscription = RxBus.getInstance().
                toObserverable(MessageData.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageData>() {
                    @Override
                    public void call(MessageData messageData) {
                        if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_RADIO_ITEM_SELECT)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
//                            if (channel != null) {
                            playedChannel = channel;
                            initData();
//                            }
                        }
                    }
                });
    }

    int pos = -1;

    public void clearPlay() {
        if (adapter != null)
            adapter.clearPlayFlag();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (adapter != null && !adapter.isInit()) {
//            initData(playedChannel);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (channelItemscription != null)
            channelItemscription.unsubscribe();
    }

    public long getLiveId() {
        long chanId = -1;
        if (getParentFragment() != null) {
            chanId = ((DzBrodcastFragment) getParentFragment()).getLiveId();
        }
        return chanId;
    }

    private String getFileId() {
        String defaultId = "livetv_";
        if (playedChannel != null)
            defaultId += playedChannel.channelID;
        return defaultId;
    }

    private String getQuerytime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        String dates = "";
        ca.add(Calendar.DATE, -6);
        dates = sDateFormat.format(ca.getTime());
        return dates;
    }

    private String getFileName() {
        return "radiotablesFragment.txt";
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

                                            ///  方法二
                                            if (entity.getM3u8_url() == null || TextUtils.isEmpty(entity.getM3u8_url())) {
//                                                if (columlist != null && columlist.size() > 0) {
//                                                    if (currentPosition < columlist.size()) {
//                                                        liveUrl = columlist.get(currentPosition).url;
//                                                    }
//                                                }
                                                if (entity.getStart_time() * 1000 < current) {
                                                    chanl.isLive = true;
                                                    chanl.isPlayback = false;
                                                    isflag = true;
                                                } else {
                                                    int pos = i > 0 ? --i : 0;
                                                    if (!dlist.isEmpty() && pos < dlist.size()) {
                                                        LiveEntity.LiveChannel tasg = dlist.get(pos);
                                                        tasg.isLive = true;
                                                        tasg.isPlayback = false;
                                                        tasg.url = liveUrl;
                                                    }
                                                }
                                                isflag = true;
                                            } else {
                                                chanl.isPlayback = true;
                                            }
                                        }
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

    protected void getData(boolean bAddTail, String date, boolean showProcessBar) {
//        if (playedChannel == null) return;
        //        if (showProcessBar && act != null && !act.isFinishing()) {
        //            mLoading = CustomeProgressDialog.show(act, "");
        //        }
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/lives/" + getLiveId() + "/playlists?";
        //        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/lives/" + 1 + "/playlists?";
        //        String time = getTimeSt(type);
        url += "start=" + date + "&limit=7";
        //        url += "start=" + "2017-01-07" + "&limit=1";
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
                    setSelectedWeekColor(0);
                }
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("LiveVideoSubFragment", "getData fail");
                e.printStackTrace();
            }
        });
    }

    private void setSelectedWeekColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mWeeks.findViewWithTag(weekIndex).
                findViewById(R.id.scroll_item_week_id);
        if (oldSelectedText != null && getActivity() != null)
            oldSelectedText.setTextColor(getResources().getColor(R.color.tv_week_name));
        if (selectedPosition != -1) {
            TextView selectedText = (TextView) mWeeks.findViewWithTag(selectedPosition).
                    findViewById(R.id.scroll_item_week_id);
            if (getActivity() != null)
                selectedText.setTextColor(getResources().getColor(R.color.public_purple_bkg));
            weekIndex = selectedPosition;
        }
    }

    class MyAdapter extends BaseAdapter {
        ArrayList<LiveEntity.LiveChannel> data;

        public boolean isInit() {
            return isInit;
        }

        boolean isInit = false;
        private int selectedPosition = -1;// 选中的位置

        public MyAdapter(ArrayList<LiveEntity.LiveChannel> data) {
            this.data = data;
        }

        public MyAdapter() {
        }

        public void setSelectPostion(int position) {
            this.selectedPosition = position;
            notifyDataSetInvalidated();
        }

        public void update(ArrayList<LiveEntity.LiveChannel> data) {
            this.data = data;
            isInit = true;
            notifyDataSetChanged();
        }

        public ArrayList<LiveEntity.LiveChannel> getData() {
            return data;
        }


        public void add(boolean addTrail, ArrayList<LiveEntity.LiveChannel> data) {
            if (addTrail)
                this.data.addAll(data);
            else
                this.data = data;
            notifyDataSetChanged();
        }

        public void clearPlayFlag() {
            selectedPosition = -1;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//                        BaseViewHodler viewHodler = BaseViewHodler.get(convertView, parent, R.layout.program_list_item, position);
//            BaseViewHodler viewHodler = BaseViewHodler.get(convertView, parent, R.layout.tv_news_item_list, position);
            BaseViewHodler viewHodler = BaseViewHodler.get(convertView, parent, R.layout.live_radio_item_list, position);
            setViewData(viewHodler, position);
            return viewHodler.getConvertView();
        }

        private void setViewData(BaseViewHodler viewHodler, final int position) {
            View body = viewHodler.getView(R.id.tv_body_layout);
//            TextView weektxt = viewHodler.getView(R.id.tv_item_week);
            TextView timeView = viewHodler.getView(R.id.tv_item_tiem);
            TextView titleView = viewHodler.getView(R.id.tv_item_title_tx);
            TextView operartxr = viewHodler.getView(R.id.tv_item_opera_btn);
            ImageView playFlag = viewHodler.getView(R.id.radio_play_mask);
            LiveEntity.LiveChannel channe = data.get(position);
            titleView.setTextColor(getResources().getColor(R.color.tv_name_selected));
            if (channe.isPlayback && channe.url != null && !TextUtils.isEmpty(channe.url)) {
                operartxr.setText("(回放)");
                operartxr.setVisibility(View.VISIBLE);
            } else {
                operartxr.setVisibility(View.GONE);
            }
            if (!channe.isPlayback) {
                if (channe.isLive) {
                    operartxr.setText("(直播)");
//                    titleView.setTextColor(getResources().getColor(R.color.red));
                    operartxr.setVisibility(View.VISIBLE);
                } else {
                    operartxr.setVisibility(View.GONE);
                }
            }
            if (selectedPosition == position) {
//                body.setBackgroundColor(getResources().getColor(R.color.white));
                playFlag.setVisibility(View.VISIBLE);
            } else {
                playFlag.setVisibility(View.GONE);
//                body.setBackgroundColor(getResources().getColor(R.color.public_bgd));
            }
            timeView.setText(data.get(position).creaTime);
            titleView.setText(data.get(position).channelName);
        }

        private void whenClicked(int position) {
            Toast.makeText(context, "position " + position + " clicked", Toast.LENGTH_SHORT).show();
        }
    }


}
