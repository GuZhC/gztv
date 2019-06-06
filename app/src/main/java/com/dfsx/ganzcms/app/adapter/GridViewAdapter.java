package com.dfsx.ganzcms.app.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.util.ImageProcessView;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.MyVideoThumbLoader;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private static final int TYPE_IMAGES = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_OTHER = 2;
    private static final int TYPE_CONUT = 3;
    private int nItemCount = 9;
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private boolean shape;
    private ArrayList<MediaModel> adList = new ArrayList<MediaModel>();
    MyVideoThumbLoader myVideoThumbLoader;
    public boolean bInit;
    private int mWidth;
    Context mContext;

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public GridViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        myVideoThumbLoader = new MyVideoThumbLoader();
        bInit = false;
        mContext = context;
    }

    public void setMaxNumber(int number) {
        nItemCount = number;
    }

    public void addTail(String url, int type, double laf, double longf) {
        MediaModel item = new MediaModel();
        item.name = UtilHelp.getNameFromUrl(url);
        item.url = url;
        item.type = type;
        item.latf = laf;
        item.longf = longf;
        adList.add(item);
        notifyDataSetChanged();
    }

    public void addVideo(String url) {
        MediaModel item = new MediaModel();
        item.name = UtilHelp.getNameFromUrl(url);
        item.url = url;
        item.type = 1;
        adList.add(item);
        notifyDataSetChanged();
    }

    public void addTail(MediaModel ery) {
        ery.name = UtilHelp.getNameFromUrl(ery.url);
        adList.add(ery);
        notifyDataSetChanged();
    }

    public ArrayList<MediaModel> getAllItems() {
        return adList;
    }

    public void removeAtIndex(int pos) {
        if (!adList.isEmpty()) {
            adList.remove(pos);
            notifyDataSetChanged();
        }
    }

    public void removeAlls() {
        if (!adList.isEmpty()) {
//            for (int i = 0; i < adList.size(); i++) {
//                MediaModel entry = adList.get(i);
//                if (entry.type == 0) {
//                    String path = entry.url;
//                    int index = path.lastIndexOf('/');
//                    String dir = path.substring(index - 5, index);
//                    if (("crash").equals(dir)) {
//                        File file = new File(path);
//                        if (file.exists()) {
//                            file.delete();
//                        }
//                    }
//                }
//            }
            adList.clear();
            notifyDataSetChanged();
        }
    }

    public void update(ArrayList<String> data, boolean bAddTail) {
        if (!data.isEmpty()) {
//                for(int i=0;i<data.size();i++)
            for (int i = 0; i < 1; i++) {
                MediaModel item = new MediaModel();
                item.url = data.get(i).toString().trim();
                item.type = 0;
                adList.add(item);
            }
//                if(bAddTail) {
//                    adList=data;
////                    adList.addAll(data);
//                }else {
//                    adList = data;
//                }
//                bInit = true;
            notifyDataSetChanged();
        }
    }

    public void updates(ArrayList<MediaModel> data, boolean bAddTail) {
        if (data != null && !data.isEmpty()) {
            boolean noData = false;
            if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                if (adList.size() >= data.size()
                        && adList.get(adList.size() - 1).url == data.get(data.size() - 1).url)
                    noData = true;
                else
                    adList.addAll(data);
            else {
                if (adList != null) {
//                        if(/*items.size() == data.size() && */
//                                items.size()  > 0 &&
//                                        items.get(0).id == data.get(0).id && items.get(0).doc_id==data.get(0).doc_id)
//                            noData = true;
                }
                if (!noData) {
                    adList = data;
                }
            }
            bInit = true;
            if (!noData)
                notifyDataSetChanged();
        } else {
            if (!bAddTail) {
                adList.clear();
                notifyDataSetChanged();
            }
        }
    }

    public int getCount() {
        if (adList.size() == nItemCount) {
            return nItemCount;
        }
        return (adList.size() + 1);
    }

    public int getSize() {
        return adList.size();
    }

    public Object getItem(int arg0) {
        return adList.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public int getItemViewType(int position) {
        if (adList.isEmpty()) {
            return TYPE_IMAGES;
        } else {
            if (position >= adList.size()) {
                return TYPE_IMAGES;
            } else {
                if ("0".equals(adList.get(position).type + "")) {
                    return TYPE_IMAGES;
                } else if ("1".equals(adList.get(position).type + "")) {
                    return TYPE_VIDEO;// 充值类型
                } else if ("2".equals(adList.get(position).type + "")) {
                    return TYPE_OTHER;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_CONUT;
    }

    class ViewHolder {
        public MediaModel item;
        public int pos;
        public ImageProcessView imag;
        public ImageView videoImag;
        public ImageView delImag;
    }

    public void delByTitle(String name) {
        if (adList != null && !adList.isEmpty()) {
            int pos = 0;
            for (MediaModel model : adList) {
                if (TextUtils.equals(name, model.name)) {
                    adList.remove(pos);
                    break;
                }
                pos++;
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View imgeView = null;
        View videoView = null;
        View otherView = null;
        int currentType = getItemViewType(position);
        if (currentType == TYPE_IMAGES) {
            ViewHolder holder = null;
            if (convertView == null) {
                imgeView = inflater.inflate(R.layout.item_gridview_video,
                        parent, false);
                holder = new ViewHolder();
                holder.imag = (ImageProcessView) imgeView
                        .findViewById(R.id.item_grida_image);
                holder.videoImag = (ImageView) imgeView
                        .findViewById(R.id.image_play_view);
                holder.delImag = (ImageView) imgeView.findViewById(R.id.commeng_ing_del_thumb);
                if (!adList.isEmpty() && position < adList.size())
                    holder.item = adList.get(position);
                imgeView.setTag(holder);
                holder.delImag.setTag(holder);
                convertView = imgeView;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.pos = position;
            holder.videoImag.setVisibility(View.GONE);
            holder.imag.setProgress(-1);
            holder.delImag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder != null) {
//                        delByTitle(holder.item.name);
                        adList.remove(holder.pos);
                        notifyDataSetChanged();
                        MediaChooserConstants.SELECTED_MEDIA_COUNT--;
                    }
                }
            });
            if (position == adList.size()) {
                holder.delImag.setVisibility(View.GONE);
                UtilHelp.LoadImageErrorUrl(holder.imag, "", null, R.drawable.file_item);
                if (position == nItemCount) {
                    holder.imag.setVisibility(View.GONE);
                }
            } else {
                holder.delImag.setVisibility(View.VISIBLE);
                String sp = adList.get(position).url.toString().trim();
//                String imagUrl = ImageDownloader.Scheme.FILE.wrap(sp);
                String imagUrl = "file:///" + sp;
                UtilHelp.LoadImageFormUrl(holder.imag, imagUrl, null);
            }
        } else if (currentType == TYPE_VIDEO) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                videoView = inflater.inflate(R.layout.item_gridview_video,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imag = (ImageProcessView) videoView
                        .findViewById(R.id.item_grida_image);
                viewHolder.videoImag = (ImageView) videoView
                        .findViewById(R.id.image_play_view);
                viewHolder.delImag = (ImageView) videoView.findViewById(R.id.commeng_ing_del_thumb);
                if (!adList.isEmpty() && position < adList.size())
                    viewHolder.item = adList.get(position);
                videoView.setTag(viewHolder);
                viewHolder.delImag.setTag(viewHolder);
                convertView = videoView;
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == adList.size()) {
                viewHolder.delImag.setVisibility(View.GONE);
                UtilHelp.LoadImageFormUrl(viewHolder.imag, "", null);
                viewHolder.imag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_item));
                if (position == nItemCount) {
                    viewHolder.imag.setVisibility(View.GONE);
                    viewHolder.videoImag.setVisibility(View.GONE);
                }
            } else {
                viewHolder.delImag.setVisibility(View.VISIBLE);
                viewHolder.delImag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder != null) {
//                        delByTitle(holder.item.name);
                            adList.remove(holder.pos);
                            notifyDataSetChanged();
                            MediaChooserConstants.SELECTED_MEDIA_COUNT--;
                        }
                    }
                });
                viewHolder.videoImag.setVisibility(View.VISIBLE);
                String videoPath = adList.get(position).url.toString().trim();
//                        new VideoLoadAsync(this.frag, viewHolder.imag, false, imageParams.width).executeOnExecutor(MediaAsync.SERIAL_EXECUTOR, videoPath);
                myVideoThumbLoader.showThumbByAsynctack(videoPath, viewHolder.imag);
//                Bitmap  bitmap=getVideoThumbnail(videoPath);
//                viewHolder.imag.setImageBitmap(bitmap);
            }
        } else if (currentType == TYPE_OTHER) {
            //其他  audio doc
            ViewHolder holder = null;
            if (convertView == null) {
                imgeView = inflater.inflate(R.layout.item_gridview_video,
                        parent, false);
                holder = new ViewHolder();
                holder.imag = (ImageProcessView) imgeView
                        .findViewById(R.id.item_grida_image);
                holder.videoImag = (ImageView) imgeView
                        .findViewById(R.id.image_play_view);
                if (!adList.isEmpty() && position < adList.size())
                    holder.item = adList.get(position);

                imgeView.setTag(holder);
                convertView = imgeView;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == adList.size()) {
                UtilHelp.LoadImageFormUrl(holder.imag, "", null);
                holder.imag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_item));
                holder.videoImag.setVisibility(View.GONE);
                if (position == nItemCount) {
                    holder.imag.setVisibility(View.GONE);
                }
            } else {
                holder.videoImag.setVisibility(View.VISIBLE);
                holder.videoImag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audeo_icon));
                holder.imag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.upfile_other_default));
            }
        }

        return convertView;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

//        public void loading() {
//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                            break;
//                        } else {
//                            Bimp.max += 1;
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                        }
//                    }
//                }
//            }).start();
//        }


    public Bitmap getVideoThumbnail(String filename) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 是否进行抖动
        options.inDither = false;
        // 设置位图颜色配置
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 定义查询external-38323432.db数据库中video表的条件
        String whereClause = MediaStore.Video.Media.DATA + " = '" + filename + "'";
        // 在video表中查询指定的视频记录
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, whereClause, null, null);
        // delete为true，表示从video表中删除指定的视频记录
        boolean delete = false;
        // 在video表中未查到视频的记录，则在video表中插入一条视频记录
        if (cursor == null || cursor.getCount() == 0) {
            // 用于保存插入记录数据的ContentValues对象
            ContentValues values = new ContentValues();
            // 保存video._data字段的值
            values.put(MediaStore.Video.Thumbnails.DATA, filename);
            // 向video表中插入记录
            mContext.getContentResolver().insert(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, values);
            // 重新查询video表中的记录
            cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, whereClause, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            // 在获取视频缩略图后将插入的记录删除（相当于卸磨杀驴）
            delete = true;
        }
        cursor.moveToFirst();
        // 获取视频记录的ID
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));

        if (videoId == null) {
            return null;
        }
        cursor.close();
        // 将视频记录的ID转换成long类型数据
        long videoIdLong = Long.parseLong(videoId);
        // 获取视频的缩略图
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), videoIdLong, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        if (delete) {
            // 删除插入的视频记录
            mContext.getContentResolver().delete(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, MediaStore.Video.Media._ID + "=?", new String[]{filename});
        }
        return bitmap;
    }
}