package com.dfsx.core.img;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by liuwb on 2016/5/11.
 */
public class ImageUrlUtil {
    /**
     * 格式化图片路径，如果是本地路径，就判断有没有file://。如果是网络图片，判断有没有http
     *
     * @param imgUrl
     * @return
     */
    public static String formatPictureUrl(String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return imgUrl;
        }

        if (isLocalPath(imgUrl) && !imgUrl.startsWith("file://")) {
            imgUrl = "file://" + imgUrl;
        } else if (!isLocalPath(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = getImagePath(imgUrl);
        }

        return imgUrl;
    }

    /**
     * 是否是本地路径
     *
     * @param path
     * @return
     */
    public static boolean isLocalPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists() || path.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())
                || path.startsWith("file://")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getImagePath(String filepath_src) {
        String filepath = filepath_src.toLowerCase();

        int nStartIndex = filepath.indexOf("src=");
        int nEndIndex = filepath.indexOf(".jpg");
        String fileuri = "";
        if (nEndIndex == -1) {
            nEndIndex = filepath.indexOf(".png");
            if (nEndIndex > nStartIndex)
                fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 4);
            if (nEndIndex == -1) {
                nEndIndex = filepath.indexOf(".jpeg");
                if (nEndIndex > nStartIndex)
                    fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 5);
            }
        } else {
            if (nEndIndex > nStartIndex)
                fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 4);
        }
        if (TextUtils.isEmpty(fileuri)) {
            fileuri = filepath;
        }
        return fileuri;
    }
}
