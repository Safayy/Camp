package com.example.camp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> implements Filterable {
    String TAG = "PlaylistAdapter";
    Context context;
    ArrayList<PlaylistModel> playlistModels;
    private ItemFilter<PlaylistModel> filter;

    public PlaylistAdapter(Context context, ArrayList<PlaylistModel> playlistModels){
        this.context = context;
        this.playlistModels = playlistModels;
        this.filter = new ItemFilter<>(playlistModels, this, new PlaylistFilterListener());
    }
    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(playlistModels.get(position).getPlaylistName());
        holder.tvArtist.setText(playlistModels.get(position).getArtistName());
        holder.imageView.setImageResource(playlistModels.get(position).getImage());
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
            return item.getPlaylistName().toLowerCase().contains(constraint);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tvName;
        TextView tvArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPlaylist);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvArtist = itemView.findViewById(R.id.tvPlaylistArtist);
        }
    }
}