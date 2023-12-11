package com.safayousif.campmusicplayer;

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

import java.util.ArrayList;

public class MainActivity extends MediaPlaybackActivity {
    private String TAG = "MAIN_ACTIVITY";
    private ArrayList<PlaylistModel> playlistModels = new ArrayList<>();
    private ArrayList<SongModel> songModels = new ArrayList<>();
    public static final int REQUEST_CODE = 101;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check Storage Permissions
        checkPermission();

        // Retrieve songs from device
        FileManager fileManager = new FileManager();
        songModels = fileManager.getSongFiles(this);
        playlistModels = fileManager.getPlaylists(this);

        // Setup Songs and Playlist Recycler View Adapter
        RecyclerView playlistRecyclerView = findViewById(R.id.rvPlaylist);
        RecyclerView songRecyclerView = findViewById(R.id.rvAllSongs);

        RecyclerViewInterface playlistRecyclerViewInterface = new RecyclerViewInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                intent.putExtra("PLAYLIST_MODEL", playlistModels.get(position));
                startActivity(intent);
//                songAdapter.notifyDataSetChanged();
            }
        };
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistModels, playlistRecyclerViewInterface);
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
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                playlistAdapter.getFilter().filter(newText);
                songAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        songAdapter.notifyDataSetChanged();
    }

    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else {
            Toast.makeText(this, "Storage permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Storage permission successfully granted", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        super.onItemClick(position, songModels.get(position));

        PlaylistModel playlist = new PlaylistModel(getResources().getString(R.string.all_songs_header), songModels, this);

        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("PLAYLIST_MODEL", playlist);
        startActivity(intent);
        songAdapter.notifyDataSetChanged();
    }
}