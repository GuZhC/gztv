package com.dfsx.ganzcms.app.util;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.util.Log;
//import net.majorkernelpanic.streaming.video.VideoQuality;

import java.util.List;

/**
 * Created by liuwb on 2016/11/8.
 */
public class CameraSizeInfoHelper {

//    private ArrayList<VideoQuality> facetimeCameraList = new ArrayList<>();
//    private ArrayList<VideoQuality> afterCameraList = new ArrayList<>();

    private boolean[] cameraCanFocus = new boolean[2];

    public void initAllCameraSize() {
        final String cam[] = {
                "Back :"
                , "Front:"
        };
        final String profile[] = {
                "QUALITY_LOW"
                , "QUALITY_HIGH"
                , "QUALITY_QCIF"
                , "QUALITY_CIF"
                , "QUALITY_480P"
                , "QUALITY_720P"
                , "QUALITY_1080P"
                , "QUALITY_QVGA"
        };

        int mNumberOfCameras = Camera.getNumberOfCameras();
//        ArrayList<VideoQuality> tmp[] = new ArrayList[]{afterCameraList, facetimeCameraList};
        boolean has;
        for (int i = Camera.CameraInfo.CAMERA_FACING_BACK; i < Camera.CameraInfo.CAMERA_FACING_FRONT + 1; i++) {
            Camera camera = null;
            try {
                camera = Camera.open(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (camera == null)
                continue;
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> allRes = parameters.getSupportedVideoSizes();
            List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
            cameraCanFocus[i] = parameters.getSupportedFocusModes().
                    contains(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.release();
            if (allRes != null) {
                for (Camera.Size size : allRes) {
                    Log.e("TAG", cam[i] + " size (w, h) ===  " + size.width + "," + size.height);
                }
            }

            if (supportedSizes != null) {
                for (Camera.Size size : supportedSizes) {
                    Log.e("TAG", cam[i] + " SupportedPreview size (w, h) ===  " + size.width + "," + size.height);
                }
            }

            for (int j = CamcorderProfile.QUALITY_480P; j < CamcorderProfile.QUALITY_1080P; j++) {
                has = CamcorderProfile.hasProfile(i, j);
                Log.e("TAG", cam[i] + "  " + profile[j] + (has ? " :Y" : " :N"));
                if (has) {
                    CamcorderProfile p = CamcorderProfile.get(i, j);
                    String resId = p.videoFrameWidth + "X" + p.videoFrameHeight;
                    Log.e("TAG", resId);
                    Log.e("TAG", "video Frame rate" + p.videoFrameRate);
                    Log.e("TAG", "video bit rate" + p.videoBitRate);
                    if (j == CamcorderProfile.QUALITY_480P)
                        p.videoBitRate = 800000;
                    if (j == CamcorderProfile.QUALITY_720P)
                        p.videoBitRate = 1024000;
                    if (j == CamcorderProfile.QUALITY_1080P)
                        p.videoBitRate = 1800000;

//                    tmp[i].add(new VideoQuality(p.videoFrameWidth,
//                            p.videoFrameHeight, p.videoFrameRate, p.videoBitRate));
                }
            }
        }
    }

    public boolean cameraCanFoucs(int cId) {
        return cameraCanFocus[cId];
    }

    public boolean isInit() {
//        return !facetimeCameraList.isEmpty() ||
//                !afterCameraList.isEmpty();
        return false;
    }

//    public ArrayList<VideoQuality> getCommonSizeList() {
//        ArrayList<VideoQuality> commList = new ArrayList<>();
//        for (VideoQuality faceV : facetimeCameraList) {
//            for (VideoQuality afterV : afterCameraList) {
//                if (faceV.resX == afterV.resX &&
//                        faceV.resY == afterV.resY) {
//                    commList.add(afterV);
//                }
//            }
//        }
//        return commList;
//    }

//    public ArrayList<VideoQuality> getFaceList() {
//        return facetimeCameraList;
//    }
//
//    public VideoQuality getFaceSuitVideoQuality() {
//        return facetimeCameraList.get(facetimeCameraList.size() - 1);
//    }
//
//    public VideoQuality getBackSuitVideoQuality() {
//        return afterCameraList.get(afterCameraList.size() - 1);
//    }
//
//    public ArrayList<VideoQuality> getBackList() {
//        return afterCameraList;
//    }
//
//    public VideoQuality getCommonSuitVideoQuality() {
//        return getCommonSizeList().get(getCommonSizeList().size() - 1);
//    }
//
//    public boolean isEmpty() {
//        return facetimeCameraList.isEmpty() &&
//                afterCameraList.isEmpty();
//    }
//
//    public boolean isSupportFaceCamera() {
//        return !facetimeCameraList.isEmpty();
//    }
}
