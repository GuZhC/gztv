package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 弹幕的控件.传入的数据先进入显示数据队列里面，在根据显示的状态依次显示队列里面的数据
 * Created by liuwb on 2016/5/10.
 */
public class BarrageBufferView extends FrameLayout {

    private static final int MSG_HON_MODE = 0;
    private static final int MSG_CENTER_MODE = 1;

    public enum showMode {
        allScreen,
        topOfScreen,
        bottomOfScreen
    }

    /**
     * 默认的文字画笔
     */
    private TextPaint txtPaint;
    private int screenHeight;
    private int screenWidth;
    private int x, y = 0;
    private int minSpace = 10;//文字左右之间的最小间距
    private int textPaddingTop = 10;//文字上下之间的间距
    private LinkedList<Point> pos = new LinkedList();
    private LinkedList<String> txts = new LinkedList();
    private LinkedList<TextPaint> txtPaints = new LinkedList();

    /**
     * 水平移动弹幕数据
     */
    private LinkedList<BarrageItem> barrageList = new LinkedList<BarrageItem>();

    /**
     * 中间位置的弹幕数据
     */
    private LinkedList<BarrageItem> barrageCenterList = new LinkedList<BarrageItem>();

    /**
     * 入口列表
     */
    private LinkedList<BarrageItem> comeList = new LinkedList<BarrageItem>();

    /**
     * 水平移动传入数据
     */
    private BlockingQueue<BarrageItem> barrageData = new LinkedBlockingQueue<BarrageItem>();
    /**
     * 中间显示的传入数据
     */
    private BlockingQueue<BarrageItem> barrageCenterData = new LinkedBlockingQueue<BarrageItem>();

    /**
     * 存储即将显示的水平移动数据的的位置
     */
    private BlockingQueue<Integer> barrageYPos = new LinkedBlockingQueue<Integer>();

    /**
     * 存储即将显示的中间的位置
     */
    private BlockingQueue<Integer> barrageCenterYPos = new LinkedBlockingQueue<Integer>();

    /**
     * 各种显示模式下的Y坐标的集合
     */
    private LinkedList<Integer> fullScreenHeights = new LinkedList<>();
    private LinkedList<Integer> topHeights = new LinkedList<>();
    private LinkedList<Integer> bottomHeights = new LinkedList<>();

    private int speed = 4;//全局的移动速度
    private int txtSize = 50;//默认的文字大小，单位为dp
    private showMode mShowMode = showMode.topOfScreen;
    private Random random = new Random();
    private int showSeconds = 3;
    /**
     * 判断是否是初始化显示
     */
    private boolean isInited = false;
    private Thread getBarrageThread;
    private GetCenterBarrageThread getCenterBarrageThread;
    /**
     * 控制水平的的线程开关
     */
    private boolean isRunThread = true;
    /**
     * 控制中间的显示线程开关
     */
    private boolean isRunCenterThread = false;

    public BarrageBufferView(Context context) {
        this(context, null);
    }

    public BarrageBufferView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        txtPaint.setTextSize(txtSize);
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        screenWidth = rect.width();
        screenHeight = rect.height();
        ViewTreeObserver observer = this.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isInited) {
                    screenWidth = getMeasuredWidth();
                    screenHeight = getMeasuredHeight();
                    initAllModeYPositionList();
                    if (getBarrageThread == null) {
                        getBarrageThread = new Thread(new GetItemTask());
                    }
                    getBarrageThread.start();
                    isInited = true;
                }
                return true;
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("TAG", "11111111111111111111");
                return false;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        screenWidth = getWidth();
        screenHeight = getHeight();
        initAllModeYPositionList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void myDraw(Canvas canvas) {
        for (int i = 0; i < barrageList.size(); i++) {
            canvas.drawText(barrageList.get(i).text,
                    barrageList.get(i).point.x,
                    barrageList.get(i).point.y, barrageList.get(i).paint);
        }
        for (int i = 0; i < barrageCenterList.size(); i++) {
            canvas.drawText(barrageCenterList.get(i).text,
                    barrageCenterList.get(i).point.x,
                    barrageCenterList.get(i).point.y,
                    barrageCenterList.get(i).paint);
        }
        logic();
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        myDraw(canvas);
    }

    private void logic() {
        for (int i = 0; i < barrageList.size(); i++) {
            barrageList.get(i).point.x -= speed;
            if (barrageList.get(i).point.x < -txtPaint.measureText(barrageList.get(i).text)) {
                barrageList.remove(i);
            }
        }

        for (int i = 0; i < comeList.size(); i++) {
            comeList.get(i).point.x -= speed;
            if (comeList.get(i).point.x < screenWidth - txtPaint.measureText(comeList.get(i).text) - minSpace) {
                LinkedList<Integer> heightList = getHeightListByMode(mShowMode);
                if (!heightList.contains(comeList.get(i).point.y)) {
                    heightList.add(comeList.get(i).point.y);
                    //设置数据位置数据唤醒位置的阻塞
                    if (barrageYPos.isEmpty()) {
                        getYPosition();
                    }
                }
                comeList.remove(i);
            }
        }

        for (int i = 0; i < barrageCenterList.size(); i++) {
            barrageCenterList.get(i).endTime = System.currentTimeMillis();
            if (barrageCenterList.get(i).endTime - barrageCenterList.get(i).startTime >= 1000 * showSeconds) {
                LinkedList<Integer> heightList = getHeightListByMode(showMode.bottomOfScreen);
                BarrageItem item = barrageCenterList.remove(i);
                if (!heightList.contains(item.point.y)) {
                    boolean isEmptyed = heightList.isEmpty();
                    heightList.add(item.point.y);
                    //设置数据位置数据唤醒位置的阻塞
                    if (isEmptyed) {
                        getYPosition(showMode.bottomOfScreen, barrageCenterYPos);
                    }
                }

            }
        }

    }

    public void setTextSize(int txtSize) {
        this.txtSize = txtSize;
        txtPaint.setTextSize(txtSize);
    }

    public void setTextColor(int color) {
        txtPaint.setColor(color);
    }

    /**
     * 设置全局的速度
     *
     * @param speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 设置中间数据的显示的时间
     *
     * @param i
     */
    public void setShowSceonds(int i) {
        this.showSeconds = i;
    }

    /**
     * 设置展示模式
     *
     * @param mode
     */
    public void setShowMode(showMode mode) {
        mShowMode = mode;
        initComeHeightList(mode, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 获取当前的模式
     *
     * @return
     */
    public showMode getShowMode() {
        return mShowMode;
    }

    private LinkedList<Integer> getHeightListByMode(showMode mode) {
        LinkedList<Integer> comeHeights = null;
        switch (mode) {
            case allScreen:
                comeHeights = fullScreenHeights;
                break;
            case topOfScreen:
                comeHeights = topHeights;
                break;
            case bottomOfScreen:
                comeHeights = bottomHeights;
                break;
        }
        return comeHeights;
    }

    private void initComeHeightList(showMode mode) {
        initComeHeightList(mode, false);
    }

    /**
     * 初始化显示的Y坐标的集合
     *
     * @param mode
     * @param isReset
     */
    private void initComeHeightList(showMode mode, boolean isReset) {
        LinkedList<Integer> comeHeights = getHeightListByMode(mode);
        if (comeHeights != null && (comeHeights.isEmpty() || isReset)) {
            comeHeights.clear();
            int line = 0;
            switch (mode) {
                case allScreen:
                    line = (screenHeight - textPaddingTop) / (txtSize + textPaddingTop);
                    for (int i = 0; i < line; i++) {
                        comeHeights.add(i * (txtSize + textPaddingTop) + textPaddingTop + txtSize);
                    }
                    break;
                case topOfScreen:
                    line = (screenHeight / 2) / (txtSize + textPaddingTop);
                    for (int i = 0; i < line; i++) {
                        comeHeights.add(i * (txtSize + textPaddingTop) + textPaddingTop + txtSize);
                    }
                    break;
                case bottomOfScreen:
                    line = (screenHeight - screenHeight / 2 - textPaddingTop) / (txtSize + textPaddingTop);
                    for (int i = 0; i < line; i++) {
                        comeHeights.add(i * (txtSize + textPaddingTop) + screenHeight / 2 + textPaddingTop + txtSize);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                speed = 0;
//                break;
//            case MotionEvent.ACTION_UP:
//                speed = 4;
//                break;
//        }
        return false;
    }

    public void sendBarrage(String txt) {
        sendBarrage(new BarrageItem(txt));
    }

    public void sendBarrage(BarrageItem item) {
        try {
            barrageData.put(item);
            if (getBarrageThread == null && !isRunThread) {
                isRunThread = true;
                getBarrageThread = new Thread(new GetItemTask());
                getBarrageThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "put Exception");
        }
    }


    public void sendBarrageOnCenter(String txt) {
        sendBarrageOnCenter(new BarrageItem(txt));
    }

    public void sendBarrageOnCenter(BarrageItem item) {
        try {
            int xpos = (int) (screenWidth - txtPaint.measureText(item.text)) / 2;
            int ypos = 0;
            Point p = new Point(xpos, ypos);
            item.point = p;
            barrageCenterData.put(item);
            if (getCenterBarrageThread == null && !isRunCenterThread) {
                isRunCenterThread = true;
                getCenterBarrageThread = new GetCenterBarrageThread();
                getCenterBarrageThread.setIsOpen(true);
                getCenterBarrageThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "barrageCenterData put error");
        }
    }

    /**
     * 清理屏幕上显示的数据。
     * （清除数据集以及关闭线程）
     */
    public void clearScreen() {
        try {
            isRunThread = false;
            if (getBarrageThread != null) {
                getBarrageThread.interrupt();
                getBarrageThread = null;
            }
            barrageList.clear();
            barrageData.clear();
            comeList.clear();
            barrageYPos.clear();


            isRunCenterThread = false;
            if (getCenterBarrageThread != null) {
                getCenterBarrageThread.interrupt();
                getCenterBarrageThread = null;
            }
            barrageCenterYPos.clear();
            barrageCenterList.clear();
            barrageCenterData.clear();

            initAllModeYPositionList();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", " clear error");
        }
    }

    /**
     * 关闭弹幕的时候调用
     */
    public void destory() {
        clearScreen();
    }

    private void initAllModeYPositionList() {
        initComeHeightList(showMode.allScreen);
        initComeHeightList(showMode.topOfScreen);
        initComeHeightList(showMode.bottomOfScreen);
    }

    class GetItemTask implements Runnable {

        @Override
        public void run() {
            while (isRunThread) {
                try {
                    BarrageItem item = barrageData.take();
                    getYPosition();

                    int comeYpos = barrageYPos.take();
                    if (item != null) {
                        Point p = new Point(screenWidth, comeYpos);
                        item.point = p;
                        handler.obtainMessage(MSG_HON_MODE, item).sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TAG", "Exception");
                }
            }
        }

    }

    private void getYPosition() {
        getYPosition(mShowMode, barrageYPos);
    }

    /**
     * 从行容器里面随机取得坐标放在 行队列里面备用
     *
     * @param mode
     * @param posQueue
     */
    private void getYPosition(showMode mode, BlockingQueue posQueue) {
        int count = 0;
        int ypos = -1;
        LinkedList<Integer> comeHeights = getHeightListByMode(mode);
        Log.e("TAG", "size == " + comeHeights.size());
        if (comeHeights.size() > 0) {
            count = random.nextInt(comeHeights.size());
            ypos = comeHeights.remove(count);
            try {
                posQueue.put(ypos);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("TAG", "put y error");
            }
        }
    }

    class centerPoint {
        int x, y;
        long startTime, endTime;

        public centerPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public class BarrageItem implements Serializable {
        String text;
        TextPaint paint;
        Point point;
        long startTime, endTime;

        public BarrageItem(String text) {
            this.text = text;
            this.paint = new TextPaint(txtPaint);
        }

        public BarrageItem(String text, int color, int size) {
            this.text = text;
            paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            paint.setTextSize(size);
            paint.setColor(color);
        }

        public BarrageItem cloneSelf() {
            BarrageItem newItem = new BarrageItem(text);
            Point p = new Point(point.x, point.y);
            newItem.point = p;
            newItem.paint = paint;
            newItem.startTime = startTime;
            newItem.endTime = endTime;
            return newItem;
        }
    }

    class GetCenterBarrageThread extends Thread {
        private boolean isOpen = false;

        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

        @Override
        public void run() {
            super.run();
            while (isRunCenterThread) {
                try {
                    BarrageItem centerData = barrageCenterData.take();
                    getYPosition(showMode.bottomOfScreen, barrageCenterYPos);
                    int ypos = barrageCenterYPos.take();
                    if (centerData.point != null) {
                        centerData.point.y = ypos;
                    }
                    handler.obtainMessage(MSG_CENTER_MODE, centerData).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HON_MODE) {
                BarrageItem barrageItem = (BarrageItem) msg.obj;
                if (barrageItem != null) {
                    barrageList.add(barrageItem);
                    comeList.add(barrageItem.cloneSelf());
                }
            } else if (msg.what == MSG_CENTER_MODE) {
                BarrageItem barrageItem = (BarrageItem) msg.obj;
                if (barrageItem != null) {
                    barrageItem.startTime = System.currentTimeMillis();
                    barrageCenterList.add(barrageItem);
                }
            }
        }
    };
}
