package com.dfsx.searchlibaray;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.InterceptTouchLinearLayout;
import com.dfsx.lzcms.liveroom.view.InterceptTouchListener;
import com.dfsx.lzcms.liveroom.view.InterceptTouchRelativeLayout;
import com.dfsx.searchlibaray.businness.SearchHistoryManager;
import com.dfsx.searchlibaray.businness.SuggestListHelper;
import com.dfsx.searchlibaray.view.SearchSuggestListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SearchActivity extends BaseActivity {

    public static final String TAG_HIS_FRAG = "com.dfsx.searchlibaray.SearchActivity_his_fragment";
    public static final String TAG_SEARCH_FRAG = "com.dfsx.searchlibaray.SearchActivity_search_fragment";

    public static final String KEY_CUSTOM_SEARCH_CONTENT_FRAGMENT = "com.dfsx.searchlibaray.SearchActivity_CUSTOM_SEARCH_CONTENT_FRAGMENT";

    private Activity act;

    private InterceptTouchRelativeLayout interceptTouchRelativeLayout;
    private EditText editText;
    private ImageView delText;
    private TextView cancelText;

    protected AbsSearchFragment searchFragment;
    private Fragment historyFragment;

    private FrameLayout suggestViewContainer;

    private SuggestListHelper suggestListHelper;

    private Handler handler = new Handler();

    private InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.act_search_layout);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        interceptTouchRelativeLayout = (InterceptTouchRelativeLayout) findViewById(R.id.intercept_view);
        suggestViewContainer = (FrameLayout) findViewById(R.id.suggest_view_container);
        editText = (EditText) findViewById(R.id.search_edit_text);
        delText = (ImageView) findViewById(R.id.del_search_text);
        cancelText = (TextView) findViewById(R.id.cancel_search_text);
        delText.setVisibility(View.GONE);
        setContentFragment();

        suggestListHelper = new SuggestListHelper(suggestViewContainer, PixelUtil.dp2px(this, 10),
                PixelUtil.dp2px(this, 10), 0, PixelUtil.dp2px(this, 150));
        suggestListHelper.setOnSuggestItemClickListener(new SearchSuggestListView.OnSuggestItemClickListener() {
            @Override
            public void onSuggestClick(String suggestText) {
                searchText(suggestText);
            }
        });


        delText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                suggestListHelper.getSuggest(editText.getText().toString());
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    delText.setVisibility(View.GONE);
                } else {
                    delText.setVisibility(View.VISIBLE);
                }
                if (searchFragment != null) {
                    searchFragment.onEditTextTextChange(editText.getText().toString());
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        String text = editText.getText().toString();
                        searchStart(text);
                        hideInputSoft();
                    }
                }
                return false;
            }
        });

        interceptTouchRelativeLayout.setInterceptTouchListener(new InterceptTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (suggestListHelper != null && !suggestListHelper.inRangeOfView(ev)) {
                    suggestListHelper.remove();
                }
                return false;
            }
        });
    }

    protected void setContentFragment() {
        showSearchContentFrag();
        showHistoryFragment();
    }

    /**
     * 搜索指定的文字
     *
     * @param text
     */
    public void searchText(final String text) {
        if (!TextUtils.isEmpty(text)) {
            editText.setText(text);
            editText.setSelection(0, text.length());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchStart(text);
                }
            }, 100);
        }
    }

    private void searchStart(String text) {
        if (historyFragment != null) {
            removeHistoryFrag();
        }
        suggestListHelper.remove();
        SearchHistoryManager.addHistory(text);
        if (searchFragment != null && !TextUtils.isEmpty(text)) {
            searchFragment.search(text);
        }
    }

    private void hideInputSoft() {
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private Fragment createFragmentByName(String fragmentName) {
        try {
            Constructor<Fragment>[] constructors = (Constructor<Fragment>[])
                    Class.forName(fragmentName).getConstructors();
            Constructor<Fragment> constructorFrag = constructors[0];
            Fragment fragment = constructorFrag.newInstance();
            fragment.setArguments(getIntent().getExtras());
            if (fragment instanceof AbsSearchFragment)
                searchFragment = (AbsSearchFragment) fragment;
            return fragment;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showSearchContentFrag() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SEARCH_FRAG);
        if (fragment == null) {
            fragment = createSearchFragment();
            getSupportFragmentManager()
                    .beginTransaction().add(R.id.search_content, fragment)
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager()
                    .beginTransaction().show(fragment)
                    .commitAllowingStateLoss();
        }
    }

    private void hideSearchFragment() {
        if (searchFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction().hide(searchFragment)
                    .commitAllowingStateLoss();
        }
    }

    private Fragment createSearchFragment() {
        String fragmentName = "";
        if (getIntent() != null) {
            fragmentName = getIntent().getStringExtra(KEY_CUSTOM_SEARCH_CONTENT_FRAGMENT);
        }
        Fragment fragment = null;
        if (!TextUtils.isEmpty(fragmentName)) {
            fragment = createFragmentByName(fragmentName);
        }
        if (fragment == null) {
            searchFragment = new FilterSearchFragment();
            fragment = searchFragment;
        }
        return fragment;
    }


    private void showHistoryFragment() {
        historyFragment = getSupportFragmentManager().findFragmentByTag(TAG_HIS_FRAG);
        if (historyFragment == null) {
            historyFragment = new HistoryFragment();
        }
        getSupportFragmentManager()
                .beginTransaction().add(R.id.search_content, historyFragment)
                .commitAllowingStateLoss();
    }

    private void removeHistoryFrag() {
        if (historyFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(historyFragment)
                    .commitAllowingStateLoss();
            historyFragment = null;
        }
    }
}
