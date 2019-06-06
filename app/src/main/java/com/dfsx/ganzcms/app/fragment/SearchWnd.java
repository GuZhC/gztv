package com.dfsx.ganzcms.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.view.ClearEditText;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.SocirtyNewsChannel;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * heyang create  2016-10-10
 */
public class SearchWnd implements View.OnClickListener {
    private Dialog dialog;
    TextView mClosed;
    private Context context;
    BaseAdapter listAdpter;
    private PullToRefreshListView pullToRefreshListView;
    ListView mListView;
    private EditText editSearch;
    private OnSearchClickListener searchClickListener;
    private int currentPage;
    private LinearLayout listEmptyContainer;
    private AdapterView.OnItemClickListener onItemClickListener;

    private EmptyView emptyView;

    public SearchWnd(Context context) {
        this.context = context;
    }

    public void showDialog() {
        dialog = new Dialog(context, R.style.Theme_RecorderDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_search, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
//		dialogWindow.setFlags(
//				WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.alpha = 0.5f;
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
//        lp.width = Util.dp2px(context,200);
//        lp.height = Util.dp2px(context,300);
//        lp.flags = (WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(lp);
//		if (Build.VERSION.SDK_INT < 19) {
//			return;
//		}
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);//动画
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.country_lvcountry);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView = pullToRefreshListView.getRefreshableView();
        listEmptyContainer = (LinearLayout) view.findViewById(R.id.empty_layout);
        editSearch = (ClearEditText) view.findViewById(R.id.filter_edit);
        if (listAdpter == null) {
            listAdpter = new ListViewAdapter(context);
        }
        mListView.setAdapter(listAdpter);
        mClosed = (TextView) view.findViewById(R.id.search_cancel_btn);
        mClosed.setOnClickListener(this);
        dialog.show();

        currentPage = 1;

        editSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                    currentPage = 1;
                    onSearch();
                    return true;
                }
                return false;
            }
        });

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                onSearch();
            }
        });

        if (onItemClickListener != null) {
            mListView.setOnItemClickListener(onItemClickListener);
        }
        setEmptyLayout();

//        ArrayList<SocirtyNewsChannel> dlist = new ArrayList<SocirtyNewsChannel>();
//        for (int i = 0; i < 5; i++) {
//            SocirtyNewsChannel chnel = new SocirtyNewsChannel();
//            chnel.newsTitle = "受期待的Xbox One游安全自拍功能";
//            dlist.add(chnel);
//        }
//        listAdpter.update(dlist, false);
    }

    private void setEmptyLayout() {
        if (mListView != null) {
            mListView.setEmptyView(listEmptyContainer);
        }
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有数据");
        tv.setTextColor(context.getResources().getColor(com.dfsx.lzcms.liveroom.R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        tv.setPadding(0, 20, 0, 0);
        emptyView.setLoadOverView(tv);
        emptyView.setVisibility(View.INVISIBLE);
        listEmptyContainer.addView(emptyView);
    }

    private void onSearch() {
        if (searchClickListener != null) {
            String key = editSearch.getText().toString();
            searchClickListener.onSearch(SearchWnd.this, key, currentPage);
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.loading();
            }
        }
    }

    public void onSearchEnd() {
        pullToRefreshListView.onRefreshComplete();
        if (emptyView != null) {
            emptyView.loadOver();
        }
    }

    /**
     * 设置listView显示的Adapter
     * 如果不设置，这使用默认的ListViewAdapter
     *
     * @param adapter
     */
    public void setListViewAdapter(BaseAdapter adapter) {
        this.listAdpter = adapter;
        if (mListView != null &&
                (mListView.getAdapter() == null ||
                        !mListView.getAdapter().getClass().getName().
                                equals(adapter.getClass().getName()))) {
            mListView.setAdapter(adapter);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
        if (mListView != null) {
            mListView.setOnItemClickListener(onItemClickListener);
        }
    }

    /**
     * 设置search的触发事件
     *
     * @param searchClickListener
     */
    public void setOnSearchClickListener(OnSearchClickListener searchClickListener) {
        this.searchClickListener = searchClickListener;
    }

    public BaseAdapter getListViewAdapter() {
        return listAdpter;
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        if (view == mClosed) {
            dismissDialog();
        }
    }

    /**
     * 搜索的出发事件
     */
    public interface OnSearchClickListener {
        void onSearch(SearchWnd searchWindow, String searchKey, int page);
    }

    public class ListViewAdapter extends BaseAdapter {
        private static final int TYPE_NEWS = 0;
        private static final int TYPE_VIDEO = 1;
        private static final int TYPE_ACTIVIRY = 2;
        private static final int TYPE_COUNT = 3;

        private final String STATE_LIST = "ListAdapter.mlist";
        private ArrayList<SocirtyNewsChannel> items = new ArrayList<SocirtyNewsChannel>();
        private LayoutInflater inflater;
        public boolean bInit;

        public ListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            bInit = false;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<SocirtyNewsChannel> sList;
            sList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            if (sList != null) {
                items = sList;
                notifyDataSetChanged();
                bInit = true;
            }
        }

        public void SetInitStatus(boolean flag) {
            bInit = flag;
        }

        public long getMinId() {
            return items.isEmpty() ? -1 : items.get(items.size() - 1).id;
        }

        public long getMaxId() {
            return items.isEmpty() ? -1 : items.get(0).id;
        }

        public void saveInstanceState(Bundle outState) {
            if (bInit) {
                outState.putParcelableArrayList(STATE_LIST, items);

            }
        }

        public boolean isInited() {
            return bInit;
        }

        public void update(ArrayList<SocirtyNewsChannel> data, boolean bAddTail) {
            if (data != null && !data.isEmpty()) {
//            HeadLineFragment.this.pullListview.setVisibility(0);
//            HeadLineFragment.this.list.setVisibility(0);
//            mRelativeLayoutFail.setVisibility(8);
                boolean noData = false;
                if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                    if (items.size() >= data.size()
                            && items.get(items.size() - 1).id == data.get(data.size() - 1).id)
                        noData = true;
                    else
                        items.addAll(data);
                else {
                    if (items != null) {
                        if (/*items.size() == data.size() && */
                                items.size() > 0 &&
                                        items.get(0).id == data.get(0).id)
                            noData = true;
                    }
                    if (!noData) {
                        items = data;
                    }
                }
                bInit = true;
                if (!noData)
                    notifyDataSetChanged();
            }
        }

        public void updateCommendNumer(int index, int commnedNum) {
            SocirtyNewsChannel chs = items.get(index);
            chs.commengNumber = commnedNum;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);
            SocirtyNewsChannel item = items.get(position);
            if (item.typeId == 0) {
                type = TYPE_NEWS;
            } else if (item.typeId == 1) {
                type = TYPE_VIDEO;
            } else if (item.typeId == 2) {
                type = TYPE_ACTIVIRY;
            } else
                type = TYPE_NEWS;
            return type;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        public class ViewHolder {
            public SocirtyNewsChannel item;
            public int pos;
            public TextView titleTextView;
            public ImageButton itemDel;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View newsView = null;
            int currentType = getItemViewType(position);
            if (currentType == TYPE_NEWS) {
                ViewHolder viewHolder = null;
                if (view == null) {
                    newsView = inflater.inflate(R.layout.search_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.titleTextView = (TextView) newsView.findViewById(R.id.search_list_item_title);
                    viewHolder.itemDel = (ImageButton) newsView.findViewById(R.id.search_list_del);
                    viewHolder.item = items.get(position);
                    newsView.setTag(viewHolder);
                    view = newsView;
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.item = items.get(position);
                viewHolder.pos = position;
                viewHolder.titleTextView.setText(viewHolder.item.newsTitle);
                viewHolder.itemDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
            return view;
        }

        public boolean remove(int position) {
            if (position < items.size()) {
                items.remove(position);
                return true;
            }
            return false;
        }

        /**
         * 添加列表项
         *
         * @param item
         */
        public void addItem(SocirtyNewsChannel item) {
            items.add(item);
        }

        /**
         * 添加指定列
         *
         * @param item
         */
        public void addItemByIndex(SocirtyNewsChannel item, int index) {
            items.add(index, item);
        }
    }
}