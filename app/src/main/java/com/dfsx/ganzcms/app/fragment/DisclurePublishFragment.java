package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.SwitchView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.upload.OkHttpUploadFile;
import com.dfsx.core.file.upload.PublishData;
import com.dfsx.core.file.upload.UploadFileData;
import com.dfsx.core.log.LogcatPushHelper;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.logonproject.busniness.TokenHelper;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ComunityFullVideoActivity;
import com.dfsx.ganzcms.app.adapter.FileUploadAdapter;
import com.dfsx.ganzcms.app.model.DiscleRevelEntry;
import com.dfsx.ganzcms.app.model.IUploadFile;
import com.dfsx.ganzcms.app.model.UploadFile;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.BottomSelectedPopupwindow;
import com.dfsx.ganzcms.app.view.live.MapUtils;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.dfsx.lscms.app.business.PostWordManager;
//import com.dfsx.lscms.app.business.UploadHelper;
//import com.dfsx.lscms.app.model.WordData;

/**
 * create  by  2018/1/10
 */
public class DisclurePublishFragment extends AbsListFragment implements MapUtils.MapCallBack {
    private static final int MAX_IMAGE_COUNT = 9;
    private static final int MAX_VIDEO_COUNT = 3;

    public static final String KEY_WORD_COLUMN_ID_LIST = "EditWordsFragment_column_id_list";
    private static final int SELECT_VIDEO = 5;
    private static final int SELECT_IMAGE = 2;
    private static final int CAMERA_IMAGE = 7;
    private static final int CAMERA_RECORD_VIDEO = 8;
    private static final int MESSAGE_TYPE = 12;

    private FileUploadAdapter adapter;

    private EditText editWordTitle;
    private EditText editWordContent;
    private EditText userName;
    private EditText userTelphone;

    private ArrayList<IUploadFile> imageUploadFileList;
    private ArrayList<IUploadFile> videoUploadFileList;

    private ArrayList<IUploadFile> uploadFileList;

    private BottomSelectedPopupwindow imageSelectedWindow;
    private BottomSelectedPopupwindow videoSelectedWindow;

    private static Uri fileUri;

    private OkHttpUploadFile uploadFile;

    private ArrayList<Long> wordColumnIdList;

    private InputMethodManager imm;

    private ContentCmsApi contentCmsApi;
    private ContentCmsApi contentApi;
    private boolean isOpenLocal = true;
    TextView pubBtn;
    TextView localname;
    ImageView localmark;
    MapUtils mapUtils;
    double latitude = 0, longitude = 0;
    String address;
    boolean isPubing = false;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == MESSAGE_TYPE) {
                if (getActivity() != null) {
                    String msg = (String) message.obj;
                    ToastUtils.toastMsgFunction(getActivity(), msg);
                }
            }
            return false;
        }
    });

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        doIntent();
        super.onViewCreated(view, savedInstanceState);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        pullToRefreshListView.setBackgroundResource(R.color.white);
//        contentCmsApi = new ContentCmsApi(getActivity());
        contentApi = new ContentCmsApi(getActivity());
        uploadFile = new OkHttpUploadFile();
        LocationManager.getInstance().locate(new OnLocationListener() {
            @Override
            public void onLocateSuccess(BDLocation location) {
                address = location.getAddrStr();
                latitude = location.getLatitude();    //获取纬度信息
                longitude = location.getLongitude();    //获取经度信息
//                float radius = location.getRadius();    //获取定位精度，默认值为0.0f
                setLocalStatus();
            }

            @Override
            public void onLocateFail(int errorType) {
//                ToastUtils.toastMsgFunction(getActivity(), "定位失敗");
                localname.setText("定位失败");
            }
        });
        initAdapterData();
        initPop();
        initTopbarAction();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!pubBtn.isEnabled() && App.getInstance().isDisclureCompalte()) {
//            if (App.getInstance().isDisclureIsOk()) {
//                clear();
//            } else {
//                clearPubtnStatus();
//            }
//        }
    }

    public void setLocalStatus() {
        if (isOpenLocal) {
            localmark.setImageResource(R.drawable.disclure_local_select);
            localname.setText(address);
        } else {
            localmark.setImageResource(R.drawable.disclure_local_normal);
            localname.setText("");
        }
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
        pubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
//                ToastUtils.toastMsgFunction(getActivity(), "暂时没开通此功能");
                Observable.just("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String aVoid) {
//                            String title = editWordTitle.getText().toString();
                                int imgeslegth = 0, videoLength = 0;
                                if (imageUploadFileList != null)
                                    imgeslegth = imageUploadFileList.size();
                                if (videoUploadFileList != null)
                                    videoLength = videoUploadFileList.size();
                                String content = editWordContent.getText().toString();
                                if (content == null || TextUtils.isEmpty(content)) {
                                    if (imgeslegth == 1) {
                                        ToastUtils.toastMsgFunction(context, "请输入发布内容");
                                        return;
                                    }
                                }
                                String name = userName.getText().toString().trim();
                                if (TextUtils.isEmpty(name)) {
                                    ToastUtils.toastMsgFunction(context, "请填写发布人");
                                    return;
                                }
                                String telephone = userTelphone.getText().toString().trim();
                                if (TextUtils.isEmpty(telephone)) {
                                    ToastUtils.toastMsgFunction(context, "请填写发布人电话");
                                    return;
                                }
                                if (!isPhone(telephone)) {
                                    ToastUtils.toastMsgFunction(context, "手机号填写有误");
                                    return;
                                }
                                if (imgeslegth == 1 && videoLength == 1) {
                                    createTopical();
                                } else {
                                    postWord((TextView) view);
                                }
                            }
                        });
            }
        });
    }

    public void onComplateUpbtn(TextView fileBtn) {
//        Observable.just("")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String aVoid) {
        int imgeslegth = 0, videoLength = 0;
        if (imageUploadFileList != null)
            imgeslegth = imageUploadFileList.size();
        if (videoUploadFileList != null)
            videoLength = videoUploadFileList.size();
        String content = editWordContent.getText().toString();
        if (content == null || TextUtils.isEmpty(content)) {
            if (imgeslegth == 1) {
                ToastUtils.toastMsgFunction(context, "请输入发布内容");
                return;
            }
        }
        String name = userName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.toastMsgFunction(context, "请填写发布人");
            return;
        }
        String telephone = userTelphone.getText().toString().trim();
        if (TextUtils.isEmpty(telephone)) {
            ToastUtils.toastMsgFunction(context, "请填写发布人电话");
            return;
        }
        if (!isPhone(telephone)) {
            ToastUtils.toastMsgFunction(context, "手机号填写有误");
            return;
        }
        if (imgeslegth == 1 && videoLength == 1) {
            createTopical();
        } else {
            postWord(fileBtn);
        }
//                    }
//                });
    }

    private void initPop() {
        imageSelectedWindow = new BottomSelectedPopupwindow(context, "拍照", "从相册中选取", "拍摄", "从视频库中选取");
//        videoSelectedWindow = new BottomSelectedPopupwindow(context, "拍摄", "从视频库中选取");

        imageSelectedWindow.initDataView();
//        videoSelectedWindow.initDataView();
        imageSelectedWindow.setOnBottomItemClickListener(new BottomSelectedPopupwindow.OnBottomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    goToCameraPhoto();
                } else if (position == 1) {
                    goSelectedPhoto();
                } else if (position == 2) {
                    goToCameraVideo();
                } else if (position == 3) {
                    goSelectedVideo();
                }
            }
        });

//        videoSelectedWindow.setOnBottomItemClickListener(new BottomSelectedPopupwindow.OnBottomItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                if (position == 0) {
//                    goToCameraVideo();
//                } else if (position == 1) {
//                    goSelectedVideo();
//                }
//            }
//        });
    }

    String error = "";

    private void postWord(TextView fileBtn) {
        isPubing = true;
        fileBtn.setText("发送中...");
        fileBtn.setBackgroundColor(0xff8c8c8c);
        fileBtn.setEnabled(false);
        App.getInstance().setDisclureCompalte(false);
        LogcatPushHelper.getInstance(context).start("discluare");
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, ArrayList<UploadFileData>>() {
                    @Override
                    public ArrayList<UploadFileData> call(Object o) {
                        ArrayList<UploadFileData> upList = new ArrayList<>();
                        try {
//                            if (videoUploadFileList != null) {
                            String videoUrl = contentApi.getVideoUpfileUrl();
//                                Log.e("TAG", "video upload Url === " + videoUrl);
//                                for (IUploadFile upF : videoUploadFileList) {
//                                    if (!TextUtils.isEmpty(upF.getFileUrl())) {
//                                        File file = new File(upF.getFileUrl());
//                                        Log.e("TAG", "video == " + upF.getFileUrl());
//                                        if (file.exists()) {
//                                            UploadFileData data = UploadFileData.videoUploadFile(videoUrl, file);
//                                            upList.add(data);
//                                        }
//                                    }
//                                }
//                            }
//                            if (imageUploadFileList != null) {
                            String imageUrl = contentApi.getImaUpfileUrl();
//                                Log.e("TAG", "image upload Url === " + imageUrl);
//                                for (IUploadFile upF : imageUploadFileList) {
//                                    if (!TextUtils.isEmpty(upF.getFileUrl())) {
//                                        File file = new File(upF.getFileUrl());
//                                        Log.e("TAG", "image == " + upF.getFileUrl());
//                                        if (file.exists()) {
//                                            UploadFileData data = UploadFileData.imageUploadFile(imageUrl, file);
//                                            upList.add(data);
//                                        }
//                                    }
//                                }
//                            }
//                            return upList;

                            if (uploadFileList != null) {
                                Log.e("TAG", "image upload Url === " + imageUrl);
                                for (IUploadFile upF : uploadFileList) {
//                                    if (!TextUtils.equals(upF.getFileType(), "_image")) {
//                                        imageUrl = contentCmsApi.getVideoUpfileUrl();
//                                    } else
//                                        imageUrl = contentCmsApi.getImaUpfileUrl();
                                    if (!TextUtils.isEmpty(upF.getFileUrl())) {
                                        File file = new File(upF.getFileUrl());
                                        Log.e("TAG", "image  Url == " + upF.getFileUrl());
                                        if (file.exists()) {
                                            String upUrl = imageUrl;
                                            UploadFileData data = null;
                                            if (!TextUtils.equals(upF.getFileType(), "_image")) {
                                                upUrl = videoUrl;
                                                data = UploadFileData.videoUploadFile(upUrl, file);
                                            } else {
                                                data = UploadFileData.imageUploadFile(upUrl, file);
                                            }
                                            if (data != null)
                                                upList.add(data);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            error = JsonCreater.getErrorMsg(e.toString());
                            Log.e("TAG", "获取上传地址 Exception === " + error);
                            e.printStackTrace();
                        }
                        return upList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<UploadFileData>>() {
                    @Override
                    public void call(ArrayList<UploadFileData> uploadFileData) {
                        if (!(uploadFileData == null || uploadFileData.size() == 0)) {
                            PublishData<DiscleRevelEntry> data = new PublishData<>();
                            MessageData<ArrayList<Long>> msg = new MessageData<>(editWordContent.getText().toString(),
                                    wordColumnIdList);
//                            PublishData<ArrayList<Long>> data = new PublishData<ArrayList<Long>>(editWordTitle.getText().toString(),
//                                    editWordContent.getText().toString());
//                            data.setColumnIdList(wordColumnIdList);
                            DiscleRevelEntry entry = new DiscleRevelEntry();
                            entry.setBody(editWordContent.getText().toString());
                            if (isOpenLocal) {
                                entry.setGeo_address(address);
                                entry.setGeo_latitude(latitude);
                                entry.setGeo_longitude(longitude);
                            }
                            entry.setPhone_number(userTelphone.getText().toString());
                            entry.setReal_name(userName.getText().toString());
                            data.setData(entry);
                            data.setUploadFileDataList(uploadFileData);
                            PublishDataManager.getInstance().post(data);
                            data.setPushAction(new PublishData.OnPublishDataAction<DiscleRevelEntry>() {
                                @Override
                                public boolean onPublishData(ArrayList<UploadFileData> uploadFileResultList, DiscleRevelEntry data) throws ApiException {
                                    if (uploadFileResultList != null &&
                                            !uploadFileResultList.isEmpty()) {
                                        return pubTopic(uploadFileResultList, data);
                                    }
                                    return false;
                                }
                            });
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            Log.e("TAG", "postWord()  获取上传地址为空==" + error);
                            clearPubtnStatus();
                            String errmsg = error + ",获取上传地址失败!";
                            if (App.getInstance().getTopActivity() != null) {
                                showRepostDialog(App.getInstance().getTopActivity(), errmsg);
                            } else {
                                ToastUtils.toastMsgFunction(context, errmsg);
                            }
                        }
                    }
                });
    }

    public void clearPubtnStatus() {
        LogcatPushHelper.getInstance(context).stop(true);
        pubBtn.setBackgroundColor(0xff5193EA);
        pubBtn.setText("上传");
        pubBtn.setEnabled(true);
    }

    private void showRepostDialog(Activity act, String msg) {
        LXDialog dialog = new LXDialog.Builder(act)
                .isEditMode(false)
                .setMessage(msg)
                .isHiddenCancleButton(true)
                .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        dialog.dismiss();
                    }
                })
                .create();
        if (act != null && !act.isFinishing()) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public boolean isPhone(String str) {
        if (!TextUtils.isEmpty(str)) {
            Pattern pattern = Pattern.compile("^1[3|4|5|7|8]{1}[0-9]{9}");
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setPubBtnStatus(boolean enable) {
        if (enable) {
            pubBtn.setEnabled(true);
        } else {
            pubBtn.setEnabled(false);
        }
    }

    public void clear() {
        isPubing = false;
        if (getActivity() == null) return;
        clearPubtnStatus();
        if (App.getInstance().isDisclureIsOk()) {
            editWordContent.setText("");
            initAdpterParams();
        }
    }

    public void createTopical() {
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        try {
                            DiscleRevelEntry entry = new DiscleRevelEntry();
                            entry.setBody(editWordContent.getText().toString());
                            if (isOpenLocal) {
                                entry.setGeo_address(address);
                                entry.setGeo_latitude(latitude);
                                entry.setGeo_longitude(longitude);
                            }
                            entry.setPhone_number(userTelphone.getText().toString());
                            entry.setReal_name(userName.getText().toString());
                            return pubTopic(null, entry);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean falg) {
                        if (!(falg == null || !falg)) {
                            editWordContent.setText("");
                            ToastUtils.toastMsgFunction(context, "爆料发布成功");
                        } else {
                            clearPubtnStatus();
                            Toast.makeText(context, "爆料发布失败!", Toast.LENGTH_SHORT).show();
                        }
                        getActivity().finish();
                    }
                });
    }

    private boolean pubTopic(ArrayList<UploadFileData> uploadFileResultList, DiscleRevelEntry entry) throws ApiException {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/users/revelation";
        JSONObject json = new JSONObject();
        try {
            JSONArray picturesArr = new JSONArray();
            JSONArray videoArrs = new JSONArray();
            if (uploadFileResultList != null && uploadFileResultList.size() > 0) {
                for (int i = 0; i < uploadFileResultList.size(); i++) {
                    UploadFileData model = uploadFileResultList.get(i);
                    if (TextUtils.equals(model.getFileType(), "image")) {
                        picturesArr.put(model.getResult().getServicePath());
                    } else {
                        JSONArray videoArr = new JSONArray();
                        videoArr.put(model.getResult().getServicePath());
                        JSONObject vodeiPa = new JSONObject();
                        vodeiPa.put("paths", videoArr);
                        videoArrs.put(vodeiPa);
                    }
                }
            }
            json.put("body", entry.getBody());
            if (picturesArr != null && picturesArr.length() > 0)
                json.put("pictures", picturesArr);
            if (videoArrs != null && videoArrs.length() > 0)
                json.put("videos", videoArrs);
            json.put("real_name", entry.getReal_name());
            json.put("phone_number", entry.getPhone_number());
            json.put("geo_latitude", entry.getGeo_latitude());
            json.put("geo_longitude", entry.getGeo_longitude());
            String addrsss = entry.getGeo_address();
            if (addrsss == null)
                addrsss = "";
            json.put("geo_address", addrsss);
//            String res=null;
            String res = HttpUtil.execute(url, new HttpParameters(json),
                    App.getInstance().getCurrentToken());
            int errorCode = -1;
            errorCode = StringUtil.getHttpResponseErrorCode(res);
            if (errorCode == 401) {
                String newToken = new TokenHelper().getTokenSync();
                res = HttpUtil.execute(url, new HttpParameters(json),
                        newToken);
            }
//            else if (errorCode == 500 || errorCode == 10000) {
//                JSONObject obj = new JSONObject(res);
//                if (obj != null) {
//                    String error = obj.optString("message");
//                    Log.e("TAG", error);
//                    throw new ApiException(error);
//                }
//            }
            else if (errorCode != -1 && errorCode != 0) {
                JSONObject obj = new JSONObject(res);
                if (obj != null) {
                    String error = obj.optString("message");
                    Log.e("TAG", "pubTopic() servel return errorMsg ===" + error);
                    throw new ApiException(error);
                }
            }
            long id = 0;
            if (errorCode == 0) {
                if (res == null || TextUtils.isEmpty(res)) {
                    String errorMsg = "连接超时";
                    throw new ApiException(errorMsg);
                } else {
                    id = Long.valueOf(res);
                }
            }
            return id > 0 ? true : false;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("TAG", "pubTopic() Exception " + e.toString());
            String errorMsg = JsonCreater.getErrorMsg(e.toString());
            throw new ApiException(errorMsg);
        }
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
                int max = MAX_IMAGE_COUNT - (imageUploadFileList != null ? imageUploadFileList.size() - 1 : 0);
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
//            方法说明：通过Intent调用系统相机拍照，如果本机版本大于等于anroid7.0需要临时授权Uri的访问权限如下：
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
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
        if (isFullImage()) {
            return;
        }
        int index = imageUploadFileList != null ? imageUploadFileList.size() - 1 : 0;
        uploadFileList.add(index, imageFile);
        if (imageUploadFileList != null) {
            if (imageUploadFileList.size() > 0) {
                imageUploadFileList.add(imageUploadFileList.size() - 1, imageFile);
            } else {
                imageUploadFileList.add(imageFile);
            }
        }
        if (isFullImage()) {
            int imageAddIndex = imageUploadFileList.size() - 1;
            boolean is = TextUtils.isEmpty(uploadFileList.get(imageAddIndex).getFileUrl());
            if (is) {
                imageUploadFileList.remove(imageAddIndex);
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
        View tailView = addTailView();
        listView.addFooterView(tailView);
        editWordTitle = (EditText) v.findViewById(R.id.edit_title);
        editWordTitle.setVisibility(View.GONE);
        editWordContent = (EditText) v.findViewById(R.id.edit_content);
        userName = (EditText) tailView.findViewById(R.id.edit_name_);
        userTelphone = (EditText) tailView.findViewById(R.id.edit_tele_txt);
        localmark = (ImageView) tailView.findViewById(R.id.dis_local_mark);
        localname = (TextView) tailView.findViewById(R.id.txt_local_content);

        if (App.getInstance().getUser() != null) {
            Account user = App.getInstance().getUser();
            if (user != null && user.getUser() != null) {
                userName.setText(user.getUser().getNickname());
                userTelphone.setText(user.getUser().getPhone_number());
            }
        }

        adapter.setOnClickEventListener(new FileUploadAdapter.OnClickEventListener() {
            @Override
            public void onDeleteClick(IUploadFile file) {
                if (TextUtils.equals(file.getFileType(), UploadFile.FILE_VIDEO)) {
//                    boolean is = videoUploadFileList.remove(file);
//                    boolean re = uploadFileList.remove(file);
//                    if (is && !isFullVideo()) {
//                        addAddVideoItem(re);
//                    }
//                    Log.e("TAG", "remove video == " + is);
//
                    boolean is = imageUploadFileList.remove(file);
                    boolean re = uploadFileList.remove(file);
                    if (is && !isFullImage()) {
                        addAddImageItem(re);
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

    public void initAdpterParams() {
        if (uploadFileList != null) {
            uploadFileList.clear();
        } else {
            uploadFileList = new ArrayList<>();
        }
        if (imageUploadFileList != null) {
            imageUploadFileList.clear();
        } else {
            imageUploadFileList = new ArrayList<>();
        }
        if (videoUploadFileList != null) {
            videoUploadFileList.clear();
        } else {
            videoUploadFileList = new ArrayList<>();
        }

//        UploadFile uploadFile = new UploadFile(FileUploadAdapter.TYPE_CARTGROY, "上传图片");
//        uploadFileList.add(uploadFile);
        addAddImageItem(false);
//        addAddVideoItem(false);
        uploadFileList.addAll(imageUploadFileList);
//        uploadFile = new UploadFile(FileUploadAdapter.TYPE_CARTGROY, "上传视频");
//        uploadFileList.add(uploadFile);
//        uploadFileList.addAll(videoUploadFileList);

        adapter.update(uploadFileList, false);
    }

    private void initAdapterData() {
        initAdpterParams();

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
                                if (TextUtils.isEmpty(file1.getFileUrl()))
                                    continue;
                                images.add(file1.getFileUrl());
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
                                addImageUrl(file);
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
                        addImageUrl(uploadFile);
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
//                uploadFileList.add(imageUploadFileList.size(), uploadFile);
                uploadFileList.add(uploadFile);
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

    protected void setTopView(FrameLayout topListViewContainer) {
        View top = LayoutInflater.from(context).inflate(R.layout.disclure_common_top_bar, null);
//        View bar = top.findViewById(R.id.top_bar);
//        int height = bar.getLayoutParams().height;
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(act, 40) + height);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(act, 40));
        top.setLayoutParams(lp);
        View backBtn = (View) top.findViewById(R.id.back_btn_view);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        pubBtn = (TextView) top.findViewById(R.id.file_up_btn);
        pubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTopbarAction();
            }
        });
        topListViewContainer.addView(top, lp);
    }

    public View addTailView() {
        final View tail = LayoutInflater.from(context).inflate(R.layout.disclure_bottom_bar, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(act, 40));
        SwitchView openBtn = (SwitchView) tail.findViewById(R.id.open_btn);
        openBtn.setOpened(true);
        openBtn.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                isOpenLocal = true;
                setLocalStatus();
                view.toggleSwitch(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                isOpenLocal = false;
                setLocalStatus();
                view.toggleSwitch(false);
            }
        });
        return tail;
    }

    @Override
    public void MyCallBackEvent(int i, String s, double latitude, double longitude) {
        String localStr = "未获取到相关位置信息";
        if (i == 0) {
            if (s != null && !TextUtils.isEmpty(s)) {
                localStr = s;
            }
        }
        if (isOpenLocal)
            localname.setText(localStr);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapUtils != null) {
            mapUtils.free();
        }
        LocationManager.getInstance().stoplocate();
    }

}