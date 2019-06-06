package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.LiveTAGHelper;
import com.dfsx.ganzcms.app.model.LiveTagInfo;
import com.dfsx.ganzcms.app.view.CustomLabelLayout;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/6/19.
 */
public class SelectLiveLabelFragment extends Fragment {

    public static final String KEY_INTENT_RETURN_LABEL_LIST = "Selected_label_List";

    private Activity act;

    private TextView topBarRightTextView;

    private TextView labelSelectedNumText;
    private CustomLabelLayout labelLayout;


    private int allCount = 8;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        if (act instanceof WhiteTopBarActivity) {
            topBarRightTextView = ((WhiteTopBarActivity) act).getTopBarRightText();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBarRightTextView.getLayoutParams();
            if (params != null) {
                params.topMargin = PixelUtil.dp2px(act, 8);
                params.bottomMargin = PixelUtil.dp2px(act, 8);
                topBarRightTextView.setLayoutParams(params);
            }
            topBarRightTextView.setPadding(10, 2, 10, 2);
            topBarRightTextView.setText("确定");
            topBarRightTextView.setTextColor(getResources().getColor(R.color.gray_75));
            topBarRightTextView.setBackgroundResource(R.drawable.shape_top_bar_right_text);
            topBarRightTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> data = labelLayout.getCurrentSelection();
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(act, "还没有选择标签", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(KEY_INTENT_RETURN_LABEL_LIST, data);
                    act.setResult(Activity.RESULT_OK, intent);
                    act.finish();
                }
            });
        }
        View v = inflater.inflate(R.layout.live_label_select_layout, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getData();
    }

    private void initView(View v) {
        labelSelectedNumText = (TextView) v.findViewById(R.id.label_selected_num_text);
        labelLayout = (CustomLabelLayout) v.findViewById(R.id.label_selected_view);
        int unSelectedColor = getResources().getColor(R.color.label_text_color);
        int selectedColor = getResources().getColor(R.color.white);
        int unSelectedRes = R.drawable.shape_label_box;
        int selectedRes = R.drawable.shape_label_selected_box;
        labelLayout.changeThemeForTextColor(selectedColor, unSelectedColor, selectedRes, unSelectedRes);
        labelLayout.setAddFlagNeedShown(false);
        labelLayout.setOuterBodyClickListener(new CustomLabelLayout.OuterBodyClickListener() {
            @Override
            public void onClick(CustomLabelLayout.BodyEntity body) {
                int selectedCount = labelLayout.getCurrentSelection().size();
                labelSelectedNumText.setText(selectedCount + "/" + allCount);
            }
        });
    }

    private void setLabelShow(String... labels) {
        setLabelData(labels);
        allCount = labels.length;
        labelSelectedNumText.setText("0/" + allCount);
    }

    private void getData() {
        LiveTAGHelper.getInstance().getAllLiveTag(new DataRequest.DataCallback<ArrayList<LiveTagInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<LiveTagInfo> data) {
                if (data != null && data.size() > 0) {
                    String[] arr = new String[data.size()];
                    for (int i = 0; i < data.size(); i++) {
                        arr[i] = data.get(i).getName();
                    }
                    setLabelShow(arr);
                } else {
                    Toast.makeText(act, "没有直播标签", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLabelData(String[] labels) {
        labelLayout.addAllBody(labels);
    }
}
