package com.dfsx.procamera.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.dfsx.core.common.Util.LoadingVideoThumbImageUtil;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.procamera.R;
import com.dfsx.procamera.busniness.IActivtiySelectItemiter;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.adapter.GridViewAdapter;

import java.io.File;
import java.util.ArrayList;


public class MyVideoGrid2Fragment extends Fragment implements OnScrollListener {
    public static final String KEY_SINGLE_MODE = "com.dfsx.selectedmedia.fragment.VideoFragment_KEY_SINGLE_MODE";
    public static final String KEY_MAX_NUBER_MODE = "com.dfsx.selectedmedia.fragment.VideoFragment_MAX_FILE_NUMBER";
    public static final String KEY_CURRENT_NUBER = "com.dfsx.selectedmedia.fragment.VideoFragment_CURRENT_NUMBER";

    private final static Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private final static String MEDIA_DATA = MediaStore.Video.Media.DATA;

    private GridViewAdapter mVideoAdapter;
    private GridView mVideoGridView;
    private Cursor mCursor;
    private int mDataColumnIndex;
    private ArrayList<MediaModel> mSelectedItems = new ArrayList<MediaModel>();
    private ArrayList<MediaModel> mGalleryModelList;
    private View mView, backBtn;
    //    private OnVideoSelectedListener mCallback;
    private boolean isSingleSelected;
    private int currentIndex = 0;
    private int maxNumber = 1;
    private TextView mComfireBtn;
    private IActivtiySelectItemiter mCallback;

    // Container Activity must implement this interface
//    public interface OnVideoSelectedListener {
//        public void onVideoSelected(int count);
//    }

    public MyVideoGrid2Fragment() {
        setRetainInstance(true);
    }

    public void setSingleSelected(boolean isSingleSelected) {
        this.isSingleSelected = isSingleSelected;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.my_grid_layout_video_chooser, container, false);

            backBtn = (View) mView.findViewById(R.id.top_back_btn);
            mComfireBtn = (TextView) mView.findViewById(R.id.act_top_comfire_btn);
            mVideoGridView = (GridView) mView.findViewById(R.id.gridViewFromMediaChooser);

            if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("name"))) {
                initVideos(getArguments().getString("name"));
            } else {
                initVideos();
            }

        } else {
            ((ViewGroup) mView.getParent()).removeView(mView);
            if (mVideoAdapter == null || mVideoAdapter.getCount() == 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
            }
        }
        if (getArguments() != null) {
            setSingleSelected(getArguments().getBoolean(KEY_SINGLE_MODE, false));
            setMaxNumber(getArguments().getInt(KEY_MAX_NUBER_MODE, 100));
            setCurrentIndex(getArguments().getInt(KEY_CURRENT_NUBER, 0));
        }
        return mView;
    }

    public void rarshData() {
        initVideos();
    }

    public void setmCallback(IActivtiySelectItemiter mCallback) {
        this.mCallback = mCallback;
    }


    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    private void clearAllItemStatus() {
        if (mVideoAdapter != null) {
            for (MediaModel mm : mVideoAdapter.getData()) {
                mm.status = false;
            }
            mVideoAdapter.notifyDataSetChanged();
        }
    }

    private void initVideos(String bucketName) {
        try {
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            String searchParams = null;
            searchParams = "bucket_display_name = \"" + bucketName + "\"";
            final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
            mCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
            setAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initVideos() {
        try {
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            //Here we set up a string array of the thumbnail ID column we want to get back
            String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, MediaStore.Video.Media.LANGUAGE, MediaStore.Video.Media.LONGITUDE, MediaStore.Video.Media.LANGUAGE, MediaStore.Video.Media.DURATION};
            mCursor = getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null, null, orderBy + " DESC");
            setAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        int count = mCursor.getCount();
        if (count > 0) {
            mDataColumnIndex = mCursor.getColumnIndex(MEDIA_DATA);
            int latfColumnIndex = mCursor.getColumnIndex(MediaStore.Video.Media.LATITUDE);
            int longfColumnIndex = mCursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE);
            int durationColumnIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                    MediaStore.Video.Thumbnails.VIDEO_ID};

            //move position to first element
            mCursor.moveToFirst();
            mGalleryModelList = new ArrayList<MediaModel>();
            for (int i = 0; i < count; i++) {
                mCursor.moveToPosition(i);

                MediaModel model = new MediaModel();
                model.setType(1);
                model.setStatus(false);

//                int id = mCursor.getInt(mCursor
//                        .getColumnIndex(MediaStore.Video.Media._ID));
//                Cursor thumbCursor = getActivity().getContentResolver().query(
//                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
//                                + "=" + id, null, null);

//                if (thumbCursor.moveToFirst()) {
//                    model.setThumb(thumbCursor.getString(thumbCursor
//                            .getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)));
//                }
                model.setUrl(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media
                        .DATA)));
                model.setDuration(mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION)));

                String url = mCursor.getString(mDataColumnIndex);
                double latf = 0.0, longtf = 0.0;
                int length = 0;
                if (latfColumnIndex != -1)
                    latf = mCursor.getDouble(latfColumnIndex);
                if (longfColumnIndex != -1)
                    longtf = mCursor.getDouble(longfColumnIndex);
                if (durationColumnIndex != -1)
                    length = mCursor.getInt(durationColumnIndex);
//                mGalleryModelList.add(new MediaModel(url, 1, false, length));
                mGalleryModelList.add(model);
            }
            mVideoAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, true);
            mVideoAdapter.videoFragment = this;
            mVideoGridView.setAdapter(mVideoAdapter);
            mVideoGridView.setOnScrollListener(this);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
        }

        mVideoGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
                MediaModel galleryModel = (MediaModel) adapter.getItem(position);
                File file = new File(galleryModel.url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(intent);
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mComfireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onComfireClick();
            }
        });

        mVideoGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // update the mStatus of each category in the adapter
                GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
                MediaModel galleryModel = (MediaModel) adapter.getItem(position);

                if (!galleryModel.status) {

                    long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url.toString()), true);
                    if (galleryModel.duration >10*1000) {
//                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_size_exeeded) + "  " + MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB + " " + getActivity().getResources().getString(R.string.mb), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),"最大上传文件限制10秒以内",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if ((currentIndex >= maxNumber)) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + maxNumber + " " + getActivity().getResources().getString(R.string.file), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (isSingleSelected && !galleryModel.status) {
                    clearAllItemStatus();
                    mSelectedItems.clear();
                    currentIndex = 0;
                }

                // inverse the status
                galleryModel.status = !galleryModel.status;
                adapter.notifyDataSetChanged();

                if (galleryModel.status) {
                    mSelectedItems.add(galleryModel);
                    currentIndex++;

                } else {
                    mSelectedItems.remove(galleryModel);
                    currentIndex--;
                }

//                if (mCallback != null) {
//                    mCallback.onVideoSelected(mSelectedItems.size());
//                    Intent intent = new Intent();
////                    intent.putStringArrayListExtra("list", mSelectedItems);
//                    intent.putParcelableArrayListExtra("list", mSelectedItems);
//                    getActivity().setResult(Activity.RESULT_OK, intent);
//                }
            }
        });
    }

    public long getActivitId() {
        long id = -1;
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof AcvityCameraTabFragment) {
                id = ((AcvityCameraTabFragment) getParentFragment()).getActivtiId();
            }
        }
        return id;
    }

    public void onComfireClick() {
        MediaModel mode = null;
        if (!(mSelectedItems == null || mSelectedItems.isEmpty()))
            mode = mSelectedItems.get(0);
        if (mCallback != null) {
            if (!(mode == null || TextUtils.isEmpty(mode.url)))
                mCallback.OnComplete(mode.url);
        }
//        else {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("object", mode);
//            bundle.putLong("actId", getActivitId());
//            DefaultFragmentActivity.start(getActivity(), ActivityPublishFragment.class.getName(), bundle);
//        }




//        mCallback.onVideoSelected(mSelectedItems.size());
//        Intent intent = new Intent();
//                    intent.putStringArrayListExtra("list", mSelectedItems);
//        intent.putParcelableArrayListExtra("list", mSelectedItems);
//        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    public void addItem(String item) {
        if (mVideoAdapter != null) {
            MediaModel model = new MediaModel(item, false);
            mGalleryModelList.add(0, model);
            mVideoAdapter.notifyDataSetChanged();
        } else {
            initVideos();
        }
    }

    public GridViewAdapter getAdapter() {
        if (mVideoAdapter != null) {
            return mVideoAdapter;
        }
        return null;
    }

    public ArrayList<MediaModel> getSelectedVideoList() {
        return mSelectedItems;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //		if (view.getId() == android.R.id.list) {
        if (view == mVideoGridView) {
            // Set scrolling to true only if the user has flinged the
            // ListView away, hence we skip downloading a series
            // of unnecessary bitmaps that the user probably
            // just want to skip anyways. If we scroll slowly it
            // will still download bitmaps - that means
            // that the application won't wait for the user
            // to lift its finger off the screen in order to
            // download.
            if (scrollState == SCROLL_STATE_FLING) {
                //chk
            } else {
                mVideoAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }


    class VideoGridAdapter extends BaseGridListAdapter<MediaModel> {
        public static final String TAG = "ActivityGridAdapter";
        private boolean isInit = false;

        public VideoGridAdapter(Context context) {
            super(context);
        }

        public boolean isInit() {
            return isInit;
        }

        public int getCount() {
            return list == null ? 0 : list.size();
        }

//        @Override
//        public int getItemViewLayoutId() {
//            return R.layout.adapter_video_grid_layout;
//        }
//
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
//                    getItemViewLayoutId(), position);
//            if (convertView == null) {
//                LinearLayout lineContainerView = holdler.getView(R.id.item_cul_line_container);
//                LinearLayout.LayoutParams params;
//                for (int i = 0; i < getColumnCount(); i++) {
//                    FrameLayout lineItemContainer = new FrameLayout(context);
//                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT);
//                    params.weight = 1;
//                    lineItemContainer.setLayoutParams(params);
//                    lineItemContainer.setId(i);
//                    lineContainerView.addView(lineItemContainer, params);
//
//                    if (i < getColumnCount() - 1) {
//                        //添加grid水平反向的分割线
//                        addItemDivideView(lineContainerView);
//                    }
//                    BaseViewHodler itemHolder = BaseViewHodler.get(null, parent, getGridItemLayoutId(), position);
//                    lineItemContainer.addView(itemHolder.getConvertView());
//                    lineItemContainer.setTag(itemHolder);
//                }
//            }
//            setItemViewData(holdler, position);
//            return holdler.getConvertView();
//        }

//        protected void addItemDivideView(LinearLayout container) {
//            int with = Util.dp2px(context, 10);
//            if (with > 0) {
//                View divideView = new View(context);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                divideView.setLayoutParams(lp);
//                divideView.setBackgroundResource(R.color.white);
//                container.addView(divideView);
//            }
//        }


//        @Override
//        protected int getHDivideLineWidth() {
//            return 10;
//        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.my_grid_video_item_chooser;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, MediaModel model) {
            if (hodler == null)
                return;
            ImageView shopThumbImageView = hodler.getView(R.id.imageVieGridItemRowView);
            TextView durationiew = hodler.getView(R.id.video_duration_time);
            CheckedTextView imageCheckView = hodler.getView(R.id.checkTextGridItemView);
            if (model == null) return;
            imageCheckView.setChecked(model.status);
            new LoadingVideoThumbImageUtil(shopThumbImageView, model.url, R.drawable.glide_default_image).load();
            durationiew.setText(StringUtil.formatTime(model.duration));
        }
    }


}

