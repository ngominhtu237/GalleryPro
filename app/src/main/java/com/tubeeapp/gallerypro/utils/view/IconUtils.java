package com.tubeeapp.gallerypro.utils.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class IconUtils {
    /**
     * Set the color of the drawable.
     *
     * @param color The colorInt, from your R file.
     * @return The current IconDrawable for chaining.
     */
    public static Drawable createSelectedIcon(Context context, int color) {
        return MaterialDrawableBuilder.with(context) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CHECK_CIRCLE) // provide an icon
                .setColor(color)
                .setToActionbarSize()
                .build();
    }
}
