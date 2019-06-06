package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.WelcomeAct;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.fragment.HeadLineFragment;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.ganzcms.app.view.ViewPagerScrollView;
import com.dfsx.searchlibaray.SearchUtil;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.*;

/**
 * Created by heyang on  2018/4/2
 */
public class MulityColumnFragment extends ImportNewsFragment implements FragmentBackHandler {

    private Activity act;
    private Context context;
    private View rootView;
    private ViewPagerScrollView pagerScrollView;
    private long mTypeId = -1;
    String mKey = "";
    private long mType = -1;
    private View backBtn;
    private TextView topTitleTxt;
    private ImageView topColumlogo;


    public static MulityColumnFragment newInstance(String key) {
        MulityColumnFragment fragment = new MulityColumnFragment();
        Bundle bundel = new Bundle();
        bundel.putString("key", key);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_news_bunch_layout, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mKey = getArguments().getString("key");
        }
        act = getActivity();
        context = getContext();
        initView();
//        ColumnCmsEntry dlist = ColumnBasicListManager.getInstance().findColumnByName("zylm");
        ColumnCmsEntry dlist = ColumnBasicListManager.getInstance().findColumnByName("zypd");
        if (dlist == null) return;
        if (!TextUtils.isEmpty(dlist.getIcon_url())) {
            Glide.with(getActivity())
                    .load(dlist.getIcon_url())
                    .asBitmap()
                    .error(R.color.transparent)
                    .into(topColumlogo);
//            Util.LoadThumebImage(topColumlogo, dlist.getIcon_url(), null);
        } else {
            topColumlogo.setVisibility(View.GONE);
            topTitleTxt.setVisibility(View.VISIBLE);
            topTitleTxt.setText(dlist.getName());
        }
        List<ColumnCmsEntry> lsit = dlist.getDlist();
        if (!(lsit == null || lsit.isEmpty())) {
            Collections.sort(lsit, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
                }
            });
            Collections.reverse(lsit);
            mTypeId = dlist.getId();
            ArrayList<ScrollItem> itemList = new ArrayList<ScrollItem>();
            for (ColumnCmsEntry cwNode : lsit) {
                if (cwNode == null) continue;
//            //    int nodeType = ColumnTYPE.getTypeBySeverUrl(context, cwNode.getUrl());
//                ScrollItem item = new ScrollItem(cwNode.getName(),
//                        HeadLineFragment.newInstance(cwNode.getI;d(), cwNode.getType(), cwNode.getSliderId()));
                ScrollItem item = createScrollItem(cwNode);
//                if (TextUtils.equals("dianshiju", cwNode.getKey())) {
//                    item = new ScrollItem(cwNode.getName(), TVSeriesListFragment.newInstance(cwNode.getId()));
//                }else
//                    item = new ScrollItem(cwNode.getName(),
//                            HeadLineFragment.newInstance(cwNode.getId(), cwNode.getType(), cwNode.getSliderId()));
                itemList.add(item);
            }
            pagerScrollView.setData(itemList, getChildFragmentManager());
        }
//        getData();
    }

    private void initView() {
        topTitleTxt = (TextView) findViewById(R.id.title_text);
        topColumlogo = (ImageView) findViewById(R.id.main_top_column_logo);
        backBtn = (View) findViewById(R.id.mulity_back_btn_view);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtil.switchLanguage(getActivity(), "zh_CN");
                getActivity().finish();
            }
        });
        View topRoghtBnt = (View) findViewById(R.id.top_rigth_btn);
        topRoghtBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(AppTibetanSearchFragment.KEY_COLUMN_ID, mTypeId);
                SearchUtil.goSearch(context, AppTibetanSearchFragment.class.getName(), bundle);
            }
        });
        rootView.setBackgroundColor(context.
                getResources().getColor(R.color.white));
        pagerScrollView = (ViewPagerScrollView) findViewById(R.id.pager_scroll_view);
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public boolean onBackPressed() {
        // 当确认没有子Fragmnt时可以直接return false
        LanguageUtil.switchLanguage(getActivity(), "zh_CN");
        return BackHandlerHelper.handleBackPress(this);
    }


}
