package com.dfsx.ganzcms.app.act;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CategoryManager;
import com.dfsx.ganzcms.app.business.LiveFileUploadHelper;
import com.dfsx.ganzcms.app.business.LiveTAGHelper;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.fragment.SelectLiveLabelFragment;
import com.dfsx.ganzcms.app.model.Category;
import com.dfsx.ganzcms.app.model.ShowRoomInfo;
import com.dfsx.ganzcms.app.view.CustomLabelLayout;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.model.RecordRoomIntentData;
import com.dfsx.lzcms.liveroom.util.AndroidUtil;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.LXBottomDialog;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import me.lake.librestreaming.sample.LiveRecordStreamingActivity;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwb on 2016/11/18.
 */
public class PrepareLiveActivity extends BaseActivity {

    public static final String KEY_LIVE_ROOM_INFO = "PrepareLiveActivity_live_room_info";
    public static final String KEY_LIVE_ROOM_CATEGORY_INFO = "PrepareLiveActivity_KEY_LIVE_ROOM_CATEGORY_INFO";
    private static String[] PERMISSIONS_STREAM = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final static String DIC = Environment.getExternalStorageDirectory()
            + "/leshan/save/";

    private static final int PHOTO_AFTER_CAMERA = 7014;
    private static final int PHOTO_AFTER_MEDIA = 7015;
    private static final int SELECT_LIVE_LABEL = 7016;
    private Activity context;
    private ImageView converImage;
    private Button btnStart;
    private EditText editSubject;
    private RadioGroup radioGroupSharePlatform;

    private CheckedTextView checkedPwdTextView;
    private View editLine;

    private EditText editPassword;

    private TextView modifyText;
    private ImageView closeImage;
    private TextView labelHintText;
    private View addLabelView;
    private View createLiveInfoView;
    private CustomLabelLayout labelView;

    private File mCurrentPhotoFile;
    private String coverImagePath = "";

    private String subject;
    private ArrayList<String> labelList;

    /**
     * 记录当前ImageView 封面显示的图片的路径
     * 当前为本地路劲
     */
    private String coverImageViewShowPath = "";

    private Handler handler = new Handler();

    private MyDataManager myDataManager;

    private CategoryManager mCategoryManager;

    private Category liveCategory;

    private String passWord = "";

    //    private LiveSettingInfo settingInfo;

    private Subscription loginSubscription;

    private Subscription shareSubscription;

    private int checkShareId;
    private int oldCheckShareId;

    private InputMethodManager im;
    private Button btnSwitchScreen;

    private boolean isFullScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        liveCategory = (Category) getIntent().getSerializableExtra(KEY_LIVE_ROOM_CATEGORY_INFO);
        setContentView(R.layout.act_prepare_live);
        converImage = (ImageView) findViewById(R.id.cover_img);
        modifyText = (TextView) findViewById(R.id.modify_text);
        createLiveInfoView = findViewById(R.id.create_layout);
        modifyText.setVisibility(View.VISIBLE);
        btnStart = (Button) findViewById(R.id.start_live_btn);
        btnSwitchScreen = (Button) findViewById(R.id.switch_screen_btn);
        editSubject = (EditText) findViewById(R.id.edt_subject);
        editPassword = (EditText) findViewById(R.id.edt_password);
        radioGroupSharePlatform = (RadioGroup) findViewById(R.id.share_platform);
        checkedPwdTextView = (CheckedTextView) findViewById(R.id.text_password_btn);
        editLine = findViewById(R.id.edit_line);
        closeImage = (ImageView) findViewById(R.id.closed_window);
        labelHintText = (TextView) findViewById(R.id.select_label_hint_text);
        addLabelView = findViewById(R.id.add_live_label_image);
        labelView = (CustomLabelLayout) findViewById(R.id.live_room_label_view);
        int textColor = getResources().getColor(R.color.label_text_color);
        int res = R.drawable.shape_label_line_box;
        labelView.changeThemeForTextColor(textColor, textColor, res, res);
        labelView.setCouldClickBody(false);
        labelView.setAddFlagNeedShown(false);
        //重新设置整个信息界面的宽度
        ViewGroup.LayoutParams p = createLiveInfoView.getLayoutParams();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        if (p == null) {
            p = new LinearLayout.
                    LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            p.width = width;
        }
        createLiveInfoView.setLayoutParams(p);

        mCategoryManager = new CategoryManager(context);
        myDataManager = new MyDataManager(context);
        if (isLogin()) {
            //设置默认的标题
            String defaultSubject = App.getInstance().getUser().getUser().getNickname() + "的直播";
            editSubject.setText(defaultSubject);
            editSubject.setSelection(0, defaultSubject.length());
        }
        initAction();
        if (liveCategory == null) {
            getCreateLiveInfo();
        } else {
            initAlertWindow();
        }
        initRegister();

        setUserLogoImageToCoverImage();
    }

    private void initAlertWindow() {
        //不用显示用户协议了
        //        if (isLogin()) {
        //            handler.postDelayed(new Runnable() {
        //                @Override
        //                public void run() {
        //                    LiveAlertWindowManager.
        //                            autoShowWindow(PrepareLiveActivity.this, btnStart, "1111111111111");
        //                }
        //            }, 300);
        //        }
    }

    private void setUserLogoImageToCoverImage() {
        if (isLogin()) {
            String logo = App.getInstance().getUser().
                    getUser().getAvatar_url();
            if (!TextUtils.isEmpty(logo)) {
                showCoverImage(logo);
            }
        }
    }

    private void setSwitchBtnResource(boolean isFull) {
        int res = isFull ?
                R.drawable.bg_text_switch_porit :
                R.drawable.bg_text_switch_full;
        btnSwitchScreen.setBackgroundResource(res);
    }


    private boolean isLogin() {
        return App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;
    }

    private void showCoverImage(String path) {
        GlideImgManager.getInstance().showImg(context,
                converImage, path);
        coverImageViewShowPath = path.replace("file://", "");
        modifyText.setVisibility(View.VISIBLE);
    }

    private void initRegister() {
        loginSubscription = RxBus.getInstance().toObserverable(Intent.class)
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (IntentUtil.ACTION_LOGIN_OK.equals(intent.getAction())) {
                            if (liveCategory == null) {
                                getCreateLiveInfo();
                            }
                            setUserLogoImageToCoverImage();

                            initAlertWindow();
                        }
                    }
                });
        shareSubscription = RxBus.getInstance().toObserverable(ShareCallBackEvent.class)
                .subscribe(new Action1<ShareCallBackEvent>() {
                    @Override
                    public void call(ShareCallBackEvent shareCallBackEvent) {
                        if (shareCallBackEvent != null) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    goRecord();
                                }
                            }, 100);
                        }
                    }
                });
    }

    public void hideInput() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);
                }
            }
        });

    }

    public void showInput() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                im.showSoftInput(editPassword, InputMethodManager.SHOW_FORCED);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        CameraInterface.doDestroy();
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
        if (shareSubscription != null) {
            shareSubscription.unsubscribe();
        }
    }

    private void checkPermission() {
        new TedPermission(context)
                .setPermissions(PERMISSIONS_STREAM)
                .setDeniedMessage("没有可用的权限! 请先在设置中打开权限后再试")
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (isLeagle()) {
                            if (liveCategory == null) {
                                Toast.makeText(context, "没有可用分分类信息", Toast.LENGTH_SHORT).show();
                            } else {
                                createLive(liveCategory.getKey());
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void setActOrientation(boolean isFull) {
        if (!isFull) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void initAction() {
        btnSwitchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullScreen = !isFullScreen;
                setActOrientation(isFullScreen);
                setSwitchBtnResource(isFullScreen);
            }
        });
        addLabelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WhiteTopBarActivity.class);
                intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, SelectLiveLabelFragment.class.getName());
                intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "选择标签");
                intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, "确定");
                startActivityForResult(intent, SELECT_LIVE_LABEL);
            }
        });
        converImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        oldCheckShareId = radioGroupSharePlatform.getCheckedRadioButtonId();
        radioGroupSharePlatform.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                oldCheckShareId = checkShareId;
                checkShareId = checkedId;
            }
        });
        for (int i = 0; i < radioGroupSharePlatform.getChildCount(); i++) {
            View childV = radioGroupSharePlatform.getChildAt(i);
            if (childV instanceof RadioButton) {
                RadioButton childRadioBtn = (RadioButton) childV;
                childRadioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == oldCheckShareId) {
                            radioGroupSharePlatform.clearCheck();
                        } else {
                            oldCheckShareId = radioGroupSharePlatform.getCheckedRadioButtonId();
                        }
                    }
                });
            }
        }

        checkedPwdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedPwdTextView.setChecked(!checkedPwdTextView.isChecked());
                editPwdStatus();
                if (checkedPwdTextView.isChecked()) {
                    editPassword.requestFocus();
                    String password = editPassword.getText().toString();
                    int selectionCount = TextUtils.isEmpty(password) ? 0 : password.length();
                    editPassword.setSelection(selectionCount);
                    editPassword.setFocusable(true);
                    editPassword.setEnabled(true);
                    showInput();
                } else {
                    hideInput();
                }
            }
        });

        editPwdStatus();

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void editPwdStatus() {
        if (checkedPwdTextView.isChecked()) {
            editLine.setVisibility(View.VISIBLE);
        } else {
            editLine.setVisibility(View.GONE);
        }
    }

    /**
     * 检查分享
     */
    private void checkShare() {
        if (radioGroupSharePlatform != null) {
            int checkId = radioGroupSharePlatform.getCheckedRadioButtonId();
            SharePlatform sharePlatform = null;
            String noteText = "";
            if (checkId == R.id.share_wb) {
                noteText = "前往微博分享";
                sharePlatform = SharePlatform.WeiBo;
                noteShareDialog(noteText, sharePlatform);
            } else if (checkId == R.id.share_wx) {
                noteText = "前往微信分享";
                sharePlatform = SharePlatform.Wechat;
                noteShareDialog(noteText, sharePlatform);
            } else if (checkId == R.id.share_friend) {
                noteText = "前往微信朋友圈分享";
                sharePlatform = SharePlatform.Wechat_FRIENDS;
                noteShareDialog(noteText, sharePlatform);
            } else {
                goRecord();
            }
        }
    }

    private void noteShareDialog(String text, final SharePlatform sharePlatform) {
        LXDialog noteDialog = new LXDialog.Builder(context)
                .isEditMode(false)
                .setMessage(text)
                .setNegativeButton("取消", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        dialog.dismiss();
                        goRecord();
                    }
                })
                .setPositiveButton("前往", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        dialog.dismiss();
                        sharePlatfrom(sharePlatform);
                    }
                })
                .create();
        if (context != null &&
                !context.isFinishing()) {
            noteDialog.show();
        }
    }

    /**
     * 分享到各过平台上面
     *
     * @param platform
     */
    private void sharePlatfrom(SharePlatform platform) {
        SharedPreferences sp = context.getSharedPreferences("Live_Create_info", 0);
        long showId = sp.getLong("Live_Create_info_show_id", -1L);
        ShareContent content = new ShareContent();
        content.title = subject;
        String nickName = "";
        try {
            nickName = App.getInstance().getUser().getUser().getNickname();
        } catch (Exception e) {
            nickName = "乐事tv";
        }
        content.disc = StringUtil.getLiveShareContent(nickName,
                subject);
        content.thumb = coverImageViewShowPath;
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getmSession().getBaseMobileWebUrl() + "/live/personal/" + showId;
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }

    private void createLive(final String categoryKey) {
        if (mLoadDialog == null || !mLoadDialog.isShowing()) {
            mLoadDialog = CustomeProgressDialog.show(context, "加载中...");
        }
        if (labelList == null) {
            labelList = new ArrayList<>();
        }
        Observable.from(labelList)
                .observeOn(Schedulers.io())
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String s) {
                        return LiveTAGHelper.getInstance().getLiveTagIdByName(s);
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> longs) {
                        long[] tagIds = null;
                        if (longs != null && longs.size() > 0) {
                            tagIds = new long[longs.size()];
                            for (int i = 0; i < longs.size(); i++) {
                                tagIds[i] = longs.get(i);
                            }
                        }
                        createLiveByNet(categoryKey,
                                tagIds);
                    }
                });

    }

    private void createLiveByNet(String categoryKey, long[] tagIds) {
        passWord = editPassword.getText().toString();
        if (TextUtils.isEmpty(passWord)) {
            passWord = "";
        }
        long curTimestamp = new Date().getTime() / 1000;
        int screenMode = isFullScreen ? 1 : 2;
        myDataManager.createPersonalShow(subject, categoryKey, coverImagePath, subject, passWord,
                false, curTimestamp, screenMode, true, tagIds,
                new DataRequest.DataCallback<ShowRoomInfo>() {
                    @Override
                    public void onSuccess(boolean isAppend, ShowRoomInfo data) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }
                        if (data != null) {
                            LogUtils.e("TAG", "liveRTMPURL === " + data.getRtmpUrl());
                            saveLiveKeyInfo(data.getId(), data.getId(), data.getRtmpUrl());
                            checkShare();
                            //                            goRecord(id, data.getRtmpUrl());
                        } else {
                            Toast.makeText(context, "创建直播失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }
                        e.printStackTrace();
                        String errorJson = e.getMessage();
                        String apiError = null;
                        if (!TextUtils.isEmpty(errorJson)) {
                            try {
                                JSONObject json = JsonCreater.jsonParseString(errorJson);
                                if (json != null) {
                                    apiError = json.optString("message");
                                }
                            } catch (ApiException e1) {
                                e1.printStackTrace();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        String errormsg = TextUtils.isEmpty(apiError) ? "创建直播失败" : apiError;
                        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 保存开启直播的关键信息
     *
     * @param id      这个Id是channelId
     * @param showId
     * @param rtmpUrl
     */
    private void saveLiveKeyInfo(long id, long showId, String rtmpUrl) {
        SharedPreferences sp = context.getSharedPreferences("Live_Create_info", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("Live_Create_info_id", id);
        editor.putLong("Live_Create_info_show_id", showId);
        editor.putString("Live_Create_info_rtmpurl", rtmpUrl);
        editor.commit();
    }

    private void goRecord() {
        SharedPreferences sp = context.getSharedPreferences("Live_Create_info", 0);
        long id = sp.getLong("Live_Create_info_id", -1L);
        String rmtpUrl = sp.getString("Live_Create_info_rtmpurl", "");
        sp.edit().clear().commit();
        if (id != -1 && !TextUtils.isEmpty(rmtpUrl)) {
            goRecord(id, rmtpUrl);
        }
    }

    private void goRecord(long id, String rtmpUrl) {
        Intent intent = new Intent(context, LiveRecordStreamingActivity.class);
        RecordRoomIntentData intentData = new RecordRoomIntentData();
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        intentData.setScreenPortrait(!isFullScreen);
        intentData.setAutoJoinRoomAtOnce(true);
        intentData.setRoomId(id);
        intentData.setRoomOwnerId(App.getInstance().getUser().getUser().getId());
        intentData.setLiveRTMPURL(rtmpUrl);
        intentData.setCoverImagePath(coverImagePath);
        intentData.setSubject(subject);

        startActivity(intent);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 10);
    }

    private void getCreateLiveInfo() {
        IsLoginCheck check = new IsLoginCheck(context);
        boolean isLogin = check.checkLogin();
        if (!isLogin) {
            return;
        }
        mLoadDialog = CustomeProgressDialog.show(context, "加载中...");
        mCategoryManager.getAllCategory(new DataRequest.DataCallback<ArrayList<Category>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<Category> data) {
                if (mLoadDialog != null) {
                    mLoadDialog.dismiss();
                }

                // 先使用第一个默认分类
                if (data != null && !data.isEmpty()) {
                    liveCategory = data.get(0);
                }

                initAlertWindow();
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(context, "获取直播分类信息失败",
                        Toast.LENGTH_SHORT).show();
                if (mLoadDialog != null) {
                    mLoadDialog.dismiss();
                }
            }
        });
    }

    private IsLoginCheck loginCheck;

    private boolean isLeagle() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Toast.makeText(context, "当前系统版本过低，请先升级系统!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (loginCheck == null) {
            loginCheck = new IsLoginCheck(context);
        }
        if (!loginCheck.checkLogin()) {
            return false;
        }
        subject = editSubject.getText().toString();
        if (TextUtils.isEmpty(subject)) {
            Toast.makeText(context, "请输入房间名字", Toast.LENGTH_SHORT).show();
            return false;
        }
        labelList = labelView.getAllBody();
        //        if (labelList == null || labelList.isEmpty()) {
        //            Toast.makeText(context, "请添加房间标签", Toast.LENGTH_SHORT).show();
        //            return false;
        //        }
        if (TextUtils.isEmpty(coverImageViewShowPath)) {
            showUploadCoverImageDialog();
            return false;
        }
        return true;
    }

    private void showUploadCoverImageDialog() {
        LXDialog dialog = new LXDialog.Builder(context)
                .setMessage("上传一个封面效果更好哦！")
                .setNegativeButton("取消", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        if (liveCategory == null) {
                            Toast.makeText(context, "没有可用的分类信息", Toast.LENGTH_SHORT).show();
                        } else {
                            createLive(liveCategory.getKey());
                        }
                    }
                })
                .setPositiveButton("上传", new LXDialog.Builder.LXDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        selectedImage();
                    }
                })
                .create();
        dialog.show();
    }

    private LXBottomDialog bottomDialog;

    private void selectedImage() {
        Intent intent = new Intent(context, ImageFragmentActivity.class);
        intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
        startActivityForResult(intent, PHOTO_AFTER_MEDIA);
        //        if (bottomDialog == null) {
        //            bottomDialog = new LXBottomDialog(context,
        //                    new String[]{"从相册选择"/*, "拍照"*/}, new LXBottomDialog.onPositionClickListener() {
        //                @Override
        //                public void onClick(int position) {
        //                    if (position == 0) {
        //                        Intent intent = new Intent(context, ImageFragmentActivity.class);
        //                        intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
        //                        startActivityForResult(intent, PHOTO_AFTER_MEDIA);
        //                    } else {
        //                        getPhotoFromCamra();
        //                    }
        //                }
        //            });
        //        }
        //        bottomDialog.show();
    }


    private void getPhotoFromCamra() {
        if (AndroidUtil.isSDCardExistAndNotFull()) {
            try {
                Intent getImageByCamera = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                if (!new File(DIC).exists()) {
                    new File(DIC).mkdirs();
                }
                mCurrentPhotoFile = new File(DIC, getPhotoFileName());
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mCurrentPhotoFile));
                startActivityForResult(getImageByCamera,
                        PHOTO_AFTER_CAMERA);
            } catch (Exception e) {
                Toast.makeText(context, "系统相机不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "存储空间不足", Toast.LENGTH_SHORT).show();

        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO_AFTER_CAMERA) {
                String filePath = mCurrentPhotoFile.getPath();
                uploadImage(filePath);
                Log.e("TAG", "filePath == " + filePath);
            } else if (PHOTO_AFTER_MEDIA == requestCode) {
                ArrayList<MediaModel> selectedList = data.
                        getParcelableArrayListExtra("list");
                String filePath = selectedList.get(0).url;
                uploadImage(filePath);
                Log.e("TAG", "filePath == " + filePath);
            } else if (requestCode == SELECT_LIVE_LABEL) {
                ArrayList<String> labels = data.getStringArrayListExtra(SelectLiveLabelFragment.KEY_INTENT_RETURN_LABEL_LIST);
                addLabelToLabelList(labels);
                if (labelList != null && !labelList.isEmpty()) {
                    labelView.setVisibility(View.VISIBLE);
                    labelView.destroy();
                    labelView.addAllBody(labelList.toArray(new String[0]));
                    labelHintText.setVisibility(View.GONE);
                } else {
                    labelView.setVisibility(View.GONE);
                    labelHintText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void addLabelToLabelList(ArrayList<String> labels) {
        if (labels != null) {
            if (labelList != null) {
                for (String label : labels) {
                    if (!isExistLabel(label)) {
                        labelList.add(label);
                    }
                }
            } else {
                labelList = labels;
            }
        }
    }

    private boolean isExistLabel(String label) {
        if (labelList != null) {
            for (String item : labelList) {
                if (TextUtils.equals(label, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private LiveFileUploadHelper uploadHelper;
    private CustomeProgressDialog mLoadDialog;

    private void uploadImage(final String upPath) {
        if (uploadHelper == null) {
            uploadHelper = new LiveFileUploadHelper(context);
        }
        mLoadDialog = CustomeProgressDialog.show(context, "上传中...");
        uploadHelper.uploadFile(upPath, new Action1<String>() {
            @Override
            public void call(String s) {
                if (mLoadDialog != null) {
                    mLoadDialog.dismiss();
                }
                String path = null;
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject res = new JSONObject(s);
                        if (res.optInt("isOK") > 0) {
                            String name = res.optString("name");
                            String dir = res.optString("relativepath");
                            path = coverImagePath = dir + name;
                            String showPath = upPath.startsWith("file://") ?
                                    upPath : "file://" + upPath;
                            showCoverImage(showPath);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(context, "上传图片失败", Toast.LENGTH_SHORT).show();
                }
                Log.e("TAG", "s == " + s);
            }
        });
    }
}
