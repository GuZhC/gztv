package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.AppSearchHelper;
import com.dfsx.ganzcms.app.business.AppTibetanSearchHelper;
import com.dfsx.searchlibaray.businness.SearchHelper;

public class AppTibetanSearchFragment extends AppSearchFragment {

    public static final String KEY_COLUMN_ID = "AppTibetanSearchFragment_key_column_id";

    private long searchColumnId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            searchColumnId = getArguments().getLong(KEY_COLUMN_ID, 0L);
        }
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.radio_filter_group)
                .setVisibility(View.GONE);
    }

    @Override
    protected SearchHelper createSearchHelper() {
        AppTibetanSearchHelper searchHelper = new AppTibetanSearchHelper(getActivity(), searchColumnId);
        return searchHelper;
    }
}
