package com.ss.gallerypro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.adapter.PhotoSplitViewAdapter;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaHelper;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.provider.CPHelper;
import com.ss.gallerypro.data.sort.PhotoComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.RecyclerClick_Listener;
import com.ss.gallerypro.event.RecyclerTouchListener;
import com.ss.gallerypro.event.amodebar.Toolbar_ActionMode_Photo;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static com.ss.gallerypro.data.AlbumHelper.getSizeAlbum;
import static com.ss.gallerypro.data.utils.DataUtils.readableFileSize;

public class PhotoSplitView extends AppCompatActivity {

    private static int NUM_COLUMNS = 4;
    private ArrayList<MediaItem> mImageList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PhotoSplitViewAdapter photoSplitViewAdapter;
    private Bucket mReceiveBucket;

    // Properties dialog album details
    private TextView tvAlbumName, tvAlbumPath, tvAlbumSize, tvAlbumCount;
    private Button btOK;

    private ActionMode mActionMode;
    private Toolbar_ActionMode_Photo toolbarActionModePhoto;
    private DeleteMediaItemTask deleteMediaItemTask;
    private boolean isDeleted; // if delete back to album fragment -> need refresh

    private SparseBooleanArray selectedItemDelete;

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public PhotoSplitView() {
        mImageList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_split_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mReceiveBucket = intent.getParcelableExtra("album");
        getSupportActionBar().setTitle(mReceiveBucket.getName());

        setSupportActionBar(toolbar);
        new GetAlbumImages().execute();

        initAlbumRecycleView();
        implementRecyclerViewClickListeners();
    }

    private void initAlbumRecycleView() {
        recyclerView = findViewById(R.id.rvAlbumImage);
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, PhotoSplitView.this), true));
        photoSplitViewAdapter = new PhotoSplitViewAdapter(this, sortingMode(), sortingOrder(), mImageList);
        GridlayoutManagerFixed gridlayoutManagerFixed = new GridlayoutManagerFixed(this, NUM_COLUMNS);
        recyclerView.setLayoutManager(gridlayoutManagerFixed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(photoSplitViewAdapter);
    }

    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, final int position) {
                if (mActionMode != null) {
                    onListItemSelect(position);
                } else {
                    Intent intent = new Intent(PhotoSplitView.this, PicturePreview.class);
                    intent.putExtra("current_image_position", position);
                    intent.putExtra("album_path", mReceiveBucket.getPathToAlbum());
                    intent.putExtra("list_image", mImageList);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                onListItemSelect(position);
            }

        }));
    }

    private void onListItemSelect(int position) {
        photoSplitViewAdapter.toggleSelection(position);

        boolean hasCheckedItems = photoSplitViewAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModePhoto = new Toolbar_ActionMode_Photo(this, mImageList, photoSplitViewAdapter);
            mActionMode = startSupportActionMode(toolbarActionModePhoto);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(photoSplitViewAdapter
                    .getSelectedCount()) + " selected");
        }

    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    public SortingMode sortingMode() {
        return photoSplitViewAdapter != null ? photoSplitViewAdapter.sortingMode() : SortingMode.DATE;
    }

    public SortingOrder sortingOrder() {
        return photoSplitViewAdapter != null ? photoSplitViewAdapter.sortingOrder() : SortingOrder.DESCENDING;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (sortingMode()) {
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
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.name_sort_mode_photo:
                photoSplitViewAdapter.changeSortingMode(SortingMode.NAME);
                item.setChecked(true);
                return true;

            case R.id.size_sort_mode_photo:
                photoSplitViewAdapter.changeSortingMode(SortingMode.SIZE);
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_mode_photo:
                photoSplitViewAdapter.changeSortingMode(SortingMode.DATE);
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_order_photo:
                item.setChecked(!item.isChecked());
                SortingOrder sortingOrder = SortingOrder.fromValue(item.isChecked());
                photoSplitViewAdapter.changeSortingOrder(sortingOrder);
                return true;

            case R.id.album_details:
                ViewGroup viewGroup = findViewById(android.R.id.content);
                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_album_details, viewGroup, false);
                tvAlbumName = dialogView.findViewById(R.id.tvAlbumName);
                tvAlbumPath = dialogView.findViewById(R.id.tvAlbumPath);
                tvAlbumSize = dialogView.findViewById(R.id.tvAlbumSize);
                tvAlbumCount = dialogView.findViewById(R.id.tvAlbumCount);
                btOK = dialogView.findViewById(R.id.buttonOk);
                tvAlbumName.setText(mReceiveBucket.getName());
                tvAlbumPath.setText(mReceiveBucket.getPathToAlbum());
                tvAlbumSize.setText(readableFileSize(getSizeAlbum(mImageList)));
                tvAlbumCount.setText(String.valueOf(mImageList.size()));

                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_split_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isEdited", isDeleted);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    public void deleteImages() {
        selectedItemDelete = photoSplitViewAdapter.getSelectedIds();
        ArrayList<MediaItem> mDeletedImages = new ArrayList<>();
        // Loop all selected ids
        for (int i = (selectedItemDelete.size() - 1); i >= 0; i--) {
            if (selectedItemDelete.valueAt(i)) {
                //If current id is selected remove the item via key
                mDeletedImages.add(mImageList.get(selectedItemDelete.keyAt(i)));
            }
        }
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.show();
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        String itemString = (selectedItemDelete.size()>1) ? " items" : " item";
        tvTitle.setText("Are you sure you want to delete "+selectedItemDelete.size()+itemString+" ?");
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        btnDelete.setOnClickListener(view -> {
            dialog.dismiss();
            deleteMediaItemTask = new DeleteMediaItemTask();
            deleteMediaItemTask.execute(mDeletedImages);
            mActionMode.finish();
        });
    }

    private class GetAlbumImages extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mImageList = CPHelper.getMedias(PhotoSplitView.this, mReceiveBucket.getBucketId(), mReceiveBucket.getName());
            Collections.sort(mImageList, PhotoComparators.getComparator(SortingMode.DATE, SortingOrder.DESCENDING));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            photoSplitViewAdapter.updateData(mImageList);
            photoSplitViewAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

    private class DeleteMediaItemTask extends AsyncTask<ArrayList<MediaItem>, Integer, Integer> {

        Dialog dialog;
        ProgressBar progressBar;
        ArrayList<MediaItem> mDeletedItems;

        @Override
        protected void onPreExecute() {
            setLockScreenOrientation(true);
            mDeletedItems = new ArrayList<>();
            dialog = new Dialog(PhotoSplitView.this);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            progressBar = dialog.findViewById(R.id.progressBar1);
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_blue_grey_700)));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(ArrayList<MediaItem>... items) {
            mDeletedItems = items[0];
            for(int i=0; i<mDeletedItems.size(); i++) {
                if(isCancelled()) break;
                else {
                    MediaHelper.deleteMedia(PhotoSplitView.this, mDeletedItems.get(i).getPathMediaItem());
                    mImageList.remove(mDeletedItems.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            //photoSplitViewAdapter.notifyDataSetChanged();
            for(int i=0; i<selectedItemDelete.size(); i++) {
                removeImage(selectedItemDelete.keyAt(i));
            }
            dialog.dismiss();
            setLockScreenOrientation(false);
            isDeleted = true;
        }
    }

    private void removeImage(int position) {
        photoSplitViewAdapter.removeImage(position);
    }

    protected void setLockScreenOrientation(boolean lock) {
        if (Build.VERSION.SDK_INT >= 18) {
            setRequestedOrientation(lock? ActivityInfo.SCREEN_ORIENTATION_LOCKED:ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return;
        }

        if (lock) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break; // value 1
                case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); break; // value 9
                case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break; // value 0
                case 3: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break; // value 8
            }
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // value 10
    }
}
