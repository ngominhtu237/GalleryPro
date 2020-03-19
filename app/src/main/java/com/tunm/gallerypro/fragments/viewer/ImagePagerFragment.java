package com.tunm.gallerypro.fragments.viewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.animation.ParallaxPageTransformer;
import com.tunm.gallerypro.customComponent.ViewPagerFixed;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.fragments.BaseFragment;
import com.tunm.gallerypro.utils.CommonBarColor;
import com.tunm.gallerypro.utils.Convert;
import com.tunm.gallerypro.utils.ViewSizeUtils;
import com.tunm.gallerypro.view.dialog.DeleteDialog;
import com.tunm.gallerypro.view.dialog.FragmentBottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.tunm.gallerypro.animation.ViewAnimation.slideDown;
import static com.tunm.gallerypro.animation.ViewAnimation.slideUp;
import static com.tunm.gallerypro.theme.SystemUI.hideNavigationBar;
import static com.tunm.gallerypro.theme.SystemUI.showNavigationBar;

public class ImagePagerFragment extends BaseFragment {

    private static final String TAG = "ImagePagerFragment";
    public static ArrayList<MediaItem> mImageList;
    private ViewPagerFixed mViewPager;
    private ImagePagerAdapter pagerAdapter;
    private View rootView;

    private Toolbar toolbar;
    private LinearLayout bottom;
    private TextView tvDate;
    int currentPosition;

    private boolean isImage;

    private ArrayList<MediaItem> mListDeletedItem = new ArrayList<>();
    private ArrayList<Integer> mDeletedItemPosition = new ArrayList<>();
    private DeletedItemCallback callback;

    private boolean isFullScreen;

    public ImagePagerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        toolbar = Objects.requireNonNull(mActivity).findViewById(R.id.toolbar);
        if (getArguments() != null) {
            currentPosition = getArguments().getInt("currentPosition");
            isImage = getArguments().getBoolean("isImage");
        }


        ((DrawerLocker) Objects.requireNonNull(mActivity)).setDrawerEnabled(false);
        toolbar.setBackgroundColor(mActivity.getColor(R.color.viewer_osd_background_color));
        toolbar.setTitle(null);

        mViewPager = rootView.findViewById(R.id.viewPager);
        mViewPager.setPageTransformer(true, new ParallaxPageTransformer());
        pagerAdapter = new ImagePagerAdapter(this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageMargin(10);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        bottom = rootView.findViewById(R.id.bottom);
        tvDate = rootView.findViewById(R.id.tvDate);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvDate.getLayoutParams();
        params.setMargins(0,  0, 0, (ViewSizeUtils.getNavigationBarHeight(mActivity)) * 3 / 2);
        tvDate.setLayoutParams(params);

        setCurrentImage(currentPosition);
        showBottomView(currentPosition);

        refreshTheme();
        showBars();
        return rootView;
    }

    @Override
    public void onResume() {
        refreshTheme();
        super.onResume();
    }

    private void showBottomView(int currentPos) {
        TextView tvName = rootView.findViewById(R.id.tvName);
        tvDate = rootView.findViewById(R.id.tvDate);
        tvName.setText(mImageList.get(currentPos).getName());
        if(mImageList.get(currentPos).getDateTaken() != null) {
            tvDate.setText(Convert.Epoch2DateString(Long.parseLong(mImageList.get(currentPos).getDateTaken())));
        }
        String dateTime;
        if(mImageList.get(currentPos).getDateTaken() != null) {
            dateTime = "Create: " + Convert.Epoch2DateString(Long.parseLong(mImageList.get(currentPos).getDateTaken()));
        } else {
            dateTime = "Modified: " + Convert.Epoch2DateString(Long.parseLong(mImageList.get(currentPos).getDateModified()) * 1000L);
        }
        tvDate.setText(dateTime);
    }

    private void setCurrentImage(int selectedImagePosition) {
        mViewPager.setCurrentItem(selectedImagePosition, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.v("ViewPager: onPageScrolled", String.valueOf(position));
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            currentPosition = position;
            showBottomView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.v("ViewPager: onPageScrolled", String.valueOf(state));
        }
    };

    @Override
    public void onDestroy() {
        if(mColorTheme.isDarkTheme()) {
            int colorBg = Objects.requireNonNull(mActivity).getColor(R.color.colorDarkBackgroundHighlight);
            toolbar.setBackgroundColor(mActivity.getColor(R.color.colorDarkBackgroundHighlight));
            CommonBarColor.setStatusBarColor(mActivity, colorBg);
            CommonBarColor.setNavigationBarColor(mActivity, colorBg);
        } else {
            toolbar.setBackgroundColor(mColorTheme.getPrimaryColor());
            CommonBarColor.setStatusBarColor(mActivity, mColorTheme.getPrimaryColor());
            CommonBarColor.setNavigationBarColor(mActivity, mColorTheme.getPrimaryColor());
        }
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    public void blo() {
        if(!isFullScreen) {
            slideDown(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(mActivity)) * 3 / 2, 500);
            slideDown(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(mActivity)), 500);
            hideBars();
        } else {
            slideUp(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(mActivity)) * 3 / 2, 500);
            slideUp(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(mActivity)), 500);
            showBars();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        toolbar.setTitle(R.string.app_name);
        slideUp(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(mActivity)) * 3 / 2, 0);
        slideUp(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(mActivity)), 0);
        if(callback != null) {
            callback.onDelete(mDeletedItemPosition, mListDeletedItem);
        }
        showNavigationBar(mActivity, getView());
        Log.v(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.image_pager_fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isImage) mActivity.getMenuInflater().inflate(R.menu.image_pager_menu, menu);
        else mActivity.getMenuInflater().inflate(R.menu.video_pager_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.general_timeline_items, false);
        menu.setGroupVisible(R.id.edit_mode_items, false);
        super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                DeleteDialog dialog = new DeleteDialog(mActivity);
                dialog.setTitle("Delete");
                dialog.setMessage("Are you sure you want to delete this item?");
                dialog.setNegativeButton("Cancel", v -> dialog.dismiss());
                dialog.setPositiveButton("Delete", v -> {
                    dialog.dismiss();
                    deleteCurrentItem();
                });
                dialog.show();
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "From Quick View...");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, mImageList.get(currentPosition).getName());
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mImageList.get(currentPosition).getPathMediaItem())));
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;

            case R.id.action_details:
                FragmentBottomSheetDialog fragment = new FragmentBottomSheetDialog();
                if (getFragmentManager() != null) {
                    fragment.setMediaItem(mImageList.get(currentPosition));
                    fragment.show(getFragmentManager(), fragment.getTag());
                }
                return true;
            case R.id.action_set_picture_as:
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.fromFile(new File(mImageList.get(currentPosition).getPathMediaItem())),"image/*");
                intent.putExtra("mimeType","image/*");
                startActivity(Intent.createChooser(intent,"Set Image"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCurrentItem() {
        mListDeletedItem.add(mImageList.get(currentPosition));
        mDeletedItemPosition.add(currentPosition);
/*        mImageList.remove(currentPosition);
        pagerAdapter.notifyDataSetChanged();*/
        mActivity.getSupportFragmentManager().popBackStack();
    }

    public void setDeleteCallback(DeletedItemCallback callback) {
        this.callback = callback;
    }

    private void refreshTheme() {
        CommonBarColor.setStatusBarColor(mActivity, Objects.requireNonNull(mActivity).getColor(R.color.viewer_osd_background_color));
        CommonBarColor.setNavigationBarColor(mActivity, Objects.requireNonNull(mActivity).getColor(R.color.viewer_osd_background_color));
    }

    private void hideBars() {
        hideNavigationBar(mActivity, getView());
        isFullScreen = true;
    }

    private void showBars() {
        showNavigationBar(mActivity, getView());
        isFullScreen = false;
    }
}
