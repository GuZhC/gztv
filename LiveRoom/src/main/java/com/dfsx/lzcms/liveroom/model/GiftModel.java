package com.dfsx.lzcms.liveroom.model;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.dfsx.core.img.GlideImgManager;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class GiftModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 800396966409270869L;

    private long id; //就是礼物的type值
    private String name;
    private int price;
    private int resId;
    private boolean isSelected;
    @SerializedName("category_id")
    private long categoryId;
    @SerializedName("icon_id")
    private long iconId;
    private int count; //此礼物 收到的个数
    /**
     * 礼物图片的路径
     */
    @SerializedName("icon_url")
    private String imgPath;
    /**
     * 礼物动画效果图片
     */
    private String gifPath;
    /**
     * 本地资源文件的图片
     */
    private int imgResId1;
    /**
     * 本地动画效果图片的资源id
     */
    private int imgResId2;
    private int num; //礼物的数量. 礼物的总数
    private int selectedNum;//选择的数量

    public GiftModel() {

    }

    public GiftModel(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public GiftModel(int id, String name, int price, int resId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.resId = resId;
    }

    public GiftModel(JSONObject json) {
        this.id = json.optInt("type");
        this.name = json.optString("name");
        this.price = json.optInt("price") * 1000;
        this.imgPath = json.optString("logo");
        this.num = json.optInt("num");
        this.count = json.optInt("pool");
    }

    public GiftModel(JSONObject json, boolean fromMeetingRoom) {
        this.id = json.optInt("id");
        this.name = json.optString("name");
        this.price = json.optInt("cose");
        this.imgPath = json.optString("icon");
        this.count = json.optInt("num");
    }

    /**
     * 优先使用图片的路径显示图片。 其次用资源文件，
     * 如果没有Gif的资源图片就使用resId1
     *
     * @param context
     * @param imageView
     */
    public void showImageView(Context context, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        if (!isSelected) {
            if (!TextUtils.isEmpty(imgPath)) {
                GlideImgManager.getInstance().
                        showImg(context, imageView, imgPath);
            } else {
                imageView.setImageResource(imgResId1);
            }
        } else {
            showSelectedImage(context, imageView);
        }
    }

    public void showGifImage(Context context, ImageView imageView) {
        showSelectedImage(context, imageView);
    }

    /**
     * 显示选中状态的礼物图片显示
     *
     * @param context
     * @param imageView
     */
    private void showSelectedImage(Context context, ImageView imageView) {
        if (!TextUtils.isEmpty(gifPath)) {
            GlideImgManager.getInstance().
                    showGif(context, imageView, gifPath);
        } else if (!TextUtils.isEmpty(imgPath)) {
            GlideImgManager.getInstance().
                    showImg(context, imageView, imgPath);
        } else {
            if (imgResId2 != 0) {
                GlideImgManager.getInstance().
                        showGif(context, imageView, imgResId2);
            } else {
                imageView.setImageResource(imgResId1);
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public long getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSelectedNum() {
        return selectedNum;
    }

    public void setSelectedNum(int selectedNum) {
        this.selectedNum = selectedNum;
    }

    public String getGifPath() {
        return gifPath;
    }

    public void setGifPath(String gifPath) {
        this.gifPath = gifPath;
    }

    public int getImgResId1() {
        return imgResId1;
    }

    public void setImgResId1(int imgResId1) {
        this.imgResId1 = imgResId1;
    }

    public int getImgResId2() {
        return imgResId2;
    }

    public void setImgResId2(int imgResId2) {
        this.imgResId2 = imgResId2;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getIconId() {
        return iconId;
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }
}
