package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.GridNews;
import com.dfsx.ganzcms.app.model.NewsGridMenu;

/**
 * 市州矩阵的详情页
 */
public class CityNewsListFragment extends HeadLineFragment {

    public static final String KEY_GRID_MENU = "CityNewsListFragment.GRID_MENU";

    private ImageView cityLogoImage;
    private TextView tvCityName;
    private TextView tvCityIntro;

    private GridNews gridMenuData;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            gridMenuData = (GridNews) getArguments().getSerializable(KEY_GRID_MENU);
        }
        super.onViewCreated(view, savedInstanceState);

        setCityNewsHeader();

        setHeaderViewData();
    }

    private void setCityNewsHeader() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.city_news_header, null);
        list.addHeaderView(view);
        cityLogoImage = (ImageView) view.findViewById(R.id.city_image_view);
        tvCityName = (TextView) view.findViewById(R.id.city_name_tv);
        tvCityIntro = (TextView) view.findViewById(R.id.city_info_intro_tx);
    }

    private void setHeaderViewData() {
        if (gridMenuData != null) {
            if (gridMenuData instanceof NewsGridMenu) {
                NewsGridMenu menu = (NewsGridMenu) gridMenuData;
                GlideImgManager.getInstance().showImg(_Context, cityLogoImage, menu.getImagePath());
                tvCityName.setText(menu.getShowTitle());
                tvCityIntro.setText(menu.getDesc());
            }

        }
    }
}
