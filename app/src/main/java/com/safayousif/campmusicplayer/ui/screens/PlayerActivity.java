package com.safayousif.campmusicplayer.ui.screens;

import static com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager.getMediaService;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import com.safayousif.campmusicplayer.ui.utils.FavoriteButtonUtil;
import com.safayousif.campmusicplayer.ui.utils.MediaChangeObserver;
import com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.domain.model.SongModel;

public class PlayerActivity extends AppCompatActivity implements MediaChangeObserver {
    private static final String TAG = "PlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Get Data //TODO remove
//        MediaStateManager mediaStateManager = MediaStateManager.getInstance();
//        PlaylistModel playlist = mediaStateManager.getCurrentPlaylist();
//        SongModel song = mediaStateManager.getCurrentSong();

        // Populate data
        ImageButton ibPlaylist = findViewById(R.id.ibPlaylist);
        ImageButton ibPrevious = findViewById(R.id.ibPrevious);
        ImageButton ibTogglePlayPause = findViewById(R.id.ibTogglePlayPause);
        ImageButton ibSkip = findViewById(R.id.ibSkip);

        ibSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaService().playSongNext();
            }
        });

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaService().playSongPrevious();
            }
        });

        // Handle playback button clicks
        ibTogglePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMediaService() != null) {
                    if (getMediaService().isSongPlaying()){
                        SongModel currentSong = MediaStateManager.getInstance().getCurrentSong();
                        getMediaService().pauseSong(currentSong.getPath());
                        ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    } else {
                        getMediaService().resumeSong();
                        ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    }
                }
            }
        });

        ibPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlaylistActivity.class);
//                intent.putExtra("PLAYLIST_MODEL", playlist);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MediaStateManager.getInstance().hasObserver(this)) {
            MediaStateManager.getInstance().addObserver(this);
        }
        setupUI();
    }

    void setupUI(){
        // Play Song
        getMediaService().playSong(MediaStateManager.getInstance().getCurrentSong().getPath()); //TODO

        // Populate Data
        SongModel currentSong = MediaStateManager.getInstance().getCurrentSong();
        String playlistName = MediaStateManager.getInstance().getCurrentPlaylist().getPlaylistName();
        if (currentSong != null){
            ImageView ivPlayerImage = findViewById(R.id.ivPlayerImage);
            TextView tvPlayerPlaylistIndicator = findViewById(R.id.tvPlayerPlaylistIndicator);
            TextView tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
            TextView tvPlayerArtist = findViewById(R.id.tvPlayerArtist);

            ivPlayerImage.setImageDrawable(currentSong.getImage(this));
            tvPlayerPlaylistIndicator.setText(getString(R.string.playing_title, playlistName));
            tvPlayerTitle.setText(currentSong.getTitle());
            tvPlayerArtist.setText(currentSong.getArtist());
        }

        // Change icon of paused song if new song is started
        ImageButton ibTogglePlayPause = findViewById(R.id.ibTogglePlayPause);
        if (getMediaService().isSongPlaying()) {
                ibTogglePlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        }

        // Set Favorite status
        ImageButton ibPlayerFavorite = findViewById(R.id.ibPlayerFavorite);
        FavoriteButtonUtil.setImage(ibPlayerFavorite, MediaStateManager.getInstance().getCurrentSong().isFavorite());
        FavoriteButtonUtil.setupFavoriteButton(ibPlayerFavorite,MediaStateManager.getInstance().getCurrentSong().isFavorite(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaStateManager.getInstance().getCurrentSong().toggleIsFavorite();
                setupUI();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaStateManager.getInstance().removeObserver(this);
    }

    @Override
    public void onSongChanged(SongModel newSong) {
        setupUI();
        Log.i(TAG, "onSongChanged: Song Updated");
    }
}