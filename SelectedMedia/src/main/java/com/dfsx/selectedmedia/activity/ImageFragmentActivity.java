package com.dfsx.selectedmedia.activity;/*
 * heyang  2015-10-19  video select
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.selectedmedia.MediaChooser;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.R;
import com.dfsx.selectedmedia.fragment.BucketVideoFragment;
import com.dfsx.selectedmedia.fragment.ImageFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ImageFragmentActivity extends BaseActivity implements ImageFragment.OnImageSelectedListener {
    public static final String KEY_SINGLE_MODE = "com.dfsx.selectedmedia.activity.ImageFragmentActivity_KEY_SINGLE_MODE";
    public static final String KEY_MAX_MODE = "com.dfsx.selectedmedia.activity.ImageFragmentActivity_KEY_MAX_MODE";
    public static final String KEY_CURRENT_NUMBER = "com.dfsx.selectedmedia.activity.ImageFragmentActivity_KEY_CURRENT_NUMBER";
    private TextView headerBarTitle;
    private ImageView headerBarCamera;
    private ImageView headerBarBack;
    private TextView headerBarDone;
    private TextView mSeletcteTx;

    public static android.support.v4.app.FragmentManager fm;
//    BucketVideoFragment  videoFragment =null;
//    BucketImageFragment imageFragment =null;

    ImageFragment imageFragment;

    private static Uri fileUri;
    private ArrayList<MediaModel> mSelectedVideo = new ArrayList<MediaModel>();
    private ArrayList<MediaModel> mSelectedImage = new ArrayList<MediaModel>();
    private final Handler handler = new Handler();


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_media_chooser);
//		FileUtils.SELECTED_MEDIA_COUNT=0;

        mSeletcteTx = (TextView) findViewById(R.id.seltect_item_txt);
        headerBarTitle = (TextView) findViewById(R.id.titleTextViewFromMediaChooserHeaderBar);
        headerBarCamera = (ImageView) findViewById(R.id.cameraImageViewFromMediaChooserHeaderBar);
        headerBarBack = (ImageView) findViewById(R.id.backArrowImageViewFromMediaChooserHeaderView);
        headerBarDone = (TextView) findViewById(R.id.doneTextViewViewFromMediaChooserHeaderView);

//        headerBarTitle.setText(getResources().getString(R.string.image));
//        headerBarTitle.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//				FileUtils.SELECTED_MEDIA_COUNT=0;
//                finish();
//            }
//        });
//		headerBarCamera.setBackgroundResource(R.drawable.ic_video_unselect_from_media_chooser_header_bar);
        headerBarCamera.setTag(getResources().getString(R.string.image));
        headerBarTitle.setText(getResources().getString(R.string.image));

        headerBarBack.setOnClickListener(clickListener);
//		headerBarCamera.setOnClickListener(clickListener);
        headerBarDone.setOnClickListener(clickListener);

//		if(! MediaChooserConstants.showCameraVideo){
//			headerBarCamera.setVisibility(View.GONE);
//		}

        fm = getSupportFragmentManager();
        // 只當容器，主要內容已Fragment呈現
//        videoFragment=new BucketVideoFragment();
//        initFragment(videoFragment,false);

        imageFragment = new ImageFragment();
        Bundle data = new Bundle();
        boolean isSingleSelected = getIntent().getBooleanExtra(KEY_SINGLE_MODE, false);
        int max = getIntent().getIntExtra(KEY_MAX_MODE, 100);
        int number = getIntent().getIntExtra(KEY_CURRENT_NUMBER, 0);
        data.putBoolean(ImageFragment.KEY_SINGLE_MODE, isSingleSelected);
        data.putInt(ImageFragment.KEY_MAX_NUBER_MODE, max);
        data.putInt(ImageFragment.KEY_CURRENT_NUBER, number);
        imageFragment.setArguments(data);
        initFragment(imageFragment, false);


//		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) headerBarCamera.getLayoutParams();
//		params.height = convertDipToPixels(40);
//		params.width  = convertDipToPixels(40);
//		headerBarCamera.setLayoutParams(params);
//		headerBarCamera.setScaleType(ScaleType.CENTER_INSIDE);
//		headerBarCamera.setPadding(convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15));

    }

    private static void initFragment(Fragment f, boolean init) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.tabContent, f);
        if (!init)
            ft.addToBackStack(null);
        ft.commit();
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == headerBarCamera) {

                if (view.getTag().toString().equals(getResources().getString(R.string.video))) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_VIDEO); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                    // start the image capture Intent
                    startActivityForResult(intent, MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_IMAGE); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                    // start the image capture Intent
                    startActivityForResult(intent, MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }

            } else if (view == headerBarDone) {

                if (imageFragment != null) {
                    if (imageFragment.getSelectedImageList() != null && imageFragment.getSelectedImageList().size() > 0) {
                        Intent imageIntent = getIntent();
                        imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
//                        imageIntent.putStringArrayListExtra("list", imageFragment.getSelectedImageList());
//                        imageIntent.putParcelableArrayListExtra("list", imageFragment.getSelectedImageList());
                        imageIntent.putExtra("list", imageFragment.getSelectedImageList());
                        setResult(-1, imageIntent);
//                        sendBroadcast(imageIntent);
                    }

                }
//				FileUtils.SELECTED_MEDIA_COUNT=0;

                finish();


//				if(mSelectedImage.size() == 0 && mSelectedVideo.size() == 0){
//					Toast.makeText(ImageFragmentActivity.this, getString(R.string.plaese_select_file), Toast.LENGTH_SHORT).show();
//
//				}else{
//
//					if(mSelectedVideo.size() > 0){
//						Intent videoIntent = new Intent();
//						videoIntent.setAction(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
//						videoIntent.putStringArrayListExtra("list", mSelectedVideo);
//						sendBroadcast(videoIntent);
//					}
//
//					if(mSelectedImage.size() > 0){
//						Intent imageIntent = new Intent();
//						imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
//						imageIntent.putStringArrayListExtra("list", mSelectedImage);
//						sendBroadcast(imageIntent);
//					}
//                    MediaChooserConstants.SELECTED_MEDIA_COUNT=0;
//					finish();
//				}

            } else if (view == headerBarBack) {
//				FileUtils.SELECTED_MEDIA_COUNT=0;\
                int count = imageFragment.getSelectedImageList().size();
                if (MediaChooserConstants.SELECTED_MEDIA_COUNT >= count)
                    MediaChooserConstants.SELECTED_MEDIA_COUNT -= count;
                imageFragment.getSelectedImageList().clear();
                finish();
            }
        }
    };

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MediaChooserConstants.folderName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
//			FileUtils.SELECTED_MEDIA_COUNT ++;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == MediaChooserConstants.BUCKET_SELECT_IMAGE_CODE) {
                ArrayList<MediaModel> list = data.getParcelableArrayListExtra("list");
                addMedia(list);
            } else if (requestCode == MediaChooserConstants.BUCKET_SELECT_VIDEO_CODE) {
                ArrayList<MediaModel> list = data.getParcelableArrayListExtra("list");
                addMedia(list);
            } else if (requestCode == MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                final AlertDialog alertDialog = MediaChooserConstants.getDialog(ImageFragmentActivity.this).create();
                alertDialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//						BucketImageFragment bucketImageFragment = (BucketImageFragment) fragmentManager.findFragmentByTag("tab1");
//						if(bucketImageFragment != null){
//							bucketImageFragment.getAdapter().addLatestEntry(fileUriString);
//							bucketImageFragment.getAdapter().notifyDataSetChanged();
//						}
                        alertDialog.dismiss();
                    }
                }, 5000);

            } else if (requestCode == MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {


                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

                final AlertDialog alertDialog = MediaChooserConstants.getDialog(ImageFragmentActivity.this).create();
                alertDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        BucketVideoFragment bucketVideoFragment = (BucketVideoFragment) fragmentManager.findFragmentByTag("tab2");
                        if (bucketVideoFragment != null) {
                            bucketVideoFragment.getAdapter().addLatestEntry(fileUriString);
                            bucketVideoFragment.getAdapter().notifyDataSetChanged();

                        }
                        alertDialog.dismiss();
                    }
                }, 5000);
            }
        }
    }

    private void addMedia(ArrayList<MediaModel> input) {
//        for (String string : input) {
//            list.add(string);
//        }
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("list", input);
//        if (mIntent != null) {
//            mIntent.putStringArrayListExtra("list", list);
//        }
        setResult(-1, intent);
        finish();
    }


    public int convertDipToPixels(float dips) {
        return (int) (dips * ImageFragmentActivity.this.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onImageSelected(int i) {
        if (i > 0) {
            mSeletcteTx.setText(i + "个已勾选");
        }
//        if( mTabHost.getTabWidget().getChildAt(1) != null){
//            if(count != 0){
//                ((TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getResources().getString(R.string.images_tab) + "  " + count);
//
//            }else{
//                ((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getResources().getString(R.string.image));
//            }
//        }else {
//            if(count != 0){
//                ((TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.images_tab) + "  "  + count);
//
//            }else{
//                ((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.image));
//            }
//        }
    }
}
