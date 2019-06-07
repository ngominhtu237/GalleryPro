package com.ss.gallerypro.fragments.list.section.abstraction;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.ss.gallerypro.utils.Convert;
import com.ss.gallerypro.view.ItemOffsetDecoration;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class BaseTimelineFragment extends BaseFragment {
    @BindView(R.id.timelineRecycleView)
    protected RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.activity_main_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    protected Activity mAttachedActivity;

    protected GridLayoutManager mLayoutManager;
    private ActionMode mActionMode;
    protected int NUM_COLUMN;

    protected BaseTimelineAdapter adapter;
    protected ITimelinePresenter presenter;
    protected ITimelineRepository model;

    public BaseTimelineFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAttachedActivity = getActivity();
        model = createModel();
        presenter = createPresenter(model);
        super.onCreate(savedInstanceState);
    }

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
        return view;
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
        if(isEnable) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setDistanceToTriggerSync(0);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
        }
    }

    public void setNullToActionMode() {
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

    protected abstract SortingMode getSortModeFromPref();

    protected abstract SortingOrder getSortOrderFromPref();

    protected abstract void setSortModeToPref(SortingMode sortingMode);

    protected abstract void setSortOrderToPref(SortingOrder sortingOrder);
}