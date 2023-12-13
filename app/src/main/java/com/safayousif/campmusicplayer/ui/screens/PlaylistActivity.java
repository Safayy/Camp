package com.safayousif.campmusicplayer.ui.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.ui.adapter.SongAdapter;
import com.safayousif.campmusicplayer.ui.utils.RecyclerViewInterface;

public class PlaylistActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static final String TAG = "PlaylistActivity"; //TODO private static final String TAG = PlaylistActivity.class.getName();
    private SongAdapter songAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Get Data
        PlaylistModel playlist = MediaStateManager.getInstance().getCurrentPlaylist();


        // Update Recycler View
        RecyclerView songRecyclerView = findViewById(R.id.rvPlaylistSongs);
        songAdapter = new SongAdapter(this, playlist.getSongs(), this);
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populate Data
        TextView tvPlaylistIndicator = findViewById(R.id.tvPlaylistIndicator);
        ImageButton tbPlaylistEditName = findViewById(R.id.tbPlaylistEditName);

        if(playlist.getPlaylistName().equals(getResources().getString(R.string.all_songs_header))){
            // Format for playing all media
            tvPlaylistIndicator.setText(getResources().getString(R.string.playing_title,
                    playlist.getPlaylistName()));
            tbPlaylistEditName.setVisibility(View.GONE);
        } else {
            // Format for playing playlist
            tvPlaylistIndicator.setText(getResources().getString(R.string.playlist_title_artist,
                    playlist.getPlaylistName(), playlist.getArtistName()));
        }
    }

    @Override
    public void onSongItemClick(int position) {
        PlaylistModel playlist = MediaStateManager.getInstance().getCurrentPlaylist();

        if(!playlist.getSongs().get(position).getIsArchived()){
            MediaStateManager.getInstance().saveState(position, playlist);
            songAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Song is archived", Toast.LENGTH_SHORT).show();
        }
    }
}