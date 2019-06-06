package com.dfsx.searchlibaray;

import android.support.v4.app.Fragment;

public abstract class AbsSearchFragment extends Fragment {

    public abstract void search(String key);


    public abstract void onEditTextTextChange(String text);
}
