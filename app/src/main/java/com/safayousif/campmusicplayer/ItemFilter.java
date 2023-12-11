package com.safayousif.campmusicplayer;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Filter;

public class ItemFilter<T> extends Filter {
    String TAG = "ItemFilter";
    private final List<T> originalList;
    private List<T> filteredList;
    private RecyclerView.Adapter adapter;
    private FilterListener<T> filterListener;
    public ItemFilter(List<T> itemModels, RecyclerView.Adapter adapter, FilterListener<T> filterListener) {
        this.filteredList = itemModels;
        this.originalList = new ArrayList<>(itemModels);
        this.adapter = adapter;
        this.filterListener = filterListener;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();
            for (T item : originalList) {
                if (filterListener.onFilter(item, filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        results.values = new ArrayList<>(filteredList);
        results.count = filteredList.size();
        Log.e(TAG, results.values.toString());
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.notifyDataSetChanged();
    }

    public interface FilterListener<T> {
        boolean onFilter(T item, String constraint);
    }
}