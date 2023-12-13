package com.safayousif.campmusicplayer.ui.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.safayousif.campmusicplayer.domain.mediastatemanager.DatabaseHelper;
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
    private PlaylistAdapter playlistAdapter;

    private void setupUI(){
        // Setup Songs and Playlist Recycler View Adapter
        RecyclerView playlistRecyclerView = findViewById(R.id.rvPlaylist);
        RecyclerView songRecyclerView = findViewById(R.id.rvAllSongs);

        playlistAdapter = new PlaylistAdapter(this, playlistModels, this, false);
        playlistRecyclerView.setAdapter(playlistAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        playlistRecyclerView.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(this, songModels, this, playlistAdapter);
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

        ImageButton ivBtnPlaylistAdd = findViewById(R.id.ivBtnPlaylistAdd);
        ImageButton ivBtnSettings = findViewById(R.id.ivBtnSettings);
        ImageButton ivBtnSync = findViewById(R.id.ivBtnSync);

        Context context = this;

        // Add Playlist onClick
        ivBtnPlaylistAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogCreatePlaylistView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist_create, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
                builder.setView(dialogCreatePlaylistView)
                        .setTitle(context.getString(R.string.dialog_playlist_create_title))
                        .setPositiveButton(context.getString(R.string.dialog_btn_playlist_create_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etCreatePlaylistName = dialogCreatePlaylistView.findViewById(R.id.etCreatePlaylistName);
                                TextView tvCreatePlaylistName = dialogCreatePlaylistView.findViewById(R.id.tvCreatePlaylistName);

                                // Retrieve the text when the positive button is clicked
                                String name = etCreatePlaylistName.getText().toString();

                                // Add entry to database
                                DatabaseHelper dbHelper = new DatabaseHelper(context);
                                PlaylistModel playlist = new PlaylistModel(
                                        name,
                                        new ArrayList<SongModel>(),
                                        context);
                                dbHelper.addPlaylist(playlist);

                                // Update playlistModels from Database
                                playlistModels = dbHelper.getPlaylists(context);
                                MediaStateManager.getInstance().setPlaylists(playlistModels);

                                // Notify
                                playlistAdapter.updatePlaylistModels(playlistModels);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.dialog_btn_cancel_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void initializeMediaStateManager() {
        Log.d(TAG, "initializeMediaStateManager: ");

        // Get songModels from filesystem and DB
        songModels = FileManager.getInstance().getSongs(this);

        // Get playlists in DB
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        playlistModels = dbHelper.getPlaylists(this);
        MediaStateManager.getInstance().setPlaylists(playlistModels);

        // Set initial playlist
        PlaylistModel allSongsPlaylist = new PlaylistModel(getResources().getString(R.string.all_songs_header), songModels, this);
        MediaStateManager.getInstance().saveState(-1, allSongsPlaylist);

        dbHelper.displayTable(getString(R.string.table_association_playlist_songs));
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
//        if (playlistAdapter != null)
//            playlistAdapter.updatePlaylistModels(MediaStateManager.getInstance().getPlaylists());
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
        playlistAdapter.clearSelectedPlaylists();
        PlaylistModel playlist = MediaStateManager.getInstance().getPlaylists().get(position);
        if(playlist.getSongs() != null)
            Log.d(TAG, "onPlaylistItemClick: Null song");
        if(playlist.getSongs().size() > 0 ){
            MediaStateManager.getInstance().saveState(0, playlist); // Start from first song

            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("PLAYLIST_MODEL", playlist);
            startActivity(intent);

            songAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No songs in playlist.\nAdd songs to listen!", Toast.LENGTH_LONG).show();
        }
    }
}