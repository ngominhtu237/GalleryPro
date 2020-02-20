package com.tubeeapp.gallerypro.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.ramotion.fluidslider.FluidSlider;
import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.theme.ui.BaseDialogTheme;
import com.tubeeapp.gallerypro.theme.ui.ButtonTheme;
import com.tubeeapp.gallerypro.utils.preferences.Prefs;

import kotlin.Unit;

public class ChooseColumnDialog extends BaseDialogTheme {

    public static final int MIN_COLUMN_MEDIA = 2;
    public static final int MAX_COLUMN_MEDIA = 6;
    public static final int MIN_COLUMN_ALBUM = 2;
    public static final int MAX_COLUMN_ALBUM = 4;

    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btOKListener = null;
    private FluidSlider mediaSlider, albumSlider;
    private int selectedTimelineColumn, selectedAlbumColumn;

    public ChooseColumnDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButtonTheme btnCancel = findViewById(R.id.btn_cancel);
        ButtonTheme btnOk = findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(btOKListener);
        btnCancel.setOnClickListener(btCancelListener);

        mediaSlider = findViewById(R.id.mediaSlider);
        albumSlider = findViewById(R.id.albumSlider);
        selectedTimelineColumn = Prefs.getTimelineColumnPortrait((Activity) mContext);
        selectedAlbumColumn = Prefs.getAlbumColumnPort((Activity) mContext);
        initializeSlider(mediaSlider, MIN_COLUMN_MEDIA, MAX_COLUMN_MEDIA, Prefs.getTimelineColumnPortrait((Activity) mContext));
        initializeSlider(albumSlider, MIN_COLUMN_ALBUM, MAX_COLUMN_ALBUM, Prefs.getAlbumColumnPort((Activity) mContext));

        mediaSlider.setPositionListener(pos -> {
            selectedTimelineColumn = (int) (MIN_COLUMN_MEDIA + ((MAX_COLUMN_MEDIA - MIN_COLUMN_MEDIA) * pos));
            mediaSlider.setBubbleText(String.valueOf(selectedTimelineColumn));
            return Unit.INSTANCE;
        });

        albumSlider.setPositionListener(pos -> {
            selectedAlbumColumn = (int) (MIN_COLUMN_ALBUM + ((MAX_COLUMN_ALBUM - MIN_COLUMN_ALBUM) * pos));
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

    public void refreshTheme() {
        super.refreshTheme();
        if (!mColorTheme.isDarkTheme()) {
            mediaSlider.setColorBar(mColorTheme.getPrimaryColor());
            mediaSlider.setColorBubble(mColorTheme.getBackgroundColor());
            mediaSlider.setColorBubbleText(mColorTheme.getAccentColor());
            mediaSlider.setColorBarText(mColorTheme.getBackgroundColor());
            albumSlider.setColorBar(mColorTheme.getPrimaryColor());
            albumSlider.setColorBubble(mColorTheme.getBackgroundColor());
            albumSlider.setColorBubbleText(mColorTheme.getAccentColor());
            albumSlider.setColorBarText(mColorTheme.getBackgroundColor());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_choose_column;
    }

    @Override
    protected float getWidth() {
        return 0.85f;
    }
}
