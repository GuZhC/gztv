package com.dfsx.lzcms.liveroom;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.ChildInterceptTouchEventDrawerLayout;
import com.dfsx.lzcms.liveroom.view.slidemenu.LiveRoomSlideMenu;
import com.dfsx.lzcms.liveroom.view.slidemenu.SlideMenu;

import java.lang.reflect.Field;

/**
 * Created by liuwb on 2016/6/30.
 */
public class DrawerActivity extends AbsChatRoomActivity {
    protected FragmentActivity context;
    private ChildInterceptTouchEventDrawerLayout drawerLayout;
    private LiveRoomSlideMenu slideMenu;
    private FrameLayout mainContentView;
    private FrameLayout drawerView;

    protected RelativeLayout topContainer;

    protected int screenWidth;
    protected int screenHeight;

    protected ImageView closeImg;

    private boolean isLayoutReady = false;
    private boolean tagSetDrawerClose = false;

    private LinearLayout contentLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_drwaer_player);
        init();
        setMainContent(mainContentView);
        setMainContent(R.id.content_frame);
        setDrawer(drawerView);
        setDrawer(R.id.right_drawer);
        setTopContainer(topContainer);
    }

    private void init() {
        slideMenu = (LiveRoomSlideMenu) findViewById(R.id.drawer_layout);
        mainContentView = (FrameLayout) findViewById(R.id.content_frame);
        drawerView = (FrameLayout) findViewById(R.id.right_drawer);
        contentLinearLayout = (LinearLayout) findViewById(R.id.scroll_container);
        topContainer = (RelativeLayout) findViewById(R.id.top_of_drawer_layout);

        Rect activityRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(activityRect);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = activityRect.height();

        LinearLayout.LayoutParams contentParams = new LinearLayout.
                LayoutParams(screenWidth, screenHeight);
        mainContentView.setLayoutParams(contentParams);

        mainContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        slideMenu.setEdgetSlideWidth(0);
        slideMenu.setPrimaryShadowWidth(0);
        slideMenu.setSlideMode(SlideMenu.MODE_SLIDE_CONTENT);

        slideMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isLayoutReady) {
                    isLayoutReady = true;
                    if (tagSetDrawerClose) {
                        slideMenu.open(false, false);
                    }
                }
            }
        });
        //        drawerLayout.setScrimColor(Color.TRANSPARENT);
        //
        //        setDrawerRightEdgeSize(context, drawerLayout, 1f);
    }

    /**
     * 打开drawer窗口并可控制
     */
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        if (slideMenu != null) {
            slideMenu.close(true);
            rightDragControl(false);
        }
    }

    private void leftDragControl(boolean isLock) {
        int slideDirectionFlag = slideMenu.getSlideDirection();
        if (!isLock) {
            slideDirectionFlag |= SlideMenu.FLAG_DIRECTION_LEFT;
        } else {
            slideDirectionFlag &= ~SlideMenu.FLAG_DIRECTION_LEFT;
        }
        slideMenu.setSlideDirection(slideDirectionFlag);
    }

    public void rightDragControl(boolean isLock) {
        int slideDirectionFlag = slideMenu.getSlideDirection();
        if (!isLock) {
            slideDirectionFlag |= SlideMenu.FLAG_DIRECTION_RIGHT;
        } else {
            slideDirectionFlag &= ~SlideMenu.FLAG_DIRECTION_RIGHT;
        }
        slideMenu.setSlideDirection(slideDirectionFlag);
    }

    /**
     * 锁定为关闭状态
     */
    public void lockDrawerClose() {
        if (isLayoutReady) {
            slideMenu.open(false, false);
            rightDragControl(true);
            leftDragControl(true);
            tagSetDrawerClose = false;
        } else {
            tagSetDrawerClose = true;
        }
    }

    /**
     * 锁定为打开状态
     */
    public void lockDrawerOpen() {
        slideMenu.close(false);
        rightDragControl(true);
        leftDragControl(true);
    }

    public void autoDrawer() {
        rightDragControl(false);
        leftDragControl(true);
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
        closeImg = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);
        params.setMargins(0, PixelUtil.dp2px(context, 10), PixelUtil.dp2px(context, 10), 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeImg.setLayoutParams(params);
        closeImg.setImageResource(R.drawable.icon_close);
        closeImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        topContainer.addView(closeImg, params);

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCloseActImageClick(v);
            }
        });
    }

    protected void OnCloseActImageClick(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 这里主要实现横竖屏播放视频的逻辑
     * 这里控制横竖屏显示的播放器容器的大小。 不知什么原因要强制设置容器的大小
     * 全屏的时候隐藏滑动浮层
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onConfigurationChangedResetContentViewSize(newConfig);
        onConfigurationChangedChangeViewVisible(newConfig);
    }

    protected void onConfigurationChangedResetContentViewSize(Configuration newConfig) {
        Rect activityRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(activityRect);
        int width = activityRect.width();
        int height = activityRect.height();
        setContentViewSize(width, height);
    }

    /**
     * 横竖屏切换的时候设置界面的一部分是否显示
     *
     * @param newConfig
     */
    protected void onConfigurationChangedChangeViewVisible(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            slideMenu.setVisibility(View.GONE);
            closeImg.setVisibility(View.GONE);
        } else {
            slideMenu.setVisibility(View.VISIBLE);
            closeImg.setVisibility(View.VISIBLE);
        }
    }

    protected final void setContentViewSize(int width, int height) {
        ViewGroup.LayoutParams params = mainContentView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(width,
                    height);
        } else {
            params.width = width;
            params.height = height;
        }
        mainContentView.setLayoutParams(params);
    }
}
