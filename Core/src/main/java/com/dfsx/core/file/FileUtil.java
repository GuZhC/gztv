package com.dfsx.core.file;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * 文件操作的一些工具类
 */
public class FileUtil {
    public static class FileType {
        public final static int TEXT = 0;
        public final static int IMAGE = 1;
        public final static int GIF = 2;
        public final static int VOICE = 3;
        public final static int VIDEO = 4;
        public final static int NOTIFI = 5;
        public final static int SHARE = 6;
    }

    // 最大的文件限制
    public static final long MAX_FILE_LENGTH = 8 * 1024 * 1024;
    public static final String TAG = "file";
    public final static String[][] MIME_MapTable = {{".amr", "audio/amr"},
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"}, {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"}, {".bmp", "image/bmp"},
            {".c", "text/plain"}, {".class", "application/octet-stream"},
            {".conf", "text/plain"}, {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"}, {".gif", "image/gif"},
            {".gtar", "application/x-gtar"}, {".gz", "application/x-gzip"},
            {".h", "text/plain"}, {".htm", "text/html"},
            {".html", "text/html"}, {".jar", "application/java-archive"},
            {".java", "text/plain"}, {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"}, {".js", "application/x-javascript"},
            {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"}, {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"}, {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"}, {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            // {".xml", "text/xml"},
            {".xml", "text/plain"}, {".z", "application/x-compress"},
            {".zip", "application/zip"}, {"", "*/*"}};

    // 以1K为最小上传单位来报告上传进度
    public static final long MIN_UNIT_UPLOAD_SQUARE = 1024;

    public static boolean canOpenFile(String fName) {
        try {
            int dotIndex = fName.lastIndexOf(".");
            if (dotIndex < 0) {
                return false;
            }
            String end = fName.substring(dotIndex, fName.length()).toLowerCase(
                    Locale.getDefault());
            if (end == "")
                return false;
            for (int i = 0; i < MIME_MapTable.length; i++) {
                if (end.equals(MIME_MapTable[i][0])) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean canOpenFile(File file) {
        try {
            String fName = file.getName();
            int dotIndex = fName.lastIndexOf(".");
            if (dotIndex < 0) {
                return false;
            }
            String end = fName.substring(dotIndex, fName.length()).toLowerCase(
                    Locale.getDefault());
            if (end == "")
                return false;
            for (int i = 0; i < MIME_MapTable.length; i++) {
                if (end.equals(MIME_MapTable[i][0])) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getFileType(File file) {
        String mime = getMIMEType(file);
        if (mime.indexOf("image") >= 0) {
            return 1;
        } else if (mime.indexOf("audio") >= 0) {
            return 3;
        } else if (mime.indexOf("video") >= 0) {
            return 4;
        } else if (mime.indexOf("application") >= 0) {
            return 10;
        } else if (mime.indexOf("text") >= 0) {
            return 11;
        } else {
            return 0;
        }
    }

    public static int getFileType(String fName) {
        String mime = getMIMEType(fName);
        if (mime.indexOf("image") >= 0) {
            return 1;
        } else if (mime.indexOf("audio") >= 0) {
            return 3;
        } else if (mime.indexOf("video") >= 0) {
            return 4;
        } else if (mime.indexOf("application") >= 0) {
            return 10;
        } else if (mime.indexOf("text") >= 0) {
            return 11;
        } else {
            return 0;
        }
    }

    public static int getFileTypes(String fName) {
        String mime = getMIMEType(fName);
        if (TextUtils.equals(fName, "image")) {
            return 1;
        } else if (TextUtils.equals(fName, "audio")) {
            return 3;
        } else if (TextUtils.equals(fName, "video")) {
            return 4;
        } else if (TextUtils.equals(fName, "application")) {
            return 10;
        } else if (TextUtils.equals(fName, "text")) {
            return 11;
        } else {
            return 0;
        }
    }

    public static String getMIMEType(String fName) {
        String type = "*/*";
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase(
                Locale.getDefault());
        if (end == "")
            return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        dotIndex = type.lastIndexOf("/");
        if (dotIndex != -1) type = type.substring(0, dotIndex);
        return type;
    }

    public static String getMIMEType(File file) {
        String fName = file.getName();
        String type = "*/*";
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase(
                Locale.getDefault());
        if (end == "")
            return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static String getSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        } else if (filePath.lastIndexOf(".") <= -1) {
            return "";
        } else {
            return filePath.substring(filePath.lastIndexOf("."));
        }
    }

    public static void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(/* uri */Uri.fromFile(file), type);
        context.startActivity(intent);
    }


    public static String saveBitmapByAccountId(String fileName,
                                               Bitmap bitmap) {
        String dic = getExternalPictureDirectory();
        File newFile = new File(dic + fileName);
        try {
            File file = new File(dic);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            return null;
        }

        return newFile.getAbsolutePath();
    }


    /**
     * 根据accountId获取对象
     *
     * @param context
     * @param fileName
     * @param accountId
     * @param obj
     */
    public static void saveFileByAccountId(Context context, String fileName,
                                           String accountId, Object obj) {
        saveFile(getFileDicByAccountId(context, accountId), fileName, obj);
    }


    public static Object getFileByAccountId(Context context, String fileName,
                                            String accountId) {
        return getFile(getFileDicByAccountId(context, accountId) + fileName);
    }

    public static String getFileDicByAccountId(Context context, String accountId) {
        return context.getFilesDir().getPath() + File.separator + accountId
                + File.separator;
    }

    public static String getExternalPictureDirectory() {
        return Environment.getExternalStorageDirectory() + File.separator + "Picture"
                + File.separator;
    }

    public static String getExternalWebimgCaheDirectory(Context context) {
        return context.getFilesDir().getPath() + File.separator + "Webimg"
                + File.separator;
    }

    public static boolean delFileByAccontId(Context context, String fileName,
                                            String accountId) {
        try {
            File dfile = new File(getFileDicByAccountId(context, accountId));
            if (!dfile.exists()) {
                return false;
            }
            File file = new File(getFileDicByAccountId(context, accountId)
                    + fileName);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveFile(String dic, String fileName, Object obj) {
        // LoggerUtil.v(TAG, "saveFile fileName = "+ dic+fileName);
        ObjectOutputStream oos = null;
        try {
            File file = new File(dic);
            if (!file.exists()) {
                file.mkdirs();
            }
            File newFile = new File(dic + fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            oos = new ObjectOutputStream(new FileOutputStream(newFile));
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static Object getFile(String fileName) {
        // LoggerUtil.v(TAG, "getFile fileName = "+ fileName);
        try {
            File file = new File(fileName);
            if (file.exists() && !file.isDirectory()) {
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file));
                Object obj = ois.readObject();
                ois.close();
                return obj;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFileByTime(String fileName, long distance) {
        try {
            File file = new File(fileName);
            if (file.exists() && !file.isDirectory()) {
                long time = file.lastModified();
                // 如果超过时间间隔了，返回空
                if (System.currentTimeMillis() - time > distance) {
                    Log.e(
                            TAG,
                            "getFile ditance is = "
                                    + (System.currentTimeMillis() - time));
                    return null;
                }
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file));
                Object obj = ois.readObject();
                ois.close();
                return obj;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return -1;
        }
        if (!fromFile.isFile()) {
            return -2;
        }
        if (!fromFile.canRead()) {
            return -3;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (!toFile.exists()) {
            toFile.mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {

            FileInputStream fosfrom = new FileInputStream(
                    fromFile);

            FileOutputStream fosto = new FileOutputStream(toFile);

            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0) {

                fosto.write(bt, 0, c);

            }

            fosfrom.close();

            fosto.close();
            return 1;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return -4;

        }

    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 读取文件的某一段
     *
     * @param fileName
     * @param buffer
     * @param byteOffset
     * @param length
     * @return
     */
    public static byte[] readFileByRange(String fileName, byte[] buffer,
                                         int byteOffset, int length) {
        try {
            RandomAccessFile rFile = new RandomAccessFile(fileName, "rw");
            rFile.read(buffer, byteOffset, length);
            rFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static byte[] readFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            if (file.length() <= 0 || file.length() > MAX_FILE_LENGTH) {
                // 文件过大
                Log.e(TAG, "fileUtil readFile file is too large!!!");
                return null;
            }
            FileInputStream inStream = new FileInputStream(new File(filename));
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            inStream.close();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] mContent = baos.toByteArray();
            return mContent;
        } else {
            return null;
        }
    }

    public static int downloadFile(String fromFilePath, String toFilePath) {
        return downloadFile(fromFilePath, toFilePath, null);
    }

    /**
     * 下载文件
     */
    public static int downloadFile(String fromFilePath, String toFilePath,
                                   DownLoadListener listener) {
        int result = -1;
        if (TextUtils.isEmpty(fromFilePath) || TextUtils.isEmpty(toFilePath)) {
            Log.e(TAG, "downloadFile args invalid");
            return result;
        }
        if (toFilePath.endsWith(File.separator)) {
            Log.e(TAG, "downloadFile args invalid");
            return result;
        }
        try {
            URL url = new URL(fromFilePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            if (conn != null) {
                int fileSize = conn.getContentLength();
                if (listener != null) {
                    listener.preDownLoad(fileSize);
                }
            }
            File tempf = new File(toFilePath).getParentFile();
            if (!tempf.exists()) {
                tempf.mkdirs();
            }
            File outputFile = new File(toFilePath);
            int downLoadedSize = 0;
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(outputFile);
                byte[] bt = new byte[1024];
                int i = 0;
                while ((i = is.read(bt)) > 0) {
                    fos.write(bt, 0, i);
                    downLoadedSize += i;
                    if (listener != null) {
                        listener.onDownLoading(downLoadedSize);
                    }
                }
                fos.flush();
                fos.close();
                is.close();
                result = 1;
            } else {
                result = -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        if (listener != null) {
            listener.afterDownLoad(result);
        }
        return result;
    }

    /**
     * 本地日志记录
     */
    public static void writeStringToFile(String filepath, String fileName,
                                         String info) {
        // SimpleDateFormat dateFormat = new SimpleDateFormat(
        // "yyyy/MM/dd hh:mm:ss");
        // Date date = new Date(creatTime);
        // String createTime = dateFormat.format(date);
        String record = info + "\n";
        if (SDCardUtil.isSDCardExistAndNotFull()) {
            try {
                if (!new File(filepath).exists()) {
                    new File(filepath).mkdirs();
                }
                File newFile = new File(filepath + fileName);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(newFile, true);
                fos.flush();
                fos.write(record.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getFileSize(File file) throws Exception {
        if (file.exists()) {
            return file.length();
        } else {
            return 0;
        }
    }

    public static long getFolderSize(File folder) {
        long size = 0;
        try {
            File flist[] = folder.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFolderSize(flist[i]);
                } else {
                    size = size + getFileSize(flist[i]);
                }
            }
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public interface DownLoadListener {
        void preDownLoad(int fileSize);

        void onDownLoading(int downLoaderSize);

        void afterDownLoad(int result);
    }
}
