package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/9/12.
 */
public class NewsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_frag_test, null);
        TextView tv = (TextView) rootView.findViewById(R.id.frag_content);
        tv.setText("要闻");
        return rootView;
    }
}
