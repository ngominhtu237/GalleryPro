package com.tunm.gallerypro.fragments;

import android.view.View;

public interface RecycleViewClickListener {
    void onClick(View view, int  position);

    void onLongClick(View view, int  position);
}
