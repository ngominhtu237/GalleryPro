package com.tunm.gallerypro.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.theme.ui.BaseDialogTheme;

public class ProgressDialogTheme extends BaseDialogTheme {
    public ProgressDialogTheme(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshTheme();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.progress_delete_items;
    }

    @Override
    protected float getWidth() {
        return 0.75f;
    }
}
