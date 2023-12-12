package com.safayousif.campmusicplayer.ui.utils;

import android.view.View;
import android.widget.ImageButton;

import com.safayousif.campmusicplayer.R;

public class FavoriteButtonUtil {
    public static void setupFavoriteButton(ImageButton favoriteButton, boolean isFavorite, View.OnClickListener clickListener) {
        setImage(favoriteButton, isFavorite);
        favoriteButton.setOnClickListener(clickListener);
    }
    public static void setImage(ImageButton favoriteButton, boolean isFavorite){
        int image = isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border;
        favoriteButton.setImageResource(image);
    }
}