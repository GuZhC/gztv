package com.dfsx.lzcms.liveroom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.dfsx.lzcms.liveroom.view.ChildInterceptTouchEventDrawerLayout;

import java.lang.reflect.Field;

/**
 * Created by liuwb on 2016/12/5.
 */
public class LiveRoomDrawerActivity extends LiveRoomSwitchActivity {

    private ChildInterceptTouchEventDrawerLayout drawerLayout;
    private FrameLayout mainContentView;
    private FrameLayout drawerView;

    protected RelativeLayout topContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setCenterViewContainer(FrameLayout centerViewContainer) {
        super.setCenterViewContainer(centerViewContainer);
        LayoutInflater.from(context).inflate(R.layout.act_drwaer_player, centerViewContainer);

        init();
        setMainContent(mainContentView);
        setMainContent(R.id.content_frame);
        setDrawer(drawerView);
        setDrawer(R.id.right_drawer);
        setTopContainer(topContainer);
    }

    private void init() {
        drawerLayout = (ChildInterceptTouchEventDrawerLayout) findViewById(R.id.drawer_layout);
        mainContentView = (FrameLayout) findViewById(R.id.content_frame);
        drawerView = (FrameLayout) findViewById(R.id.right_drawer);
        topContainer = (RelativeLayout) findViewById(R.id.top_of_drawer_layout);

        LinearLayout.LayoutParams contentParams = new LinearLayout.
                LayoutParams(sw, sh);
        Log.e("TAG", "sh=== " + sh);
        mainContentView.setLayoutParams(contentParams);

        ViewGroup.LayoutParams params = drawerLayout.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(sw, sh);
        } else {
            params.width = sw;
            params.height = sh;
        }
        drawerLayout.setLayoutParams(params);

        drawerLayout.setScrimColor(Color.TRANSPARENT);

        setDrawerRightEdgeSize(context, drawerLayout, 1f);

//        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return handleTouchEvent(event);
//            }
//        });
    }

    @Override
    protected void onTouchDown() {
        super.onTouchDown();
    }

    /**
     * 打开drawer窗口并可控制
     */
    public void openDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * 锁定为关闭状态
     */
    public void lockDrawerClose() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * 锁定为打开状态
     */
    public void lockDrawerOpen() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    public void autoDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public ChildInterceptTouchEventDrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    /**
     * 抽屉滑动范围控制
     *
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage 占全屏的份额0~1
     */
    private void setDrawerRightEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // find ViewDragHelper and set it accessible
//            Field rightDraggerField = drawerLayout.getClass().getDeclaredField("mRightDragger");
            Field rightDraggerField = getDeclaredField(drawerLayout, "mRightDragger");
            rightDraggerField.setAccessible(true);
            ViewDragHelper rightDragger = (ViewDragHelper) rightDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = rightDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(rightDragger);
            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(rightDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            Log.e("NoSuchFieldException", e.getMessage().toString());
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessException", e.getMessage().toString());
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    public Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }

        return null;
    }

    /**
     * 设置主界面的显示内容。不能和setMainContent(int id)同时使用
     *
     * @param v
     */
    protected void setMainContent(FrameLayout v) {

    }

    /**
     * 设置主界面的显示内容。不能和setMainContent(FrameLayout v)同时使用
     *
     * @param id
     */
    protected void setMainContent(int id) {

    }

    /**
     * 设置滑动块的内容，不能和setDrawer(int id)同时使用
     *
     * @param v
     */
    protected void setDrawer(FrameLayout v) {
    }

    /**
     * 设置滑动块的内容，不能和setDrawer(FrameLayout v)同时使用
     *
     * @param id
     */
    protected void setDrawer(int id) {

    }

    /**
     * 设置浮在drawerLayout之上的控件
     *
     * @param topContainer
     */
    protected void setTopContainer(RelativeLayout topContainer) {

        ImageView closeImg = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);
        params.setMargins(0, 20, 20, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeImg.setLayoutParams(params);
        closeImg.setImageResource(R.drawable.icon_close);
        closeImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        topContainer.addView(closeImg, params);

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
