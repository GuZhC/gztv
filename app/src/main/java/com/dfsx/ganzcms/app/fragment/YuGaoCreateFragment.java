package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.qqtheme.framework.WheelPicker.picker.LinkagePicker;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
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
import com.dfsx.ganzcms.app.model.Category;
import com.dfsx.ganzcms.app.model.ShowRoomInfo;
import com.dfsx.ganzcms.app.view.CustomLabelLayout;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwb on 2017/7/12.
 */
public class YuGaoCreateFragment extends Fragment {
    private static final int PHOTO_AFTER_MEDIA = 7015;
    private static final int SELECT_LIVE_LABEL = 7016;

    private Context context;
    private ImageButton liveCoverImage;
    private TextView uploadImageImage;

    private EditText editLiveTitle;
    private TextView liveTimeText;

    private Button btnSaveYuGao;

    private String coverImageViewShowPath;

    private String coverImagePath = "";

    private IsLoginCheck loginCheck;

    private LiveFileUploadHelper uploadHelper;
    private CustomeProgressDialog mLoadDialog;

    private CategoryManager mCategoryManager;

    private Category liveCategory;

    private Subscription loginSubscription;
    private long selectedTime;

    private String title;

    private MyDataManager myDataManager;

    private CheckBox checkLiveScreenModeView;
    private View addLabelView;
    private CustomLabelLayout labelView;
    private TextView labelHintText;

    private ArrayList<String> labelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.frag_create_yugao, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDataManager = new MyDataManager(context);
        mCategoryManager = new CategoryManager(context);
        liveCoverImage = (ImageButton) view.findViewById(R.id.cover_img);
        uploadImageImage = (TextView) view.findViewById(R.id.text_right_selected);
        editLiveTitle = (EditText) view.findViewById(R.id.edit_live_title);
        liveTimeText = (TextView) view.findViewById(R.id.live_start_time);
        btnSaveYuGao = (Button) view.findViewById(R.id.btn_save_yugao);
        labelHintText = (TextView) view.findViewById(R.id.select_label_hint_text);
        addLabelView = view.findViewById(R.id.add_live_label_image);
        labelView = (CustomLabelLayout) view.findViewById(R.id.live_room_label_view);
        checkLiveScreenModeView = (CheckBox) view.findViewById(R.id.live_screen_mode_checked);

        int textColor = getResources().getColor(R.color.label_text_color);
        int res = R.drawable.shape_label_line_box;
        labelView.changeThemeForTextColor(textColor, textColor, res, res);
        labelView.setCouldClickBody(false);
        labelView.setAddFlagNeedShown(false);

        uploadImageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage();
            }
        });

        liveTimeText.setText(StringUtil.getHourMinutesTimeText(new Date().getTime()));
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
        liveTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> dayList = new ArrayList<String>() {
                    {
                        add("今天");
                        add("明天");
                        add("后天");
                    }
                };
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis() + 1 * 60 * 1000);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                ArrayList<ArrayList<String>> dayHourList = new ArrayList<>();
                ArrayList<ArrayList<ArrayList<String>>> dayHourMinuteList = new ArrayList<>();
                for (int d = 0; d < dayList.size(); d++) {
                    int h = d == 0 ? hour : 0;
                    ArrayList<String> hours = new ArrayList<String>();
                    ArrayList<ArrayList<String>> hourMinutes = new ArrayList<ArrayList<String>>();
                    for (; h < 24; h++) {
                        String hourText = h < 10 ? "0" + h : "" + h;
                        hours.add(hourText);
                        int m = h == hour ? minutes : 0;
                        ArrayList<String> minuteList = new ArrayList<String>();
                        for (; m < 60; m++) {
                            String minText = m < 10 ? "0" + m : "" + m;
                            minuteList.add(minText);
                        }
                        hourMinutes.add(minuteList);
                    }
                    dayHourList.add(hours);
                    dayHourMinuteList.add(hourMinutes);
                }


                LinkagePicker picker = new LinkagePicker(getActivity(),
                        dayList, dayHourList, dayHourMinuteList);
                picker.setOnLinkageListener(new LinkagePicker.OnLinkageListener() {
                    @Override
                    public void onPicked(String first, String second, String third) {
                        int day = 0;
                        if (TextUtils.equals(first, "明天")) {
                            day = 1;
                        } else if (TextUtils.equals(first, "后天")) {
                            day = 2;
                        }
                        String text = (day != 0 ? first + " " : "") + second + ":" + third;
                        liveTimeText.setText(text);
                        int hour = Integer.valueOf(second);
                        int min = Integer.valueOf(third);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + day);
                        c.set(Calendar.HOUR_OF_DAY, hour);
                        c.set(Calendar.MINUTE, min);
                        c.set(Calendar.SECOND, 0);
                        selectedTime = c.getTimeInMillis();
                        Log.e("TAG", "set time == " + StringUtil.getTimeText(selectedTime / 1000));
                    }
                });
                picker.setOffset(1);
                picker.setTextSize(14);
                picker.show();
            }
        });
        btnSaveYuGao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveCategory != null) {
                    if (isLeagle()) {
                        createLive(liveCategory.getKey());
                    }
                } else {
                    Toast.makeText(context, "没获取到直播分类", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUserLogoImageToCoverImage();

        getCreateLiveInfo();

        initRegister();
    }

    private boolean isLeagle() {
        title = editLiveTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(context, "请输入直播标题", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedTime == 0) {
            Toast.makeText(context, "请设置开播时间", Toast.LENGTH_SHORT).show();
            return false;
        }

        labelList = labelView.getAllBody();
        //        if (labelList == null || labelList.isEmpty()) {
        //            Toast.makeText(context, "请添加房间标签", Toast.LENGTH_SHORT).show();
        //            return false;
        //        }
        return true;
    }

    private void selectedImage() {
        Intent intent = new Intent(context, ImageFragmentActivity.class);
        intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
        startActivityForResult(intent, PHOTO_AFTER_MEDIA);
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
                        createLiveYugao(categoryKey,
                                tagIds);
                    }
                });
    }

    private void createLiveYugao(String categoryKey, long[] tagIds) {
        long curTimestamp = selectedTime / 1000;
        int screenMode = checkLiveScreenModeView.isChecked() ? 1 : 2;
        myDataManager.createPersonalShow(title, categoryKey, coverImagePath, title, "", true,
                curTimestamp, screenMode, true, tagIds,
                new DataRequest.DataCallback<ShowRoomInfo>() {
                    @Override
                    public void onSuccess(boolean isAppend, ShowRoomInfo data) {
                        if (mLoadDialog != null) {
                            mLoadDialog.dismiss();
                        }
                        if (data != null) {
                            LogUtils.e("TAG", "liveRTMPURL === " + data.getRtmpUrl());
                            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            //                            goRecord(id, data.getRtmpUrl());
                        } else {
                            Toast.makeText(context, "创建预告失败", Toast.LENGTH_SHORT).show();
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
                        String errormsg = TextUtils.isEmpty(apiError) ? "保存预告失败" : apiError;
                        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
                    }
                });
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
                        }
                    }
                });
    }

    private boolean isLogin() {
        return App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;
    }

    private void showCoverImage(String path) {
        GlideImgManager.getInstance().showImg(context,
                liveCoverImage, path);
        coverImageViewShowPath = path.replace("file://", "");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (PHOTO_AFTER_MEDIA == requestCode) {
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
