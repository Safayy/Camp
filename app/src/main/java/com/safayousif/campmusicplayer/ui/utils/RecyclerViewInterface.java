package com.safayousif.campmusicplayer.ui.utils;

public interface RecyclerViewInterface {
    void onSongItemClick(int position);
    default void onPlaylistItemClick(int position){

    }
}