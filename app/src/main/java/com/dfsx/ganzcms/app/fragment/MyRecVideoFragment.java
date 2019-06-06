package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.SocirtyNewsChannel;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.selectedmedia.MediaModel;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/9/16.
 */
public class MyRecVideoFragment extends AbsListFragment implements AdapterView.OnItemClickListener {

    private Activity act;
    protected PullToRefreshListView pullToRefreshListView;
    protected ListView listView;
    private View loadFailView;
    private ListImagesAdapter adapter;
    private int offset;
    private int mTypeId = -1;
    private DataFileCacheManager<ArrayList<SocirtyNewsChannel>> dataFileCacheManager;
    private Cursor mImageCursor;
    private EmptyView mEmptyView;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @Override
    public void setListAdapter(ListView listView) {
        if (adapter == null) {
            adapter = new ListImagesAdapter();
        }
        listView.setAdapter(adapter);
        adapter.update();
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        mEmptyView = new EmptyView(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mEmptyView.setLayoutParams(p);
        container.addView(mEmptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_video_layout, null);
        mEmptyView.setLoadOverView(emptyLayout);
        mEmptyView.loading();
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private void initNewVideo() {
        try {
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            String searchParams = null;
//            String bucket = bucketName;
//            searchParams = "bucket_display_name = \"" + bucket + "\"";

//            String selection = MediaStore.Video.Media.DATA + " like %?";
//            searchParams = "/DCIM/iResource/";
            searchParams = UtilHelp.PUBLIC_RECORD_VIDEO_PATH;
//            String[] selectionArgs = {searchParams + "%"};

            final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.LATITUDE, MediaStore.Video.Media.LONGITUDE};
//            mImageCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
            mImageCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Video.Media.DATA + " LIKE" + " '%" + searchParams + "%'", null, orderBy + " DESC");
            if (mImageCursor == null) {

                return;
            }
            pullToRefreshListView.onRefreshComplete();
            if (mImageCursor.getCount() > 0) {
                int dataColumnIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int nameColumnIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int durColumnIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int latonfColumnIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LATITUDE);
                int longfColumnIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LONGITUDE);
                mImageCursor.moveToFirst();
                double latongf = 0.0;
                double longf = 0.0;
                int lengthg;
                for (int i = 0; i < mImageCursor.getCount(); i++) {
                    mImageCursor.moveToPosition(i);
                    String url = mImageCursor.getString(dataColumnIndex);
                    String name = mImageCursor.getString(nameColumnIndex);
//                    latongf = mImageCursor.getDouble(latonfColumnIndex);
//                    longf = mImageCursor.getDouble(longfColumnIndex);
                    lengthg = mImageCursor.getInt(durColumnIndex);
//                    String  up=UtilHelp.stringForTime((int) lengthg);
                    MediaModel galleryModel = new MediaModel(name, url, lengthg);
                    adapter.addItem(galleryModel);
                }

                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.loadOver();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private DataRequest.DataCallback<ArrayList<SocirtyNewsChannel>>
//            callback = new DataRequest.DataCallback<ArrayList<SocirtyNewsChannel>>() {
//        @Override
//        public void onSuccess(boolean isAppend, ArrayList<SocirtyNewsChannel> data) {
//            if (!isAppend && data == null) {
//                adapter.clear();
//            } else {
//                adapter.update(data, isAppend);
//            }
//            pullToRefreshListView.onRefreshComplete();
//        }
//
//        @Override
//        public void onFail(ApiException e) {
//            ArrayList<SocirtyNewsChannel> dlist = new ArrayList<SocirtyNewsChannel>();
//            for (int i = 0; i < 4; i++) {
//                SocirtyNewsChannel chne = new SocirtyNewsChannel();
//                chne.newsTitle = "【超级试驾员杯】十一山西东线深度自驾游~新路线，新高峰！";
//                dlist.add(chne);
//            }
//            adapter.update(dlist, false);
//            pullToRefreshListView.onRefreshComplete();
//        }
//    };

    protected void getData() {
        String path = "";
        Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<MediaModel>>() {
                    @Override
                    public ArrayList<MediaModel> call(String topicalEntry) {
                        ArrayList<MediaModel> list = new ArrayList<MediaModel>();
//                        try {
//                            if (App.getInstance().getUser() != null) {
//                                if (topicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
//                                    int res = mTopicalApi.isAttentionOther(topicalEntry.getAuthor_id());
//                                    topicalEntry.setRelationRole(res);
//                                } else
//                                    topicalEntry.setRelationRole(-1);
//                            } else
//                                topicalEntry.setRelationRole(-1);
//                            if (topicalEntry.getAttachmentInfos() == null) {
//                                String respone = mTopicalApi.getSyncAtthmentById(topicalEntry.getAttachments());
//                                if (!TextUtils.isEmpty(respone.toString().trim())) {
//                                    JSONObject json = JsonCreater.jsonParseString(respone);
//                                    Gson gson = new Gson();
//                                    JSONArray arr = json.optJSONArray("result");
//                                    if (arr != null && arr.length() > 0) {
//                                        for (int i = 0; i < arr.length(); i++) {
//                                            try {
//                                                JSONObject obj = (JSONObject) arr.get(i);
//                                                Attachment cp = gson.fromJson(obj.toString(),
//                                                        Attachment.class);
//                                                list.add(cp);
//                                                topicalEntry.setAttachmentss(list);
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (ApiException e) {
//                            e.printStackTrace();
//                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MediaModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<MediaModel> entry) {

                    }
                });
//                .subscribe(new Observer<ArrayList<MediaModel>>() {
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                                        if (pullListview != null)
//                                            pullListview.onRefreshComplete();
//                                        Log.e("MyDocxFragment", "get data fail");
//                    }
//
//                    @Override
//                    public void onNext(ArrayList<MediaModel> data) {
//                        int a = 0;
//                        if (adapter != null && data != null) {
//                          //  adapter.update((ArrayList<TopicalEntry>) data, isAppend);
//                        }
//                    }
//                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - listView.getHeaderViewsCount();
        try {
            boolean isLeagle = position >= 0 && position < adapter.getCount();
            if (isLeagle) {
                MediaModel channel = adapter.getData().get(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ListImagesAdapter extends BaseAdapter {

        private ArrayList<MediaModel> list = new ArrayList<>();

        private Context context;

        public void update() {
            loading();
            ;
        }

        public void update(ArrayList<MediaModel> data, boolean bAddTail) {
            list = data;
            notifyDataSetChanged();
            loading();
            ;
        }

        public void addItem(MediaModel mode) {
            list.add(mode);
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public ArrayList<MediaModel> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
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
            if (context == null) {
                context = parent.getContext();
            }
            BaseViewHodler viewHodler = BaseViewHodler.
                    get(convertView, parent, getViewId(), position);
            setViewHolderData(viewHodler, position);
            return viewHodler.getConvertView();
        }

        protected int getViewId() {
            return R.layout.news_video_list_hsrocll_item;
        }

        protected void setViewHolderData(BaseViewHodler holder, int position) {
            ImageView itemImage = holder.getView(R.id.item_img);
            TextView itemTiltle = holder.getView(R.id.item_title);
            TextView itemcolumn = holder.getView(R.id.item_autor);
            TextView itemCreateTime = holder.getView(R.id.item_create_time);
            MediaModel channel = list.get(position);
            GlideImgManager.getInstance().showImg(context, itemImage, channel.url);
            itemTiltle.setText(channel.name);
//            String autorText = TextUtils.isEmpty(channel.author) ? "" : channel.author;
//            itemcolumn.setText(autorText);
//            itemCreateTime.setText(channel.newsTime);
        }

        public void removeAlls() {
            if (!list.isEmpty()) {
                list.clear();
            }
        }

        public void loading() {
            adapter.removeAlls();
            new Thread(new Runnable() {
                public void run() {
//                    initVideo("iResource");
                    initNewVideo();
//                    Message message = new Message();
//                    message.what = 1;
//                    handler.sendMessage(message);
                }
            }).start();
        }

    }

}
