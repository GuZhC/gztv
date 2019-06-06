package com.dfsx.core.common.act;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import com.dfsx.core.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by liuwb on 2016/8/19.
 */
public class DefaultFragmentActivity extends BaseActivity {

    public static final String KEY_FRAGMENT_NAME = "com.dfsx.lzcms.liveroom.DefaultFragmentActivity_fragment_name";
    public static final String KEY_FRAGMENT_PARAM = "com.dfsx.lzcms.liveroom.DefaultFragmentActivity_fragment_PRRAM";

    protected Fragment fragment;
    protected FragmentActivity context;

    protected LinearLayout topViewContainer;

    //heyang 2016-10-27
    public static void start(Context context, String className) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, className);
        contextStartActivity(context, intent);
    }

    public static void start(Context context, String className, Intent intent) {
        if (intent == null) {
            intent = new Intent(context, DefaultFragmentActivity.class);
        } else {
            intent.setClass(context, DefaultFragmentActivity.class);
        }
        intent.putExtra(KEY_FRAGMENT_NAME, className);
        contextStartActivity(context, intent);
    }

    public static void start(Context context, String className, long param1) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, className);
        intent.putExtra(KEY_FRAGMENT_PARAM, param1);
        contextStartActivity(context, intent);
    }

    public static void start(Context context, String className, Bundle bundle) {
        Intent intent = new Intent(context, DefaultFragmentActivity.class);
        intent.putExtra(KEY_FRAGMENT_NAME, className);
        intent.putExtras(bundle);
        contextStartActivity(context, intent);
    }

    public static void contextStartActivity(Context context, Intent intent) {
        try {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frag_content);
        context = this;
        doIntent();

        topViewContainer = (LinearLayout) findViewById(R.id.top_bar_container_view);

        addTopView(topViewContainer);
    }

//    public String isCheckToPersonHome() {
//        String fragmentName = getIntent().getStringExtra(KEY_FRAGMENT_NAME);
//        if (TextUtils.isEmpty(fragmentName)) {
//            Uri uri = getIntent().getData();
//            String frag = uri.getFragment();
//            if (frag == null && uri != null && !TextUtils.isEmpty(uri.toString())) {
//                long id = getIntent().getLongExtra("id", -1);
//                //                long id = Long.parseLong(uri.toString());
//                if (id != -1) {
//                    getIntent().putExtra("id", id);
//                    frag = "com.dfsx.ganzcms.app.fragment.PersonHomeFragment";
//                } else {
//                    frag = "com.dfsx.logonproject.dzfragment.RegVerifyFragment";
//                }
//            }
//            fragmentName = frag;
//        }
//        return fragmentName;
//    }

    public String isCheckToPersonHome() {
        String fragmentName = getIntent().getStringExtra(KEY_FRAGMENT_NAME);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String host = uri.getHost();
            String frag = uri.getFragment();
            if (TextUtils.equals(host, "go_home")) {
                long id = getIntent().getLongExtra("id", -1);
                getIntent().putExtra("id", id);
                frag = "com.dfsx.ganzcms.app.fragment.PersonHomeFragment";
            } else if (TextUtils.equals(host, "go_checktele")) {
                frag = "com.dfsx.logonproject.dzfragment.RegVerifyFragment";
            } else if (TextUtils.equals(host, "go_communitPub")) {
                getIntent().putExtra("type", 1);
                frag = "com.dfsx.ganzcms.app.fragment.CommunityPubFileFragment";
            }
            fragmentName = frag;
        }
        return fragmentName;
    }

    private void doIntent() {
        String fragmentName = isCheckToPersonHome();
        if (TextUtils.isEmpty(fragmentName)) {
            return;
        }
        try {
            FragmentManager fragmentManager = context.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragment = fragmentManager.findFragmentByTag("container_frag");
            //            if(fragment instanceof  ViewClickLister)
            //              viewCall= (ViewClickLister) fragment;

            if (fragment == null) {
                Constructor<Fragment>[] constructors = (Constructor<Fragment>[])
                        Class.forName(fragmentName).getConstructors();
                Constructor<Fragment> constructorFrag = constructors[0];
                fragment = constructorFrag.newInstance();
                fragment.setArguments(getIntent().getExtras());

                if (fragment instanceof ViewClickLister)
                    viewCall = (ViewClickLister) fragment;
                transaction.add(R.id.container, fragment, "container_frag");
            } else {
                transaction.show(fragment);
            }
            transaction.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void addTopView(LinearLayout topViewContainer) {

    }

    public interface ViewClickLister {
        public void AreaClick(MotionEvent event);
    }

    ViewClickLister viewCall = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (viewCall != null)
                viewCall.AreaClick(ev);
            //            if (isShouldHideInput(v, ev)) {
            //                InputMethodManager imm = (InputMethodManager).getSystemService(Context.INPUT_METHOD_SERVICE);
            //                if (imm != null) {
            //                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            //                }
            //            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /***
     *
     * heyang  2017-11/1
     *   对Fragment 实现返回键监听，Fragment实现 FragmentBackHandler  接口。重写onBackPressed（）方法
     */
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }
}
