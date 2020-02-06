package com.ss.gallerypro.fragments.list.section.abstraction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ss.gallerypro.CustomModelClass;
import com.ss.gallerypro.R;
import com.ss.gallerypro.setting.callback.ThemeChangeObserver;
import com.ss.gallerypro.theme.ColorTheme;
import com.ss.gallerypro.view.SquareImageView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseContentViewHolder extends RecyclerView.ViewHolder implements ThemeChangeObserver {

    @BindView(R.id.ivTimelineThumbnail)
    SquareImageView ivTimelineThumbnail;

    @BindView(R.id.ivTimelineCheckbox)
    ImageView ivTimelineCheckbox;

    protected ColorTheme mColorTheme;
    private Context mContext;

    protected BaseContentViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        ButterKnife.bind(this, view);
        mColorTheme = new ColorTheme(context);
        refreshTheme();
        CustomModelClass.getInstance().addThemeChangeObserver(this);
    }

    /**
     * Set the color of the drawable.
     *
     * @param color The colorInt, from your R file.
     * @return The current IconDrawable for chaining.
     */
    private Drawable createSelectedIcon(int color) {
        return MaterialDrawableBuilder.with(mContext) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CHECK_CIRCLE) // provide an icon
                .setColor(color)
                .setToActionbarSize()
                .build();
    }

    private void refreshTheme() {
        ivTimelineCheckbox.setImageDrawable(createSelectedIcon(
                // mColorTheme.isDarkTheme() ? mContext.getColor(R.color.colorDarkAccent) : mColorTheme.getPrimaryColor()));
                mContext.getColor(R.color.colorDarkAccent)));
    }

    @Override
    public void requestUpdateTheme() {
        refreshTheme();
    }
}
