package com.dfsx.ganzcms.app.act;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.GuidePageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by heynag on 2017/7/25
 * 引导页
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "GuideActivity";

    private ViewPager vp;
    private int[] imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合
    private ViewGroup vg;//放置圆点

    //实例化原点View
    private ImageView iv_point;
    private ImageView[] ivPointArray;

    //最后一页的按钮
    private ImageView ib_start;

    Timer timer = new Timer();
    private final long SPLASH_LENGTH = 4000;
    private int recLen = 4;
    Handler handler = new Handler();
    Button timeJumpBtn;
    boolean isStart = false;


    final Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    timeJumpBtn.setText(recLen + "s跳过");
                    if (recLen == 0) {
                        isStart = false;
                        timer.cancel();
                        Intent intent = new Intent(GuideActivity.this, MainTabActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_guide);

        timeJumpBtn = (Button) findViewById(R.id.time_jumps);
        ib_start = (ImageView) findViewById(R.id.guide_ib_start);
        ib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, MainTabActivity.class));
                timer.cancel();
                finish();
            }
        });

        //加载ViewPager
        initViewPager();

        //加载底部圆点
        initPoint();
    }

    /**
     * 加载底部圆点
     */
    private void initPoint() {
//        //这里实例化LinearLayout
//        vg = (ViewGroup) findViewById(R.id.guide_ll_point);
//        //根据ViewPager的item数量实例化数组
//        ivPointArray = new ImageView[viewList.size()];
//        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
//        int size = viewList.size();
//        for (int i = 0; i < size; i++) {
//            iv_point = new ImageView(this);
//            iv_point.setLayoutParams(new ViewGroup.LayoutParams(16, 16));
//            iv_point.setPadding(Util.dp2px(this,30), 0, Util.dp2px(this,50), 0);//left,top,right,bottom
//            ivPointArray[i] = iv_point;
//            //第一个页面需要设置为选中状态，这里采用两张不同的图片
//            if (i == 0) {
//                iv_point.setBackgroundResource(R.drawable.g_on);
//            } else {
//                iv_point.setBackgroundResource(R.drawable.g_nomal);
//            }
//            //将数组中的ImageView加入到ViewGroup
//            vg.addView(ivPointArray[i]);
//        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.guide_ll_point);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(20, 20);
        mParams.setMargins(10, 0, 10, 0);//设置小圆点左右之间的间隔
        ivPointArray = new ImageView[viewList.size()];
        //判断小圆点的数量，从0开始，0表示第一个
        for (int i = 0; i < viewList.size(); i++) {
            ImageView iv_point = new ImageView(this);
            iv_point.setLayoutParams(mParams);
            if (i == 0) {
                iv_point.setBackgroundResource(R.drawable.g_on);
            } else {
                iv_point.setBackgroundResource(R.drawable.g_nomal);
            }
            ivPointArray[i] = iv_point;//得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。
            layout.addView(iv_point);//添加到布局里面显示
        }
    }

    /**
     * 加载图片ViewPager
     */
    private void initViewPager() {
        vp = (ViewPager) findViewById(R.id.guide_vp);
        //实例化图片资源
        imageIdArray = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0; i < len; i++) {
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(imageIdArray[i]);

            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        //View集合初始化好后，设置Adapter
        vp.setAdapter(new GuidePageAdapter(viewList));
        //设置滑动监听
        vp.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动后的监听
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0; i < length; i++) {
            ivPointArray[position].setBackgroundResource(R.drawable.g_on);
            if (position != i) {
                ivPointArray[i].setBackgroundResource(R.drawable.g_nomal);
            }
        }

        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1) {
            ib_start.setVisibility(View.VISIBLE);
            timeJumpBtn.setVisibility(View.VISIBLE);
            if (!isStart) {
                isStart = true;
                timer.schedule(task, 1000, 1000);
            }
        } else {
            ib_start.setVisibility(View.GONE);
        }
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            recLen--;
            Message message = new Message();
            message.what = 1;
            handler2.sendMessage(message);
        }
    };

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }


}
