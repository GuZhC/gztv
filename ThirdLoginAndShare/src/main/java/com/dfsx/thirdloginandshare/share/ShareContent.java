package com.dfsx.thirdloginandshare.share;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

import com.dfsx.core.CoreApp;
import com.dfsx.thirdloginandshare.R;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by fengmin@staff.tixa.com on 2015/10/20 0020.
 * 分享内容
 */
public class ShareContent  implements Serializable{

    public enum UrlType {
        Video,
        Music,
        Image,
        WebPage,
    }

    public String title;
    public String url;
    public String disc;
    //qq和微博只能给本地图片路径
    public String thumb;
    public UrlType type;
    public Boolean flag;
    public long id;
    public boolean isVote;
    public String  language;   //语言

    public ShareContent() {
        type=UrlType.WebPage;
    }

    public String getPicUrl() {
        return thumb;
    }

    public String getContent() {
        return disc;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    //    /**
//     * @return 临时分享图片文件的完全路径
//     */
//    public String getTempImageFilePath() {
//        File file = getTempImageFile();
//        if (null != file) {
//            return file.getAbsolutePath();
//        } else {
//            return null;
//        }
//    }
//
//    public File getTempImageFile() {
//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED);
//        if (!sdCardExist) {
//            return null;
//        }
//        if (image == null) {
//            return null;
//        }
//        if (null != tempFile) {
//            return tempFile;
//        }
//        String filename = ImageUtil.getfilename();
//        ImageUtil.saveBitmapToFile(this.image, filename);
//        File file = new File(filename);
//        if (null != file && file.exists()) {
//            tempFile = file;
//            return file;
//        } else {
//            return null;
//        }
//
//    }
    public boolean isVote() {
        return isVote;
    }

    public void setVote(boolean vote) {
        isVote = vote;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getSmsContent() {
        if (!TextUtils.isEmpty(this.url)) {
            return this.disc + this.url + getString(R.string.share_content);
        } else {
            return this.disc;
        }
    }

    public String getWeiXinContent() {
        if (!TextUtils.isEmpty(disc)) {
            return disc;
        } else {
            return "";
        }
    }

    public String getWeiXinTitle() {
        return "分享";
    }

    private String getString(int id) {
        return CoreApp.getInstance().getResources().getString(id);
    }
}
