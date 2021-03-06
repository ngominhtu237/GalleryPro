package com.tunm.gallerypro.fragments.list.section.abstraction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.tunm.gallerypro.CallBackToActivityListener;
import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.DeleteMediaItemObserver;
import com.tunm.gallerypro.DeleteMediaItemSubject;
import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.MainActivity;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.activity.AboutActivity;
import com.tunm.gallerypro.activity.SettingsActivity;
import com.tunm.gallerypro.customComponent.GridlayoutManagerFixed;
import com.tunm.gallerypro.data.MediaHelper;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.provider.FileChangeListener;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.BaseFragment;
import com.tunm.gallerypro.fragments.ICheckedItem;
import com.tunm.gallerypro.fragments.RecycleViewClickListener;
import com.tunm.gallerypro.fragments.ViewHolderListener;
import com.tunm.gallerypro.fragments.home.HomeFragment;
import com.tunm.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tunm.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.tunm.gallerypro.fragments.list.section.abstraction.view.ITimelineView;
import com.tunm.gallerypro.fragments.viewer.DeletedItemCallback;
import com.tunm.gallerypro.fragments.viewer.ImagePagerFragment;
import com.tunm.gallerypro.setting.callback.ColumnChangeObserver;
import com.tunm.gallerypro.theme.ColorTheme;
import com.tunm.gallerypro.utils.CommonBarColor;
import com.tunm.gallerypro.view.ItemOffsetDecoration;
import com.tunm.gallerypro.view.LockableViewPager;
import com.tunm.gallerypro.view.dialog.ChooseColumnDialog;
import com.tunm.gallerypro.view.dialog.DeleteDialog;
import com.tunm.gallerypro.view.dialog.SortDialogTimeline;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class BaseTimelineFragment extends BaseFragment implements RecycleViewClickListener, ICheckedItem,
        ViewHolderListener, DeletedItemCallback, FileChangeListener, ColumnChangeObserver, ITimelineView, DeleteMediaItemObserver {
    @BindView(R.id.timelineRecycleView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    @BindView(R.id.loading_layout)
    protected RelativeLayout mLoadingLayout;

    protected GridlayoutManagerFixed mLayoutManager;
    protected ActionMode mActionMode;
    protected int columnNumber;

    private BaseTimelineAdapter adapter;
    protected ITimelinePresenter presenter;
    protected ITimelineRepository model;
    protected BaseActionMode actionMode;
    private ArrayList<Integer> mListDeletedPosition = new ArrayList<>();

    private HomeFragment parentFragment;
    public static int currentPosition;

    protected CallBackToActivityListener callBackListener;
    private ColorTheme colorTheme;
    private LockableViewPager parentViewPager;

    private boolean isZoomIn, isScroll;
    private View rootView;

    public BaseTimelineFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        model = createModel();
        presenter = createPresenter(model);
        actionMode = createActionMode();
        parentFragment = ((HomeFragment) this.getParentFragment());
        ((MainActivity) mActivity).addFileChangeListener(this);
        colorTheme = new ColorTheme(mActivity);
        CustomModelClass.getInstance().addColumnChangeObserver(this);
        DeleteMediaItemSubject.getInstance().registerObserver(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity instanceof CallBackToActivityListener)
            callBackListener = (CallBackToActivityListener) mActivity;
    }

    protected abstract BaseActionMode createActionMode();

    protected abstract ITimelineRepository createModel();

    protected abstract ITimelinePresenter createPresenter(ITimelineRepository model);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) mActivity).setDrawerEnabled(true);

        loadData();
        initRecycleView();

        CustomModelClass.getInstance().addThemeChangeObserver(this);
        parentViewPager = (LockableViewPager) container;
        return rootView;
    }

    protected abstract void loadData();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private void scrollToPosition() {
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                mRecyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(BaseTimelineFragment.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    mRecyclerView.post(() -> layoutManager.scrollToPosition(BaseTimelineFragment.currentPosition));
                }
            }
        });
    }

    /**
     *
     * Có 2 cách để tạo Base: 1 là tạo abstract method in base sau đó impl ở bên ngoài từng thằng con
     * 2 là truyền kiểu dữ liệu base vào khi khởi tạo lớp
     */

    protected SortingMode getSortingMode() {
        return adapter != null ? adapter.getSortingMode() : getSortModeFromPref();
    }

    protected SortingOrder getSortingOrder() {
        return adapter != null ? adapter.getSortingOrder() : getSortOrderFromPref();
    }

    @Override
    public void onResume() {
        refreshTheme();
        super.onResume();
    }

    private void refreshTheme() {
        if(colorTheme.isDarkTheme()) {
            mRecyclerView.setBackgroundColor(mActivity.getColor(R.color.colorDarkBackground));
            CommonBarColor.setStatusBarColor(mActivity, mActivity.getColor(R.color.colorDarkBackgroundHighlight));
            CommonBarColor.setNavigationBarColor(mActivity, mActivity.getColor(R.color.colorDarkBackgroundHighlight));
            mLoadingLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkBackground));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorDarkBackgroundHighlight));
        } else {
            mRecyclerView.setBackgroundColor(colorTheme.getBackgroundColor());
            CommonBarColor.setStatusBarColor(mActivity, mColorTheme.getPrimaryColor());
            CommonBarColor.setNavigationBarColor(mActivity, mColorTheme.getPrimaryColor());
            mLoadingLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = MediaHelper.getTimelineColumnPortrait(mActivity);
        } else {
            columnNumber = MediaHelper.getTimelineColumnLandscape(mActivity);
        }
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(mActivity, R.dimen.timeline_item_spacing);
        mRecyclerView.addItemDecoration(itemOffsetDecoration);
        mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
        mLayoutManager.setItemPrefetchEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        adapter = createAdapter();
        adapter.setRecycleViewClickListener(this);
        adapter.setCheckedItemListener(this);
        adapter.setViewHolderListener(this);
        mRecyclerView.setAdapter(adapter);
        setSpanSize();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mLayoutManager.setScrollEnabled(true);
                        isScroll = false;
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mActivity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if(!isScroll) {
                    float scaleFactor = detector.getScaleFactor();
                    isZoomIn = scaleFactor > 1;
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mLayoutManager.setScrollEnabled(false);
                parentViewPager.setSwipeLocked(true);
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if(isZoomIn) {
                    if(columnNumber > ChooseColumnDialog.MIN_COLUMN_MEDIA) {
                        columnNumber--;
                        MediaHelper.setTimelineColumnPortrait(columnNumber);
                        animateRecyclerLayoutChange();
                    } else {
                        Toast.makeText(mActivity, "min column is: " + ChooseColumnDialog.MIN_COLUMN_MEDIA, Toast.LENGTH_SHORT).show();
                        mLayoutManager.setScrollEnabled(true);
                        parentViewPager.setSwipeLocked(false);
                    }
                }
                else {
                    if(columnNumber < ChooseColumnDialog.MAX_COLUMN_MEDIA) {
                        columnNumber++;
                        MediaHelper.setTimelineColumnPortrait(columnNumber);
                        animateRecyclerLayoutChange();
                    } else {
                        Toast.makeText(mActivity, "max column is: " + ChooseColumnDialog.MAX_COLUMN_MEDIA, Toast.LENGTH_SHORT).show();
                        mLayoutManager.setScrollEnabled(true);
                        parentViewPager.setSwipeLocked(false);
                    }
                }
                super.onScaleEnd(detector);
            }
        });
        mRecyclerView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return false;
        });
    }

    protected void animateRecyclerLayoutChange() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(300);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
                mLayoutManager.requestLayout();
                setSpanSize();
                mRecyclerView.setLayoutManager(mLayoutManager);
                adapter.notifyDataSetChanged();

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(300);
                mRecyclerView.startAnimation(fadeIn);
                mLayoutManager.setScrollEnabled(true);
                parentViewPager.setSwipeLocked(false);
            }
        });
        mRecyclerView.clearAnimation();
        mRecyclerView.startAnimation(fadeOut);
    }

    private void setSpanSize() {
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (ViewType.HEADER_VIEW_TYPE == adapter.getItemViewType(position)) {
                    return getHeaderColumn();
                }
                return getContentColumn();
            }
        });
    }

    private int getContentColumn() {
        return 1;
    }

    private int getHeaderColumn() {
        return columnNumber;
    }

    protected abstract BaseTimelineAdapter createAdapter();

    protected abstract SortingMode getSortModeFromPref();

    protected abstract SortingOrder getSortOrderFromPref();

    protected abstract void setSortModeToPref(SortingMode sortingMode);

    protected abstract void setSortOrderToPref(SortingOrder sortingOrder);

    @Override
    public void onClick(View view, int position) {
        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
            ImagePagerFragment.mImageList = getAdapter().getMediaItems();
//            SquareImageView transitioningView = view.findViewById(R.id.ivTimelineThumbnail);
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            ImagePagerFragment pagerFragment = (ImagePagerFragment) fragmentManager.findFragmentByTag("ImagePagerFragment");
            if(pagerFragment == null) {
                pagerFragment = new ImagePagerFragment();
                Bundle args = new Bundle();
                args.putInt("currentPosition", position);
                args.putBoolean("isImage", isImage());
                pagerFragment.setArguments(args);
                pagerFragment.setDeleteCallback(this);
            }

            fragmentManager
                    .beginTransaction()
//                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .add(R.id.fragment_container, pagerFragment, ImagePagerFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    protected abstract boolean isImage();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timeline_camera:
                startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
                return true;

            case R.id.action_timeline_sort:
                showChoiceDialog(getSortingMode(), getSortingOrder());
                return true;

            case R.id.action_timeline_about:
                startActivity(new Intent(mActivity, AboutActivity.class));
                return true;

            case R.id.action_timeline_setting:
                startActivity(new Intent(mActivity, SettingsActivity.class));
                return true;

            case R.id.action_timeline_delete:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

//    public void setEnableSwipeRefresh(boolean isEnable) {
//        if(mSwipeRefreshLayout != null) {
//            if (isEnable) {
//                mSwipeRefreshLayout.setEnabled(true);
//                mSwipeRefreshLayout.setDistanceToTriggerSync(0);
//            } else {
//                mSwipeRefreshLayout.setEnabled(false);
//                mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
//            }
//        }
//    }

    public void setNullToActionMode() {
        new Handler().postDelayed(() -> {
            // Do something after 1s = 1000ms
            parentFragment.showAppBarLayout();
        }, 1000);

        parentViewPager.setSwipeLocked(false);
        if (mActionMode != null)
            mActionMode = null;
    }

    private void showChoiceDialog(SortingMode sortingMode, SortingOrder sortingOrder) {
        SortDialogTimeline dialog = new SortDialogTimeline(mActivity);
        dialog.setTitle("Sort");
        dialog.setSortingMode(sortingMode);
        dialog.setSortingOrder(sortingOrder);
        dialog.setCancelButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setSortButton("OK", v -> {
            int i1 = dialog.getCheckedSortingMode();
            int i2 = dialog.getCheckedSortingOrder();
            adapter.changeSorting(SortingMode.fromValue(i1), SortingOrder.fromValue(i2));
            setSortModeToPref(SortingMode.fromValue(i1));
            setSortOrderToPref(SortingOrder.fromValue(i2));
            dialog.dismiss();
        });
        dialog.show();
    }

    public BaseTimelineAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void change(int number) {
        // call change menu in ActionMode
        if(actionMode != null) {
            actionMode.changeMenu(number);
        }
    }

    public void onListItemSelect(int position) {
        adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            mActionMode = Objects.requireNonNull(mActivity).startSupportActionMode(actionMode);
            parentFragment.hideAppBarLayout();
            parentViewPager.setSwipeLocked(true);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            parentViewPager.setSwipeLocked(false);
        }

        if (mActionMode != null) {
            mActionMode.setTitle(getAdapter().getSelectedCount() + " selected");
        }
    }

    public ArrayList<Integer> getListDeletedPosition() {
        return mListDeletedPosition;
    }

    public void setListDeletedPosition(ArrayList<Integer> mListDeletedPosition) {
        this.mListDeletedPosition = mListDeletedPosition;
    }

    @SuppressLint("SetTextI18n")
    public void deleteMedias() {
        ArrayList<MediaItem> mDeleteItems = new ArrayList<>();
        mListDeletedPosition.clear();
        for (int i = (getAdapter().getSelectedIds().size() - 1); i >= 0; i--) {
            if (getAdapter().getSelectedIds().valueAt(i)) {
                mDeleteItems.add((MediaItem) getAdapter().getMediaItems().get(getAdapter().getSelectedIds().keyAt(i)));
                mListDeletedPosition.add(getAdapter().getSelectedIds().keyAt(i));
                Log.d("deleted item position ", String.valueOf(getAdapter().getSelectedIds().keyAt(i)));
            }
        }
        DeleteDialog dialog = new DeleteDialog(mActivity);
        dialog.setTitle("Delete");
        String s = mListDeletedPosition.size() == 1 ? "item" : "items";
        dialog.setMessage("Are you sure you want to delete " + mListDeletedPosition.size() + " " + s + "?");
        dialog.setNegativeButton("Cancel", v -> dialog.dismiss());
        dialog.setPositiveButton("Delete", v -> {
            dialog.dismiss();
            presenter.deleteMedias(mDeleteItems);
            mActionMode.finish();
        });
        dialog.show();
    }

    @Override
    public void requestUpdateTheme() {
        super.requestUpdateTheme();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void requestUpdateColumn() {
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = MediaHelper.getTimelineColumnPortrait(mActivity);
        } else {
            columnNumber = MediaHelper.getTimelineColumnLandscape(mActivity);
        }
        setupColumn();
        Log.v("update column", "BaseTimelineFragment");
    }

    public void setupColumn() {
        if (columnNumber != mLayoutManager.getSpanCount() && mRecyclerView != null) {
            mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
            mLayoutManager.requestLayout();
            setSpanSize();
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public void onGetTimelineSuccess(ArrayList<MediaItem> mediaItems) {
        if(mediaItems.size() > 0) {
            getAdapter().setMediaItems(mediaItems);
            getAdapter().changeSorting(getSortingMode(), getSortingOrder());
            if(mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
            if(desertPlaceholder != null) desertPlaceholder.setVisibility(View.GONE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if(desertPlaceholder != null) desertPlaceholder.setVisibility(View.VISIBLE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteTimelineSuccess() {
        for(int i=0; i<getListDeletedPosition().size(); i++) {
            getAdapter().removeMedia(getListDeletedPosition().get(i));
        }
        if(getAdapter().getMediaItems().size() == 0) {
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.GONE);
            desertPlaceholder.setVisibility(View.VISIBLE);
        }
        DeleteMediaItemSubject.getInstance().unRegisterObserver(this);
        DeleteMediaItemSubject.getInstance().notifyDataChange();
        DeleteMediaItemSubject.getInstance().registerObserver(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DeleteMediaItemSubject.getInstance().unRegisterObserver(this);
    }
}
