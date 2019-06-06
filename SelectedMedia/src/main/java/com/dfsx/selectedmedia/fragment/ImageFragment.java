package com.dfsx.selectedmedia.fragment;


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.R;
import com.dfsx.selectedmedia.adapter.GridViewAdapter;


public class ImageFragment extends Fragment {
    public static final String KEY_SINGLE_MODE = "com.dfsx.selectedmedia.fragment.ImageFragment_KEY_SINGLE_MODE";
    public static final String KEY_MAX_NUBER_MODE = "com.dfsx.selectedmedia.fragment.ImageFragment_MAX_FILE_NUMBER";
    public static final String KEY_CURRENT_NUBER = "com.dfsx.selectedmedia.fragment.ImageFragment_CURRENT_NUBER_NUMBER";
    private ArrayList<MediaModel> mSelectedItems = new ArrayList<MediaModel>();
    private ArrayList<MediaModel> mGalleryModelList;
    private GridView mImageGridView;
    private View mView;
    private OnImageSelectedListener mCallback;
    private GridViewAdapter mImageAdapter;
    private Cursor mImageCursor;

    private boolean isSingleSelected;
    private int  currentIndex;

//    public int getMaxNumber() {
//        return maxNumber;
//    }


    private int  maxNumber=100;
    // Container Activity must implement this interface
    public interface OnImageSelectedListener {
        public void onImageSelected(int count);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnImageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnImageSelectedListener");
        }
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public ImageFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);

            mImageGridView = (GridView) mView.findViewById(R.id.gridViewFromMediaChooser);


            if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("name"))) {
                initPhoneImages(getArguments().getString("name"));
            } else {
                initPhoneImages();
            }

        } else {
            ((ViewGroup) mView.getParent()).removeView(mView);
            if (mImageAdapter == null || mImageAdapter.getCount() == 0) {
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

    private void initPhoneImages(String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = bucketName;
            searchParams = "bucket_display_name = \"" + bucket + "\"";

            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE};
            mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");

            setAdapter(mImageCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPhoneImages() {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE};
            mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");

            setAdapter(mImageCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(Cursor imagecursor) {

        if (imagecursor.getCount() > 0) {

            mGalleryModelList = new ArrayList<MediaModel>();

            for (int i = 0; i < imagecursor.getCount(); i++) {
                imagecursor.moveToPosition(i);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int latfColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
                int longfColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
                double latf = 0.0, longf = 0.0;
                if (latfColumnIndex != -1)
                    latf = imagecursor.getDouble(latfColumnIndex);
                if (longfColumnIndex != -1)
                    longf = imagecursor.getDouble(longfColumnIndex);
                MediaModel galleryModel = new MediaModel(imagecursor.getString(dataColumnIndex).toString(), latf, longf, false);
                mGalleryModelList.add(galleryModel);
            }


            mImageAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, false);
            mImageGridView.setAdapter(mImageAdapter);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
        }

        mImageGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
                MediaModel galleryModel = (MediaModel) adapter.getItem(position);
                File file = new File(galleryModel.url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
                return true;
            }
        });

        mImageGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                // update the mStatus of each category in the adapter
                GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
                MediaModel galleryModel = (MediaModel) adapter.getItem(position);


                if (!galleryModel.status) {
                    long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url.toString()), false);
                    if (size != 0) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_size_exeeded) + "  " + MediaChooserConstants.SELECTED_IMAGE_SIZE_IN_MB + " " + getActivity().getResources().getString(R.string.mb), Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    if ((MediaChooserConstants.MAX_MEDIA_LIMIT == MediaChooserConstants.SELECTED_MEDIA_COUNT)) {
//                    if ((maxNumber == MediaChooserConstants.SELECTED_MEDIA_COUNT)) {
//                        if (MediaChooserConstants.SELECTED_MEDIA_COUNT < 2) {
//                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + MediaChooserConstants.SELECTED_MEDIA_COUNT + " " + getActivity().getResources().getString(R.string.file), Toast.LENGTH_SHORT).show();
//                            return;
//                        } else {
//                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + MediaChooserConstants.SELECTED_MEDIA_COUNT + " " + getActivity().getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                    }

                    if ((currentIndex>=maxNumber)) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + maxNumber + " " + getActivity().getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
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

                if (mCallback != null) {
                    mCallback.onImageSelected(mSelectedItems.size());
                    Intent intent = new Intent();
//					intent.putStringArrayListExtra("list", mSelectedItems);
                    intent.putParcelableArrayListExtra("list", mSelectedItems);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }
        });
    }

    public void setSingleSelected(boolean isSingleSelected) {
        this.isSingleSelected = isSingleSelected;
    }

    private void clearAllItemStatus() {
        if (mImageAdapter != null) {
            for (MediaModel mm : mImageAdapter.getData()) {
                mm.status = false;
            }
            mImageAdapter.notifyDataSetChanged();
        }
    }
    public ArrayList<MediaModel> getSelectedImageList() {
        return mSelectedItems;
    }

    public void addItem(String item) {
        if (mImageAdapter != null) {
            MediaModel model = new MediaModel(item, false);
            mGalleryModelList.add(0, model);
            mImageAdapter.notifyDataSetChanged();
        } else {
            initPhoneImages();
        }
    }
}
