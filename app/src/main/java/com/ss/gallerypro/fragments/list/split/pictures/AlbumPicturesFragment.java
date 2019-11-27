package com.ss.gallerypro.fragments.list.split.pictures;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.amodebar.Toolbar_ActionMode_Photo;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.list.normal.abstraction.BaseListFragment;
import com.ss.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.ss.gallerypro.fragments.list.normal.albums.AlbumsFragment;
import com.ss.gallerypro.fragments.list.split.pictures.model.MediaRepositoryImpl;
import com.ss.gallerypro.fragments.list.split.pictures.presenter.IMediaPresenter;
import com.ss.gallerypro.fragments.list.split.pictures.presenter.MediaPresenterImpl;
import com.ss.gallerypro.fragments.list.split.pictures.view.IMediaView;
import com.ss.gallerypro.fragments.viewer.ImagePagerFragment;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.dialog.DeleteDialog;
import com.ss.gallerypro.view.GridSpacingItemDecoration;
import com.ss.gallerypro.view.SquareImageView;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static com.ss.gallerypro.data.AlbumHelper.getSizeAlbum;
import static com.ss.gallerypro.data.utils.DataUtils.readableFileSize;

public class AlbumPicturesFragment extends BaseListFragment implements IMediaView, RecycleViewClickListener, BaseListViewAdapter.CheckedItemInterface {

    private AlbumPictureViewAdapter adapter;
    private Toolbar_ActionMode_Photo toolbarActionModePhoto;
    private View rootView;
    private SparseBooleanArray selectedMediaDelete;

    private Bucket mReceiveBucket;
    private IMediaPresenter presenter;

    public AlbumPicturesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MediaPresenterImpl(this, new MediaRepositoryImpl(mAttachedActivity));

        Bundle bundle = getArguments();
        if(bundle != null) {
            mReceiveBucket = bundle.getParcelable("album");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mReceiveBucket != null) {
            Objects.requireNonNull(((AppCompatActivity) mAttachedActivity).getSupportActionBar()).setTitle(mReceiveBucket.getName());
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        // back from another fragment not call onCreate Fragment
        presenter.getMedias(mReceiveBucket);
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
        DeleteDialog dialog = new DeleteDialog(mAttachedActivity);
        dialog.setTitle("Delete");
        String s = selectedMediaDelete.size() == 1 ? "item" : "items";
        dialog.setMessage("Are you sure you want to delete " + selectedMediaDelete.size() + " " + s + "?");
        dialog.setNegativeButton("Cancel", v -> dialog.dismiss());
        dialog.setPositiveButton("Delete", v -> {
            dialog.dismiss();
            presenter.deleteMedias(mDeletedMedias);
            mActionMode.finish();
        });
        dialog.show();
    }

    protected void onListItemSelect(int position) {
        adapter.toggleSelection(position);

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModePhoto = new Toolbar_ActionMode_Photo(this, getContext(), adapter.getMediaList(), adapter);
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(mAttachedActivity)).startSupportActionMode(toolbarActionModePhoto);
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
        int NUM_COLUMNS = 4;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, Objects.requireNonNull(getContext())), true));
        adapter = new AlbumPictureViewAdapter(getContext(), getSortingMode(), getSortingOrder());
        adapter.setItemCheckedInterface(this);
        GridlayoutManagerFixed gridlayoutManagerFixed = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
        recyclerView.setLayoutManager(gridlayoutManagerFixed);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void implementRecyclerViewClickListeners() {
        adapter.setRecycleViewClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
//            PicturePreview.mImageList = adapter.getMediaList();
//            Intent intent = new Intent(getContext(), PicturePreview.class);
//            intent.putExtra("current_image_position", position);
//            startActivity(intent);

            ImagePagerFragment.mImageList = adapter.getMediaList();
            SquareImageView transitioningView = view.findViewById(R.id.ivTimelineThumbnail);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            ImagePagerFragment pagerFragment = (ImagePagerFragment) fragmentManager.findFragmentByTag("ImagePagerFragment");
            if(pagerFragment == null) {
                pagerFragment = new ImagePagerFragment();
                Bundle args = new Bundle();
                args.putInt("currentPosition", position);
                args.putBoolean("isImage", true);
                pagerFragment.setArguments(args);
                // pagerFragment.setDeleteCallback(this);
            }

            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, pagerFragment, ImagePagerFragment.class.getSimpleName())
                    .hide(AlbumPicturesFragment.this)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        onListItemSelect(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mAttachedActivity.getMenuInflater().inflate(R.menu.album_pictures_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected SortingMode getSortingMode() {
        return adapter != null ? adapter.getSortingMode() : SortingMode.DATE_TAKEN;
    }

    @Override
    protected SortingOrder getSortingOrder() {
        return adapter != null ? adapter.getSortingOrder() : SortingOrder.DESCENDING;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (getSortingMode()) {
            case NAME:
                menu.findItem(R.id.name_sort_mode_photo).setChecked(true);
                break;
            case DATE_TAKEN:
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
                adapter.changeSortingMode(SortingMode.DATE_TAKEN);
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
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_album_details, viewGroup, false);
                TextView tvAlbumName = dialogView.findViewById(R.id.tvAlbumName);
                TextView tvAlbumPath = dialogView.findViewById(R.id.tvAlbumPath);
                TextView tvAlbumSize = dialogView.findViewById(R.id.tvAlbumSize);
                TextView tvAlbumCount = dialogView.findViewById(R.id.tvCountAlbum);
                Button btOK = dialogView.findViewById(R.id.buttonOk);
                tvAlbumName.setText(mReceiveBucket.getName());
                tvAlbumPath.setText(mReceiveBucket.getPathToAlbum());
                tvAlbumSize.setText(readableFileSize(getSizeAlbum(adapter.getMediaList())));
                tvAlbumCount.setText(String.valueOf(adapter.getMediaList().size()));

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
    protected int getLayoutId() {
        return R.layout.fragment_album_pictures;
    }

    @Override
    public void change(int numbItemCheck) {
        if(toolbarActionModePhoto != null) {
            toolbarActionModePhoto.changeMenu(numbItemCheck);
        }
    }

    private void removeImage(int position) {
        adapter.removeImage(position);
    }

    @Override
    public void onGetMediaSuccess(ArrayList<MediaItem> medias) {
        adapter.setDataList(medias);
        adapter.changeSortingMode(getSortingMode());
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onDeleteMediaSuccess() {
        for(int i=selectedMediaDelete.size()-1; i>=0; i--) {
            removeImage(selectedMediaDelete.keyAt(i));
        }
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
