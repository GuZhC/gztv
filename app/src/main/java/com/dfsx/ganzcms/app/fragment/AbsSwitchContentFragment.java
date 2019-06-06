package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfsx.ganzcms.app.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 内容切换的Fragment
 * Created by liuwb on 2017/7/20.
 */
public abstract class AbsSwitchContentFragment extends Fragment {

    protected Activity activity;
    private FragmentManager manager;
    protected int currentShowIndex = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.frag_asb_switch_content_frag, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = getChildFragmentManager();

        setShowFragmentIndex(getDefaultShowFragmentIndex());
    }


    /**
     * 显示的是列表对应位置的Fragment
     *
     * @param index
     */
    public void setShowFragmentIndex(int index) {
        if (currentShowIndex == index) {
            return;
        }
        List<String> fragNameList = getFragmentNameList();
        if (fragNameList != null && index < fragNameList.size() && index >= 0) {
            FragmentTransaction transaction = manager.beginTransaction();
            String fragName = fragNameList.get(index);
            if (currentShowIndex != -1) { //隐藏刚刚显示的fragment
                Fragment oldFragment = manager.findFragmentByTag(fragNameList.get(currentShowIndex));
                if (oldFragment != null) {
                    transaction.hide(oldFragment);
                }
            }
            Fragment newFrag = manager.
                    findFragmentByTag(fragName);
            if (newFrag == null) {
                newFrag = createFragmentByFragmentClassName(fragName);
                transaction.add(R.id.frag_content_view, newFrag, fragName);
            } else {
                transaction.show(newFrag);
            }
            transaction.commit();
        }
        currentShowIndex = index;
    }

    abstract List<String> getFragmentNameList();

    protected int getDefaultShowFragmentIndex() {
        return 0;
    }

    private Fragment createFragmentByFragmentClassName(String fragmentName) {
        try {
            Constructor<Fragment>[] constructors = (Constructor<Fragment>[])
                    Class.forName(fragmentName).getConstructors();
            Constructor<Fragment> constructorFrag = constructors[0];
            return constructorFrag.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
