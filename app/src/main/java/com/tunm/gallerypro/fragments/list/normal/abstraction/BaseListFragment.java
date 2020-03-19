package com.tunm.gallerypro.fragments.list.normal.abstraction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.tunm.gallerypro.DeleteMediaItemObserver;
import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.MainActivity;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.customComponent.GridlayoutManagerFixed;
import com.tunm.gallerypro.data.LayoutType;
import com.tunm.gallerypro.data.provider.FileChangeListener;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.BaseFragment;
import com.tunm.gallerypro.fragments.home.HomeFragment;
import com.tunm.gallerypro.utils.CommonBarColor;
import com.tunm.gallerypro.view.AnimatedRecyclerView;
import com.tunm.gallerypro.view.ItemOffsetDecoration;

import butterknife.BindView;

abstract public class BaseListFragment extends BaseFragment implements FileChangeListener, DeleteMediaItemObserver {

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    @BindView(R.id.albumRecycleView)
    protected AnimatedRecyclerView mRecyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected ItemOffsetDecoration itemOffsetDecoration;

    protected LayoutType mLayoutType;
    protected int columnNumber;
    protected ActionMode mActionMode;

//    private ContentProviderObserver mProviderObserver;

    protected HomeFragment parentFragment;

    private View rootView;

    public BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        parentFragment = ((HomeFragment) this.getParentFragment());
        ((MainActivity) mActivity).addFileChangeListener(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        initRecycleView(rootView);
        implementRecyclerViewClickListeners();
        ((DrawerLocker) mActivity).setDrawerEnabled(true);
        return rootView;
    }

    protected abstract void implementRecyclerViewClickListeners();

    @Override
    public void onResume() {
        if(mLayoutType == LayoutType.GRID) {
            setupColumn();
        }
        super.onResume();
        refreshTheme();
    }

    protected void refreshTheme() {
        if(mColorTheme.isDarkTheme()) {
            mRecyclerView.setBackgroundColor(mActivity.getColor(R.color.colorDarkBackground));
            CommonBarColor.setStatusBarColor(mActivity, mActivity.getColor(R.color.colorDarkBackgroundHighlight));
            CommonBarColor.setNavigationBarColor(mActivity, mActivity.getColor(R.color.colorDarkBackgroundHighlight));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorDarkBackgroundHighlight));
        } else {
            mRecyclerView.setBackgroundColor(mColorTheme.getBackgroundColor());
            CommonBarColor.setStatusBarColor(mActivity, mColorTheme.getPrimaryColor());
            CommonBarColor.setNavigationBarColor(mActivity, mColorTheme.getPrimaryColor());
            rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        }
    }

    public void setupColumn() {
        if (columnNumber != ((GridLayoutManager) mLayoutManager).getSpanCount()) {
            mRecyclerView.setLayoutManager(new GridlayoutManagerFixed(getContext(), columnNumber));
            ((GridlayoutManagerFixed) mLayoutManager).setSpanCount(columnNumber);
        }
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    protected void initRecycleView(View v) {
        mLayoutType = getLayoutType();
        mRecyclerView.setHasFixedSize(true);
        itemOffsetDecoration = new ItemOffsetDecoration(mActivity, R.dimen.timeline_item_spacing);
        mRecyclerView.addItemDecoration(itemOffsetDecoration);
    }

    //Set enable SwipeRefreshLayout
//    public void setEnableSwipeRefresh(boolean isEnable) {
//        if(isEnable) {
//            mSwipeRefreshLayout.setEnabled(true);
//            mSwipeRefreshLayout.setDistanceToTriggerSync(0);
//        } else {
//            mSwipeRefreshLayout.setEnabled(false);
//            mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
//        }
//    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected LayoutType getLayoutType() {
        return null;
    }

    protected SortingMode getSortingMode() {
        return null;
    }

    protected SortingOrder getSortingOrder() {
        return null;
    }
}
