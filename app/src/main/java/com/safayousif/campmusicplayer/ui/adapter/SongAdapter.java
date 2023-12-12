package com.safayousif.campmusicplayer.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


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
    public SongAdapter(Context context, ArrayList<SongModel> songModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
//        this.songModels = songModels;
        this.songModels = MediaStateManager.getInstance().getCurrentPlaylist().getSongs();
        this.filter = new ItemFilter<>(songModels, this, new SongFilterListener());
        this.recyclerViewInterface = recyclerViewInterface;
    }

//    public SongAdapter(Context context, ArrayList<SongModel> songModels) {
//        this.context = context;
//        this.songModels = songModels;
//        this.filter = new ItemFilter<>(songModels, this, new SongFilterListener());
//    }

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
        if (MediaStateManager.getInstance().getCurrentPlaylist() != null && //TODO fix make less verbose
                position < MediaStateManager.getInstance().getCurrentPlaylist().getSongs().size() &&
                MediaStateManager.getInstance().getCurrentSong() == MediaStateManager.getInstance().getCurrentPlaylist().getSongs().get(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Reset background color
        }
        FavoriteButtonUtil.setupFavoriteButton(holder.ibFavorite, songModels.get(position).isFavorite(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songModels.get(holder.getAdapterPosition()).toggleIsFavorite();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
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
            return item.getTitle().toLowerCase().contains(constraint);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvArtist;
        ImageButton ibFavorite;
        ImageButton ibMenu;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSong);
            tvTitle = itemView.findViewById(R.id.tvSongsTitle);
            tvArtist = itemView.findViewById(R.id.tvSongsArtist);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            ibMenu = itemView.findViewById(R.id.ibMenu);

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
                    if(cvMenu.getVisibility() == View.GONE)
                        cvMenu.setVisibility(View.VISIBLE);
                    else
                        cvMenu.setVisibility(View.GONE);
                }
            });
        }
    }
}