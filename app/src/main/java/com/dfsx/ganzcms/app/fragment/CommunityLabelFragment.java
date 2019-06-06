package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.ArrayList;

/**
 * Created by heyang on  2017/7/27   tags
 */
public class CommunityLabelFragment extends Fragment {

    public static final String TAG = "CommunityLabelFragment";

    private Activity act;

    private TextView topBarRightTextView;

    private TextView labelSelectedNumText;

    private TabGrouplayout labelLayout;

    private int allCount = 0;
    private int selectCount = 0;
    ArrayList<String> mArrLabels;
    ArrayList<String> mSelectArrLabels;

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
                    intent.putExtra(WhiteTopBarActivity.KEY_PARAM, data);
                    act.setResult(Activity.RESULT_OK, intent);
                    act.finish();
                }
            });
        }
        View v = inflater.inflate(R.layout.community_label_select_layout, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mArrLabels = (ArrayList<String>) getArguments().getSerializable(WhiteTopBarActivity.KEY_PARAM);
            mSelectArrLabels = (ArrayList<String>) getArguments().getSerializable(WhiteTopBarActivity.KEY_PARAM2);
        }
        initView(view);
        initData(mArrLabels, mSelectArrLabels);
    }

    private void initView(View v) {
        labelSelectedNumText = (TextView) v.findViewById(R.id.label_selected_num_text);
        labelLayout = (TabGrouplayout) v.findViewById(R.id.label_selected_view);
        int unSelectedColor = getResources().getColor(R.color.label_text_color);
        int selectedColor = getResources().getColor(R.color.white);
        int unSelectedRes = R.drawable.shape_label_box;
        int selectedRes = R.drawable.shape_label_selected_box;
//        labelLayout.changeThemeForTextColor(selectedColor, unSelectedColor, selectedRes, unSelectedRes);
//        labelLayout.setAddFlagNeedShown(false);
//        labelLayout.setOuterBodyClickListener(new CustomLabelLayout.OuterBodyClickListener() {
//            @Override
//            public void onClick(CustomLabelLayout.BodyEntity body) {
//                int selectedCount = labelLayout.getCurrentSelection().size();
//                labelSelectedNumText.setText(selectedCount + "/" + allCount);
//            }
//        });
    }

    private void initData(ArrayList<String> arr, ArrayList<String> selectList) {
//        ArrayList<String> select = labelLayout.getCurrentSelection();
//        ArrayList<String> op = labelLayout.getAllBody();
        if (arr != null && arr.size() > 0) {
//            setLabelData(arr.toArray(new String[0]));
            allCount = arr.size();
        }
        if (selectList != null && selectList.size() > 0) {
            selectCount = selectList.size();
//            labelLayout.setSelectedBodies(selectList.toArray(new String[0]));
        }
//        setLabelData(new String[]{"音乐", "户外", "美食", "游戏", "大讲堂", "实时政事", "奔跑吧", "任天堂"});
        labelSelectedNumText.setText(selectCount + "/" + allCount);

        labelLayout.initData(getActivity(), arr, selectList);


    }

//    private void setLabelData(ArrayList<String> labels) {
//        labelLayout.addAllBody(labels);
//    }
}
