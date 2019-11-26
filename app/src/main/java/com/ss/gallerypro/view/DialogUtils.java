package com.ss.gallerypro.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Window;

public class DialogUtils {
    public static void changeDialogBackground(final Dialog dialog, final int resId) {
        dialog.getWindow().getDecorView().setBackgroundResource(resId);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog)dialog).getWindow();
                window.getDecorView().setBackgroundResource(resId);
            }
        });
    }
}
