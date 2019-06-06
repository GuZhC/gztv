package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.core.common.adapter.BaseViewHodler;

import java.util.ArrayList;

/**
 * heyang   2017-5-19
 */
public class MyServiceGridAdapter<T> extends BaseAdapter {
    private Context mContext;

//	public String[] img_text = { "在线购物", "水电查询", "购买车票",
//			"快递查询", "购买机票", "天气查询",
//			"医院查询", "车辆查询", "电影查询",
//	         "公交查询","大巴查询","招聘信息",};
//	public int[] imgs = { R.drawable.online_shoping, R.drawable.shuidian,R.drawable.chebiao,
//			R.drawable.kuaidi, R.drawable.feiji,R.drawable.tianqi,
//			R.drawable.yiyuan, R.drawable.cheliang,R.drawable.dianying,
//			R.drawable.gongjiao, R.drawable.daba, R.drawable.zhaopin };

    ArrayList<T> items = new ArrayList<T>();

    public MyServiceGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
//		return img_text.length;
        return items == null ? 0 : items.size();
    }

    public void update(ArrayList<T> data, boolean bAddTail) {
        if (bAddTail)
            items.addAll(data);
        else
            items = data;
//        mbInit = true;
        notifyDataSetChanged();
    }

    public T getData(int position) {
        if (items == null || items.isEmpty()) return null;
        if (position >= items.size()) return null;
        return items.get(position);
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(
//                    R.layout.my_grid_item, parent, false);
//        }
        BaseViewHodler holeder = new BaseViewHodler(parent, R.layout.my_grid_item, position);
        TextView tv = holeder.getView(R.id.tv_item);
        ImageView iv = holeder.getView(R.id.iv_item);
//        iv.setBackgroundResource(R.drawable.online_shoping);
//		tv.setText(img_text[position]);
        ContentCmsEntry entry = (ContentCmsEntry) items.get(position);
        tv.setText(entry.getTitle());
//        Util.LoadThumebImage(iv, entry.getThumb(), null);
        tv.setTag(entry);
        String thumb = "";
        if (!(entry.getThumbnail_urls() == null || entry.getThumbnail_urls().isEmpty())) {
            thumb = entry.getThumbnail_urls().get(0);
        }
//        Util.LoadThumebImage(iv, thumb, null);
        Glide.with(mContext)
                .load(thumb)
                .asBitmap()
                .error(R.drawable.glide_default_image)
                .into(iv);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ColumnEntry entry1 = (ColumnEntry) view.findViewById(R.id.tv_item).getTag();
//                if (entry1 != null)
//                    ColumnTYPE.gotoComWebreq(view.getContext(), entry1.getKey(), entry1.getName());
//            }
//        });
        return holeder.getConvertView();
    }

}
