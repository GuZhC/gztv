package com.dfsx.lzcms.liveroom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.lzcms.liveroom.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/16.
 * 使用ListView实现GridView的功能
 */
public abstract class BaseGridListAdapter<T> extends BaseListViewAdapter<T> implements View.OnClickListener {

    protected ArrayList<ArrayList<T>> gridList;

    private static int KET_TAG_DATA = R.id.tag_base_grid_list;

    private OnGridItemClickListener onGridItemClickListener;

    private OnGridItemMenuClickListener onGridItemMenuClickListener;

    public BaseGridListAdapter(Context context) {
        super(context);
    }

    protected List<T> menuList;

    @Override
    public void update(List data, boolean isAdd) {
        if (isAdd && list != null) {
            list.addAll(data);
            addRoomList(data);
        } else {
            list = data;
            gridList = toGridList(data);
        }
        notifyDataSetChanged();
    }

    public void update(List data, List menuData, boolean isAdd) {
        if (isAdd && list != null) {
            list.addAll(data);
            addRoomList(data);
        } else {
            list = data;
            gridList = toGridList(data);
        }
        if (isAdd && menuList != null) {
            menuList.addAll(menuData);
        } else {
            menuList = menuData;
        }
        notifyDataSetChanged();
    }

    public void updateMenu(List data, boolean isAdd) {
        if (isAdd && menuList != null) {
            menuList.addAll(data);
        } else {
            menuList = data;
        }
        notifyDataSetChanged();
    }

    protected ArrayList<ArrayList<T>> toGridList(List<T> list) {
        ArrayList<ArrayList<T>> pairList = new ArrayList<>();
        if (list != null) {
            ArrayList<T> pairData = null;
            for (int i = 0; i < list.size(); i++) {
                if (i % getColumnCount() == 0) {
                    pairData = new ArrayList<>();
                    pairList.add(pairData);
                }
                if (pairData != null) {
                    pairData.add(list.get(i));
                }
            }
        }
        return pairList;
    }

    public void addRoomList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (gridList.isEmpty()) {
            gridList.addAll(toGridList(list));
        } else {
            ArrayList<T> lastPair = gridList.get(gridList.size() - 1);
            if (lastPair != null && lastPair.size() < getColumnCount()) {
                int count = getColumnCount() - lastPair.size();
                int listSize = list.size();
                count = Math.min(count, listSize);
                for (int i = 0; i < count; i++) {
                    T r = list.remove(0);//把新来的列表的头几个数据放到上面没有补齐的数据中
                    if (r != null) {
                        lastPair.add(r);
                    }
                }
            }
            gridList.addAll(toGridList(list));
        }
    }

    public List<T> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return gridList == null ? 0 : gridList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridList.get(position);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_base_grid_container;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                getItemViewLayoutId(), position);
        if (convertView == null) {
            int containerWidth = parent.getWidth();
            LinearLayout lineContainerView = holdler.getView(R.id.list_line_container);
            LinearLayout.LayoutParams params;
            //添加menu
            if (getGridItemLayoutId() != 0 && getGridMenuWidth() != 0) {
                params = new LinearLayout.LayoutParams(getGridMenuWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                FrameLayout menuContainer = new FrameLayout(context);
                menuContainer.setLayoutParams(params);
                menuContainer.setId(R.id.base_grid_menu_container_id);
                lineContainerView.addView(menuContainer, params);

                BaseViewHodler menuHolder = BaseViewHodler.get(null, parent,
                        getGridMenuLayoutId(), position);
                menuContainer.addView(menuHolder.getConvertView());
                menuContainer.setTag(menuHolder);
            }
            //添加grid
            for (int i = 0; i < getColumnCount(); i++) {
                if (i == 0) {
                    addItemStartDivideVIew(lineContainerView);
                }
                FrameLayout lineItemContainer = new FrameLayout(context);
                if (containerWidth > 0) {
                    int allUseWidth = containerWidth - getGridMenuWidth() - getHDLeftDivideLineWidth() -
                            getHDRightDivideLineWidth() - (getColumnCount() - 1) *
                            getHDivideLineWidth();

                    int itemWidth = allUseWidth / getColumnCount();
                    params = new LinearLayout.LayoutParams(itemWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                }
                lineItemContainer.setLayoutParams(params);
                lineItemContainer.setId(i);
                lineContainerView.addView(lineItemContainer, params);

                if (i < getColumnCount() - 1) {
                    //添加grid水平反向的分割线
                    addItemDivideView(lineContainerView);
                }

                if (i == getColumnCount() - 1) {
                    addItemEndDivideView(lineContainerView);
                }
                BaseViewHodler itemHolder = BaseViewHodler.get(null, parent, getGridItemLayoutId(), position);
                lineItemContainer.addView(itemHolder.getConvertView());
                lineItemContainer.setTag(itemHolder);
            }
        }
        setItemViewData(holdler, position);
        return holdler.getConvertView();
    }

    /**
     * 可重写设置Grid左边的菜单布局
     *
     * @return
     */
    protected int getGridMenuLayoutId() {
        return 0;
    }

    /**
     * 可重写设置Grid左边的菜单布局大小
     *
     * @return
     */
    protected int getGridMenuWidth() {
        return 0;
    }

    protected void addItemStartDivideVIew(LinearLayout container) {
        if (getHDLeftDivideLineWidth() > 0) {
            View divideView = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getHDLeftDivideLineWidth(),
                    getHDLeftDivideLineHeight());
            divideView.setLayoutParams(lp);
            divideView.setBackgroundResource(getHDLeftDivideLineRes());
            container.addView(divideView);
        }
    }

    protected void addItemEndDivideView(LinearLayout container) {
        if (getHDRightDivideLineWidth() > 0) {
            View divideView = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getHDRightDivideLineWidth(),
                    getHDRightDivideLineHeight());
            divideView.setLayoutParams(lp);
            divideView.setBackgroundResource(getHDRightDivideLineRes());
            container.addView(divideView);
        }
    }

    protected void addItemDivideView(LinearLayout container) {
        int with = getHDivideLineWidth();
        if (with > 0) {
            View divideView = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with,
                    getHDivideLineHeight());
            divideView.setLayoutParams(lp);
            divideView.setBackgroundResource(getHDivideLineRes());
            container.addView(divideView);
        }
    }


    @Override
    public void setItemViewData(BaseViewHodler holder, int position) {
        if (getGridMenuWidth() > 0 && getGridMenuLayoutId() != 0) {
            FrameLayout menuContainer = holder.getView(R.id.base_grid_menu_container_id);
            BaseViewHodler menuHolder = (BaseViewHodler) menuContainer.getTag();
            setGridMenuItemData(holder, menuHolder, position);
            menuContainer.setTag(KET_TAG_DATA, new ClickTagData(position, 0));
            menuContainer.setOnClickListener(this);
        }
        for (int i = 0; i < getColumnCount(); i++) {
            FrameLayout itemContainer = (FrameLayout) holder.getView(i);
            BaseViewHodler itemHolder = (BaseViewHodler) itemContainer.getTag();
            setGridItemData(holder, itemHolder, position, i);
            itemContainer.setTag(KET_TAG_DATA, new ClickTagData(position, i));
            itemContainer.setOnClickListener(this);
        }
    }

    protected void setGridItemData(BaseViewHodler lineHolder, BaseViewHodler hodler, int listViewPosition,
                                   int columnPosition) {
        if (listViewPosition >= 0 && listViewPosition < gridList.size()) {
            ArrayList<T> lineData = gridList.get(listViewPosition);
            if (columnPosition < lineData.size()) {
                lineHolder.getView(columnPosition).setVisibility(View.VISIBLE);
                T data = lineData.get(columnPosition);
                setGridItemViewData(hodler, data);
            } else {
                lineHolder.getView(columnPosition).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 可重写设置Grid左边的菜单内容显示
     *
     * @return
     */
    protected void setGridMenuItemData(BaseViewHodler lineHolder, BaseViewHodler menuHolder, int postion) {

    }

    /**
     * 设置水平方想的分割线
     * 单位 px
     *
     * @return
     */
    protected int getHDivideLineWidth() {
        return 5;
    }

    protected int getHDivideLineHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected int getHDivideLineRes() {
        return R.color.public_bgd;
    }

    protected int getHDLeftDivideLineWidth() {
        return 0;
    }

    private int getHDLeftDivideLineHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;

    }

    protected int getHDLeftDivideLineRes() {
        return R.color.public_bgd;
    }

    protected int getHDRightDivideLineWidth() {
        return 0;
    }

    private int getHDRightDivideLineHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;

    }

    protected int getHDRightDivideLineRes() {
        return R.color.public_bgd;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.base_grid_menu_container_id) {
            if (onGridItemMenuClickListener != null) {
                Object keyTag = v.getTag(KET_TAG_DATA);
                if (keyTag != null && keyTag instanceof ClickTagData) {
                    ClickTagData tagData = ((ClickTagData) keyTag);
                    onGridItemMenuClickListener.onGridMenuClick(tagData.line);
                }
            }
        } else {
            if (onGridItemClickListener != null) {
                Object keyTag = v.getTag(KET_TAG_DATA);
                if (keyTag != null && keyTag instanceof ClickTagData) {
                    ClickTagData tagData = ((ClickTagData) keyTag);
                    onGridItemClickListener.onGridItemClick(tagData.line, tagData.column);
                }
            }
        }
    }

    public abstract int getColumnCount();

    public abstract int getGridItemLayoutId();

    public abstract void setGridItemViewData(BaseViewHodler hodler, T data);

    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }

    public void setOnGridItemMenuClickListener(OnGridItemMenuClickListener onGridItemMenuClickListener) {
        this.onGridItemMenuClickListener = onGridItemMenuClickListener;
    }

    public interface OnGridItemClickListener {
        void onGridItemClick(int linePosition, int column);
    }

    public interface OnGridItemMenuClickListener {
        void onGridMenuClick(int linePosition);
    }

    static class ClickTagData {
        int line;
        int column;

        public ClickTagData() {

        }

        public ClickTagData(int line, int column) {
            this.line = line;
            this.column = column;
        }
    }
}
