package com.safayousif.campmusicplayer.domain.mediastatemanager;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.safayousif.campmusicplayer.domain.model.PlaylistModel;

public class MediaService extends Service {
    private static final String TAG = "MediaService";
    private MediaPlayer mediaPlayer;
    private String playingPath = "";

    public class MediaBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    private final IBinder binder = new MediaBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void playSong(String path) {
        boolean isPlayingSong = path.equals(playingPath);
        // Play song if its a new song
        if(!isPlayingSong){
            stopSong();
            mediaPlayer = new MediaPlayer();
            try {
                playingPath = path;
                mediaPlayer.setDataSource(playingPath);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //TODO check if last song, loop
                        PlaylistModel playlist = MediaStateManager.getInstance().getCurrentPlaylist();
                        int position = MediaStateManager.getInstance().getCurrentPosition();
                        ++position;
                        if(!(position >= playlist.getSongs().size())){
                            MediaStateManager.getInstance().setCurrentPosition(position);
                            playSong(MediaStateManager.getInstance().getCurrentSong().getPath());
                        }
                    }
                });
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playSongPrevious() {
        int position = MediaStateManager.getInstance().getCurrentPosition();
//        --position;
//        if (!(position < 0))
            MediaStateManager.getInstance().setCurrentPosition(--position);
    }

    public void playSongNext() {
//        PlaylistModel playlist = MediaStateManager.getInstance().getCurrentPlaylist();
        int position = MediaStateManager.getInstance().getCurrentPosition();
//        ++position;
//        if(!(position >= playlist.getSongs().size())){
            MediaStateManager.getInstance().setCurrentPosition(++position);
//        }
    }

    public void resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pauseSong(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isSongPlaying() {
        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }
}