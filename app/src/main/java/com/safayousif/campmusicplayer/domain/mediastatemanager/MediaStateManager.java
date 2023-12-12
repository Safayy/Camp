package com.safayousif.campmusicplayer.domain.mediastatemanager;

import android.util.Log;

import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.domain.model.SongModel;
import com.safayousif.campmusicplayer.ui.utils.MediaChangeObserver;

import java.util.ArrayList;
import java.util.List;

public class MediaStateManager {
    private static final String TAG = "MediaStateManager";
    private static MediaStateManager instance;
    private static int currentPosition;
    private static SongModel currentSong;
    private static PlaylistModel currentPlaylist;
    private static MediaService mediaService;
    private List<MediaChangeObserver> songChangeObservers = new ArrayList<>();

    private MediaStateManager() {}

    public static synchronized MediaStateManager getInstance() {
        if (instance == null) {
            instance = new MediaStateManager();
            currentPosition = -1;
        }
        return instance;
    }

    public void saveState(int position, PlaylistModel playlistModel){
        setCurrentPlaylist(playlistModel);
        setCurrentPosition(position);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        if(position < MediaStateManager.getInstance().getCurrentPlaylist().getSongs().size() &&
            position >= 0)
            currentPosition = position;
        notifySongChange(getCurrentSong());
    }

    public SongModel getCurrentSong() {
//        if (getCurrentPosition() == -1 || getCurrentPlaylist() == null || getCurrentPosition() >= getCurrentPlaylist().getSongs().size() ){
        if (getCurrentPosition() == -1 || getCurrentPlaylist() == null){
            Log.e(TAG, "getCurrentSong: null songModel" );
                return null;
        }
        SongModel songModel = getCurrentPlaylist().getSongs().get(getCurrentPosition());
        return songModel;
    }

    public PlaylistModel getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(PlaylistModel playlistModel) {
//        if (getCurrentPlaylist() != null && !getCurrentPlaylist().toString().equals(playlistModel.toString())) {
//            setCurrentPosition(0);
//        }
        currentPlaylist = playlistModel;
    }

    public boolean hasObserver(MediaChangeObserver observer) {
        return songChangeObservers.contains(observer);
    }

    public void addObserver(MediaChangeObserver observer) {
        if (!hasObserver(observer)) {
            songChangeObservers.add(observer);
        }
    }

    public void removeObserver(MediaChangeObserver observer) {
        songChangeObservers.remove(observer);
    }

    private void notifySongChange(SongModel newSong) {
        for (MediaChangeObserver observer : songChangeObservers) {
            observer.onSongChanged(newSong);
        }
    }

    public static void setMediaServiceInstance(MediaService service) {
        mediaService = service;
    }

    public static MediaService getMediaService(){
        if (mediaService == null)
            mediaService = new MediaService();
        return mediaService;
    }
}