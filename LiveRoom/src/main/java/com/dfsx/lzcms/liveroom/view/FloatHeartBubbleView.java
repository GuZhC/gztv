package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import com.dfsx.lzcms.liveroom.R;

import java.util.LinkedList;
import java.util.Random;

/**
 * 类YY直播的心形冒泡控件。
 * 从控件的底部中间位置冒泡。以一定的速度向上移动，随机左右移动速度,在向上移动中改变其透明度.
 * Created by liuwb on 2016/6/28.
 */
public class FloatHeartBubbleView extends View {

    private Context context;

    public int BKG_COLORS = R.color.white;
    public int HEART_RESOURCE = R.drawable.icon_heart;
    /**
     * 心形背景之间的间距
     */
    public int DXDY = 4;

    private int speedY = 4;

    /**
     * 绘制的数据集
     */
    private LinkedList<FloatHeart> heartList = new LinkedList<FloatHeart>();
    /**
     * 准备绘制的数据集，起暂存作用， 当开启绘制的时候，把这个集合里面的数据全部转移到绘制集里面
     */
    private LinkedList<FloatHeart> prepareHeartList = new LinkedList<FloatHeart>();

    private int screenHeight;
    private int screenWidth;


    /**
     * 标记当前View是否初始化完成了（包括大小分配等情况）
     */
    private boolean isPrepared;
    /**
     * 判断是否是初始化显示
     */
    private boolean isInited = false;

    public FloatHeartBubbleView(Context context) {
        this(context, null);
    }

    public FloatHeartBubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatHeartBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        ViewTreeObserver observer = this.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isInited) {
                    screenWidth = getMeasuredWidth();
                    screenHeight = getMeasuredHeight();
                    isInited = true;
                    isPrepared = true;
                    prepareDrawLogic();
                }
                return true;
            }
        });
    }

    public void addAnHeart(int color) {
        if(isPrepared) {
            heartList.add(createHeart(color));
        } else {
            prepareHeartList.add(createHeart(color));
        }
    }

    public void clear() {
        heartList.clear();
        prepareHeartList.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < heartList.size(); i++) {
            FloatHeart h = heartList.get(i);
            Bitmap contentBitmap = getScaleBitmap(h.bitmap, h.scale);
            canvas.drawBitmap(contentBitmap,
                    h.point.x - contentBitmap.getWidth() / 2,
                    h.point.y - contentBitmap.getHeight(),
                    h.paint);
            contentBitmap.recycle();
        }
        drawOverLogic();
        invalidate();
    }

    private void prepareDrawLogic() {
        if(prepareHeartList.size() > 0 && isPrepared) {
//            Log.e("TAG", "2222222222222222222");
            boolean isAddOk = heartList.addAll(prepareHeartList);
//            Log.e("TAG", "2222222222222222222add == " + isAddOk + "size ==" + heartList.size());
            prepareHeartList.clear();
        }
    }
    private void drawOverLogic() {
        for (int i = 0; i < heartList.size(); i++) {
            FloatHeart h = heartList.get(i);
            h.scale -= 3;
            if (h.scale <= 1) {
                h.scale = 1;
            }
            h.point.y -= speedY;
            h.point.x += getXSpeed();
            h.alpha = 1f / (screenHeight) * h.point.y;
            if (h.scale == 1) {
                h.paint = setBitmapPaint(h.paint, h.alpha);
            }

            if (h.point.x <= 0 || h.point.y <= 0 || h.alpha <= 0) {
                h.bitmap.recycle();
                heartList.remove(i);
            }

        }
    }

    private int getXSpeed() {
        Random random = new Random();
        int speedX = random.nextInt(8);
        int symbolX = random.nextInt(2) >= 1 ? 1 : -1;
        return speedX * symbolX;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        screenWidth = getWidth();
        screenHeight = getHeight();
        isPrepared = true;
        prepareDrawLogic();
    }

    protected Bitmap getScaleBitmap(Bitmap bitmap, int percent) {
        if(bitmap != null && !bitmap.isRecycled()) {
            int w = bitmap.getWidth() / percent;
            int h = bitmap.getHeight() / percent;
            return Bitmap.createScaledBitmap(bitmap, w, h , false);
        }
        return bitmap;
    }

    /**
     * 绘制带有白色环状的图片
     * @param color 中间心形的颜色
     * @return
     */
    protected Bitmap getBitmap(int color) {
        int dxdy = DXDY;
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),
                HEART_RESOURCE);
        int w = sourceBitmap.getWidth();
        int h = sourceBitmap.getHeight();
        int bkgW = w + 2 * dxdy;
        int bkgH = h + 2 * dxdy;

        Bitmap resultMap = Bitmap.createBitmap(bkgW, bkgH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(resultMap);
        Rect bkgRect = new Rect(0, 0, bkgW, bkgH);
        Rect heartRect = new Rect(dxdy,dxdy, bkgW - dxdy, bkgH - dxdy);

        //draw background heart
        Paint bkgPaint = new Paint();
        bkgPaint.setAntiAlias(true);
        bkgPaint.setDither(true);
        ColorFilter filter = new PorterDuffColorFilter(getColor(BKG_COLORS), PorterDuff.Mode.SRC_IN);
        bkgPaint.setColorFilter(filter);
        c.drawBitmap(sourceBitmap, bkgRect, bkgRect, bkgPaint);

        //draw content heart
        Paint heartPaint = new Paint();
        heartPaint.setAntiAlias(true);
        heartPaint.setDither(true);
        ColorFilter hFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        heartPaint.setColorFilter(hFilter);
        c.drawBitmap(sourceBitmap, bkgRect, heartRect, heartPaint);
        sourceBitmap.recycle();
        return resultMap;
    }

    private Paint setBitmapPaint(Paint paint, float alpha) {
        if (paint == null) {
            paint = new Paint();
        }
        int alph = (int) (255 * alpha);
        paint.setAlpha(alph);
        paint.setAntiAlias(true);
        paint.setDither(true);
        return paint;
    }

    private int getColor(int resId) {
        return getResources().getColor(resId);
    }

    private FloatHeart createHeart(int color) {
        FloatHeart heart = new FloatHeart();
        Point p = new Point();
        p.x = screenWidth / 2;
        p.y = screenHeight;
        heart.point = p;
        heart.alpha = 1;
        heart.paint = setBitmapPaint(null, 1f);
        heart.bitmap = getBitmap(color);
        heart.scale = 5;
        return heart;
    }

    class FloatHeart {
        Point point;
        Paint paint;
        int scale;
        float alpha;
        Bitmap bitmap;
    }
}
