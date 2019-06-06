package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.ApiVersionErrorActivity;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.PictureManager;
import com.dfsx.ganzcms.app.model.AdsEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.videoijkplayer.media.MediaItem;
import com.google.gson.Gson;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by heyang  on 2017/1/6.
 */
public class WelcomeAct extends BaseActivity {

    private static final int UPDATE_BACKGROUND_IMAGE = 0x000045;
    private ImageView _backImagView;
    private CountDownTimer durTimer, skipTimer;
    private TextView topTimeTxt;
    private View bottomLogoView;
    private boolean isFullAdware = false;    //  是不是显示全屏广告
    private boolean isCanJump = true;
    private int _playDuration = 3, _skipDuration = 0;
    private String _imgePath, linkUrl;
    private boolean isAdComplate = true;
    private Context context;
    private boolean openAd = false;
    private boolean isDoword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        context = this;
        _backImagView = (ImageView) findViewById(R.id.showAd_back);
        _backImagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(linkUrl)) {
                    return;
                }
                stopTimer();
                openAd = true;
                Bundle bundles = new Bundle();
                bundles.putString(BaseAndroidWebFragment.PARAMS_URL, linkUrl);
                WhiteTopBarActivity.startAct(context, BaseAndroidWebFragment.class.getName(), "", "",
                        bundles);
            }
        });
        bottomLogoView = (View) findViewById(R.id.bottom_layout);
        topTimeTxt = (TextView) findViewById(R.id.top_time_txt);
        topTimeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCanJump && isAdComplate)
                    onJump();
            }
        });
        Log.e("Wel", "======================start");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestUrl();
//                reqBackGroundImage();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openAd) {
            startTime();
            openAd = false;
        }
    }

    public void requestUrl() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    reqpopAdware();
                    reqAdImage();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        dowork();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dowork();
                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }


    public void dowork() {
        if (isDoword)
            return;
        isDoword = true;
//        MediaItem item = new MediaItem();
//        item.setAdware(true);
//        item.setType(2);
//        item.setDuration(5);
//        item.setSkinTime(0);
//        item.setLinkUrl("https://www.baidu.com/");
//        item.setUrl("https://img2.autoimg.cn/admdfs/g27/M09/FE/FA/ChsEnVuSJIGAEt1RAAJwg9atgbE933.jpg");

        MediaItem item = PictureManager.getInstance().getStartAdware();
        if (item != null) {
            if (item.getType() == 1) {
                Intent intent = new Intent(this, StartAdFullVideoActivity.class);
                intent.putExtra("isFull",isFullAdware);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return;
            }
            _playDuration = item.getDuration();
            _skipDuration = item.getSkinTime();
            _imgePath = item.getUrl();
            isFullAdware = item.isFull();
            linkUrl = item.getLinkUrl();
        } else {
            onJump();
            return;
        }
//        playdur = PictureManager.getInstance().getAdDuration();
//        skipDur = PictureManager.getInstance().getAdSkipDuration();
//        isFullAdware = PictureManager.getInstance().isFullAdware();
        if (_skipDuration < 0) {
            _skipDuration = item.getDuration();
            isCanJump = false;
        } else if (_skipDuration > 0) {
            _skipDuration = _skipDuration > _playDuration ? Math.min(_skipDuration, _playDuration) : _skipDuration;
        } else if (_skipDuration == 0) {
            isAdComplate = true;
        }
        if (isFullAdware) {
            bottomLogoView.setVisibility(View.GONE);
        } else {
            bottomLogoView.setVisibility(View.VISIBLE);
        }
        readLocalImage();
    }

    public void onJump() {
        stopTimer();
        gotoMainAct();
    }

    public void readLocalImage() {
        Util.LoadImagGif(this, _imgePath, _backImagView);
        stopTimer();
        startTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public void stopTimer() {
        if (durTimer != null) {
            durTimer.cancel();
            durTimer = null;
        }
        if (skipTimer != null) {
            skipTimer.cancel();
            skipTimer = null;
        }
    }

    public void startTime() {
        topTimeTxt.setVisibility(View.VISIBLE);
        durTimer = new CountDownTimer(_playDuration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _playDuration--;
            }

            @Override
            public void onFinish() {
                gotoMainAct();
            }
        }.start();
        if (_skipDuration > 0) {
            _skipDuration++;
            isAdComplate = false;
            skipTimer = new CountDownTimer(_skipDuration * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (_skipDuration > 0)
                        _skipDuration--;
                    topTimeTxt.setText(UtilHelp.createAdwareTime(context, (int) (millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    isAdComplate = true;
                    topTimeTxt.setText(UtilHelp.createAdwareTime(context, 0));
                }
            }.start();
        } else {
            topTimeTxt.setText(UtilHelp.createAdwareTime(context, 0));
        }
    }

//    public void reqBackGroundImage() {
//        Observable.just("").
//                subscribeOn(Schedulers.io()).
//                observeOn(Schedulers.io()).
//                map(new Func1<String, AdsEntry>() {
//                    @Override
//                    public AdsEntry call(String url) {
//                        reqAdImage();
//                        reqpopAdware();
//                        return null;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<AdsEntry>() {
//                               @Override
//                               public void onCompleted() {
//                               }
//
//                               @Override
//                               public void onError(Throwable e) {
//                                   e.printStackTrace();
//                                   dowork();
//                               }
//
//                               @Override
//                               public void onNext(AdsEntry entry) {
//                                   dowork();
//                               }
//                           }
//                );
//    }

    public void reqAdImage() throws Exception {
        String url = App.getInstance().getmSession().getAdServerUrl() + "/public/app/home-ads";
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            JsonCreater.checkThrowError(jsonObject);
            //过滤返回的{}
            if (jsonObject != null && jsonObject.toString().length() > 2) {
                AdsEntry entry = new Gson().fromJson(jsonObject.toString(), AdsEntry.class);
                if (entry != null) {
                    MediaItem item = covertAdsEntry(jsonObject, entry);
                    PictureManager.getInstance().setStartAdware(item);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void reqpopAdware() {
        String url = App.getInstance().getmSession().getAdServerUrl() + "/public/app/pop-ads";
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            JsonCreater.checkThrowError(jsonObject);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                AdsEntry entry = new Gson().fromJson(jsonObject.toString(), AdsEntry.class);
                if (entry != null) {
                    MediaItem item = covertAdsEntry(jsonObject, entry);
                    PictureManager.getInstance().setPopAdsware(item);
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MediaItem covertAdsEntry(JSONObject json, AdsEntry entry) {
        MediaItem item = new MediaItem();
        item.setId(entry.getId());
        item.setSkinTime(entry.getSkip_time());
        item.setFull(entry.getShow_type() == 0 ? true : false);
        JSONObject ad = json.optJSONObject("ad");
        Gson gs = new Gson();
        if (ad != null && !TextUtils.isEmpty(ad.toString())) {
            try {
                AdsEntry.AdItem adItem = gs.fromJson(ad.toString(), AdsEntry.AdItem.class);
                if (adItem != null) {
                    item.setType(adItem.getType());
                    item.setAdid(adItem.getId());
                    item.setLinkUrl(adItem.getLink_url());
                    String path = adItem.getPicture_url();
                    int dur = adItem.getDuration();
                    item.setDuration(dur);
                    if (adItem.getType() == 1) {
                        UtilHelp.parseVideoVersions(ad, adItem, gs);
                        if (!(adItem.getVideoAdItem() == null ||
                                adItem.getVideoAdItem().getVersions() == null)) {
                            path = adItem.getVideoAdItem().getVersions().getUrl();
                            int videoDuraion = adItem.getVideoAdItem().getDuration();
                            item.setVideoDurtaion(videoDuraion);
                        }
                    }
                    item.setUrl(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    public void gotoMainAct() {
        SharedPreferences preferences = getSharedPreferences("count", 0); // 存在则打开它，否则创建新的Preferences
        int count = preferences.getInt("count", 0); // 取出数据

        if (App.getInstance().getmSession().isForceUpdateApp()) {
            Intent intent = new Intent(App.getInstance(), ApiVersionErrorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().getApplicationContext().startActivity(intent);
        } else {
            /**
             *如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
             */
            if (count == 0) {
                Intent intent1 = new Intent();
                intent1.setClass(this, MainTabActivity.class);
                startActivity(intent1);
            } else {
                Intent intent2 = new Intent();
                intent2.setClass(this, MainTabActivity.class);
                startActivity(intent2);
            }
            Log.e("Wel", "======================end");
            finish();

            //实例化Editor对象
            SharedPreferences.Editor editor = preferences.edit();
            //存入数据
            editor.putInt("count", 1); // 存入数据
            //提交修改
            editor.commit();
        }
    }
}
