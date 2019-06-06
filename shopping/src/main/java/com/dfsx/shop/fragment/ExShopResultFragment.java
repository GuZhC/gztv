package com.dfsx.shop.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.shop.R;
import com.dfsx.shop.busniness.CreditShopApi;
import com.dfsx.shop.model.ShopEntryInfo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by heyang on 2017/7/25
 */
public class ExShopResultFragment extends Fragment {
    private static final String TAG = "ExShopResultFragment";
    public static final int RESULT_OK = -1;
    private final int NETWORK_BUSY = 54;
    //定时任务
    private RelativeLayout mRelativeLayoutFail;
    private CreditShopApi _creditShopApi;

    private ImageView _shopImage;
    private TextView _shopName;
    private TextView _remianTxt;
    private TextView _takeScore;
    private TextView _remainScore;
    private TextView _buyTimeTxt;
    private TextView _buyDescribeTxt;
    private ImageView _saveBtn;
    TextView[] buttons = new TextView[12];
    LinearLayout _codeContainer;

    private ShopEntryInfo _shopInfo;
    private String _exchange_code;
    private double _totalPrice = 0.0f;
    private String _exchangeDes = "";

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NETWORK_BUSY) {
                if (getActivity() != null) {
                } else {
                    mRelativeLayoutFail.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_ex_shop_result_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _shopInfo = (ShopEntryInfo) bundle.getSerializable("obj");
            _exchange_code = bundle.getString("exchange_code");
            _totalPrice = bundle.getDouble("takeScore");
            _exchangeDes = bundle.getString("exchange_describe");
        }
        _creditShopApi = new CreditShopApi(getContext());
        initView(view);
        iniData();
        showExchangCode(_exchange_code);
//        showExchangCode("122112111112");
    }

    public void initView(View view) {
        _codeContainer = (LinearLayout) view.findViewById(R.id.code_container);
        _shopImage = (ImageView) view.findViewById(R.id.c_shop_image);
        _shopName = (TextView) view.findViewById(R.id.c_shop_name);
        _remianTxt = (TextView) view.findViewById(R.id.c_shop_reamin);
        _takeScore = (TextView) view.findViewById(R.id.c_shop_take_interals);
        _remainScore = (TextView) view.findViewById(R.id.c_shop_remain_interal);
        _buyTimeTxt = (TextView) view.findViewById(R.id.rs_shop_time);
        _buyDescribeTxt = (TextView) view.findViewById(R.id.c_shop_describe);
        _saveBtn = (ImageView) view.findViewById(R.id.rs_shop_save);
        _saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _saveExcodeimg();
            }
        });
        initButtons();
    }

    public void initButtons() {
        for (int i = 0; i < 12; i++) {
            TextView btn = new TextView(getActivity());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Util.dp2px(getActivity(), 30), Util.dp2px(getActivity(), 32));
//            lp.rightMargin = Util.dp2px(getActivity(), 10);
//            btn.setLayoutParams(lp);
            btn.setBackgroundResource(R.drawable.rs_shop_number_bankground);
            btn.setTextSize(15);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            btn.setGravity(Gravity.CENTER);
            btn.setTextColor(getActivity().getResources().getColor(R.color.white));
//            int itemWidth = Util.dp2px(getActivity(),50);
//            if (i > 0)
//                itemWidth = Util.dp2px(getActivity(), 5) +itemWidth ;
//            btn.setPadding(Util.dp2px(getActivity(), 5), 0, Util.dp2px(getActivity(), 5), 0);
            _codeContainer.addView(btn, lp);
            View space = new View(getActivity());
            RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(Util.dp2px(getActivity(), 2), Util.dp2px(getActivity(), 22));
            space.setLayoutParams(lpp);
            _codeContainer.addView(space);
            buttons[i] = btn;
//            _codeContainer.addView(btn);
        }
    }

    public void showExchangCode(String code) {
        if (code == null || TextUtils.isEmpty(code)) return;
        char[] arr = code.toCharArray();
        for (int i = arr.length - 1; i >= 0; i--) {
            if (i < buttons.length)
                buttons[i].setText(arr[i] + "");
        }
    }

    public void iniData() {
        if (_shopInfo != null) {
            String thumb = "";
            if (_shopInfo.getImages() != null &&
                    _shopInfo.getImages().size() > 0)
                thumb = _shopInfo.getImages().get(0).getUrl();
            Util.LoadThumebImage(_shopImage, thumb, null);
            _shopName.setText(_shopInfo.getName());
            String strFormat = String.format("%d", _shopInfo.getStock()) + "剩余";
            _remianTxt.setText(strFormat);
            strFormat = String.format("%0.1f", _totalPrice);
            _takeScore.setText(strFormat);
            _remainScore.setText("");
            _buyDescribeTxt.setText(_exchangeDes);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
        String systemTime = dateFormat.format(new java.util.Date());
        _buyTimeTxt.setText(systemTime);
    }

    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        if (fragment == null) return null;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case 0:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void _saveExcodeimg() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                saveCurrentImage();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                ToastUtils.toastMsgFunction(getActivity(), "没有权限");
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }


    /**
     * 获取和保存当前屏幕的截图
     */
    private void saveCurrentImage() {
        //1.构建Bitmap
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = getActivity().getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        String SavePath = getSDCardPath() + "/dazhou/ScreenImage";

        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath + "/Screen_ex_code.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                ToastUtils.toastMsgFunction(getActivity(), "截屏文件已保存至SDCard/dazhou/ScreenImage/下");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }


}
