package com.ss.gallerypro.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.theme.ColorTheme;

import java.util.Objects;

public class DeleteDialogCustom extends Dialog {

    private String title;
    private String message;
    private String btCancelText;
    private String btDeleteText;
    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btDeleteListener = null;
    private ColorTheme colorTheme;

    public DeleteDialogCustom(@NonNull Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
    }

    public DeleteDialogCustom(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DeleteDialogCustom(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        TextView tvTitle = findViewById(R.id.title_dialog_custom);
        TextView tvMessage = findViewById(R.id.message_dialog_custom);
        tvMessage.setText(message);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnDelete = findViewById(R.id.btn_delete);
        btnCancel.setText(btCancelText);
        btnDelete.setText(btDeleteText);

        btnCancel.setOnClickListener(btCancelListener);
        btnDelete.setOnClickListener(btDeleteListener);

        // background
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(colorTheme.getPrimaryHighLightColor());
        gd.setCornerRadius(25);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);

        tvTitle.setTextColor(colorTheme.getAccentColor());
        tvMessage.setTextColor(colorTheme.getHighLightColor());
        btnCancel.setTextColor(colorTheme.getPrimaryColor());
        btnDelete.setTextColor(colorTheme.getPrimaryColor());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNegativeButton(String cancel, View.OnClickListener onClickListener) {
        this.btCancelText = cancel;
        this.btCancelListener = onClickListener;
    }

    public void setPositveButton(String delete, View.OnClickListener onClickListener) {
        this.btDeleteText = delete;
        this.btDeleteListener = onClickListener;
    }

}
