package com.safayousif.campmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.os.IBinder;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    private MediaService mediaService;
    private boolean isBound = false;
    private SongModel songModel;
    private PlaylistModel playlistModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Get Parcelables
        playlistModel = getIntent().getParcelableExtra("PLAYLIST_MODEL");
        PlaylistModel position = getIntent().getParcelableExtra("POSITION");

        // Populate data
        ImageButton ibPlaylist = findViewById(R.id.ibPlaylist);
        ImageButton ibPlayerFavorite = findViewById(R.id.ibPlayerFavorite);
        ImageButton ibPrevious = findViewById(R.id.ibPrevious);
        ImageButton ibTogglePlayPause = findViewById(R.id.ibTogglePlayPause);
        ImageButton ibSkip = findViewById(R.id.ibSkip);

        // Handle playback button clicks
        ibTogglePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound && mediaService != null) {
                    if (mediaService.isSongPlaying()){
                        mediaService.pauseSong(songModel.getPath());
                        ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    } else {
                        mediaService.resumeSong();
                        ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    }
                }
            }
        });

        ibPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlaylistActivity.class);
                intent.putExtra("PLAYLIST_MODEL", playlistModel);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bind Media Server
        Intent serviceIntent = new Intent(this, MediaService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        songModel = MediaStateManager.getInstance().getCurrentSong();
        ImageView ivPlayerImage = findViewById(R.id.ivPlayerImage);
        TextView tvPlayerPlaylistIndicator = findViewById(R.id.tvPlayerPlaylistIndicator);
        TextView tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
        TextView tvPlayerArtist = findViewById(R.id.tvPlayerArtist);

        ivPlayerImage.setImageDrawable(songModel.getImage(this));
        tvPlayerPlaylistIndicator.setText(getString(R.string.playing_title, playlistModel.getPlaylistName()));
        tvPlayerTitle.setText(songModel.getTitle());
        tvPlayerArtist.setText(songModel.getArtist());

        // Change icon of paused song if new song is started
        ImageButton ibTogglePlayPause = findViewById(R.id.ibTogglePlayPause);
        if (isBound && mediaService != null && mediaService.isSongPlaying()) {
            ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        }
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
}