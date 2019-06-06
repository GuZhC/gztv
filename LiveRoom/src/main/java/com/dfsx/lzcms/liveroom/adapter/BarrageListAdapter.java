package com.dfsx.lzcms.liveroom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.model.Level;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.BarrageListViewSimple;
import com.dfsx.lzcms.liveroom.view.ChatMessageTextView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by liuwb on 2016/6/29.
 */
public class BarrageListAdapter extends BaseRecyclerViewAdapter {

    private ArrayList<BarrageListViewSimple.BarrageItem> listData;
    private Context context;

    private OnMessageViewClickListener messageViewClickListener;

    private int showTextColor;

    public BarrageListAdapter(Context context, ArrayList<BarrageListViewSimple.BarrageItem> listData) {
        this.context = context;
        this.listData = listData;
    }

    public ArrayList<BarrageListViewSimple.BarrageItem> getAdapterData() {
        return listData;
    }

    public void setData(ArrayList<BarrageListViewSimple.BarrageItem> listData) {
        this.listData = listData;
    }

    public void setShowTextColor(int color) {
        this.showTextColor = color;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_list_barrage, null);
        return new BaseRecyclerViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder baseRecyclerViewHolder, int i) {
        ChatMessageTextView tvMsg = baseRecyclerViewHolder.getView(R.id.tv_msg);
        ImageView levelImage = baseRecyclerViewHolder.getView(R.id.item_level_image);
        if (showTextColor != 0) {
            tvMsg.setTextColor(showTextColor);
        }
        String name = listData.get(i).name;
        tvMsg.setTag(listData.get(i));
        //        if (!TextUtils.isEmpty(name) && !name.contains(":")) {
        //            name += "";
        //        } else {
        //            name += " ";
        //        }
        CharSequence content = listData.get(i).content;
        if (listData.get(i).nameColor != 0) {
            tvMsg.setContent(name, listData.get(i).nameColor, content);
        } else {
            tvMsg.setContent(name, content);
        }

        tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageViewClickListener != null) {
                    messageViewClickListener.onMessageClick(v, v.getTag());
                }
            }
        });
        tvMsg.setNameBackgroundTopPaddingListener(new LevelViewSizeLayoutListener(levelImage));
        UserLevelManager.getInstance().showLevelImage(context, listData.get(i).userLevelId,
                levelImage);
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    public void setOnMessageViewClickListener(OnMessageViewClickListener l) {
        messageViewClickListener = l;
    }

    class LevelViewSizeLayoutListener implements ChatMessageTextView.
            NameBackgroundTopPaddingListener {

        private ImageView levelView;


        public LevelViewSizeLayoutListener(ImageView iv) {
            levelView = iv;
        }

        private void setLevelImageViewTopmargin(int topMargin) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    levelView.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , PixelUtil.dp2px(context, 18));
                params.leftMargin = PixelUtil.dp2px(context, 15);
            }
            params.topMargin = topMargin;
            levelView.setLayoutParams(params);
        }

        @Override
        public void onNameBkgPaddingTop(int padding) {
            setLevelImageViewTopmargin(padding);
        }
    }
}
