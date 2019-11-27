package com.ss.gallerypro.view.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.theme.ColorTheme;
import com.ss.gallerypro.theme.ui.ButtonTheme;
import com.ss.gallerypro.theme.ui.RadioButtonTheme;
import com.ss.gallerypro.utils.Convert;

import java.util.Objects;

public class SortDialogTimeline extends AlertDialog {

    private String btCancelText;
    private String btSortText;
    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btSortListener = null;
    private SortingMode sortingMode;
    private SortingOrder sortingOrder;
    private ColorTheme colorTheme;
    private ButtonTheme btnCancel, btnSort;
    RadioButtonTheme[] rb, rb1;
    RadioGroup rgSortingMode, rgSortingOrder;

    public SortDialogTimeline(@NonNull Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sort_layout);
        btnCancel = findViewById(R.id.cancel);
        btnSort = findViewById(R.id.sort);
        btnCancel.setText(btCancelText);
        btnSort.setText(btSortText);

        rgSortingMode = findViewById(R.id.rgSortingMode);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        if (rgSortingMode != null) {
            rgSortingMode.setOrientation(RadioGroup.VERTICAL);
        }
        rb = new RadioButtonTheme[SortingMode.getNames().length];
        for(int i=0; i<SortingMode.getNames().length; i++){
            rb[i] = new RadioButtonTheme(getContext());
            rb[i].setId(i);
            rb[i].setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            rb[i].setPadding(20, 0, 0, 0);
            rb[i].setLayoutParams(params);
            rb[i].setText(Convert.formatEnumStringDialog(SortingMode.getNames()[i]));
            if(sortingMode == SortingMode.fromValue(i)) rb[i].setChecked(true);
            rgSortingMode.addView(rb[i]);
        }
        rgSortingOrder = findViewById(R.id.rgSortingOrder);
        if (rgSortingOrder != null) {
            rgSortingOrder.setOrientation(RadioGroup.VERTICAL);
        }
        rb1 = new RadioButtonTheme[SortingOrder.getNames().length];
        for(int i=0; i<SortingOrder.getNames().length; i++){
            rb1[i] = new RadioButtonTheme(getContext());
            rb1[i].setId(i);
            rb1[i].setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            rb1[i].setPadding(20, 0, 0, 0);
            rb1[i].setLayoutParams(params);
            rb1[i].setText(Convert.formatEnumStringDialog(SortingOrder.getNames()[i]));
            if(sortingOrder == SortingOrder.fromValue(i)) rb1[i].setChecked(true);
            rgSortingOrder.addView(rb1[i]);
        }

        btnCancel.setOnClickListener(btCancelListener);
        btnSort.setOnClickListener(btSortListener);

        refreshTheme();
    }

    private void refreshTheme() {
        if(colorTheme.isDarkTheme()) {
            Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(R.color.colorDarkPrimary);
        } else {
            //
        }
    }

    public void setSortingMode(SortingMode sortingMode) {
        this.sortingMode = sortingMode;
    }

    public void setSortingOrder(SortingOrder sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    public int getCheckedSortingMode() {
        return rgSortingMode.getCheckedRadioButtonId();
    }

    public int getCheckedSortingOrder() {
        return rgSortingOrder.getCheckedRadioButtonId();
    }

    public void setCancelButton(String cancel, View.OnClickListener onClickListener) {
        this.btCancelText = cancel;
        this.btCancelListener = onClickListener;
    }

    public void setSortButton(String delete, View.OnClickListener onClickListener) {
        this.btSortText = delete;
        this.btSortListener = onClickListener;
    }
}
