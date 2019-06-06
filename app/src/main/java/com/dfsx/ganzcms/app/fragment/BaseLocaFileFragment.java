package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.R;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.dfsx.selectedmedia.activity.VideoFragmentActivity.KEY_SINGLE_MODE;

/**
 * Created by heyang on 2016/11/9.
 */
public abstract class BaseLocaFileFragment extends Fragment {
    protected static final int RESULT_OK = -1;
    protected static final int SELECT_VIDEO = 5;
    protected static final int SELECT_IMAG = 2;
    protected static final int SELECT_AUDIO = 6;
    protected static final int CAMERA_ACTIVITY = 7;
    private static Uri fileUri;
    public static String PUBLIC_PATH = Environment.getExternalStorageDirectory() + "/DCIM/axcms/";
    public ArrayList<MediaModel> _tempList = null;

    /**
     * 使用Fragment的startActivityForResult方法跳转到WhiteTopBarActivity
     *
     * @param context
     * @param fragName
     * @param title
     * @param rightTitle
     * @param requestCode
     */
    protected void frgStartWhiteTopBarActivityForResult(Context context, String fragName, String title, String rightTitle, int requestCode) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, title);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        startActivityForResult(intent, requestCode);
    }

    public void gotoTakeImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                takePhotos();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void gotoSelectImage(final boolean isSingle) {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
                if (isSingle) {
                    intent.putExtra("KEY_SINGLE_MODE", true);
                } else {
                    intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, 9);
                }
                startActivityForResult(intent, SELECT_IMAG);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void gotoSelectImage(final boolean isSingle, final int max) {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
                if (isSingle) {
                    intent.putExtra("KEY_SINGLE_MODE", true);
                } else {
                    intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, max);
                }
                startActivityForResult(intent, SELECT_IMAG);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void gotoSelectVideo() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.putExtra(KEY_SINGLE_MODE, true);
                intent.setClass(getActivity(), VideoFragmentActivity.class);
                startActivityForResult(intent, SELECT_VIDEO);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void takePhotos() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(1); // create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(PUBLIC_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public void hideInputSoft(EditText edt) {
        if (edt == null) return;
        InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
        }
    }

    public ArrayList<MediaModel> get_tempList() {
        return _tempList;
    }

    public abstract void onSelectImagesData(ArrayList<MediaModel> list);

    public abstract void onSelectVideosData(ArrayList<MediaModel> list);

    ///   0:images  1:video   2:audito
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                /**
                 case CAMERA_VIDEO:
                 getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                 String fileUrString = fileUri.toString().replaceFirst("file:///", "/").trim();
                 //                    if (adapter.getSize() < 1) {
                 cursor = getActivity().getContentResolver().query(Uri.parse(fileUrString), null,
                 null, null, null);
                 double laf = 0.0f;
                 double logf = 0.0f;
                 if (cursor != null) {
                 if (cursor.moveToNext()) {
                 int lafIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
                 laf = Double.parseDouble(cursor
                 .getString(lafIndex));
                 int logfIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
                 logf = Double.parseDouble(cursor
                 .getString(logfIndex));
                 }
                 }
                 adapter.addTail(fileUrString, fileUrString, 0, laf, logf);
                 break;   **/
                case SELECT_IMAG: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    onSelectImagesData(bitmapList);
//                    finishActivityWithResult("paths", bitmapList, SELECT_IMAG);
//                    String[] paths = (String[]) bitmapList.toArray();
//                    finishActivityWithResult("paths", bitmapList, 2);
//                    if (!bitmapList.isEmpty()) {
//                        for (int i = 0; i < bitmapList.size(); i++) {
//                            MediaModel model = bitmapList.get(i);
//                            if (model != null) {
//                                model.type = 0;
//                                adapter.addTail(model);
//                                finishActivityWithResult("imgs", bitmapList, 2);
//                            }
//                        }
//                    }
                }
                break;
                case CAMERA_ACTIVITY: {
                    try {
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
//                        adapter.addTail(fileUriString, 0, 0, 0);
                        ArrayList<MediaModel> list = new ArrayList<>();
                        MediaModel model = new MediaModel(fileUriString, 0);
                        list.add(model);
                        onSelectImagesData(list);
                    } catch (Exception io_e) {
                        io_e.printStackTrace();
                    }
                }
                break;
                case SELECT_VIDEO: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    onSelectVideosData(bitmapList);
//                    finishActivityWithResult("paths", bitmapList, SELECT_VIDEO);
                }
                break;
                case SELECT_AUDIO: {
                    if (data != null) {
                        ArrayList<String> slist = data.getStringArrayListExtra("list");
                        if (slist != null && !slist.isEmpty()) {
                            ArrayList<MediaModel> dlist = new ArrayList<>();
                            for (int i = 0; i < slist.size(); i++) {
                                MediaModel mode = new MediaModel(slist.get(i), 2);
                                dlist.add(mode);
                            }
                            finishActivityWithResult("paths", dlist, SELECT_AUDIO);
                        }
//                        if (slist != null) {
//                            if (!slist.isEmpty()) {
//                                for (int i = 0; i < slist.size(); i++) {
//                                    MediaModel entry = new MediaModel(slist.get(i).toString(), 2);
//                                    adapter.addTail(entry);
//                                }
//                            }
//                        } else {
//                            String path = data.getData().getPath();
//                            MediaModel entry = new MediaModel(path.toString(), 2);
//                            adapter.addTail(entry);
//                        }
                    }
                }
                break;
            }
            getActivity().setResult(1, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * finish并返回resultCode,与frgStartWhiteTopBarActivityForResult方法搭配使用
     *
     * @param key
     * @param value
     * @param resultCode
     */
    protected void finishActivityWithResult(String key, ArrayList<MediaModel> value, int resultCode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, value);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        _tempList = value;
        getActivity().setResult(resultCode, intent);
//        getActivity().finish();
    }
}
