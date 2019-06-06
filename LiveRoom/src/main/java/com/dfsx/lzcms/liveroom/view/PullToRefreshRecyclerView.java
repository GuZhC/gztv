package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by Administrator on 2015/12/2.
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    private PullRecyclerHelper pullRecyclerHelper;

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView rv = new RecyclerView(context, attrs);
        return rv;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        if (pullRecyclerHelper != null) {
            return pullRecyclerHelper.isReadyForPullEnd() && isLastItemVisiable();
        }
        return isLastItemVisiable();
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (pullRecyclerHelper != null) {
            return pullRecyclerHelper.isReadyForPullStart() && isLastItemVisiable();
        }
        return isFirstItemVisible();
    }


    private boolean isFirstItemVisible() {
        if (getRefreshableView() != null) {
            final RecyclerView.Adapter adapter = getRefreshableView().getAdapter();
            final LinearLayoutManager mLayoutmanager = (LinearLayoutManager) getRefreshableView().getLayoutManager();


            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else {
                /**
                 * This check should really just be:
                 * mRootView.getFirstVisiblePosition() == 0, but PtRListView
                 * internally use a HeaderView which messes the positions up. For
                 * now we'll just add one to account for it and rely on the inner
                 * condition which checks getTop().
                 */

                int[] into = {0, 0};
                if (mLayoutmanager != null)
                    into[0] = mLayoutmanager.findFirstVisibleItemPosition();
                if (into.length > 0 && into.length > 0 && into[0] <= 1) {
                    final View firstVisibleChild = getRefreshableView().getChildAt(0);
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.getTop() >= getRefreshableView().getTop();
                    }
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisiable() {
        if (getRefreshableView() != null) {
            final RecyclerView.Adapter adapter = getRefreshableView().getAdapter();
            final LinearLayoutManager mLayoutmanager = (LinearLayoutManager) getRefreshableView().getLayoutManager();

            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else {
                final int lastItemPosition = adapter.getItemCount() - 1;
                int[] into = {0, 0};
                if (mLayoutmanager != null)
                    into[0] = mLayoutmanager.findLastVisibleItemPosition();
                if (into[0] >= lastItemPosition - 1) {
                    int dex = into[0] - mLayoutmanager.findFirstVisibleItemPosition();
                    final View lastVisibleChild = getRefreshableView().getChildAt(dex);
                    if (lastVisibleChild != null) {
                        return lastVisibleChild.getBottom() <= getRefreshableView().getBottom();
                    }
                }
            }
        }

        return false;
    }


    @Override
    protected void onPtrSaveInstanceState(Bundle state) {
        state.putSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY, ChildrenViewStateHelper.newInstance(this).saveChildrenState());
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle state) {
        if (state instanceof Bundle) {
            final Bundle localState = (Bundle) state;
            ChildrenViewStateHelper.newInstance(this).
                    restoreChildrenState(localState.
                            getSparseParcelableArray
                                    (ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY));
        } else {
            super.onPtrRestoreInstanceState(state);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(final SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    public void setPullRecyclerHelper(PullRecyclerHelper recyclerHelper) {
        this.pullRecyclerHelper = recyclerHelper;
    }

    public interface PullRecyclerHelper {
        boolean isReadyForPullEnd();

        boolean isReadyForPullStart();
    }
}
