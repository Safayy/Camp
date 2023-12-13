package com.safayousif.campmusicplayer.domain.mediastatemanager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.domain.model.SongModel;
import com.safayousif.campmusicplayer.ui.utils.MediaChangeObserver;

import java.util.ArrayList;
import java.util.List;

public class MediaStateManager {
    private static final String TAG = "MediaStateManager";
    private static MediaStateManager instance;
    private static int currentPosition;
    private static PlaylistModel currentPlaylist;
    private static MediaService mediaService;
    private List<MediaChangeObserver> songChangeObservers = new ArrayList<>();
    private static ArrayList<PlaylistModel> playlistModels;

    private MediaStateManager() {}

    public static synchronized MediaStateManager getInstance() {
        if (instance == null) {
            instance = new MediaStateManager();
            currentPosition = -1; // Default no position
        }
        return instance;
    }

    // Public Methods
    public static MediaService getMediaService(){
        if (mediaService == null)
            mediaService = new MediaService();
        return mediaService;
    }

    public void saveState(int position, PlaylistModel playlistModel){
        setCurrentPlaylist(playlistModel);
        setCurrentPosition(position);
    }

    public SongModel getCurrentSong() {
        if (getCurrentPosition() == -1 || getCurrentPlaylist() == null ||
                getCurrentPosition() >= getCurrentPlaylist().getSongs().size() ||
                getCurrentPlaylist().getSongs().size() == 0){
            Log.e(TAG, "getCurrentSong: null songModel" );
            return null;
        }
        SongModel songModel = getCurrentPlaylist().getSongs().get(getCurrentPosition());
        return songModel;
    }

    public PlaylistModel getCurrentPlaylist() {
        return currentPlaylist;
    }

    // Default Packages | Only accessible by members of the package
    int getCurrentPosition() {
        return currentPosition;
    }

    void setCurrentPosition(int position) {
        if(position < MediaStateManager.getInstance().getCurrentPlaylist().getSongs().size() &&
            position >= 0)
            currentPosition = position;
        notifySongChange(getCurrentSong());
    }

    // Private Methods
    private void setCurrentPlaylist(PlaylistModel playlistModel) {
//        if (getCurrentPlaylist() != null && !getCurrentPlaylist().toString().equals(playlistModel.toString())) {
//            setCurrentPosition(0);
//        }
        currentPlaylist = playlistModel;
    }

    public void setPlaylists(ArrayList<PlaylistModel> newPlaylistModels){
        playlistModels = newPlaylistModels;
    }

    public ArrayList<PlaylistModel> getPlaylists(){
        return playlistModels;
    }

    // Observable Methods
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

    public void removeSong(SongModel songModel, Context context) {
        if (getCurrentSong() != null && songModel != null && getCurrentSong().equals(songModel)){
            Toast.makeText(context, "Cannot archive song in use!", Toast.LENGTH_SHORT).show();
        } else {
            if (currentPlaylist.getSongs().contains(songModel)) {
                currentPlaylist.getSongs().remove(songModel);
            }
        }
    }
}