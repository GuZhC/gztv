package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.lzcms.liveroom.fragment.VoteWebFragment;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

/**
 * Created by heyang on 2017/9/30
 */
public class NewsWebVoteFragment extends VoteWebFragment implements FragmentBackHandler {

    private String mThumb;
    NewsDatailHelper newsDatailHelper;
    View rootView;
    ShareContent mShareContent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (activity instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) activity).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newsDatailHelper.shareNewUiWnd(rootView, mShareContent);
                        }
                    });
        }
        if (getArguments() != null) {
            mShareContent = (ShareContent) getArguments().getSerializable("object");
        }
        setLeftGoBack();
        newsDatailHelper = new NewsDatailHelper(activity);
        return rootView;
    }

    protected void setLeftGoBack() {
        if (getActivity() instanceof WhiteTopBarActivity) {
            if (((WhiteTopBarActivity) getActivity()).getTopBarLeft() != null) {
                ((WhiteTopBarActivity) getActivity()).getTopBarLeft().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mAgentWeb.back()) {
                            getActivity().finish();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return mAgentWeb.back();
    }


}