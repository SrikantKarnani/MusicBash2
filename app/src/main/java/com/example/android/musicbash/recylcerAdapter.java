package com.example.android.musicbash;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.musicbash.databinding.SongLayoutBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srikant on 3/12/2017.
 */

public class recylcerAdapter extends RecyclerView.Adapter<recylcerAdapter.recyclerViewHolder> {

    Context context;
    LayoutInflater lf;
    Cursor cursor;
    List<Songs> songItems;

    public recylcerAdapter(Context context, List list, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        songItems = list;
    }

    @Override
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        lf = LayoutInflater.from(context);
        SongLayoutBinding binding = SongLayoutBinding.inflate(lf, parent, false);
        return new recyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final recyclerViewHolder holder, final int position) {
        cursor.moveToPosition(position);
        holder.bind(songItems.get(position));
    }

    @Override
    public int getItemCount() {
        return (songItems.size());
    }

    public void filter(List<Songs> newList) {
        songItems = new ArrayList<>();
        songItems.addAll(newList);
        notifyDataSetChanged();

    }

    class recyclerViewHolder extends RecyclerView.ViewHolder {
        private SongLayoutBinding binding;

        recyclerViewHolder(SongLayoutBinding binding) {
            super(binding.root);
            this.binding = binding;
        }
        public void bind(Songs song) {
            binding.setSong(song);
            binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Player.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("position", getAdapterPosition()).putExtra("shuffle", false).putExtra("songList", (Serializable) songItems);
                    context.startActivity(intent);
                }
            });
        }
    }
}
