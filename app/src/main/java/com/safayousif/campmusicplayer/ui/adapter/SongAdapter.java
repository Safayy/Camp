package com.safayousif.campmusicplayer.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.safayousif.campmusicplayer.domain.mediastatemanager.DatabaseHelper;
import com.safayousif.campmusicplayer.domain.mediastatemanager.FileManager;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.ui.screens.MainActivity;
import com.safayousif.campmusicplayer.ui.screens.PlaylistActivity;
import com.safayousif.campmusicplayer.ui.utils.FavoriteButtonUtil;
import com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager;
import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.domain.model.SongModel;
import com.safayousif.campmusicplayer.ui.mapper.ItemFilter;
import com.safayousif.campmusicplayer.ui.utils.RecyclerViewInterface;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {
    String TAG = "SongAdapter";
    Context context;
    private RecyclerViewInterface recyclerViewInterface;
    ArrayList<SongModel> songModels;
    private ItemFilter<SongModel> filter;
    private PlaylistAdapter mainPlaylistAdapter;
    public SongAdapter(Context context, ArrayList<SongModel> songModels, RecyclerViewInterface recyclerViewInterface, PlaylistAdapter mainPlaylistAdapter) {
        this.context = context;
//        this.songModels = songModels;
        this.songModels = MediaStateManager.getInstance().getCurrentPlaylist().getSongs();
        this.filter = new ItemFilter<>(songModels, this, new SongFilterListener());
        this.recyclerViewInterface = recyclerViewInterface;
        this.mainPlaylistAdapter = mainPlaylistAdapter;
    }

    public SongAdapter(Context context, ArrayList<SongModel> songModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.songModels = MediaStateManager.getInstance().getCurrentPlaylist().getSongs();
        this.filter = new ItemFilter<>(songModels, this, new SongFilterListener());
        this.recyclerViewInterface = recyclerViewInterface;
        this.mainPlaylistAdapter = mainPlaylistAdapter;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.song, parent, false);
        return new SongAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(songModels.get(position).getImage(context));
        holder.tvTitle.setText(songModels.get(position).getTitle());
        holder.tvArtist.setText(songModels.get(position).getArtist());
//        if (position == MediaStateManager.getInstance().getCurrentPosition()) {
        if (MediaStateManager.getInstance().getCurrentPlaylist() != null && //TODO fix so that it doesnt highlight the wrong song if its in the position, but differnt playlist/filterd
                MediaStateManager.getInstance().getCurrentPlaylist().getSongs() != null &&
                position < MediaStateManager.getInstance().getCurrentPlaylist().getSongs().size() &&
                MediaStateManager.getInstance().getCurrentSong() == MediaStateManager.getInstance().getCurrentPlaylist().getSongs().get(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Reset background color
        }

        if (songModels.get(holder.getAdapterPosition()).getTitle().equals(context.getString(R.string.all_songs_header))){
            holder.ibMenu.setVisibility(View.GONE);
        }

        if (context instanceof PlaylistActivity) {
            holder.ibMenu.setVisibility(View.GONE);
            holder.ibMenu.setVisibility(View.GONE);
        }

        FavoriteButtonUtil.setupFavoriteButton(holder.ibFavorite, songModels.get(position).isFavorite(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songModels.get(holder.getAdapterPosition()).toggleIsFavorite();
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateSongIsFavorite(songModels.get(holder.getAdapterPosition()));
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        
        holder.ibAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogManagePlaylistView(context, songModels.get(holder.getAdapterPosition()));
            }
        });

        holder.tvAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogManagePlaylistView(context, songModels.get(holder.getAdapterPosition()));
            }
        });

        holder.tvArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Archive Physical Song
//                SongModel songModel = songModels.get(holder.getAdapterPosition());
//                FileManager.getInstance().archiveSong(songModel);
//
//                // Remove from view
//                MediaStateManager.getInstance().removeSong(songModel, context);
//
//                // Remove from songModels in the adapter
//                if (mainPlaylistAdapter != null) {
//                    mainPlaylistAdapter.removeSong(songModel);
//                }
//
//                // Update archived state in playlistModels
//                ArrayList<PlaylistModel> playlistModels = MediaStateManager.getInstance().getPlaylists();
//                for (PlaylistModel playlist : playlistModels) {
//                    if (playlist.getSongs().contains(songModel)) {
//                        playlist.getSongs().remove(songModel);
//                        SongModel archivedSong = new SongModel(songModel.getTitle());
//                        playlist.getSongs().add(archivedSong);
//                    }
//                }
//
//                // Reflect changes
////                songModels =
//                notifyDataSetChanged();
//
//                MediaStateManager.getInstance().setPlaylists(playlistModels);
//                mainPlaylistAdapter.updatePlaylistModels(playlistModels);
//                mainPlaylistAdapter.notifyDataSetChanged();
            }
        });
    }

    void showDialogManagePlaylistView(Context context, SongModel songModel){
        View dialogCreatePlaylistView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist_manage, null);

        RecyclerView rvDialogPlaylistsManage = dialogCreatePlaylistView.findViewById(R.id.rvDialogPlaylistsManage);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        rvDialogPlaylistsManage.setLayoutManager(layoutManager);

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(
                context,
                MediaStateManager.getInstance().getPlaylists(),
                new RecyclerViewInterface(){}, true);

        rvDialogPlaylistsManage.setAdapter(playlistAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
        builder.setView(dialogCreatePlaylistView)
                .setTitle(context.getString(R.string.dialog_playlist_add_title, songModel.getTitle()))
                .setPositiveButton(context.getString(R.string.dialog_btn_done_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get Selected Items
                        ArrayList<PlaylistModel> selectedPlaylists = playlistAdapter.getSelectedPlaylists();

                        DatabaseHelper dbHelper = new DatabaseHelper(context);
                        dbHelper.associateSongWithPlaylists(songModel, selectedPlaylists);

                        // Update Main Playlist Adapter
                        ArrayList<PlaylistModel> playlistModels = dbHelper.getPlaylists(context);
                        MediaStateManager.getInstance().setPlaylists(playlistModels);
                        mainPlaylistAdapter.updatePlaylistModels(playlistModels);

                        // Update playlistModels from Database
                        playlistAdapter.clearSelectedPlaylists();

                        Log.d(TAG, "onPlaylistItemClick: " + selectedPlaylists.size());
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    @Override
    public int getItemCount() { return songModels.size(); }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class SongFilterListener implements ItemFilter.FilterListener<SongModel> {
        @Override
        public boolean onFilter(SongModel item, String constraint) {
            return (item.getTitle()+item.getArtist()).toLowerCase().contains(constraint);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvArtist;
        ImageButton ibFavorite;
        ImageButton ibMenu;
        ImageButton ibAddPlaylist;
        TextView tvAddPlaylist;
        TextView tvArchive;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSong);
            tvTitle = itemView.findViewById(R.id.tvSongsTitle);
            tvArtist = itemView.findViewById(R.id.tvSongsArtist);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            ibMenu = itemView.findViewById(R.id.ibMenu);
            ibAddPlaylist = itemView.findViewById(R.id.ivBtnAddPlaylist);
            tvAddPlaylist = itemView.findViewById(R.id.tvAddPlaylist);
            tvArchive = itemView.findViewById(R.id.tvArchive);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onSongItemClick(position);
                        }
                    }
                }
            });
            ibMenu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    CardView cvMenu = itemView.findViewById(R.id.cvMenu);
                    // Toggle visibility
                    if(cvMenu.getVisibility() == View.INVISIBLE)
                        cvMenu.setVisibility(View.VISIBLE);
                    else
                        cvMenu.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}