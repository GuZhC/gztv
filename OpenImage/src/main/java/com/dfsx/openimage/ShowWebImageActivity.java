package com.dfsx.openimage;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.img.ImageUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ShowWebImageActivity extends BaseActivity {
    public static final String IMAGE_URLS = "image_urls";
    public static final String POSITION = "position";
    public static final String IMAGE_URL_ARRAY = "image_url_array";
    private String[] imageArray;
    //	private ImageLoader imageLoader;
    //	private DisplayImageOptions options;
    private int position;
    private GestureImageView[] mImageViews;
    private MyViewPager viewPager;
    private TextView page;
    private int count;
    private ImageView downloadbtn;
    private int  cureentIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_image_activity);
        getIntentValue();
        //		imageLoader = ImageLoader.getInstance();
        //		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        //				this).threadPriority(Thread.NORM_PRIORITY - 2)
        //				.threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(this))
        //				.denyCacheImageMultipleSizesInMemory()
        //				.discCacheFileNameGenerator(new Md5FileNameGenerator())
        //				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
        //		imageLoader.init(config);
        //		options = new DisplayImageOptions.Builder().cacheInMemory(true)
        //				.cacheOnDisc(false).considerExifParams(true)
        //				.bitmapConfig(Bitmap.Config.RGB_565).build();
        initViews();
    }

    private void getIntentValue() {
        Intent intent = getIntent();
        String urls = intent.getStringExtra(IMAGE_URLS);
        position = intent.getIntExtra(POSITION, 0);
        if (TextUtils.isEmpty(urls)) {
            imageArray = intent.getStringArrayExtra(IMAGE_URL_ARRAY);
        } else {
            imageArray = urls.split(",");
        }
        for (int i = 0; i < imageArray.length; i++) {
            //			imageArray[i]+="?w=330&h=0&s=2";
        }

        count = imageArray.length;
    }

    private void initViews() {
        downloadbtn = (ImageView) findViewById(R.id.download_img);
        downloadbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveimg();
            }
        });
        page = (TextView) findViewById(R.id.text_page);
        if (count <= 1) {
            page.setVisibility(View.GONE);
        } else {
            page.setVisibility(View.VISIBLE);
            page.setText((position + 1) + "/" + count);
        }

        viewPager = (MyViewPager) findViewById(R.id.web_image_viewpager);
        viewPager.setPageMargin(20);
        viewPager.setAdapter(new ImagePagerAdapter(getWebImageViews()));
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(9);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            public void onPageSelected(int arg0) {
                cureentIndex=arg0;
                page.setText((arg0 + 1) + "/" + count);
                mImageViews[arg0].reset();
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void saveimg() {
        new TedPermission(ShowWebImageActivity.this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                downLoadIamge();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                ToastUtils.toastMsgFunction(ShowWebImageActivity.this, "没有权限");
            }
        }).setDeniedMessage(getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    private String saveImageUrl;
    private String saveFilePath;

    private void downLoadIamge() {
        if (imageArray != null && imageArray.length > 0) {
            if (cureentIndex >= 0 && cureentIndex < imageArray.length) {
                saveImageUrl = imageArray[cureentIndex];
                if (!TextUtils.isEmpty(saveImageUrl)) {
//                    String fileName = saveImageUrl.substring(saveImageUrl.lastIndexOf("/") + 1,
//                            saveImageUrl.length());
                    int len = saveImageUrl.lastIndexOf("?");
                    if (len == -1) len = saveImageUrl.length();
                    String fileName = saveImageUrl.substring(saveImageUrl.lastIndexOf("/") + 1,
                            len);
                    Log.e("TAG", "fileName == " + fileName);
                    String dir = Environment.getExternalStorageDirectory().getPath() + "/";
                    File dirFile = new File(dir);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    saveFilePath = dir + "download/" + fileName;
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
//                            Looper.prepare();
                            try {
                                Bitmap saveTempBitmap = Glide.
                                        with(ShowWebImageActivity.this).
                                        load(saveImageUrl).
                                        asBitmap().
                                        into(-1, -1).
                                        get();
                                ImageUtil.saveBitmapToFile(saveTempBitmap, saveFilePath);
                                return true;
                            } catch (final ExecutionException e) {
                                e.printStackTrace();
                            } catch (final InterruptedException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            Toast.makeText(ShowWebImageActivity.this, "图片保存到 " + saveFilePath, Toast.LENGTH_SHORT).show();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(saveFilePath))));
                        }
                    }.execute();

                }
            }
        }
    }

    private static int getMemoryCacheSize(Context context) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
            // limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }
        return memoryCacheSize;
    }

    private List<View> getWebImageViews() {
        mImageViews = new GestureImageView[count];
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < count; i++) {
            View view = layoutInflater.inflate(R.layout.web_image_item, null);
            final GestureImageView imageView = (GestureImageView) view
                    .findViewById(R.id.image);
            final ProgressBar progressBar = (ProgressBar) view
                    .findViewById(R.id.loading);
            mImageViews[i] = imageView;

//            Glide.with(this)
//                    .load(imageArray[i])
//                    //                .override(width, height)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
//                    //					.centerCrop()//对图片进行裁剪
//                    //					.placeholder(R.drawable.main_page_zhibo)//加载图片之前显示的图片
//                    //					.error(R.drawable.main_page_zhibo)//加载图片失败的时候显示的默认图
//                    .crossFade()//让图片显示的时候带有淡出的效果
//                    .into(imageView);

//            Glide
//                    .with(this)
//                    .load(imageArray[i])
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//图片缓存策略,这个一般必须有
//                    .thumbnail(0.1f)
//                    .crossFade()//让图片显示的时候带有淡出的效果
//                    .into(imageView);

            //缩略图请求   新版本
            DrawableRequestBuilder thumbnailRequest = Glide
                    .with(this)
                    .load(imageArray[i]);
            Glide.with(this).
                    load(imageArray[i]).
//                    override(80, 80).//设置最终显示的图片像素为80*80,注意:这个是像素,而不是控件的宽高
        skipMemoryCache(true).//跳过内存缓存
                    diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                    thumbnail(thumbnailRequest).//设置缩略图
                    into(imageView);

            //			Util.LoadThumebImage(imageView, imageArray[i], progressBar);

            //			LoadImageFormUrl(imageView, imageArray[i], progressBar);
            //			imageLoader.displayImage(imageArray[i], imageView, options,
            //
            //					new SimpleImageLoadingListener() {
            //
            //						@Override
            //						public void onLoadingComplete(String imageUri,
            //								View view, Bitmap loadedImage) {
            //							progressBar.setVisibility(View.GONE);
            //						}
            //
            //						@Override
            //						public void onLoadingFailed(String imageUri, View view,
            //								FailReason failReason) {
            //							progressBar.setVisibility(View.GONE);
            //						}
            //
            //						@Override
            //						public void onLoadingStarted(String imageUri, View view) {
            //							progressBar.setVisibility(View.VISIBLE);
            //						}
            //
            //					});
            imageView.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    finish();
                }
            });
            views.add(view);
        }
        viewPager.setGestureImages(mImageViews);
        return views;
    }

    public void LoadImageFormUrl(final ImageView img, String imageUrl, final ProgressBar spinner) {

        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }
        GlideImgManager.getInstance().showImg(img.getContext(), img, imageUrl,
                new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mImageViews != null) {
            mImageViews = null;
        }
        //		imageLoader.clearMemoryCache();
        super.onDestroy();
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private List<View> views;

        public ImagePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
