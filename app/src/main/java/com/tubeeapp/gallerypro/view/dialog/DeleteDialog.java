package com.tubeeapp.gallerypro.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.theme.ui.BaseDialogTheme;
import com.tubeeapp.gallerypro.theme.ui.ButtonTheme;
import com.tubeeapp.gallerypro.theme.ui.TextViewPrimary;

public class DeleteDialog extends BaseDialogTheme {

    private String message;
    private String btCancelText;
    private String btDeleteText;
    private View.OnClickListener btCancelListener = null;
    private View.OnClickListener btDeleteListener = null;
    private TextViewPrimary tvMessage;
    private ButtonTheme btnCancel, btnDelete;

    public DeleteDialog(@NonNull Context context) {
        super(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom;
    }

    @Override
    protected float getWidth() {
        return 0.85f;
    }
}
