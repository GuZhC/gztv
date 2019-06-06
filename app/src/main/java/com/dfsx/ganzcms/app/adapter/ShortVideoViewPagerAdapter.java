package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.dfsx.core.img.GlideRoundTransform;
import com.dfsx.ganzcms.app.R;

import java.util.List;

/**
 * @author : GuZhC
 * @date :  2019/6/1 9:46
 * @description : 顶部banner viewPager 适配器
 */
public class ShortVideoViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private final Context context;
    private final List<String> mData;
    private PagerClick pagerClick;
    private ViewPager viewPager;
    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };


    public ShortVideoViewPagerAdapter(List<String> mData, Context context, ViewPager viewPager) {
        this.mData = mData;
        this.context = context;
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }


    @Override
    public int getCount() {
        if (mData.size() < 2) {
            return mData.size();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        //Warning：不要在这里调用removeView
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position %= mData.size();
        final String data = mData.get(position);
        View view = LayoutInflater.from(container.getContext()).inflate( R.layout.item_short_video_top_viewpager, null);
        ImageView img = (ImageView) view.findViewById(R.id.img_short_video_top);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_short_video_top_content);
        tvContent.setText(data);
        Glide.with(context)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559367407524&di=2a7d6a1a12707287e908d61922a637c2&imgtype=0&src=http%3A%2F%2Fpic72.nipic.com%2Ffile%2F20150715%2F9448607_192612583000_2.jpg")
                .transform(new CenterCrop(context), new GlideRoundTransform(context,8))
                .into(img);
        //对ViewPager页号求模取出View列表中要显示的项
        if (position < 0) {
            position = mData.size() + position;
        }
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        final int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pagerClick != null) {
                    pagerClick.onPagerClik(mData.get(finalPosition));
                }
            }
        });
        return view;
    }

    public void startScroll() {
        stopScroll();
        handler.postDelayed(runnable, 5000);    //延时执行
    }


    public void stopScroll() {
//        stopScroll = true;
        handler.removeCallbacks(runnable);
        handler.removeMessages(0);
    }

    public void setPagerClick(PagerClick pagerClick) {
        this.pagerClick = pagerClick;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        stopScroll();
    }

    @Override
    public void onPageSelected(int position) {
//        startScroll();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            //滑动中
            case ViewPager.SCROLL_STATE_DRAGGING:
                stopScroll();
                break;
            //未滑动
            case ViewPager.SCROLL_STATE_IDLE:
                startScroll();
                break;
            default:
                break;
        }
    }

    public interface PagerClick {
        void onPagerClik(String data);
    }
}
