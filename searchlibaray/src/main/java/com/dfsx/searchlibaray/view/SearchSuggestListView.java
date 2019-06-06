package com.dfsx.searchlibaray.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.searchlibaray.R;
import com.dfsx.searchlibaray.adapter.BaseListViewAdapter;

import java.util.ArrayList;

public class SearchSuggestListView extends FrameLayout {

    private ListView listView;

    private Context context;

    private SuggestAdapter adapter;

    private OnSuggestItemClickListener listener;

    public SearchSuggestListView(Context context) {
        this(context, null);
    }

    public SearchSuggestListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchSuggestListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context)
                .inflate(R.layout.pop_sugest_list, this);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new SuggestAdapter(context);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                if (adapter.getData() != null && position >= 0 && position < adapter.getData().size()) {
                    if (listener != null) {
                        listener.onSuggestClick(adapter.getData().get(position));
                    }
                }

            }
        });
    }

    public void setList(ArrayList<String> list) {
        if (adapter != null) {
            adapter.update(list, false);
        }
    }

    public void setOnSuggestItemClickListener(OnSuggestItemClickListener l) {
        this.listener = l;
    }


    public interface OnSuggestItemClickListener {
        void onSuggestClick(String suggestText);
    }

    public class SuggestAdapter extends BaseListViewAdapter<String> {

        public SuggestAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_suggest_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView tv = holder.getView(R.id.suggest_text);
            tv.setText(list.get(position));
        }
    }
}
