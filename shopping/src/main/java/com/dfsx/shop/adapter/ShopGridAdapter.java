package com.dfsx.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.shop.R;
import com.dfsx.shop.model.ShopEntry;

/**
 * Created by heyang on 2017/7/24.
 */
public class ShopGridAdapter extends BaseGridListAdapter<ShopEntry> {
    public static final String TAG = "ShopGridAdapter";
    private Context context;
    private boolean isInit = false;

    public ShopGridAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.adapter_item_live_user_grid_layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                getItemViewLayoutId(), position);
        if (convertView == null) {
            LinearLayout lineContainerView = holdler.getView(R.id.item_room_line_container);
            LinearLayout.LayoutParams params;
            for (int i = 0; i < getColumnCount(); i++) {
                FrameLayout lineItemContainer = new FrameLayout(context);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                lineItemContainer.setLayoutParams(params);
                lineItemContainer.setId(i);
                lineContainerView.addView(lineItemContainer, params);

                if (i < getColumnCount() - 1) {
                    //添加grid水平反向的分割线
                    addItemDivideView(lineContainerView);
                }
                BaseViewHodler itemHolder = BaseViewHodler.get(null, parent, getGridItemLayoutId(), position);
                lineItemContainer.addView(itemHolder.getConvertView());
                lineItemContainer.setTag(itemHolder);
            }
        }
        setItemViewData(holdler, position);
        return holdler.getConvertView();
    }

    protected void addItemDivideView(LinearLayout container) {
        int with = Util.dp2px(context, 10);
        if (with > 0) {
            View divideView = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(with,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            divideView.setLayoutParams(lp);
            divideView.setBackgroundResource(R.color.white);
            container.addView(divideView);
        }
    }


    @Override
    protected int getHDivideLineWidth() {
        return 10;
    }

    private String getNumTextString(long num) {
        if (num / 10000 > 0) {
            float fw = num / 10000f;
            return String.format("%.1f", fw) + "w人";
        } else {
            int bNum = (int) num;
            return bNum + "人";
        }
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getGridItemLayoutId() {
        return R.layout.adapter_shop_grid_layout;
    }

    @Override
    public void setGridItemViewData(BaseViewHodler holder, ShopEntry shop) {
        isInit = true;
        View itemContainerView = holder.getView(R.id.grid_container_layout);
        View priceContainer = holder.getView(R.id.shop_price_container);
        ImageView shopThumbImageView = holder.getView(R.id.shop_image_view);
        ImageView solidOutImag = holder.getView(R.id.shop_sail_out);
        View bottomDivideLine = holder.getView(R.id.bottom_horizontal_line);
        TextView shopNameTextView = holder.getView(R.id.shop_name);
        TextView shopBuyNumTextView = holder.getView(R.id.shop_buynum_text);
        TextView shopPriceTextView = holder.getView(R.id.shop_price_text);
        bottomDivideLine.setVisibility(View.VISIBLE);
        GlideImgManager.getInstance().showImg(context, shopThumbImageView,
                shop.getCover_url());
        shopNameTextView.setText(shop.getName());
        String exchageNumber = String.format("%d", shop.getExchange_count()) + "人兑换";
        shopBuyNumTextView.setText(exchageNumber);
        shopPriceTextView.setText(shop.getPrice() + "");
        int staock = shop.getStatus();
        if (staock == 0) {
            priceContainer.setVisibility(View.GONE);
            solidOutImag.setVisibility(View.VISIBLE);
            shopNameTextView.setTextColor(0x802e2e2e);
            shopBuyNumTextView.setVisibility(View.GONE);
        } else {
            shopBuyNumTextView.setVisibility(View.VISIBLE);
            priceContainer.setVisibility(View.VISIBLE);
            solidOutImag.setVisibility(View.GONE);
            shopNameTextView.setTextColor(0xff212121);
        }
    }

    public boolean isInit() {
        return isInit;
    }
}
