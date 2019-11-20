package com.ss.gallerypro.fragments.list.section.abstraction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.provider.ContentProviderObserver;
import com.ss.gallerypro.data.provider.ProviderChangeListener;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.fragments.ICheckedItem;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.ViewHolderListener;
import com.ss.gallerypro.fragments.home.HomeFragment;
import com.ss.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;
import com.ss.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.ss.gallerypro.fragments.viewer.DeletedItemCallback;
import com.ss.gallerypro.utils.Convert;
import com.ss.gallerypro.view.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class BaseTimelineFragment extends BaseFragment implements RecycleViewClickListener, ICheckedItem, ViewHolderListener, DeletedItemCallback, ProviderChangeListener {
    @BindView(R.id.timelineRecycleView)
    protected RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.activity_main_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    protected Activity mAttachedActivity;

    protected GridLayoutManager mLayoutManager;
    protected ActionMode mActionMode;
    protected int NUM_COLUMN;

    private BaseTimelineAdapter adapter;
    protected ITimelinePresenter presenter;
    protected ITimelineRepository model;

    protected BaseActionMode actionMode;

    private ArrayList<Integer> mListDeletedPosition = new ArrayList<>();

    private HomeFragment parentFragment;

    public static int currentPosition;

    private ContentProviderObserver mProviderObserver;

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
        super.onCreate(savedInstanceState);
    }

    protected abstract BaseActionMode createActionMode();

    protected abstract ITimelineRepository createModel();

    protected abstract ITimelinePresenter createPresenter(ITimelineRepository model);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(true);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }
        initRecycleView();

        prepareTransitions();
        postponeEnterTransition();
        return view;
    }

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

    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        NUM_COLUMN = getColumnRecycleView();
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(mAttachedActivity, R.dimen.timeline_item_spacing);
        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new GridlayoutManagerFixed(getContext(), NUM_COLUMN);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        adapter = createAdapter();
        adapter.setRecycleViewClickListener(this);
        adapter.setCheckedItemListener(this);
        adapter.setViewHolderListener(this);
        mRecyclerView.setAdapter(adapter);
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

    protected abstract int getContentColumn();

    protected abstract int getHeaderColumn();

    protected abstract BaseTimelineAdapter createAdapter();

    protected abstract int getColumnRecycleView();

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
                Toast.makeText(mAttachedActivity, "Open About Activity", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_timeline_setting:
                Toast.makeText(mAttachedActivity, "Open Setting Activity", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_timeline_delete:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected final SwipeRefreshLayout.OnRefreshListener listener = this::createSwipeEvent;

    protected abstract void createSwipeEvent();

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setEnableSwipeRefresh(boolean isEnable) {
        if(mSwipeRefreshLayout != null) {
            if (isEnable) {
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setDistanceToTriggerSync(0);
            } else {
                mSwipeRefreshLayout.setEnabled(false);
                mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
            }
        }
    }

    public void setNullToActionMode() {
        parentFragment.showAppBarLayout();
        if (mActionMode != null)
            mActionMode = null;
    }

    private void showChoiceDialog(SortingMode sortingMode, SortingOrder sortingOrder) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mAttachedActivity);
        View view = getLayoutInflater().inflate(R.layout.dialog_sort, null);
        Button btCancel = view.findViewById(R.id.cancel);
        Button btOk = view.findViewById(R.id.ok);

        // create two sort radio group
        RadioButton[] rb, rb1;
        RadioGroup rgSortingMode = view.findViewById(R.id.rgSortingMode);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        rgSortingMode.setOrientation(RadioGroup.VERTICAL);
        rb = new RadioButton[SortingMode.getNames().length];
        for(int i=0; i<SortingMode.getNames().length; i++){
            rb[i] = new RadioButton(getContext());
            rb[i].setId(i);
            rb[i].setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            rb[i].setPadding(20, 0, 0, 0);
            rb[i].setLayoutParams(params);
            rb[i].setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            rb[i].setText(Convert.formatEnumStringDialog(SortingMode.getNames()[i]));
            if(sortingMode == SortingMode.fromValue(i)) rb[i].setChecked(true);
            rgSortingMode.addView(rb[i]);
        }
        RadioGroup rgSortingOrder = view.findViewById(R.id.rgSortingOrder);
        rgSortingOrder.setOrientation(RadioGroup.VERTICAL);
        rb1 = new RadioButton[SortingOrder.getNames().length];
        for(int i=0; i<SortingOrder.getNames().length; i++){
            rb1[i] = new RadioButton(getContext());
            rb1[i].setId(i);
            rb1[i].setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            rb1[i].setPadding(20, 0, 0, 0);
            rb1[i].setLayoutParams(params);
            rb1[i].setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            rb1[i].setText(Convert.formatEnumStringDialog(SortingOrder.getNames()[i]));
            if(sortingOrder == SortingOrder.fromValue(i)) rb1[i].setChecked(true);
            rgSortingOrder.addView(rb1[i]);
        }

        mBuilder.setView(view);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        btCancel.setOnClickListener(v -> dialog.dismiss());
        btOk.setOnClickListener(v -> {
            int i1 = rgSortingMode.getCheckedRadioButtonId();
            int i2 = rgSortingOrder.getCheckedRadioButtonId();
            adapter.changeSorting(SortingMode.fromValue(i1), SortingOrder.fromValue(i2));
            setSortModeToPref(SortingMode.fromValue(i1));
            setSortOrderToPref(SortingOrder.fromValue(i2));
            dialog.dismiss();
        });
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
        }

        if (mActionMode != null) {
            mActionMode.setTitle(getAdapter().getSelectedCount() + " selected");
        }
        // hide tab bar
        if (parentFragment != null && hasCheckedItems) {
            parentFragment.hideAppBarLayout();
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
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.dialog_custom);
        dialog.show();
        Button btCancel = dialog.findViewById(R.id.btn_cancel);
        Button btDelete = dialog.findViewById(R.id.btn_delete);
        btCancel.setOnClickListener(view -> dialog.dismiss());
        btDelete.setOnClickListener(view -> {
            dialog.dismiss();
            presenter.deleteMedias(mDeleteItems);
            mActionMode.finish();
        });
    }
}
