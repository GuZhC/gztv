package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.NoScrollGridView;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/29.
 */
public class RenZhengFragment extends AbsListFragment {

    public static final int REQUEST_CODE_SELECTED_IMAGE = 10;

    private RenZhengAdapter adapter;

    private NoScrollGridView imageGridView;

    private Button btnRenzheng;

    private ImageGridAdapter imageAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.addHeaderView(LayoutInflater.from(context)
                .inflate(R.layout.header_ren_zheng, null));

        listView.addFooterView(LayoutInflater.from(context)
                .inflate(R.layout.footer_ren_zhen_layout, null));

        initView(view);
    }

    private void initView(View v) {
        imageGridView = (NoScrollGridView) v.findViewById(R.id.image_grid_view);
        btnRenzheng = (Button) v.findViewById(R.id.btn_ren_zheng);

        imageAdapter = new ImageGridAdapter(context);
        imageAdapter.update(new ArrayList<String>(), false);
        imageGridView.setAdapter(imageAdapter);


        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageAdapter.getData() != null) {
                    String url = imageAdapter.getData().get(position);
                    if (ImageGridAdapter.DEFALUT_ADD_IMAGE_STR.equals(url)) {
                        gotoSelectImage();
                    }
                }
            }
        });

        btnRenzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeagle()) {
                    goRenzheng();
                }
            }
        });
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new RenZhengAdapter(context);
        listView.setAdapter(adapter);

        ArrayList<RenZhengItemData> datas = new ArrayList<>();

        datas.add(new RenZhengItemData(1, "真实姓名", ""));
        datas.add(new RenZhengItemData(1, "真实性别", ""));
        datas.add(new RenZhengItemData(1, "身份证号", ""));
        datas.add(new RenZhengItemData(1, "手机号码", ""));
        datas.add(new RenZhengItemData(1, "现居地址", ""));

        adapter.update(datas, false);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    private void goRenzheng() {
        LSUtils.toastNoFunction(context);
    }

    public List<String> getShowImageList() {
        return imageAdapter.getShowImageList();
    }

    public void gotoSelectImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(context, ImageFragmentActivity.class);
                intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, 9);
                startActivityForResult(intent, REQUEST_CODE_SELECTED_IMAGE);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    private boolean isLeagle() {
        List<RenZhengItemData> itemDatas = adapter.getData();
        for (RenZhengItemData data : itemDatas) {
            if (data != null && TextUtils.isEmpty(data.content)) {
                Toast.makeText(context, "请填写" + data.title,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (getShowImageList() == null || getShowImageList().isEmpty()) {
            Toast.makeText(context, "请选择认证图片",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECTED_IMAGE) {
                Bundle b = data.getExtras();
                ArrayList<MediaModel> MediaList = null;
                if (null != b) {
                    MediaList = (ArrayList<MediaModel>) b.get("list");
                }
                if (MediaList != null && !MediaList.isEmpty()) {
                    ArrayList<String> stringList = new ArrayList<>();
                    for (int i = 0; i < MediaList.size(); i++) {
                        MediaModel model = MediaList.get(i);
                        if (model != null && !TextUtils.isEmpty(model.url)) {
                            stringList.add(model.url);
                        }
                    }
                    imageAdapter.update(stringList, true);
                }
            }
        }
    }

    class RenZhengAdapter extends BaseListViewAdapter<RenZhengItemData> {

        public RenZhengAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_ren_zheng;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, final int position) {
            TextView titleTextView = holder.getView(R.id.item_ren_zheng_name);
            TextView contentTextView = holder.getView(R.id.item_ren_zheng_text);
            RenZhengItemData data = list.get(position);
            titleTextView.setText(data.title);
            contentTextView.setText(data.content);

            contentTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(position).content = s.toString();
                }
            });
        }
    }


    class RenZhengItemData {
        int id;
        String title;
        String content;

        public RenZhengItemData() {

        }

        public RenZhengItemData(int id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }

    }

    class ImageGridAdapter extends BaseListViewAdapter<String> {

        public static final String DEFALUT_ADD_IMAGE_STR = "ImageGridAdapter_ADD_IMAGE";

        public ImageGridAdapter(Context context) {
            super(context);
        }

        @Override
        public void update(List<String> data, boolean isAdd) {
            if (!isAdd || list == null) {
                list = data;
            } else {
                int count = list.size() - 1;
                if (count < 0) {
                    count = 0;
                }
                list.addAll(count, data);
            }
            if (list == null || list.isEmpty()) {
                list = new ArrayList<>();
                list.add(DEFALUT_ADD_IMAGE_STR);
            } else {
                String lastStr = list.get(list.size() - 1);
                if (!DEFALUT_ADD_IMAGE_STR.equals(lastStr)) {
                    list.add(DEFALUT_ADD_IMAGE_STR);
                }
            }
            notifyDataSetChanged();
        }

        public List<String> getShowImageList() {
            if (list != null && !list.isEmpty()) {
                String lastStr = list.get(list.size() - 1);
                if (DEFALUT_ADD_IMAGE_STR.equals(lastStr)) {
                    ArrayList<String> data = new ArrayList<>();
                    for (int i = 0; i < list.size() - 1; i++) {
                        data.add(list.get(i));
                    }
                    return data;
                }
            }
            return list;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_ren_zheng_image_view;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView image = holder.getView(R.id.item_image);
            ImageView delImage = holder.getView(R.id.item_del_thumb);

            String imageUrl = list.get(position);

            if (!DEFALUT_ADD_IMAGE_STR.equals(imageUrl) && position != getCount() - 1) {
                delImage.setVisibility(View.VISIBLE);
                GlideImgManager.getInstance().showImg(context, image, imageUrl);
                delImage.setTag(imageUrl);
            } else {
                delImage.setVisibility(View.GONE);
                Glide.with(act)
                        .load(R.drawable.file_item)
                        .into(image);
            }

            delImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = (String) v.getTag();
                    if (!TextUtils.isEmpty(imageUrl)) {
                        list.remove(imageUrl);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

}
