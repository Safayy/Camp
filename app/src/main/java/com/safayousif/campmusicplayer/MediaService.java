package com.safayousif.campmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MediaService extends Service {
    private MediaPlayer mediaPlayer;
    int position;

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
        if (mediaPlayer != null) {
            stopSong();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void resumeSong(){
        if(mediaPlayer != null){
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