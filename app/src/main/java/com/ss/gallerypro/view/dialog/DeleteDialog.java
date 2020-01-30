package com.ss.gallerypro.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.ss.gallerypro.R;
import com.ss.gallerypro.theme.ColorTheme;
import com.ss.gallerypro.theme.ui.ButtonTheme;
import com.ss.gallerypro.theme.ui.TextViewPrimary;

import java.util.Objects;

public class DeleteDialog extends Dialog {

    private String message;
    private String btCancelText;
    private String btDeleteText;
    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btDeleteListener = null;
    private ColorTheme colorTheme;
    private TextViewPrimary tvMessage;
    private ButtonTheme btnCancel, btnDelete;

    public DeleteDialog(@NonNull Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        tvMessage = findViewById(R.id.message_dialog_custom);
        tvMessage.setText(message);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);
        btnCancel.setText(btCancelText);
        btnDelete.setText(btDeleteText);

        btnCancel.setOnClickListener(btCancelListener);
        btnDelete.setOnClickListener(btDeleteListener);

        refreshTheme();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNegativeButton(String cancel, View.OnClickListener onClickListener) {
        this.btCancelText = cancel;
        this.btCancelListener = onClickListener;
    }

    public void setPositiveButton(String delete, View.OnClickListener onClickListener) {
        this.btDeleteText = delete;
        this.btDeleteListener = onClickListener;
    }

    private void refreshTheme() {
        if(colorTheme.isDarkTheme()) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(getContext().getColor(R.color.colorDarkPrimary));
            gd.setCornerRadius(25);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);
        } else {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(colorTheme.getBackgroundHighLightColor());
            gd.setCornerRadius(25);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);
        }
    }

}
