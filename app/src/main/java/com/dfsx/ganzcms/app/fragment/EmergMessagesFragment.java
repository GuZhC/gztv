package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.adapter.ListViewAdapter;
import com.dfsx.ganzcms.app.business.SOSMessageHelper;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.util.SOSRadioPlayManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 应急消息界面
 */

public class EmergMessagesFragment extends HeadLineFragment {


    public static EmergMessagesFragment newInstance(long id, String type, long slideId, long dynaicId) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("type", type);
        bundle.putLong("slideId", slideId);
        bundle.putLong("dynamicId", dynaicId);
        EmergMessagesFragment fragment = new EmergMessagesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView recyclerView;
    private SOSAdapter sosAdapter;
    private CameraManager cameraManager;
    private Camera m_Camera = null;

    private boolean isNightRequestOpen;

    private SOSMessageHelper sosMessageHelper = new SOSMessageHelper(new Action1<Boolean>() {
        @Override
        public void call(Boolean aBoolean) {
            if (!isNightRequestOpen) {//如果是手电筒请求打开灯，就不要接收SOS的闪灯信号了
                switchLight(aBoolean, null);
            }
        }
    });


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View groupView = LayoutInflater.from(getActivity()).inflate(R.layout.sos_header,null);
        recyclerView = (RecyclerView) groupView.findViewById(R.id.recycler_sos);
        initData();
        initRecyclerView();
        list.addHeaderView(groupView);
        adapter = new EmercyAdapter(getActivity());
        list.setAdapter(adapter);
        //取消点击进入详情
        list.setOnItemClickListener(null);
    }

    public void startSOS() {
        sosMessageHelper.start();
    }

    public void stopSOS() {
        sosMessageHelper.stop();
    }

    public void call110() {
        boolean is = callPhone("110");
        if (!is) {
            ToastUtils.showLong(getActivity(),"启动电话失败");
        }
    }

    private HelpItem getAudioItem() {
        return getHelpItemByName("高音频");
    }

    private HelpItem getSOSItem() {
        return getHelpItemByName("SOS求救");
    }

    private HelpItem getLightItem() {
        return getHelpItemByName("手电筒");
    }

    private HelpItem getHelpItemByName(String name) {
        try {
            for (HelpItem item : sosAdapter.getData()) {
                if (TextUtils.equals(item.name, name)) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean callPhone(final String phoneNum) {
        try {
            new TedPermission(getContext())
                    .setPermissions(Manifest.permission.CALL_PHONE)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phoneNum));
                            getActivity().startActivity(intent);
                            Log.e("TAG", "phone call-----------");
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            ToastUtils.showLong(getContext(),"没有打电话权限");
                        }
                    })
                    .check();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startAudio() {
        try {
            AssetFileDescriptor fd = getActivity().getAssets().openFd("media/hf.mp3");
            SOSRadioPlayManager.getInstance().playAssetFile(fd, true);
            SOSRadioPlayManager.getInstance().setOnMediaPlayStateChangeListener(new SOSRadioPlayManager.OnMediaPlayStateChangeListener() {
                @Override
                public void onStateChange(int currentState) {
                    boolean isPlaying = currentState == SOSRadioPlayManager.STATE_PLAYING;
                    Log.e("TAG", "audio playing ----- " + isPlaying);
                    HelpItem audioItem = getAudioItem();
                    if (audioItem != null) {
                        audioItem.isUseing = isPlaying;
                        sosAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopAudio() {
        SOSRadioPlayManager.getInstance().stop();
    }


    @SuppressLint("ServiceCast")
    public boolean switchLight(boolean isOpen, Action1<Boolean> action) {
        if (action != null) {
            Log.e("TAG", "action != null --- true");
        }
        new TedPermission(getContext())
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new LightPermissionCheckResult(isOpen, action))
                .check();
        return true;
    }

    private boolean controlCameraLight(boolean isOpen) {
        if (cameraManager == null) {
            cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        }
        Log.e("TAG", "controlCameraLight ------- open == " + isOpen);
        boolean is = false;
        if (cameraManager != null) {
            if (isOpen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraManager.setTorchMode("0", true);
                        is = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PackageManager pm = getActivity().getPackageManager();
                    FeatureInfo[] features = pm.getSystemAvailableFeatures();
                    for (FeatureInfo f : features) {
                        if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                            if (null == m_Camera) {
                                m_Camera = Camera.open();
                            }
                            final Camera.Parameters parameters = m_Camera.getParameters();
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            m_Camera.setParameters(parameters);
                            m_Camera.startPreview();
                            is = true;
                            break;
                        }
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraManager.setTorchMode("0", false);
                        is = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (m_Camera != null) {
                        m_Camera.stopPreview();
                        final Camera.Parameters parameters = m_Camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        m_Camera.setParameters(parameters);
                        is = true;
                    }
                }
            }
        }
        return is;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraManager != null) {
            switchLight(false, null);
        }
        if (m_Camera != null) {
            m_Camera.stopPreview();
            m_Camera.release();
            m_Camera = null;
        }
        SOSRadioPlayManager.getInstance().stop();
        SOSRadioPlayManager.getInstance().release();
    }

    private void initData() {
        ArrayList<HelpItem> list = new ArrayList<>();
        HelpItem item = new HelpItem("高音频", R.drawable.icon_sos_voice);
        list.add(item);
        item = new HelpItem("手电筒", R.drawable.icon_sos_torchlight);
        isNightRequestOpen = false;
        list.add(item);
        item = new HelpItem("110", R.drawable.icon_sos_police);
        list.add(item);
        item = new HelpItem("夜间求救", R.drawable.icon_sos_night_help);
        list.add(item);
        item = new HelpItem("SOS", R.drawable.icon_sos_sos);
        list.add(item);
        sosAdapter = new SOSAdapter();
        sosAdapter.update(list,false);
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5, GridLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(sosAdapter);
        sosAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                HelpItem item = sosAdapter.getData().get(position);
                if (TextUtils.equals(item.name, "高音频")) {
                    item.isUseing = !item.isUseing;
                    if (item.isUseing) {
                        startAudio();
                    } else {
                        stopAudio();
                    }
                } else if (TextUtils.equals(item.name, "手电筒")) {
                    boolean open = !item.isUseing;
                    isNightRequestOpen = open;
                    if (open) {
                        //关闭SOS闪灯
                        HelpItem sosItem = getSOSItem();
                        if (sosItem != null && sosItem.isUseing) {
                            sosItem.isUseing = false;
                            stopSOS();
                        }
                    }
                    switchLight(open, new BaseAction<HelpItem>(item) {
                        @Override
                        public void call(HelpItem data, Boolean aBoolean) {
                            if (aBoolean) {
                                data.isUseing = !data.isUseing;
                                sosAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else if (TextUtils.equals(item.name, "110")) {
                    call110();
                } else if (TextUtils.equals(item.name, "夜间求救")) {
                    Intent intent = new Intent();
                    intent.putExtras(GanZiTopBarActivity.getTitleBundle(0, "夜间求救"));
                    GanZiTopBarActivity.start(getContext(), NightSOSFragment.class.getName(), intent);
                } else if (TextUtils.equals(item.name, "SOS")) {
                    if (getLightItem() != null) {
                        getLightItem().isUseing = false;
                        isNightRequestOpen = false;
                    }
                    item.isUseing = !item.isUseing;
                    if (item.isUseing) {
                        startSOS();
                    } else {
                        stopSOS();
                    }
                }
                sosAdapter.notifyDataSetChanged();
            }
        });
    }


    class HelpItem {
        public String name;
        public int iconRes;
        public boolean isUseing;

        public HelpItem(String name, int iconRes) {
            this.name = name;
            this.iconRes = iconRes;
            isUseing = false;
        }
    }

    public class SOSAdapter extends BaseRecyclerViewDataAdapter<HelpItem>{

        private OnItemClickListener onItemClickListener;

        public OnItemClickListener getOnItemClickListener() {
            return onItemClickListener;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.sos_item,parent,false);
            return new BaseRecyclerViewHolder(view,viewType);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, final int position) {
            View body = holder.getView(R.id.relative_body);
            TextView textItem = holder.getView(R.id.text_item);
            ImageView imageUse = holder.getView(R.id.image_use);
            ImageView imageRes = holder.getView(R.id.image_res);
            textItem.setText(list.get(position).name);
            imageRes.setImageResource(list.get(position).iconRes);
            imageUse.setVisibility(list.get(position).isUseing ? View.VISIBLE : View.INVISIBLE);
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.itemClick(position);
                    }
                }
            });
        }


    }

    public interface OnItemClickListener{
        void itemClick(int position);
    }

    class LightPermissionCheckResult implements PermissionListener {

        private boolean isOpen;
        private Action1<Boolean> requestAction;

        public LightPermissionCheckResult(boolean isOpen, Action1<Boolean> requestAction) {
            this.isOpen = isOpen;
            this.requestAction = requestAction;
        }

        @Override
        public void onPermissionGranted() {
            boolean is = controlCameraLight(isOpen);
            if (requestAction != null) {
                requestAction.call(is);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getActivity(),"没有相机权限!,请手电打开相机权限",Toast.LENGTH_LONG).show();
            requestAction.call(false);
        }
    }

    abstract class BaseAction<D> implements Action1<Boolean> {
        private D data;

        public BaseAction(D data) {
            this.data = data;
        }

        @Override
        public final void call(Boolean aBoolean) {
            call(data, aBoolean);
        }

        public abstract void call(D data, Boolean aBoolean);
    }
    
    public class EmercyAdapter extends ListViewAdapter {
        public EmercyAdapter(Context context) {
            super(context);
        }

        @Override
        public void update(ArrayList<ContentCmsEntry> data, boolean bAddTail) {
            Iterator<ContentCmsEntry> iterator = data.iterator();
            while (iterator.hasNext()){
                ContentCmsEntry cmsEntry = iterator.next();
                if (TextUtils.isEmpty(cmsEntry.getEmergencyType())){
                    iterator.remove();
                }
            }
            super.update(data, bAddTail);
        }
    }


}
