package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/6/20.
 */
public class ImageListGroupView extends FrameLayout implements View.OnClickListener {

    public static final int LINE_COLOR = R.color.white;
    public static final int LINE_WIDTH_PX = 2;
    public static final float imageRatio = 0.68966f;
    private Context context;
    private LinearLayout imageContainerView;

    private int widgetLength;
    private Runnable runAfterOnlayout;
    private int lineHeight;

    private int divideLineColorRes = LINE_COLOR;

    private OnItemImageClickListener onItemImageClickListener;

    public ImageListGroupView(Context context) {
        this(context, null);
    }

    public ImageListGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageListGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageListGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        imageContainerView = new LinearLayout(context);
        imageContainerView.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageContainerView.setLayoutParams(containerParams);
        addView(imageContainerView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        widgetLength = right - left;

        if (runAfterOnlayout != null) {
            runAfterOnlayout.run();
            runAfterOnlayout = null;
        }
    }

    private View createDivideLineView() {
        View view = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LINE_WIDTH_PX,
                lineHeight);
        params.weight = 0;
        view.setLayoutParams(params);
        view.setBackgroundResource(divideLineColorRes);
        return view;
    }

    private void addNewImageShowView(int imageId, String imageUrl) {
        RectangleRelativeLayout layout = new RectangleRelativeLayout(context);
        layout.setRatio(imageRatio);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        layout.setLayoutParams(params);
        imageContainerView.addView(layout);

        ImageView imageView = new ImageView(context);
        RelativeLayout.LayoutParams imageP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageP);
        imageView.setId(imageId);
        imageView.setOnClickListener(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideImgManager.getInstance().showImg(context, imageView, imageUrl);
        layout.addView(imageView);
    }

    private void addAllView(String[] imageUrls) {
        if (imageUrls != null) {
            imageContainerView.removeAllViews();
            if (imageUrls.length > 0) {
                lineHeight = (int) (widgetLength / imageUrls.length * imageRatio);
            } else {
                lineHeight = 0;
            }
            for (int i = 0; i < imageUrls.length; i++) {
                addNewImageShowView(i, imageUrls[i]);
                if (i != imageUrls.length - 1) {
                    imageContainerView.addView(createDivideLineView());
                }
            }
        }
    }

    public void setImageList(final String... imageUrls) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addAllView(imageUrls);
            }
        };
        runAfterOnlayout = runnable;
        if (widgetLength > 0) {
            runAfterOnlayout.run();
            runAfterOnlayout = null;
        }
    }

    public void setDivideLineColor(int divideLineColor) {
        this.divideLineColorRes = divideLineColor;
    }

    @Override
    public void onClick(View v) {
        if (onItemImageClickListener != null) {
            int id = v.getId();
            onItemImageClickListener.onItemImageClick(this, id);
        }
    }

    public void setOnItemImageClickListener(OnItemImageClickListener onItemImageClickListener) {
        this.onItemImageClickListener = onItemImageClickListener;
    }

    public interface OnItemImageClickListener {
        void onItemImageClick(View imageListView, int position);
    }
}
