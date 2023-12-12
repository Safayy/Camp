package com.safayousif.campmusicplayer.ui.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.safayousif.campmusicplayer.domain.mediastatemanager.FileManager;
import com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager;
import com.safayousif.campmusicplayer.ui.adapter.PlaylistAdapter;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.ui.adapter.SongAdapter;
import com.safayousif.campmusicplayer.domain.model.SongModel;
import com.safayousif.campmusicplayer.ui.utils.RecyclerViewInterface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    private String TAG = "MainActivity";
    private ArrayList<PlaylistModel> playlistModels = new ArrayList<>();
    private ArrayList<SongModel> songModels = new ArrayList<>();
    public static final int REQUEST_CODE = 101;
    private SongAdapter songAdapter;

    private void setupUI(){
        // Setup Songs and Playlist Recycler View Adapter
        RecyclerView playlistRecyclerView = findViewById(R.id.rvPlaylist);
        RecyclerView songRecyclerView = findViewById(R.id.rvAllSongs);

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistModels, this);
        playlistRecyclerView.setAdapter(playlistAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        playlistRecyclerView.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(this, songModels, this);
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup filter
        SearchView svSearch = findViewById(R.id.svSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                playlistAdapter.getFilter().filter(newText);
                songAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void initializeMediaStateManager() {
        FileManager fileManager = new FileManager();
        songModels = fileManager.getSongFiles(this);
        playlistModels = fileManager.getPlaylists(this);

        PlaylistModel allSongsPlaylist = new PlaylistModel(getResources().getString(R.string.all_songs_header), songModels, this);
        MediaStateManager.getInstance().setCurrentPlaylist(allSongsPlaylist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check Storage Permissions
        checkPermission();
        initializeMediaStateManager();
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update adapter to reflect isFavorite songs
        if(songAdapter != null)
            songAdapter.notifyDataSetChanged();
    }

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Storage permission successfully granted", Toast.LENGTH_SHORT).show();
            } else {
                // Ask for permission if rejected
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    @Override
    public void onSongItemClick(int position) {
        PlaylistModel playlist = new PlaylistModel(getResources().getString(R.string.all_songs_header), songModels, this);
        MediaStateManager.getInstance().saveState(position, playlist);

        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
        songAdapter.notifyDataSetChanged();
    }

    public void onPlaylistItemClick(int position) {
        PlaylistModel playlist = playlistModels.get(position);
        MediaStateManager.getInstance().saveState(position, playlist);

        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("PLAYLIST_MODEL", playlist);
        startActivity(intent);
        songAdapter.notifyDataSetChanged();
    }
}