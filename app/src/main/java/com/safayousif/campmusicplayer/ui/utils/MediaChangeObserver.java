package com.safayousif.campmusicplayer.ui.utils;

import com.safayousif.campmusicplayer.domain.model.SongModel;

public interface MediaChangeObserver {
    void onSongChanged(SongModel newSong);
}