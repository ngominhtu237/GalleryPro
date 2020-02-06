package com.ss.gallerypro.fragments.list.section.abstraction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ss.gallerypro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseHeaderViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.tv_timeline_header)
    TextView tvTitle;

    protected BaseHeaderViewHolder(Context context, View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
