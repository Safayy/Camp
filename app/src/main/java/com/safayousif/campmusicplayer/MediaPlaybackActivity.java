package com.safayousif.campmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.safayousif.campmusicplayer.MediaService;
import com.safayousif.campmusicplayer.RecyclerViewInterface;

public abstract class MediaPlaybackActivity extends AppCompatActivity implements RecyclerViewInterface {
    private static final String TAG = "MediaPlaybackActivity";
    private MediaService mediaService;
    private boolean isBound = false;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind Media Service
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // Retrieve the last saved position
        position = retrievePosition();
    }

    public boolean getIsBound() {
        return isBound;
    }

    public void onItemClick(int position, String path) {
        // Start song
        if (mediaService == null) {
            mediaService = new MediaService();
        }
        mediaService.playSong(path);
        Log.d(TAG, "Super onItemClick: Playing song position in playlist" + position);

        // Save the current position
        savePosition(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MediaService.MediaBinder binder = (MediaService.MediaBinder) service;
            mediaService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    public void savePosition(int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("POSITION_KEY", position);
        editor.apply();
    }

    public int retrievePosition() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt("POSITION_KEY", -1);
    }
}