package com.tubeeapp.gallerypro.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.theme.ColorTheme;
import com.tubeeapp.gallerypro.theme.ui.TextViewPrimary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.tubeeapp.gallerypro.data.utils.DataUtils.readableFileSize;

public class FragmentBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private ColorTheme colorTheme;
    private MediaItem mediaItem;
    private TextViewPrimary nameContent, pathContent, sizeContent, resolutionContent, dateTakenContent, dateModifiedContent;
    private ImageView nameIcon, pathIcon, sizeIcon, resolutionIcon, dateTakenIcon, dateModifiedIcon;
    private View view;
    public void setMediaItem(MediaItem item) {
        this.mediaItem = item;
    }

    private void refreshTheme() {
        if(colorTheme.isDarkTheme()) {
            setColorBg(R.color.colorDarkPrimary);
            // icon
            nameIcon.setImageResource(R.mipmap.icon_dialog_file_white);
            pathIcon.setImageResource(R.mipmap.icon_dialog_path_white);
            sizeIcon.setImageResource(R.mipmap.icon_dialog_memory_white);
            resolutionIcon.setImageResource(R.mipmap.icon_dialog_resolution_white);
            dateTakenIcon.setImageResource(R.mipmap.icon_dialog_date_white);
            dateModifiedIcon.setImageResource(R.mipmap.icon_dialog_date_white);
        } else {
            //
            nameIcon.setImageResource(R.mipmap.icon_dialog_file_black);
            pathIcon.setImageResource(R.mipmap.icon_dialog_path_black);
            sizeIcon.setImageResource(R.mipmap.icon_dialog_memory_black);
            resolutionIcon.setImageResource(R.mipmap.icon_dialog_resolution_black);
            dateTakenIcon.setImageResource(R.mipmap.icon_dialog_date_black);
            dateModifiedIcon.setImageResource(R.mipmap.icon_dialog_date_black);
        }
    }

    private void setColorBg(int colorRs) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(getContext().getColor(colorRs));
        gd.setCornerRadii(new float[]{40, 40, 40, 40, 0, 0, 0, 0});
        ((View) view.getParent()).setBackgroundDrawable(gd);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = View.inflate(getContext(), R.layout.fragment_bottom_sheet_details, null);
        dialog.setContentView(view);
        findView(view);
        colorTheme = new ColorTheme(getActivity());
        refreshTheme();
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        mBehavior.setPeekHeight(height * 6 / 10);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    hideView();
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    showView();
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        nameContent.setText(mediaItem.getName());
        pathContent.setText(mediaItem.getPathMediaItem());
        sizeContent.setText(readableFileSize(Long.valueOf(mediaItem.getSize())));
        resolutionContent.setText(mediaItem.getWidth() + " x " + mediaItem.getHeight());

        String format = "MM-dd-yyyy HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        String dateTaken, dateModified;
        if (mediaItem.getDateTaken() != null) {
            dateTaken = formatter.format(new Date(Long.parseLong(mediaItem.getDateTaken())));
        } else {
            dateTaken = "null";
        }
        if (mediaItem.getDateModified() != null) {
            dateModified = formatter.format(new Date(Long.parseLong(mediaItem.getDateModified()) * 1000L));
        } else {
            dateModified = "null";
        }
        dateTakenContent.setText(dateTaken);
        dateModifiedContent.setText(dateModified);
        return dialog;
    }

    private void hideView() {
    }

    private void showView() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void findView(View v) {
        nameContent = v.findViewById(R.id.bottom_dialog_name_content);
        pathContent = v.findViewById(R.id.bottom_dialog_path_content);
        sizeContent = v.findViewById(R.id.bottom_dialog_size_content);
        resolutionContent = v.findViewById(R.id.bottom_dialog_resolution_content);
        dateTakenContent = v.findViewById(R.id.bottom_dialog_date_taken_content);
        dateModifiedContent = v.findViewById(R.id.bottom_dialog_date_modified_content);

        nameIcon = v.findViewById(R.id.bottom_dialog_name_icon);
        pathIcon = v.findViewById(R.id.bottom_dialog_path_icon);
        sizeIcon = v.findViewById(R.id.bottom_dialog_size_icon);
        resolutionIcon = v.findViewById(R.id.bottom_dialog_resolution_icon);
        dateTakenIcon = v.findViewById(R.id.bottom_dialog_date_taken_icon);
        dateModifiedIcon = v.findViewById(R.id.bottom_dialog_date_modified_icon);
    }
}
