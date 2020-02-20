package com.tubeeapp.gallerypro.fragments.list.section.abstraction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
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
import com.tubeeapp.gallerypro.CallBackToActivityListener;
import com.tubeeapp.gallerypro.CustomModelClass;
import com.tubeeapp.gallerypro.DrawerLocker;
import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.activity.AboutActivity;
import com.tubeeapp.gallerypro.activity.SettingsActivity;
import com.tubeeapp.gallerypro.customComponent.GridlayoutManagerFixed;
import com.tubeeapp.gallerypro.data.MediaHelper;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.data.provider.ContentProviderObserver;
import com.tubeeapp.gallerypro.data.provider.ProviderChangeListener;
import com.tubeeapp.gallerypro.data.sort.SortingMode;
import com.tubeeapp.gallerypro.data.sort.SortingOrder;
import com.tubeeapp.gallerypro.fragments.BaseFragment;
import com.tubeeapp.gallerypro.fragments.ICheckedItem;
import com.tubeeapp.gallerypro.fragments.RecycleViewClickListener;
import com.tubeeapp.gallerypro.fragments.ViewHolderListener;
import com.tubeeapp.gallerypro.fragments.home.HomeFragment;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.tubeeapp.gallerypro.fragments.viewer.DeletedItemCallback;
import com.tubeeapp.gallerypro.setting.callback.ColumnChangeObserver;
import com.tubeeapp.gallerypro.theme.ColorTheme;
import com.tubeeapp.gallerypro.utils.CommonBarColor;
import com.tubeeapp.gallerypro.view.ItemOffsetDecoration;
import com.tubeeapp.gallerypro.view.LockableViewPager;
import com.tubeeapp.gallerypro.view.dialog.ChooseColumnDialog;
import com.tubeeapp.gallerypro.view.dialog.DeleteDialog;
import com.tubeeapp.gallerypro.view.dialog.SortDialogTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class BaseTimelineFragment extends BaseFragment implements RecycleViewClickListener, ICheckedItem,
        ViewHolderListener, DeletedItemCallback, ProviderChangeListener, ColumnChangeObserver {
    @BindView(R.id.timelineRecycleView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    @BindView(R.id.loading_layout)
    protected RelativeLayout loadingLayout;

    protected Activity mAttachedActivity;

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

    private ContentProviderObserver mProviderObserver;
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
        mAttachedActivity = getActivity();
        model = createModel();
        presenter = createPresenter(model);
        actionMode = createActionMode();
        parentFragment = ((HomeFragment) this.getParentFragment());
        mProviderObserver = new ContentProviderObserver();
        mProviderObserver.setChangeListener(this);
        mAttachedActivity.getContentResolver().
                registerContentObserver(
                        MediaStore.Files.getContentUri("external"),
                        true,
                        mProviderObserver);
        colorTheme = new ColorTheme(mAttachedActivity);
        CustomModelClass.getInstance().addColumnChangeObserver(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof CallBackToActivityListener)
            callBackListener = (CallBackToActivityListener) getActivity();
    }

    protected abstract BaseActionMode createActionMode();

    protected abstract ITimelineRepository createModel();

    protected abstract ITimelinePresenter createPresenter(ITimelineRepository model);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(true);

        loadData();
        initRecycleView();

        prepareTransitions();
        postponeEnterTransition();

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
            mRecyclerView.setBackgroundColor(mAttachedActivity.getColor(R.color.colorDarkBackground));
            CommonBarColor.setStatusBarColor(getActivity(), getActivity().getColor(R.color.colorDarkBackgroundHighlight));
            loadingLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkBackground));
        } else {
            mRecyclerView.setBackgroundColor(colorTheme.getBackgroundColor());
            CommonBarColor.setStatusBarColor(getActivity(), mColorTheme.getPrimaryColor());
            loadingLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = MediaHelper.getTimelineColumnPortrait(mAttachedActivity);
        } else {
            columnNumber = MediaHelper.getTimelineColumnLandscape(mAttachedActivity);
        }
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(mAttachedActivity, R.dimen.timeline_item_spacing);
        mRecyclerView.addItemDecoration(itemOffsetDecoration);
        mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
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
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mAttachedActivity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
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
                        Toast.makeText(mAttachedActivity, "min column is: " + ChooseColumnDialog.MIN_COLUMN_MEDIA, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mAttachedActivity, "max column is: " + ChooseColumnDialog.MAX_COLUMN_MEDIA, Toast.LENGTH_SHORT).show();
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
                mLayoutManager.setSpanCount(columnNumber);
                setSpanSize();
                mLayoutManager.requestLayout();
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

    }

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
                startActivity(new Intent(mAttachedActivity, AboutActivity.class));
                return true;

            case R.id.action_timeline_setting:
                startActivity(new Intent(mAttachedActivity, SettingsActivity.class));
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
        parentFragment.showAppBarLayout();
        parentViewPager.setSwipeLocked(false);
        if (mActionMode != null)
            mActionMode = null;
    }

    private void showChoiceDialog(SortingMode sortingMode, SortingOrder sortingOrder) {
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mAttachedActivity);

        SortDialogTimeline dialog = new SortDialogTimeline(mAttachedActivity);
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
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(actionMode);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            parentFragment.showAppBarLayout();
            parentViewPager.setSwipeLocked(false);
        }

        if (mActionMode != null) {
            mActionMode.setTitle(getAdapter().getSelectedCount() + " selected");
        }
        // hide tab bar
        if (parentFragment != null && hasCheckedItems) {
            parentFragment.hideAppBarLayout();
            parentViewPager.setSwipeLocked(true);
        }
    }

    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = mRecyclerView
                                .findViewHolderForAdapterPosition(BaseTimelineFragment.currentPosition);
                        if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.card_image));
                    }
                });
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
        DeleteDialog dialog = new DeleteDialog(mAttachedActivity);
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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = MediaHelper.getTimelineColumnPortrait(mAttachedActivity);
        } else {
            columnNumber = MediaHelper.getTimelineColumnLandscape(mAttachedActivity);
        }
        setupColumn();
        Log.v("update column", "BaseTimelineFragment");
    }

    public void setupColumn() {
        if (columnNumber != mLayoutManager.getSpanCount()) {
            mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
            mLayoutManager.setSpanCount(columnNumber);
            setSpanSize();
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
    }
}
