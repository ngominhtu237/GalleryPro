package com.ss.gallerypro.fragments.listHeader.abstraction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ss.gallerypro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseHeaderViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.tv_timeline_header)
    TextView tvTitle;

    protected BaseHeaderViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
