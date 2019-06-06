package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.util.AttributeSet;
import com.dfsx.lzcms.liveroom.adapter.BarrageListAdapter;
import com.dfsx.lzcms.liveroom.adapter.OnMessageViewClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个类YY直播的垂直弹幕刷新窗口.
 * 使用addItemData方法实现自动添加到底部并显示
 * Created by liuwb on 2016/6/29.
 */
public class BarrageListViewSimple extends ListRecyclerView {

    /**
     * 显示的最大条数
     */
    public static final int MAX_SHOW_LENGTH = 500;

    private ArrayList<BarrageItem> listData;
    private BarrageListAdapter adapter;
    private int itemDefaultTextColor;

    private OnMessageViewClickListener messageViewClickListener;

    public BarrageListViewSimple(Context context) {
        this(context, null);
    }

    public BarrageListViewSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(ArrayList<BarrageItem> data) {
        this.listData = data;
    }

    public ArrayList<BarrageItem> getData() {
        return listData;
    }

    public void setDefaultShowTextColor(int textColor) {
        this.itemDefaultTextColor = textColor;
        if (adapter != null && textColor != 0) {
            adapter.setShowTextColor(textColor);
        }
    }

    /**
     * 添加一条数据在尾部。
     * 如果显示的数据恰好在尾部就要让新增的数据在尾部显示
     *
     * @param itemList
     * @param isToBottom 设定是否强制到底部
     */
    public void addItemData(List<BarrageItem> itemList, boolean isToBottom) {
        boolean isNeedScrollToBottom = isLastItemVisiable();
        if (listData == null) {
            listData = new ArrayList<BarrageItem>();
        }
        listData.addAll(itemList);
        checkMaxShowLine();
        updateAdapter();

        if (isToBottom || isNeedScrollToBottom) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.
                            smoothScrollToPosition(listData.size() - 1);
                }
            }, 80);
        }
    }

    private void checkMaxShowLine() {
        if (listData != null && !listData.isEmpty()) {
            if (listData.size() > MAX_SHOW_LENGTH) {
                int deleteLength = listData.size() - MAX_SHOW_LENGTH;
                for (int i = deleteLength - 1; i >= 0; i--) {
                    listData.remove(i);
                }
            }
        }
    }

    public void addItemData(BarrageItem item) {
        ArrayList<BarrageItem> list = new ArrayList<>();
        list.add(item);
        addItemData(list, false);
    }

    public void clearData() {
        if (adapter != null) {
            if (adapter.getAdapterData() != null) {
                adapter.getAdapterData().clear();
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setOnMessageViewClickListener(OnMessageViewClickListener messageViewClickListener) {
        if (adapter != null) {
            adapter.setOnMessageViewClickListener(messageViewClickListener);
        }
        this.messageViewClickListener = messageViewClickListener;
    }

    public void updateAdapter() {
        if (adapter == null) {
            adapter = new BarrageListAdapter(context, listData);
            if (messageViewClickListener != null) {
                adapter.setOnMessageViewClickListener(messageViewClickListener);
            }
            if (itemDefaultTextColor != 0) {
                adapter.setShowTextColor(itemDefaultTextColor);
            }
            setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public static class BarrageItem implements Serializable {
        public String name;
        public CharSequence content;
        public int nameColor;
        public long userLevelId;

        public BarrageItem() {

        }

        public BarrageItem(String name, CharSequence content) {
            this.name = name;
            this.content = content;
        }

        public BarrageItem(String name, CharSequence content, long levelId) {
            this.name = name;
            this.content = content;
            this.userLevelId = levelId;
        }

        public BarrageItem(String name, int nameColor, CharSequence content) {
            this.name = name;
            this.content = content;
            this.nameColor = nameColor;
        }

        public BarrageItem(String name, int nameColor, CharSequence content, long levelId) {
            this.name = name;
            this.content = content;
            this.nameColor = nameColor;
            this.userLevelId = levelId;
        }
    }
}
