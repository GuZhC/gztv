package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.ChatMember;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/6/30.
 */
public class VisitorLogoListView extends HorizontalListRecyclerView {

    private List<ChatMember> logoList;

    private VisitorAdapter adapter;

    public VisitorLogoListView(Context context) {
        this(context, null);
    }

    public VisitorLogoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(List<ChatMember> logoList) {
        this.logoList = logoList;
    }

    public List<ChatMember> getData() {
        return logoList;
    }

    public void update() {
        if (adapter == null) {
            adapter = new VisitorAdapter();
            setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void addALogog(ChatMember logo) {
        if (logoList == null) {
            logoList = new ArrayList<>();
        }
        logoList.add(logo);
        update();
    }

    public void addLogoList(List<ChatMember> list) {
        if (list != null) {
            if (logoList == null) {
                logoList = new ArrayList<>();
            }
            logoList.addAll(list);
            update();
        }
    }

    public void updateLogList(List<ChatMember> list) {
        if (logoList == null) {
            logoList = new ArrayList<>();
        } else {
            logoList.clear();
        }
        addLogoList(list);
    }

    class VisitorAdapter extends BaseRecyclerViewAdapter {


        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_visitor_list, null);
            return new BaseRecyclerViewHolder(v, i);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder baseRecyclerViewHolder, int i) {
            ImageView logoImageView = baseRecyclerViewHolder.getView(R.id.logo);
            String path = logoList == null || logoList.get(i) == null ? null : logoList.get(i).getLogo();
            if (!TextUtils.isEmpty(path)) {
                LSLiveUtils.showUserLogoImage(context, logoImageView, path);
            } else {
                logoImageView.setImageResource(R.drawable.icon_defalut_no_login_logo);
            }
        }

        @Override
        public int getItemCount() {
            return logoList == null ? 0 : logoList.size();
        }
    }

}
