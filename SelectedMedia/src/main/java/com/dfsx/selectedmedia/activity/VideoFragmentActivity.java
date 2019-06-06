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
import com.dfsx.selectedmedia.fragment.BucketImageFragment;
import com.dfsx.selectedmedia.fragment.BucketVideoFragment;
import com.dfsx.selectedmedia.fragment.ImageFragment;
import com.dfsx.selectedmedia.fragment.VideoFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class VideoFragmentActivity extends BaseActivity implements VideoFragment.OnVideoSelectedListener {
	public static final String KEY_SINGLE_MODE = "KEY_SINGLE_MODE";
	public static final String KEY_MAX_MODE = "com.dfsx.selectedmedia.activity.VideoFragmentActivity_KEY_MAX_MODE";
	public static final String KEY_CURRENT_NUMBER = "com.dfsx.selectedmedia.activity.VideoFragmentActivity_KEY_CURRENT_NUMBER";

	private TextView headerBarTitle;
	private ImageView headerBarCamera;
	private ImageView headerBarBack;
	private TextView headerBarDone;
	private TextView mSeletcteTx;

    public static android.support.v4.app.FragmentManager fm;
//    BucketVideoFragment  videoFragment =null;
    VideoFragment  videoFragment =null;

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
//        MediaChooserConstants.SELECTED_MEDIA_COUNT=0;

		mSeletcteTx= (TextView)findViewById(R.id.seltect_item_txt);
		headerBarTitle  = (TextView)findViewById(R.id.titleTextViewFromMediaChooserHeaderBar);
		headerBarCamera = (ImageView)findViewById(R.id.cameraImageViewFromMediaChooserHeaderBar);
		headerBarBack   = (ImageView)findViewById(R.id.backArrowImageViewFromMediaChooserHeaderView);
		headerBarDone   = (TextView)findViewById(R.id.doneTextViewViewFromMediaChooserHeaderView);
//		mTabHost        = (FragmentTabHost) findViewById(android.R.id.tabhost);

//		headerBarTitle.setText(getResources().getString(R.string.video));
//        headerBarTitle.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MediaChooserConstants.SELECTED_MEDIA_COUNT=0;
//                finish();
//            }
//        });
//		headerBarCamera.setBackgroundResource(R.drawable.ic_video_unselect_from_media_chooser_header_bar);
		headerBarCamera.setTag(getResources().getString(R.string.video));

		headerBarBack.setOnClickListener(clickListener);
//		headerBarCamera.setOnClickListener(clickListener);
		headerBarDone.setOnClickListener(clickListener);

//		if(! MediaChooserConstants.showCameraVideo){
//			headerBarCamera.setVisibility(View.GONE);
//		}

        fm = getSupportFragmentManager();
        // 只當容器，主要內容已Fragment呈現  遍历两级目录
//        videoFragment=new BucketVideoFragment();
//        initFragment(videoFragment,false);

        // 直接遍历显示  一级
		videoFragment=new VideoFragment();
		Bundle data = new Bundle();
		boolean isSingleSelected = getIntent().getBooleanExtra(KEY_SINGLE_MODE, false);
		int max = getIntent().getIntExtra(KEY_MAX_MODE, 100);
		int number=getIntent().getIntExtra(KEY_CURRENT_NUMBER, 0);
		data.putBoolean(VideoFragment.KEY_SINGLE_MODE, isSingleSelected);
		data.putInt(VideoFragment.KEY_MAX_NUBER_MODE, max);
		data.putInt(VideoFragment.KEY_CURRENT_NUBER, number);
		videoFragment.setArguments(data);
		initFragment(videoFragment,false);

//		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) headerBarCamera.getLayoutParams();
//		params.height = convertDipToPixels(40);
//		params.width  = convertDipToPixels(40);
//		headerBarCamera.setLayoutParams(params);
//		headerBarCamera.setScaleType(ScaleType.CENTER_INSIDE);
//		headerBarCamera.setPadding(convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15));

	}

    private static void initFragment(Fragment f, boolean init){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.tabContent, f);
        if(!init)
            ft.addToBackStack(null);
        ft.commit();
    }

    OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(view == headerBarCamera){

				if(view.getTag().toString().equals(getResources().getString(R.string.video))){
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_VIDEO); // create a file to save the image
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

				}else{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_IMAGE); // create a file to save the image
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}

			}else if(view == headerBarDone){

                if(videoFragment != null){
                    if(videoFragment.getSelectedVideoList() != null && videoFragment.getSelectedVideoList().size() > 0){
                        Intent imageIntent = getIntent();
                        imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
//                        imageIntent.putStringArrayListExtra("list", imageFragment.getSelectedImageList());
//                        imageIntent.putParcelableArrayListExtra("list", imageFragment.getSelectedImageList());
                        imageIntent.putExtra("list", videoFragment.getSelectedVideoList());
                        setResult(-1, imageIntent);
//                        sendBroadcast(imageIntent);
                    }
                }
                MediaChooserConstants.SELECTED_MEDIA_COUNT=0;

                finish();

                /***
				if(mSelectedImage.size() == 0 && mSelectedVideo.size() == 0){
					Toast.makeText(VideoFragmentActivity.this, getString(R.string.plaese_select_file), Toast.LENGTH_SHORT).show();

				}else{

					if(mSelectedVideo.size() > 0){
						Intent videoIntent = new Intent();
						videoIntent.setAction(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
						videoIntent.putStringArrayListExtra("list", mSelectedVideo);
						sendBroadcast(videoIntent);
					}

					if(mSelectedImage.size() > 0){
						Intent imageIntent = new Intent();
						imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
						imageIntent.putStringArrayListExtra("list", mSelectedImage);
						sendBroadcast(imageIntent);
					}
                    MediaChooserConstants.SELECTED_MEDIA_COUNT=0;
					finish();
				}
                        **/
			}else if(view == headerBarBack){
//                MediaChooserConstants.SELECTED_MEDIA_COUNT=0;
//					finish();
				int count = videoFragment.getSelectedVideoList().size();
				if (MediaChooserConstants.SELECTED_MEDIA_COUNT >= count)
					MediaChooserConstants.SELECTED_MEDIA_COUNT -= count;
				videoFragment.getSelectedVideoList().clear();
				finish();
			}
		}
	};

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MediaChooserConstants.folderName);
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if(type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            MediaChooserConstants.SELECTED_MEDIA_COUNT =0;
        }
        return super.onKeyDown(keyCode, event);
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode,  data);

		if(resultCode ==  Activity.RESULT_OK){

			if (requestCode == MediaChooserConstants.BUCKET_SELECT_IMAGE_CODE){
				ArrayList<MediaModel> list = data.getParcelableArrayListExtra("list");
				addMedia(list);

			}else if(requestCode == MediaChooserConstants.BUCKET_SELECT_VIDEO_CODE){
				ArrayList<MediaModel> list = data.getParcelableArrayListExtra("list");
				addMedia(list);
			}else if (requestCode == MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
				final AlertDialog alertDialog = MediaChooserConstants.getDialog(VideoFragmentActivity.this).create();
				alertDialog.show();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//Do something after 2000ms
						String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
						android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
						BucketImageFragment bucketImageFragment = (BucketImageFragment) fragmentManager.findFragmentByTag("tab1");
						if(bucketImageFragment != null){   
							bucketImageFragment.getAdapter().addLatestEntry(fileUriString);
							bucketImageFragment.getAdapter().notifyDataSetChanged();
						}
						alertDialog.dismiss();
					}
				}, 5000);

			}else if (requestCode == MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE){


				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

				final AlertDialog alertDialog = MediaChooserConstants.getDialog(VideoFragmentActivity.this).create();
				alertDialog.show();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//Do something after 2000ms
						String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
						android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
						BucketVideoFragment bucketVideoFragment = (BucketVideoFragment) fragmentManager.findFragmentByTag("tab2");
						if(bucketVideoFragment != null){   
							bucketVideoFragment.getAdapter().addLatestEntry(fileUriString);
							bucketVideoFragment.getAdapter().notifyDataSetChanged();

						}
						alertDialog.dismiss();
					}
				}, 5000);
			}
		}
	}

	private void addMedia(ArrayList<MediaModel> input ) {
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("list", input);
//        if (mIntent != null) {
//            mIntent.putStringArrayListExtra("list", list);
//        }
        setResult(-1, intent);
        finish();
    }


	public int convertDipToPixels(float dips){
		return (int) (dips * VideoFragmentActivity.this.getResources().getDisplayMetrics().density + 0.5f);
	}

    @Override
    public void onVideoSelected(int count) {
		if(count>0)
		{
			mSeletcteTx.setText(count+"个已勾选");
		}
    }
}
