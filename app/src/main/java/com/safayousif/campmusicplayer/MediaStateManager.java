package com.safayousif.campmusicplayer;

public class MediaStateManager {
    private static MediaStateManager instance;
    private int currentPosition = -1;
    private SongModel currentSong;
    private PlaylistModel currentPlaylist;

    private MediaStateManager() {}

    public static synchronized MediaStateManager getInstance() {
        if (instance == null) {
            instance = new MediaStateManager();
        }
        return instance;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public SongModel getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongModel songModel) {
        currentSong = songModel;
    }

    public PlaylistModel getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(PlaylistModel playlistModel){
        currentPlaylist = playlistModel;
    }
}