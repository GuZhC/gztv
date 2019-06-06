package com.dfsx.logonproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import cn.qqtheme.framework.WheelPicker.entity.City;
import cn.qqtheme.framework.WheelPicker.entity.County;
import cn.qqtheme.framework.WheelPicker.entity.Province;
import cn.qqtheme.framework.WheelPicker.picker.AddressPicker;
import cn.qqtheme.framework.init.AddressInitTask;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.logonproject.R;
import org.w3c.dom.Text;

/**
 * Created by wxl on 2016/11/8.
 */

public class AddressChangeFragment extends BaseResultFragment {
    private View rootView;
    private EditText editText;
    private TextView topBarRight;
    private TextView addessBtn;
    private String strProvice, strCity, strRegion, strAddress;
    private View layout;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == topBarRight) {
                try {
                    onFocusChange(false, editText);
                    hideKeyboard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String str = editText.getText().toString().trim();
                Bundle bundle = new Bundle();
                String[] provice = new String[3];
                provice[0] = strProvice;
                provice[1] = strCity;
                provice[2] = strRegion;
                bundle.putStringArray("provice", provice);
                bundle.putString("address", str);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                getActivity().setResult(18, intent);
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.address_change_fragment, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doIntent();
        if (getActivity() instanceof WhiteTopBarActivity) {
            topBarRight = ((WhiteTopBarActivity) getActivity()).getTopBarRightText();
            topBarRight.setOnClickListener(listener);
        }
        layout = (View) view.findViewById(R.id.person_area_layout);
        editText = (EditText) view.findViewById(R.id.address_edittext);
        addessBtn = (TextView) view.findViewById(R.id.add_sel_btn);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddressInitTask(getActivity(), new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        strProvice = province.getAreaName();
                        strCity = city.getAreaName();
                        strRegion = county.getAreaName();
                        String str = province.getAreaName() + city.getAreaName() + county.getAreaName();
                        addessBtn.setText(str);
                    }
                }).execute(strProvice, strCity, strRegion);
//                addressInitTask.execute(strProvice, strCity, strRegion);
            }
        });
        String strforamt = strProvice + strCity + strRegion;
        addessBtn.setText(strforamt);
        editText.setText(strAddress);
    }

    public void doIntent() {
        strProvice = "四川";
        strCity = "成都";
        strRegion = "武侯区";
        if (getArguments() != null) {
            String[] provice = getArguments().getStringArray("provice");
            strAddress = getArguments().getString("address", "");
            if (!(provice == null || provice.length == 0)) {
                if (provice.length == 3) {
                    if (!TextUtils.isEmpty(provice[0]))
                        strProvice = provice[0];
                    if (!TextUtils.isEmpty(provice[1]))
                        strCity = provice[1];
                    if (!TextUtils.isEmpty(provice[2]))
                        strRegion = provice[2];
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
