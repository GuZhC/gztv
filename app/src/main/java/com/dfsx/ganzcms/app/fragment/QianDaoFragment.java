package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.shop.fragment.CreditShopFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

public class QianDaoFragment extends AbsListFragment {

    private ShopGridAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setBackgroundResource(R.color.white);

        testData();
        addFooter();
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new ShopGridAdapter(context);
        listView.setAdapter(adapter);

        View header = LayoutInflater.from(context)
                .inflate(R.layout.qiandao_info_layout, null);
        listView.addHeaderView(header);


    }

    private void addFooter() {
        if (listView.getFooterViewsCount() > 1) {
            return;
        }
        View footer = LayoutInflater.from(context)
                .inflate(R.layout.qiandao_join_shop_layout, null);
        listView.addFooterView(footer);

        footer.findViewById(R.id.btn_join_shop)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WhiteTopBarActivity.startAct(context,
                                CreditShopFragment.class.getName(), "积分商城", "兑换记录");
                    }
                });


    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    private void testData() {

        ArrayList<IShopInfo> list = new ArrayList<>();
        list.add(new TestData());
        list.add(new TestData());
        list.add(new TestData());

        adapter.update(list, false);
    }


    class TestData implements IShopInfo {
        String testImage = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1839338503,954939257&fm=173&s=E343DB001F8A26410C76100E0300F0C1&w=600&h=356&img.JPEG";

        @Override
        public String getShopThumb() {
            return testImage;
        }

        @Override
        public String getShopName() {
            return "test";
        }

        @Override
        public String getShopPriceText() {
            return "10000积分";
        }
    }


    public interface IShopInfo {
        String getShopThumb();

        String getShopName();

        String getShopPriceText();
    }

    class ShopGridAdapter extends BaseGridListAdapter<IShopInfo> {

        public ShopGridAdapter(Context context) {
            super(context);
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_shop_grid;
        }

        @Override
        protected int getHDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDivideLineWidth() {
            return PixelUtil.dp2px(context, 7);
        }

        @Override
        protected int getHDLeftDivideLineWidth() {
            return PixelUtil.dp2px(context, 15);
        }

        @Override
        protected int getHDLeftDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDRightDivideLineWidth() {
            return PixelUtil.dp2px(context, 15);
        }

        @Override
        protected int getHDRightDivideLineRes() {
            return R.color.white;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, IShopInfo data) {
            ImageView shopThumbImage = hodler.getView(R.id.image_shop);
            TextView shopNameText = hodler.getView(R.id.tv_shop_name);
            TextView shopPricetext = hodler.getView(R.id.tv_shop_price);

            Glide.with(act)
                    .load(data.getShopThumb())
                    .error(R.drawable.glide_default_image)
                    .placeholder(R.drawable.glide_default_image)
                    .fitCenter()
                    .crossFade()
                    .into(shopThumbImage);
            shopNameText.setText(data.getShopName());
            shopPricetext.setText(data.getShopPriceText());
        }
    }
}
