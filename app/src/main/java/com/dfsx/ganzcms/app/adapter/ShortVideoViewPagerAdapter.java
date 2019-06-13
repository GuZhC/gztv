package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.dfsx.ganzcms.app.model.ContentCmsEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : GuZhC
 * @date :  2019/6/1 9:46
 * @description : 顶部banner viewPager 适配器
 */
public class ShortVideoViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private final Context context;
    private final List<ContentCmsEntry> mData = new ArrayList<>();
    private PagerClick pagerClick;
    private ViewPager viewPager;
    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getCount() > viewPager.getCurrentItem()){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    };


    public ShortVideoViewPagerAdapter(List<ContentCmsEntry> mData, Context context, ViewPager viewPager) {
        for (int i = 0; i < mData.size(); i++) {
            if (i == 5) break;
            this.mData.add(mData.get(i));
        }
        this.context = context;
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public int getCount() {
        if (mData.size() < 2) {
            return 1;
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
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_short_video_top_viewpager, null);
        ImageView img = (ImageView) view.findViewById(R.id.img_short_video_top);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_short_video_top_content);
        if (mData.size() != 0) {
            position %= mData.size();
            //对ViewPager页号求模取出View列表中要显示的项
            if (position < 0) {
                position = mData.size() + position;
            }
            final String title = mData.get(position).getTitle();
            tvContent.setText(title);
            Glide.with(context)
                    .load(mData.get(position).getThumbnail_urls().get(0))
                    .transform(new CenterCrop(context), new GlideRoundTransform(context, 8))
                    .into(img);
            final int finalPosition = position;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pagerClick != null) {
                        pagerClick.onPagerClik(mData.get(finalPosition).getId());
                    }
                }
            });
        }
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
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
        void onPagerClik(Long id);
    }
}
