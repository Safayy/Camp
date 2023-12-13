package com.safayousif.campmusicplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.domain.mediastatemanager.MediaStateManager;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.domain.model.SongModel;
import com.safayousif.campmusicplayer.ui.mapper.ItemFilter;
import com.safayousif.campmusicplayer.ui.utils.RecyclerViewInterface;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> implements Filterable {
    String TAG = "PlaylistAdapter";
    Context context;
    private RecyclerViewInterface recyclerViewInterface;
    ArrayList<PlaylistModel> playlistModels;
    private ItemFilter<PlaylistModel> filter;
    private static ArrayList<PlaylistModel> selectedPlaylists = new ArrayList<>();
    static boolean isSelectable;

    public PlaylistAdapter(Context context, ArrayList<PlaylistModel> playlistModels, RecyclerViewInterface recyclerViewInterface, Boolean isSelectable){
        this.context = context;
        this.playlistModels = playlistModels; //new ArrayList<>(playlistModels)
        this.filter = new ItemFilter<>(playlistModels, this, new PlaylistFilterListener());
        this.recyclerViewInterface = recyclerViewInterface;
        this.isSelectable = isSelectable;
    }
    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view, recyclerViewInterface);
    }
    public void updatePlaylistModels(ArrayList<PlaylistModel> updatedPlaylistModels) {
        this.playlistModels.clear();
        this.playlistModels.addAll(updatedPlaylistModels);
        this.filter.updateList(updatedPlaylistModels);
        this.notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(playlistModels.get(position).getPlaylistName());
        holder.tvArtist.setText(
                context.getString(R.string.playlist_title_artist_attribution,
                        playlistModels.get(position).getArtistName()));
        holder.imageView.setImageDrawable(playlistModels.get(position).getImage(context));
    }

    @Override
    public int getItemCount() {
        return playlistModels.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class PlaylistFilterListener implements ItemFilter.FilterListener<PlaylistModel> {
        @Override
        public boolean onFilter(PlaylistModel item, String constraint) {

            return (item.getPlaylistName()+item.getArtistName()).toLowerCase().contains(constraint);
        }
    }
    public ArrayList<PlaylistModel> getSelectedPlaylists() {
        return selectedPlaylists;
    }
    public void clearSelectedPlaylists() {
        selectedPlaylists = new ArrayList<PlaylistModel>();
    }
    public void removeSong(SongModel songModel) {
        if (playlistModels.contains(songModel)) {
            int position = playlistModels.indexOf(songModel);
            playlistModels.remove(songModel);
            notifyItemRemoved(position);
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvName;
        TextView tvArtist;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPlaylist);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvArtist = itemView.findViewById(R.id.tvPlaylistArtist);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PlaylistModel playlist = MediaStateManager.getInstance().getPlaylists().get(position);

                        if(isSelectable){
                            // Toggle selection
                            if (selectedPlaylists.contains(playlist)) {
                                selectedPlaylists.remove(playlist);
                                itemView.setBackgroundResource(0); // Remove background
                            } else {
                                selectedPlaylists.add(playlist);
                                itemView.setBackgroundResource(R.drawable.selected_border); // Add border
                            }
                        }
                        // Notify the interface about the click
                        recyclerViewInterface.onPlaylistItemClick(position);
                    }
                }
            });
        }
    }
}