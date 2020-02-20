package com.tubeeapp.gallerypro.fragments.viewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tubeeapp.gallerypro.DrawerLocker;
import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.animation.ParallaxPageTransformer;
import com.tubeeapp.gallerypro.customComponent.ViewPagerFixed;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.fragments.BaseFragment;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.BaseTimelineFragment;
import com.tubeeapp.gallerypro.utils.CommonBarColor;
import com.tubeeapp.gallerypro.utils.Convert;
import com.tubeeapp.gallerypro.utils.ViewSizeUtils;
import com.tubeeapp.gallerypro.view.dialog.DeleteDialog;
import com.tubeeapp.gallerypro.view.dialog.FragmentBottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImagePagerFragment extends BaseFragment {

    public static ArrayList<MediaItem> mImageList;
    private ArrayList<Fragment> fragments;
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

    public ImagePagerFragment() {
        fragments = new ArrayList<>();
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
        if (getArguments() != null) {
            currentPosition = getArguments().getInt("currentPosition");
            isImage = getArguments().getBoolean("isImage");
        }

        ((DrawerLocker) Objects.requireNonNull(getActivity())).setDrawerEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setToolbar(true);

        mViewPager = rootView.findViewById(R.id.viewPager);
        mViewPager.setPageTransformer(true, new ParallaxPageTransformer());
        pagerAdapter = new ImagePagerAdapter(this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageMargin(10);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        bottom = rootView.findViewById(R.id.bottom);
        tvDate = rootView.findViewById(R.id.tvDate);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvDate.getLayoutParams();
        params.setMargins(0,  0, 0, (ViewSizeUtils.getNavigationBarHeight(getActivity())) * 3 / 2);
        tvDate.setLayoutParams(params);

        setCurrentImage(currentPosition);

        showBottomView(currentPosition);

        prepareSharedElementTransition();

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }

        return rootView;
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

    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the ImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.
                        Fragment currentFragment = (Fragment) mViewPager.getAdapter()
                                .instantiateItem(mViewPager, BaseTimelineFragment.currentPosition);
                        View view = currentFragment.getView();
                        if (view == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.image));
                    }
                });
    }

    private void setCurrentImage(int selectedImagePosition) {
        mViewPager.setCurrentItem(selectedImagePosition, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.v("onPageScrolled", String.valueOf(position));
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            currentPosition = position;
            showBottomView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void onDestroy() {
        if(mColorTheme.isDarkTheme()) {
            int colorBg = getActivity().getColor(R.color.colorDarkBackgroundHighlight);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getColor(R.color.colorDarkBackgroundHighlight)));
            CommonBarColor.setStatusBarColor(getActivity(), colorBg);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColorTheme.getPrimaryColor()));
            CommonBarColor.setStatusBarColor(getActivity(), mColorTheme.getPrimaryColor());
        }
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setToolbar(false);
        super.onDestroy();
    }

    private void setToolbar(boolean isMargin) {
        toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, isMargin ? ViewSizeUtils.getStatusBarHeight(getActivity()) : 0, 0, 0);
        toolbar.setLayoutParams(params);
    }

    public void blo() {
        if(!isFullScreen()) {
            Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            slideDown(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(getActivity())) * 3 / 2, 500);
            slideDown(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(getActivity())), 500);
        } else {
            Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            slideUp(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(getActivity())) * 3 / 2, 500);
            slideUp(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(getActivity())), 500);
        }
    }

    public boolean isFullScreen() {
        int flg = Objects.requireNonNull(getActivity()).getWindow().getAttributes().flags;
        boolean flag = false;
        if ((flg & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            flag = true;
        }
        return flag;
    }

    // slide the rootView from its current position to below itself
    public void slideDown(View view, int delta, int duration){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                delta); // toYDelta
        animate.setDuration(duration);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideUp(View view, int delta, int duration){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                delta,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(duration);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        if(isFullScreen()) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            slideUp(bottom, bottom.getHeight() + (ViewSizeUtils.getNavigationBarHeight(getActivity())) * 3 / 2, 0);
            slideUp(toolbar, -(toolbar.getHeight() + ViewSizeUtils.getStatusBarHeight(getActivity())), 0);
        }
        if(callback != null) {
            callback.onDelete(mDeletedItemPosition, mListDeletedItem);
        }
        super.onDestroyView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.image_pager_fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isImage) getActivity().getMenuInflater().inflate(R.menu.image_pager_menu, menu);
        else getActivity().getMenuInflater().inflate(R.menu.video_pager_menu, menu);
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
                DeleteDialog dialog = new DeleteDialog(getActivity());
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
//                ViewGroup viewGroup = rootView.findViewById(android.R.id.content);
//                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture_details, viewGroup, false);
//                TextView tvItemDate = dialogView.findViewById(R.id.tvItemDate);
//                TextView dateTakenTitle = dialogView.findViewById(R.id.dateTakenTitle);
//                TextView tvItemSize = dialogView.findViewById(R.id.tvItemSize);
//                TextView tvItemResolution = dialogView.findViewById(R.id.tvItemResolution);
//                TextView tvItemPath = dialogView.findViewById(R.id.tvItemPath);
//                TextView tvItemTitle = dialogView.findViewById(R.id.tvItemTitle);
//                Button btOK = dialogView.findViewById(R.id.buttonOk);
//                MediaItem mediaItem = mImageList.get(currentPosition);
//
//                String format = "MM-dd-yyyy HH:mm:ss";
//                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
//
//                String dateTime;
//                if(mediaItem.getDateTaken() != null) {
//                    dateTime = formatter.format(new Date(Long.parseLong(mediaItem.getDateTaken())));
//                } else {
//                    dateTakenTitle.setText("Date modified");
//                    dateTime = formatter.format(new Date(Long.parseLong(mediaItem.getDateModified())  * 1000L));
//                }
//                tvItemDate.setText(dateTime);
//                tvItemSize.setText(readableFileSize(Long.valueOf(mediaItem.getSize())));
//                tvItemResolution.setText(mediaItem.getWidth() + "x" + mediaItem.getHeight());
//                tvItemPath.setText(new File(mediaItem.getPathMediaItem()).getParent());
//                tvItemTitle.setText(mediaItem.getName() + mediaItem.getPathMediaItem().substring(mediaItem.getPathMediaItem().lastIndexOf(".")));
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setView(dialogView);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                btOK.setOnClickListener((view) -> alertDialog.dismiss());
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
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setDeleteCallback(DeletedItemCallback callback) {
        this.callback = callback;
    }
}
