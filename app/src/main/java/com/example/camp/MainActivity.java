package com.example.camp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String TAG = "MAIN_ACTIVITY";

    ArrayList<PlaylistModel> playlistModels = new ArrayList<>();
    ArrayList<SongModel> songModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Songs and Playlist Recycler View Adapter
        RecyclerView playlistRecyclerView = findViewById(R.id.rvPlaylist);
        RecyclerView songRecyclerView = findViewById(R.id.rvAllSongs);

        setupPlaylistModels(); //TODO replace fileManager.getPlaylists()
        setupSongsModels(); //TODO replace FileManager.getSongs()

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistModels);
        playlistRecyclerView.setAdapter(playlistAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        playlistRecyclerView.setLayoutManager(linearLayoutManager);

        Log.d(TAG, "onCreate: " + songModels.get(0).title);
        SongAdapter songAdapter = new SongAdapter(this, songModels);
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Filter
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

    private void setupPlaylistModels(){
        String[] playlistName = {"Resistance", "GREATEST SHITS"};
        String[] authorName = {"Pinochio-P", "Utsu-P"};

        for (int i = 0 ; i < playlistName.length; i++){
            playlistModels.add(new PlaylistModel(playlistName[i], authorName[i], 1)); //TODO fix image
        }
    }
    private void setupSongsModels(){
        String[] title = {"Odds and Ends", "Venom"};
        String[] artist = {"Pinochio-P", "Utsu-P"};
        boolean[] isFavorite = {true, false};

        for (int i = 0 ; i < title.length; i++){
            songModels.add(new SongModel(title[i], artist[i], isFavorite[i], 1)); //TODO fix image
        }
    }
}