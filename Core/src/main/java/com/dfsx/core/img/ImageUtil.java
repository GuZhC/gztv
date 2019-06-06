package com.dfsx.core.img;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.dfsx.core.CoreApp;

import java.io.*;
import java.lang.reflect.Field;

/**
 * 图片处理的一些工具方法。 主要是对图片进行缩放，和防止一些Bitmap的转换时的outofmemory。
 * 图片存储到本地的工具方法
 */
public class ImageUtil {

    private static String TAG = "ImageUtil";

    private static final int MIN_WIDTH = 320;
    private static final int MIN_HEIGHT = 480;
    private static int defaultWidth = 720;
    private static int defaultHeight = 1280;
    //这个用于显示长图 临时将像素点数量x2
    private static int defaultSpecifyFactor = 4;
    private static final String TEMP_PATH = "/yscms/img/";
    private static String PIC_PATH = null;

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff000000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getMinimumHeight();
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }


    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap drawableToBitmapByBD(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    /**
     * 获取本地图片缩略图
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getThumbilByLocalPath(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap getThumbilByNetPath(String imagePath, int width, int height) {
//        URL myFileUrl = null;
        if (imagePath == null)
            return null;
        try {
//            myFileUrl = new URL(imagePath);
//            HttpURLConnection conn;
//
//            conn = (HttpURLConnection) myFileUrl.openConnection();
//
//            conn.setDoInput(true);
//            conn.connect();
//            InputStream is = conn.getInputStream();
            Bitmap bitmap = Glide.with(CoreApp.getInstance()).load(imagePath)
                    .asBitmap().into(width, height).get();
            //使用Glide获取的Bitmap， 他的最终长宽比是按原图的比例缩放得到的。其值在需求的长宽比附近。
            //因此需要再校验一下图片，使最终得到图片的长宽比不大于要求的比例。达到合适的大小
            return getSmallBitmap(bitmap, width, height);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按指定宽度获取图片的缩略图。 包含网络路径和本地路径
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getThumbil(String imagePath, int width, int height) {
        if (ImageUrlUtil.isLocalPath(imagePath)) {
            return getThumbilByLocalPath(imagePath, width, height);
        } else {
            return getThumbilByNetPath(imagePath, width, height);
        }
    }

    /**
     * 按指定字节大小获取路径的缩略图。 按1个像素为2bit计算. Config.RGB_565
     * @param imagePath
     * @param sizeKB
     * @return
     */
    public static Bitmap getThumbil(String imagePath, int sizeKB) {
        int size = (int) Math.sqrt(sizeKB / 2 * 1024);
        return getThumbil(imagePath, size, size);
    }

    /**
     * 获取视频的缩略图
     *
     * @param videoPath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height) {
        System.out.println("videoPath" + videoPath);
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,
                Thumbnails.MICRO_KIND);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground,
                                         int bgWidth, int bgHeight, int fgWidth, int fgHeight, int type) {
        if (background == null) {
            return null;
        }
        Bitmap newbmp = Bitmap
                .createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        int oldWidth = foreground.getWidth();
        int oldHeight = foreground.getHeight();
        float scaleWidth = ((float) fgWidth) / oldWidth;
        float scaleHeight = ((float) fgHeight) / oldHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newFGbmp = Bitmap.createBitmap(foreground, 0, 0,
                oldWidth, oldHeight, matrix, true);
        Canvas cv = new Canvas(newbmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        cv.drawBitmap(background, 0, 0, paint);
        if (type == 1) {
            cv.drawBitmap(newFGbmp, 0, 0, paint);
        } else if (type == 2) {
            cv.drawBitmap(newFGbmp, bgWidth - fgWidth, 0, paint);
        } else if (type == 3) {
            cv.drawBitmap(newFGbmp, 0, bgHeight - fgHeight, paint);
        } else if (type == 4) {
            cv.drawBitmap(newFGbmp, bgWidth - fgWidth, bgHeight - fgHeight,
                    paint);
        } else {
            cv.drawBitmap(newFGbmp, bgWidth / 2 - fgWidth / 2, bgHeight / 2
                    - fgHeight / 2, paint);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newbmp;
    }

    public static Bitmap toConformBitmap(final Bitmap background,
                                         final Bitmap foreground) {
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth() < bgWidth / 5 ? foreground
                .getWidth() : bgWidth / 5;
        int fgHeight = foreground.getHeight() < bgHeight / 5 ? foreground
                .getHeight() : bgHeight / 5;
        return toConformBitmap(background, foreground, bgWidth, bgHeight,
                fgWidth, fgHeight, 0);
    }

    /**
     * @param background
     * @param foreground
     * @param type       0:center 1 左上对齐 2 靠右对齐 3 考下对齐 4 右下对齐
     * @return bitmap
     */
    public static Bitmap toConformBitmap(final Bitmap background,
                                         final Bitmap foreground, int type) {
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        return toConformBitmap(background, foreground, bgWidth, bgHeight,
                fgWidth, fgHeight, type);
    }

    /**
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 14;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 图片变灰
     *
     * @param context
     * @param resid
     * @return
     */
    public static Drawable toGrayDrawable(Context context, int resid) {
        Drawable drawable = context.getResources().getDrawable(resid);
        drawable.mutate();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
        drawable.setColorFilter(cf);
        return drawable;
    }

    /**
     * solve Loading IMG out of Memory
     * 按原图片大小读取。
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        return readBitMap(context, resId, Config.RGB_565);
    }

    public static Bitmap readBitMap(Context context, int resId, Config config) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inDither = true;
            opt.inInputShareable = true;
            // 获取资源图片
            InputStream is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存图片到本地路径
     *
     * @param path     本地路径
     * @param mBitmap
     * @param compress
     */
    public static void saveMyBitmap(String path, Bitmap mBitmap, int compress) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "no sdcard");
            return;
        }
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("test", "在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, compress, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩本地图片
     *
     * @param filePath
     * @param compressInt
     */
    public static void compressImage(String filePath, int compressInt) {
        try {
            Bitmap image = ImageUtil.getSmallBitmap(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, compressInt, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            baos.writeTo(fileOutputStream);
            fileOutputStream.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得最终图片的上宽比不大于传入的长宽比
     * @param image
     * @param with
     * @param height
     * @return
     */
    public static Bitmap getSmallBitmap(Bitmap image, int with, int height) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.outWidth = image.getWidth();
        newOpts.outHeight = image.getHeight();
        newOpts.inPreferredConfig = Config.RGB_565;
        newOpts.inSampleSize = calculateInSampleSize(newOpts, with, height);
//        newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap getSmallBitmap(String filePath) {
        return getSmallBitmap(filePath, defaultWidth, defaultHeight);
    }

    /**
     * 采用像素点算压缩比了
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return null;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                Log.e("test", "bimap util getSmallBitmap is null");
                return null;
            }
//			long length = file.length();
//			if(length<=0 || length > FileUtil.MAX_FILE_LENGTH){
//				Log.e("test", "bimap util getSmallBitmap is too large");
//				return null;
//			}

            final BitmapFactory.Options options = new BitmapFactory.Options();
            //		options.inPreferredConfig = Config.RGB_565;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap bm = BitmapFactory.decodeFile(filePath, options);
            if (bm == null) {
                return null;
            }
            int degree = readPictureDegree(filePath);
            bm = rotateBitmap(bm, degree);
//			ByteArrayOutputStream baos = null ;
//			try{
//				baos = new ByteArrayOutputStream();
//				bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
//
//			}finally{
//				try {
//					if(baos != null)
//						baos.close() ;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
            return bm;
        } catch (OutOfMemoryError e) {
            defaultSpecifyFactor = 1;
            //内存溢出后的处理
            if (defaultWidth > MIN_WIDTH || defaultHeight > MIN_HEIGHT) {
                defaultWidth = MIN_WIDTH;
                defaultHeight = MIN_HEIGHT;
                return getSmallBitmap(filePath, reqWidth < defaultWidth ? reqWidth : defaultWidth,
                        reqHeight < defaultHeight ? reqHeight : defaultHeight);
            }

            return getSmallBitmap(filePath, reqWidth / 2, reqHeight / 2);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int tempFactor = 2;
        if ((float) height / width > 2.5F || (float) width / height > 2.5F) {
            //长图或者是高图，像素点数量x2
            tempFactor = defaultSpecifyFactor;
        }

        final int TOTAL_PIXELS = reqWidth * reqHeight * tempFactor;
        if (TOTAL_PIXELS >= width * height * 2) {
            //像素点在要求的范围内，不做压缩
            return inSampleSize;
        }

        inSampleSize = (int) Math.ceil(Math.sqrt((width * height) / (TOTAL_PIXELS / 2.0F)));
        if (inSampleSize > 1 && inSampleSize <= 2) {
            inSampleSize = 2;
        } else if (inSampleSize > 2 && inSampleSize <= 4) {
            inSampleSize = 4;
        } else if (inSampleSize > 4 && inSampleSize <= 8) {
            inSampleSize = 8;
        }else if(inSampleSize > 8 && inSampleSize <= 16) {
            inSampleSize = 16;
        }
        Log.v("SampleSize", inSampleSize + "");
        return inSampleSize;
    }

    /**
     * 按指定宽高大小读取资源图片
     * 可以有效的防治内存溢出
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        InputStream is = res.openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 读取资源文件的图片，按照kb来返回图片. 按正方形的图片计算
     *
     * @param res
     * @param resId
     * @param sizeKB
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int sizeKB) {
        int size = (int) Math.sqrt(sizeKB / 2 * 1024);
        return decodeSampledBitmapFromResource(res, resId, size, size);
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * 检测ImageView是否在用resourceId资源来显示
     *
     * @param imageView
     * @return
     */
    public static boolean isImageViewUseResourceId(ImageView imageView, boolean defaulBool) {
        try {
            int resource = 0;
            Class ImageViewClass = Class.forName("android.widget.ImageView");
            Field field = ImageViewClass.getDeclaredField("mResource");
            field.setAccessible(true);
            resource = field.getInt(imageView);
            return resource > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaulBool;
    }

    public static String getfilename() {
        String dir = getDir();
        if (dir != null) {
            long time = System.currentTimeMillis();
            return dir + time + ".png";
        } else {
            return null;
        }

    }

    public static String getDir() {
        if (PIC_PATH == null) {
            // 判断sd卡是否存在
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
                String path = sdDir.getAbsolutePath() + TEMP_PATH;
                PIC_PATH = path;
                return path;
            } else {
                return null;
            }
        } else {
            return PIC_PATH;
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, String filename) {

        FileOutputStream fos = null;
        File file = new File(filename);
        if (file.exists()) {
            return;
        }
        if (!file.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmap2byte(bitmap));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static byte[] bitmap2byte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
