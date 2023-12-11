package com.safayousif.campmusicplayer;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistActivity extends MediaPlaybackActivity {

    private static final String TAG = "PlaylistActivity";
    private SongAdapter songAdapter;
    private PlaylistModel playlistModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Get Parcelables
        SongModel songModel = getIntent().getParcelableExtra("SONG_MODEL");
        playlistModel = getIntent().getParcelableExtra("PLAYLIST_MODEL");

        // Update Recycler View
        RecyclerView songRecyclerView = findViewById(R.id.rvPlaylistSongs);
        songAdapter = new SongAdapter(this, playlistModel.getSongs(), this);
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Highlight playing song
//        int position = 0;
//        for (int i=0; i< playlistModel.getSongs().size(); i++){
//            if (playlistModel.getSongs().get(i).getTitle().equals(songModel.getTitle()))
//                break;
//        }
        Log.d(TAG, "Position of playing song is " +super.retrievePosition());
        songAdapter.setCurrentlyPlayingPosition(super.retrievePosition());

        // Populate Data
        TextView tvPlaylistIndicator = findViewById(R.id.tvPlaylistIndicator);
        ImageButton tbPlaylistEditName = findViewById(R.id.tbPlaylistEditName);

        if(playlistModel.getPlaylistName().equals(getResources().getString(R.string.all_songs_header))){
            // Don't show artist if playing All Songs
            tvPlaylistIndicator.setText(getResources().getString(R.string.playing_title,
                    playlistModel.getPlaylistName()));
            // Don't allow edit name
            tbPlaylistEditName.setVisibility(View.GONE);
        } else {
            tvPlaylistIndicator.setText(getResources().getString(R.string.playlist_title_artist,
                    playlistModel.getPlaylistName(), playlistModel.getArtistName()));
        }
    }

    @Override
    public void onItemClick(int position) {
        super.onItemClick(position, playlistModel.getSongs().get(position).getPath());
        super.savePosition(position);
        songAdapter.setCurrentlyPlayingPosition(position);
    }
}