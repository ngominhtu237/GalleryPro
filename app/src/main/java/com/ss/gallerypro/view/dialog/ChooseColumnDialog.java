package com.ss.gallerypro.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.ramotion.fluidslider.FluidSlider;
import com.ss.gallerypro.R;
import com.ss.gallerypro.theme.ColorTheme;
import com.ss.gallerypro.theme.ui.ButtonTheme;
import com.ss.gallerypro.utils.preferences.Prefs;

import java.util.Objects;

import kotlin.Unit;

public class ChooseColumnDialog extends Dialog {

    private static final int MIN_COLUMN_MEDIA = 2;
    private static final int MAX_COLUMN_MEDIA = 6;
    private static final int MIN_COLUMN_ALBUM = 2;
    private static final int MAX_COLUMN_ALBUM = 4;

    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btOKListener = null;
    private FluidSlider mediaSlider, albumSlider;
    private ColorTheme colorTheme;
    private Activity mActivity;
    private int selectedTimelineColumn, selectedAlbumColumn;

    public ChooseColumnDialog(@NonNull Activity activity) {
        super(activity);
        mActivity = activity;
        colorTheme = new ColorTheme(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_column);
        ButtonTheme btnCancel = findViewById(R.id.btn_cancel);
        ButtonTheme btnOk = findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(btOKListener);
        btnCancel.setOnClickListener(btCancelListener);

        mediaSlider = findViewById(R.id.mediaSlider);
        albumSlider = findViewById(R.id.albumSlider);
        selectedTimelineColumn = Prefs.getTimelineColumnPortrait(mActivity);
        selectedAlbumColumn = Prefs.getAlbumColumnPort(mActivity);
        initializeSlider(mediaSlider, MIN_COLUMN_MEDIA, MAX_COLUMN_MEDIA, Prefs.getTimelineColumnPortrait(mActivity));
        initializeSlider(albumSlider, MIN_COLUMN_ALBUM, MAX_COLUMN_ALBUM, Prefs.getAlbumColumnPort(mActivity));

        mediaSlider.setPositionListener(pos -> {
            selectedTimelineColumn = (int)(MIN_COLUMN_MEDIA + ((MAX_COLUMN_MEDIA - MIN_COLUMN_MEDIA)  * pos));
            mediaSlider.setBubbleText(String.valueOf(selectedTimelineColumn));
            return Unit.INSTANCE;
        });

        albumSlider.setPositionListener(pos -> {
            selectedAlbumColumn = (int)(MIN_COLUMN_ALBUM + ((MAX_COLUMN_ALBUM - MIN_COLUMN_ALBUM)  * pos));
            albumSlider.setBubbleText(String.valueOf(selectedAlbumColumn));
            return Unit.INSTANCE;
        });

        refreshTheme();
    }

    public int getSelectedTimelineColumn() {
        return selectedTimelineColumn;
    }

    public int getSelectedAlbumColumn() {
        return selectedAlbumColumn;
    }

    private void initializeSlider(FluidSlider slider, int min, int max, int currentColumn) {
        int total = max - min;

        slider.setBeginTrackingListener(() -> {
            Log.v("slider", "BeginTracking");
            return Unit.INSTANCE;
        });

        slider.setEndTrackingListener(() -> {
            Log.v("slider", "EndTracking");
            return Unit.INSTANCE;
        });

        // pos: 0 -> 1
        // set bubbleText dua vao pos theo CT: val = min + (total * pos)
        slider.setPosition((currentColumn - min) / (float) total);
        slider.setBubbleText(String.valueOf(currentColumn));
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max));
    }

    public void setNegativeButton(String cancel, View.OnClickListener onClickListener) {
        this.btCancelListener = onClickListener;
    }

    public void setPositiveButton(String ok, View.OnClickListener onClickListener) {
        this.btOKListener = onClickListener;
    }

    private void refreshTheme() {
        if(colorTheme.isDarkTheme()) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(getContext().getColor(R.color.colorDarkPrimary));
            gd.setCornerRadius(25);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);
        } else {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(colorTheme.getBackgroundColor());
            gd.setCornerRadius(25);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);
            mediaSlider.setColorBar(colorTheme.getPrimaryColor());
            mediaSlider.setColorBubble(colorTheme.getBackgroundColor());
            mediaSlider.setColorBubbleText(colorTheme.getAccentColor());
            mediaSlider.setColorBarText(colorTheme.getBackgroundColor());
            albumSlider.setColorBar(colorTheme.getPrimaryColor());
            albumSlider.setColorBubble(colorTheme.getBackgroundColor());
            albumSlider.setColorBubbleText(colorTheme.getAccentColor());
            albumSlider.setColorBarText(colorTheme.getBackgroundColor());
        }
    }
}
