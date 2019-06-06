package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ComunityFullVideoActivity;
import com.dfsx.ganzcms.app.adapter.FileUploadAdapter;
import com.dfsx.core.file.upload.OkHttpUploadFile;
import com.dfsx.ganzcms.app.business.PostWordManager;
import com.dfsx.ganzcms.app.business.UploadHelper;
import com.dfsx.ganzcms.app.model.IUploadFile;
import com.dfsx.ganzcms.app.model.UploadFile;
import com.dfsx.core.file.upload.UploadFileData;
import com.dfsx.ganzcms.app.model.WordData;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.BottomSelectedPopupwindow;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 快速投稿
 */
public class EditWordsFragment extends AbsListFragment {

    private static final int MAX_IMAGE_COUNT = 1000;
    private static final int MAX_VIDEO_COUNT = 3;

    public static final String KEY_WORD_COLUMN_ID_LIST = "EditWordsFragment_column_id_list";

    private static final int SELECT_VIDEO = 5;
    private static final int SELECT_IMAGE = 2;
    private static final int CAMERA_IMAGE = 7;
    private static final int CAMERA_RECORD_VIDEO = 8;

    private FileUploadAdapter adapter;

    private EditText editWordTitle;
    private EditText editWordContent;

    private ArrayList<IUploadFile> imageUploadFileList;
    private ArrayList<IUploadFile> videoUploadFileList;

    private ArrayList<IUploadFile> uploadFileList;

    private BottomSelectedPopupwindow imageSelectedWindow;
    private BottomSelectedPopupwindow videoSelectedWindow;


    private static Uri fileUri;

    private OkHttpUploadFile uploadFile;

    private ArrayList<Long> wordColumnIdList;

    private InputMethodManager imm;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        doIntent();
        super.onViewCreated(view, savedInstanceState);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        pullToRefreshListView.setBackgroundResource(R.color.white);
        uploadFile = new OkHttpUploadFile();
        initAdapterData();
        initPop();
        initTopbarAction();
    }

    private void doIntent() {
        if (getArguments() != null) {
            try {
                wordColumnIdList = (ArrayList<Long>) getArguments().
                        getSerializable(KEY_WORD_COLUMN_ID_LIST);
            } catch (Exception e) {
                e.printStackTrace();
                wordColumnIdList = null;
            }

        }
    }

    private void initTopbarAction() {
        if (act instanceof WhiteTopBarActivity) {
            TextView rightText = ((WhiteTopBarActivity) act).getTopBarRightText();
            RxView.clicks(rightText)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            String title = editWordTitle.getText().toString();
                            if (TextUtils.isEmpty(title)) {
                                Toast.makeText(context, "请输入标题", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String content = editWordContent.getText().toString();
                            if (TextUtils.isEmpty(content)) {
                                Toast.makeText(context, "请输入发布内容", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            postWord();
                        }
                    });
        }
    }


    private void initPop() {
        imageSelectedWindow = new BottomSelectedPopupwindow(context, "拍照", "从相册中选取");
        videoSelectedWindow = new BottomSelectedPopupwindow(context, "拍摄", "从视频库中选取");

        imageSelectedWindow.initDataView();
        videoSelectedWindow.initDataView();
        imageSelectedWindow.setOnBottomItemClickListener(new BottomSelectedPopupwindow.OnBottomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    goToCameraPhoto();
                } else if (position == 1) {
                    goSelectedPhoto();
                }
            }
        });

        videoSelectedWindow.setOnBottomItemClickListener(new BottomSelectedPopupwindow.OnBottomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    goToCameraVideo();
                } else if (position == 1) {
                    goSelectedVideo();
                }
            }
        });
    }

    private void postWord() {
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, ArrayList<UploadFileData>>() {
                    @Override
                    public ArrayList<UploadFileData> call(Object o) {
                        try {
                            ArrayList<UploadFileData> upList = new ArrayList<>();
                            if (videoUploadFileList != null) {
                                String videoUrl = UploadHelper.getCMSVideoUploadUrl();
                                Log.e("TAG", "video upload Url === " + videoUrl);
                                for (IUploadFile upF : videoUploadFileList) {
                                    if (!TextUtils.isEmpty(upF.getFileUrl())) {
                                        File file = new File(upF.getFileUrl());
                                        Log.e("TAG", "video == " + upF.getFileUrl());
                                        if (file.exists()) {
                                            UploadFileData data = UploadFileData.videoUploadFile(videoUrl, file);
                                            upList.add(data);
                                        }
                                    }
                                }
                            }
                            if (imageUploadFileList != null) {
                                String imageUrl = UploadHelper.getCMSImageUploadUrl();
                                Log.e("TAG", "image upload Url === " + imageUrl);
                                for (IUploadFile upF : imageUploadFileList) {
                                    if (!TextUtils.isEmpty(upF.getFileUrl())) {
                                        File file = new File(upF.getFileUrl());
                                        Log.e("TAG", "image == " + upF.getFileUrl());
                                        if (file.exists()) {
                                            UploadFileData data = UploadFileData.imageUploadFile(imageUrl, file);
                                            upList.add(data);
                                        }
                                    }
                                }
                            }
                            return upList;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<UploadFileData>>() {
                    @Override
                    public void call(ArrayList<UploadFileData> uploadFileData) {
                        if (uploadFileData != null) {
                            WordData data = new WordData(editWordTitle.getText().toString(),
                                    editWordContent.getText().toString());
                            data.setColumnIdList(wordColumnIdList);
                            data.setUploadFileDataList(uploadFileData);
                            PostWordManager.getInstance().post(data);
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            Toast.makeText(context, "获取上传地址失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    private void goToCameraPhoto() {
        gotoTakeImage();
    }

    private void goToCameraVideo() {
        gotoRecordVideo();
    }

    private void goSelectedPhoto() {
        gotoSelectImage();
    }

    public void gotoSelectImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
                int max = MAX_IMAGE_COUNT - (imageUploadFileList != null ? imageUploadFileList.size() - 1 : 0);
                int value = Math.min(9, max);
                intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, value);
                startActivityForResult(intent, SELECT_IMAGE);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    private void goSelectedVideo() {
        hideInputSoft();
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), VideoFragmentActivity.class);
                int max = MAX_VIDEO_COUNT - (videoUploadFileList != null ? videoUploadFileList.size() - 1 : 0);
                int value = Math.min(9, max);
                intent.putExtra(VideoFragmentActivity.KEY_MAX_MODE, value);
                startActivityForResult(intent, SELECT_VIDEO);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void gotoTakeImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                takePhotos();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void takePhotos() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_IMAGE); // create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoRecordVideo() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(context, DefaultFragmentActivity.class);
                intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_NAME,
                        CommonityRecordFragment.class.getName());
                intent.putExtra("is_set_result", true);
                startActivityForResult(intent, CAMERA_RECORD_VIDEO);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
                )
                .check();
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(UtilHelp.PUBLIC_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void hideInputSoft() {
        if (editWordTitle == null) return;
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editWordTitle.getWindowToken(), 0);
        }
    }

    private void addImageUrl(UploadFile imageFile) {
        if (isFullVideo()) {
            return;
        }
        int index = imageUploadFileList != null ? imageUploadFileList.size() - 1 : 0;
        uploadFileList.add(index + 1, imageFile);
        if (imageUploadFileList != null) {
            if (imageUploadFileList.size() > 0) {
                imageUploadFileList.add(imageUploadFileList.size() - 1, imageFile);
            } else {
                imageUploadFileList.add(imageFile);
            }
        }
        if (isFullImage()) {
            int imageAddIndex = imageUploadFileList.size();
            boolean is = TextUtils.isEmpty(uploadFileList.get(imageAddIndex).getFileUrl());
            if (is) {
                imageUploadFileList.remove(imageAddIndex - 1);
                uploadFileList.remove(imageAddIndex);
            }
        }
    }

    private void addVideoFile(UploadFile videoFile) {
        if (isFullVideo()) {
            return;
        }
        if (videoUploadFileList != null) {
            if (videoUploadFileList.size() > 0) {
                videoUploadFileList.add(videoUploadFileList.size() - 1, videoFile);
            } else {
                videoUploadFileList.add(videoFile);
            }
        }
        if (uploadFileList != null) {
            int index = uploadFileList.size() - 1;
            uploadFileList.add(index, videoFile);
        }
        if (isFullVideo()) {
            int addVideoIndex = uploadFileList.size() - 1;
            if (TextUtils.isEmpty(uploadFileList.get(addVideoIndex).getFileUrl())) {
                uploadFileList.remove(addVideoIndex);
                videoUploadFileList.remove(videoUploadFileList.size() - 1);
            }
        }
    }

    private boolean isFullImage() {
        if (imageUploadFileList != null) {
            boolean isHasAdd = TextUtils.isEmpty(imageUploadFileList.get(imageUploadFileList.size() - 1).getFileUrl());
            if (isHasAdd) {
                return imageUploadFileList.size() >= MAX_IMAGE_COUNT + 1;
            } else {
                return imageUploadFileList.size() >= MAX_IMAGE_COUNT;
            }
        }
        return false;
    }

    private boolean isFullVideo() {
        if (videoUploadFileList != null) {
            boolean isHasAdd = TextUtils.isEmpty(videoUploadFileList.get(videoUploadFileList.size() - 1).getFileUrl());
            if (isHasAdd) {
                return videoUploadFileList.size() >= MAX_VIDEO_COUNT + 1;
            } else {
                return videoUploadFileList.size() >= MAX_VIDEO_COUNT;
            }
        }
        return false;
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new FileUploadAdapter(context);
        listView.setAdapter(adapter);

        View v = LayoutInflater.from(context)
                .inflate(R.layout.edit_words_header_layout, null);
        listView.addHeaderView(v);
        editWordTitle = (EditText) v.findViewById(R.id.edit_title);
        editWordContent = (EditText) v.findViewById(R.id.edit_content);


        adapter.setOnClickEventListener(new FileUploadAdapter.OnClickEventListener() {
            @Override
            public void onDeleteClick(IUploadFile file) {
                if (TextUtils.equals(file.getFileType(), UploadFile.FILE_VIDEO)) {
                    boolean is = videoUploadFileList.remove(file);
                    boolean re = uploadFileList.remove(file);
                    if (is && !isFullVideo()) {
                        addAddVideoItem(re);
                    }
                    Log.e("TAG", "remove video == " + is);
                } else {
                    boolean is = imageUploadFileList.remove(file);
                    boolean re = uploadFileList.remove(file);
                    if (is && !isFullImage()) {
                        addAddImageItem(re);
                    }
                    Log.e("TAG", "remove image == " + is);
                }

                adapter.update(uploadFileList, false);
            }
        });
    }


    private void initAdapterData() {
        uploadFileList = new ArrayList<>();
        imageUploadFileList = new ArrayList<>();
        videoUploadFileList = new ArrayList<>();

        UploadFile uploadFile = new UploadFile(FileUploadAdapter.TYPE_CARTGROY, "上传图片");
        uploadFileList.add(uploadFile);
        addAddImageItem(false);
        addAddVideoItem(false);
        uploadFileList.addAll(imageUploadFileList);
        uploadFile = new UploadFile(FileUploadAdapter.TYPE_CARTGROY, "上传视频");
        uploadFileList.add(uploadFile);
        uploadFileList.addAll(videoUploadFileList);

        adapter.update(uploadFileList, false);


        adapter.setOnGridItemClickListener(new BaseGridListAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemClick(int linePosition, int column) {
                try {
                    ArrayList<IUploadFile> list = adapter.getGridData().get(linePosition);
                    IUploadFile file = list.get(column);
                    if (TextUtils.isEmpty(file.getFileUrl())) {
                        if (TextUtils.equals(file.getFileType(), UploadFile.FILE_IMAGE)) {
                            imageSelectedWindow.show(listView);
                        } else {
                            videoSelectedWindow.show(listView);
                        }
                    } else {
                        if (TextUtils.equals(file.getFileType(), UploadFile.FILE_IMAGE)) {
                            int pos = findImagePos(file);
                            ArrayList<String> images = new ArrayList<>();
                            for (IUploadFile file1 : imageUploadFileList) {
								if(!TextUtils.isEmpty(file1.getFileUrl())) {
									images.add(file1.getFileUrl());
								}   
                            }
                            OpenImageUtils.openImage(context, pos, images.toArray(new String[0]));
                        } else {
                            Intent intent = new Intent(context, ComunityFullVideoActivity.class);
                            intent.putExtra("url", file.getFileUrl());
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private int findImagePos(IUploadFile file) {
        if (imageUploadFileList != null) {
            for (int i = 0; i < imageUploadFileList.size(); i++) {
                if (TextUtils.equals(file.getFileUrl(), imageUploadFileList.get(i).getFileUrl())) {
                    return i;
                }
            }
        }
        return 0;
    }

    private int findVideoPos(IUploadFile file) {
        if (videoUploadFileList != null) {
            for (int i = 0; i < videoUploadFileList.size(); i++) {
                if (TextUtils.equals(file.getFileUrl(), videoUploadFileList.get(i).getFileUrl())) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    if (!bitmapList.isEmpty()) {
                        for (int i = 0; i < bitmapList.size(); i++) {
                            MediaModel model = bitmapList.get(i);
                            if (model != null) {
                                UploadFile file = new UploadFile(model.url, UploadFile.FILE_IMAGE);
                                file.setFileThumbPath(model.url);
                                addImageUrl(file);
                            }
                        }
                        adapter.update(uploadFileList, false);
                    }
                }
                break;
                case CAMERA_IMAGE: {
                    try {
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        UploadFile file = new UploadFile(fileUriString, UploadFile.FILE_IMAGE);
                        file.setFileThumbPath(fileUriString);
                        addImageUrl(file);
                        adapter.update(uploadFileList, false);
                    } catch (Exception io_e) {
                        io_e.printStackTrace();
                    }
                }
                break;
                case SELECT_VIDEO: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    if (!bitmapList.isEmpty()) {
                        for (int i = 0; i < bitmapList.size(); i++) {
                            MediaModel model = bitmapList.get(i);
                            if (model != null) {
                                UploadFile file = new UploadFile(model.url, UploadFile.FILE_VIDEO);
                                addVideoFile(file);
                                adapter.update(uploadFileList, false);
                            }

                        }
                    }
                }
                break;
                case CAMERA_RECORD_VIDEO: {
                    if (data != null) {
                        String filePath = data.getStringExtra("path");
                        Log.e("TAG", "record video path == " + filePath);
                        UploadFile uploadFile = new UploadFile(filePath, UploadFile.FILE_VIDEO);
                        addVideoFile(uploadFile);
                        adapter.update(uploadFileList, false);
                    }
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addAddImageItem(boolean isAddUploadList) {
        if (imageUploadFileList == null) {
            imageUploadFileList = new ArrayList<>();
        }
        boolean isAdd = !imageUploadFileList.isEmpty() &&
                TextUtils.isEmpty(imageUploadFileList.get(imageUploadFileList.size() - 1).getFileUrl());
        if (!isAdd) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFileThumbPath(StringUtil.
                    resourceIdToUri(context, R.drawable.file_item)
                    .toString());
            uploadFile.setFileType(UploadFile.FILE_IMAGE);
            uploadFile.setShowType(FileUploadAdapter.TYPE_GRID);
            imageUploadFileList.add(uploadFile);
            if (isAddUploadList) {
                uploadFileList.add(imageUploadFileList.size(), uploadFile);
            }
        }
    }

    private void addAddVideoItem(boolean isAddUploadList) {
        if (videoUploadFileList == null) {
            videoUploadFileList = new ArrayList<>();
        }
        boolean isAdd = !videoUploadFileList.isEmpty() &&
                TextUtils.isEmpty(videoUploadFileList.get(videoUploadFileList.size() - 1).getFileUrl());
        if (!isAdd) {
            UploadFile uploadFile = new UploadFile("", UploadFile.FILE_VIDEO);
            uploadFile.setFileThumbPath(StringUtil.
                    resourceIdToUri(context, R.drawable.file_item)
                    .toString());
            videoUploadFileList.add(uploadFile);
            if (isAddUploadList) {
                uploadFileList.add(uploadFile);
            }
        }
    }
}
