package com.dfsx.ganzcms.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.IUploadFile;
import com.dfsx.ganzcms.app.model.UploadFile;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class FileUploadAdapter extends BaseGridListAdapter<IUploadFile> {

    public static final int TYPE_GRID = 0;
    public static final int TYPE_CARTGROY = 1;
    public static final int TYPE_COUNT = 2;

    private OnClickEventListener eventListener;

    public FileUploadAdapter(Context context) {
        super(context);
    }

    @Override
    protected ArrayList<ArrayList<IUploadFile>> toGridList(List<IUploadFile> list) {
        ArrayList<ArrayList<IUploadFile>> pairList = new ArrayList<>();
        if (list != null) {
            ArrayList<IUploadFile> pairData = null;
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                if (count % getColumnCount() == 0 || list.get(i).getShowType() != TYPE_GRID) {
                    pairData = new ArrayList<>();
                    pairList.add(pairData);
                    count = 0;
                }
                if (pairData != null) {
                    pairData.add(list.get(i));
                }
                if (list.get(i).getShowType() != TYPE_GRID) {
                    count = 0;
                } else {
                    count++;
                }
            }
        }
        return pairList;
    }

    public ArrayList<ArrayList<IUploadFile>> getGridData() {
        return gridList;
    }

    @Override
    public void addRoomList(List<IUploadFile> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (gridList.isEmpty()) {
            gridList.addAll(toGridList(list));
        } else {
            ArrayList<IUploadFile> lastPair = gridList.get(gridList.size() - 1);
            if (lastPair != null && lastPair.size() < getColumnCount()) {
                int count = getColumnCount() - lastPair.size();
                int listSize = list.size();
                count = Math.min(count, listSize);
                for (int i = 0; i < count; i++) {
                    IUploadFile r = list.get(0);//把新来的列表的头几个数据放到上面没有补齐的数据中
                    if (r != null && r.getShowType() == TYPE_GRID) {
                        lastPair.add(r);
                        list.remove(r);
                    } else {
                        break;
                    }
                }
            }
            gridList.addAll(toGridList(list));
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        ArrayList<IUploadFile> files = gridList.get(position);
        return files.get(0).getShowType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_GRID) {
            return super.getView(position, convertView, parent);
        } else {
            BaseViewHodler hodler = BaseViewHodler.get(convertView,
                    parent, R.layout.adapter_edit_words_type, position);
            setWordsTypeClassifyData(hodler, position);
            return hodler.getConvertView();
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getGridItemLayoutId() {
        return R.layout.adapter_upload_file;
    }

    @Override
    protected int getHDLeftDivideLineRes() {
        return R.color.white;
    }

    @Override
    protected int getHDLeftDivideLineWidth() {
        return PixelUtil.dp2px(context, 15);
    }

    @Override
    protected int getHDRightDivideLineRes() {
        return R.color.white;
    }

    @Override
    protected int getHDRightDivideLineWidth() {
        return PixelUtil.dp2px(context, 15);
    }

    @Override
    protected int getHDivideLineWidth() {
        return PixelUtil.dp2px(context, 10);
    }

    @Override
    protected int getHDivideLineRes() {
        return R.color.white;
    }

    @Override
    public void setGridItemViewData(BaseViewHodler hodler, IUploadFile data) {
        ImageView fileThumbImage = hodler.getView(R.id.upload_file_thumb_image);
        ImageView videoTagView = hodler.getView(R.id.image_play_view);
        ImageView deleteTagView = hodler.getView(R.id.commeng_ing_del_thumb);
        if (!TextUtils.isEmpty(data.getFileThumbImage())) {
            Glide.with(context)
                    .load(data.getFileThumbImage())
                    .into(fileThumbImage);
        } else {
            new LoadingVideoThumbImage(fileThumbImage, data.getFileUrl()).load();
        }

        if (TextUtils.isEmpty(data.getFileUrl())) {
            deleteTagView.setVisibility(View.GONE);
            videoTagView.setVisibility(View.GONE);
        } else {
            deleteTagView.setVisibility(View.VISIBLE);
            if (TextUtils.equals(data.getFileType(), UploadFile.FILE_VIDEO)) {
                videoTagView.setVisibility(View.VISIBLE);
            } else {
                videoTagView.setVisibility(View.GONE);
            }
        }
        deleteTagView.setTag(data);
        deleteTagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventListener != null) {
                    eventListener.onDeleteClick((IUploadFile) v.getTag());
                }
            }
        });
    }

    protected void setWordsTypeClassifyData(BaseViewHodler hodler, int position) {
        TextView classifyText = hodler.getView(R.id.tv_upload_file_type);
        View divideView = hodler.getView(R.id.divide_line);
        ArrayList<IUploadFile> files = gridList.get(position);
        if (files != null && files.size() > 0) {
            classifyText.setText(files.get(0).getShowTypeText());
        }
        divideView.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
    }

    public void setOnClickEventListener(OnClickEventListener l) {
        this.eventListener = l;
    }

    public interface OnClickEventListener {
        void onDeleteClick(IUploadFile file);
    }

    class LoadingVideoThumbImage {
        private ImageView imageView;
        private String url;

        public LoadingVideoThumbImage(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void load() {
            Observable.just(this)
                    .observeOn(Schedulers.io())
                    .map(new Func1<LoadingVideoThumbImage, Bitmap>() {
                        @Override
                        public Bitmap call(LoadingVideoThumbImage loadingVideoThumImage) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = ThumbnailUtils.createVideoThumbnail(loadingVideoThumImage.getUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                                int mMaxWidth = Math.max(loadingVideoThumImage.getImageView().getWidth(), 250);
                                if (bitmap != null) {
                                    bitmap = Bitmap.createScaledBitmap(bitmap, mMaxWidth, mMaxWidth, false);
                                    return bitmap;
                                }
                                return null;
                            } catch (Exception e) {
                                if (e != null) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            imageView.setImageResource(R.drawable.glide_default_image);
                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(R.drawable.glide_default_image);
                            }
                        }
                    });
        }
    }
}