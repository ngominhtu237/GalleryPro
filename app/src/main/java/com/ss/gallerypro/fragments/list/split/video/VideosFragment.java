package com.ss.gallerypro.fragments.list.split.video;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.amodebar.Toolbar_ActionMode_Video;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.list.abstraction.BaseListFragment;
import com.ss.gallerypro.fragments.list.abstraction.BaseListViewAdapter;
import com.ss.gallerypro.fragments.list.split.video.model.VideoRepositoryImpl;
import com.ss.gallerypro.fragments.list.split.video.presenter.IVideoPresenter;
import com.ss.gallerypro.fragments.list.split.video.presenter.VideoPresenterImpl;
import com.ss.gallerypro.fragments.list.split.video.view.IVideoView;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static com.ss.gallerypro.data.MediaHelper.getSizeMedia;
import static com.ss.gallerypro.data.utils.DataUtils.readableFileSize;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFragment extends BaseListFragment implements IVideoView, RecycleViewClickListener, BaseListViewAdapter.CheckedItemInterface {

    private VideosAdapter adapter;
    private Toolbar_ActionMode_Video toolbarActionModeVideo;
    private View rootView;
    private SparseBooleanArray selectedMediaDelete;

    private IVideoPresenter presenter;

    public VideosFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mAttachedActivity != null) {
            ((AppCompatActivity) mAttachedActivity).getSupportActionBar().setTitle("Video");
        }
        presenter = new VideoPresenterImpl(this, new VideoRepositoryImpl(mAttachedActivity));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        presenter.getVideoList();
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(true);
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    public void deleteMedias() {
        selectedMediaDelete = adapter.getSelectedIds();
        ArrayList<MediaItem> mDeletedMedias = new ArrayList<>();
        // Loop all selected ids
        for (int i = (selectedMediaDelete.size() - 1); i >= 0; i--) {
            if (selectedMediaDelete.valueAt(i)) {
                //If current id is selected remove the item via key
                mDeletedMedias.add(adapter.getMediaList().get(selectedMediaDelete.keyAt(i)));
            }
        }
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.dialog_custom);
        dialog.show();
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        String itemString = (selectedMediaDelete.size()>1) ? " items" : " item";
        tvTitle.setText("Are you sure you want to delete " + selectedMediaDelete.size() + itemString + " ?");
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        btnDelete.setOnClickListener(view -> {
            dialog.dismiss();
            presenter.deleteMedias(mDeletedMedias);
            mActionMode.finish();
        });
    }

    protected void onListItemSelect(int position) {
        adapter.toggleSelection(position);

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModeVideo = new Toolbar_ActionMode_Video(this, getContext(), adapter.getMediaList(), adapter);
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(mAttachedActivity)).startSupportActionMode(toolbarActionModeVideo);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");
        }
    }

    @Override
    protected void initRecycleView(View v) {
        super.initRecycleView(v);
        recyclerView.setItemAnimator(new LandingAnimator());
        int NUM_COLUMNS = 3;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, Objects.requireNonNull(getContext())), true));
        adapter = new VideosAdapter(getContext(), getSortingMode(), getSortingOrder());
        adapter.setItemCheckedInterface(this);
        GridlayoutManagerFixed gridlayoutManagerFixed = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
        recyclerView.setLayoutManager(gridlayoutManagerFixed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mAttachedActivity.getMenuInflater().inflate(R.menu.album_pictures_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (getSortingMode()) {
            case NAME:
                menu.findItem(R.id.name_sort_mode_photo).setChecked(true);
                break;
            case DATE:
                menu.findItem(R.id.date_taken_sort_mode_photo).setChecked(true);
                break;
            case SIZE:
            default:
                menu.findItem(R.id.size_sort_mode_photo).setChecked(true);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.name_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.NAME);
                item.setChecked(true);
                return true;

            case R.id.size_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.SIZE);
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.DATE);
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_order_photo:
                item.setChecked(!item.isChecked());
                SortingOrder sortingOrder = SortingOrder.fromValue(item.isChecked());
                adapter.changeSortingOrder(sortingOrder);
                return true;

            case R.id.album_details:
                ViewGroup viewGroup = rootView.findViewById(android.R.id.content);
                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_video_details, viewGroup, false);
                TextView tvSize = dialogView.findViewById(R.id.tvSize);
                TextView tvVideoCount = dialogView.findViewById(R.id.tvCountVideo);
                Button btOK = dialogView.findViewById(R.id.buttonOk);
                tvSize.setText(readableFileSize(getSizeMedia(adapter.getMediaList())));
                tvVideoCount.setText(String.valueOf(adapter.getMediaList().size()));

                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                btOK.setOnClickListener((view) -> alertDialog.dismiss());

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void implementRecyclerViewClickListeners() {
        adapter.setRecycleViewClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album_pictures;
    }

    @Override
    protected SortingMode getSortingMode() {
        return adapter != null ? adapter.getSortingMode() : SortingMode.DATE;
    }

    @Override
    protected SortingOrder getSortingOrder() {
        return adapter != null ? adapter.getSortingOrder() : SortingOrder.DESCENDING;
    }

    @Override
    public void change(int numbItemCheck) {
        if(toolbarActionModeVideo != null) {
            toolbarActionModeVideo.changeMenu(numbItemCheck);
        }
    }

    @Override
    public void onGetVideoSuccess(ArrayList<MediaItem> mediaItems) {
        adapter.setDataList(mediaItems);
        adapter.changeSortingMode(getSortingMode());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteVideoSuccess() {
        for(int i=selectedMediaDelete.size()-1; i>=0; i--) {
            removeVideo(selectedMediaDelete.keyAt(i));
        }
    }

    private void removeVideo(int position) {
        adapter.removeVideo(position);
    }
}
