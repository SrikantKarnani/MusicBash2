package com.example.android.musicbash;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static android.R.attr.bitmap;
import static android.R.attr.filter;
import static android.os.Build.VERSION_CODES.M;
import static android.support.design.widget.Snackbar.make;

/**
 * Created by Srikant on 3/12/2017.
 */

public class recylcerAdapter extends RecyclerView.Adapter<recylcerAdapter.recyclerViewHolder> {

    Context context;
    LayoutInflater lf;
    Cursor cursor;
    List<songs> songItems;
    private SimpleTarget target;

    public recylcerAdapter(Context context, List list,Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        songItems = list;
    }
        @Override
    public recyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            lf = LayoutInflater.from(context);
            View view = lf.inflate(R.layout.song_layout,parent,false);
        return new recyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final recyclerViewHolder holder, final int position) {
        cursor.moveToPosition(position);
        String name = songItems.get(position).getTitle();
        holder.tv.setText(name);
        long albumID = Integer.parseInt(songItems.get(position).getAlbumId());
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
        Glide.with(context).load(uri).placeholder(R.mipmap.music).error(R.mipmap.music)
                .crossFade().centerCrop().into(holder.im);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Player.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position",position).putExtra("shuffle",false).putExtra("songList", (Serializable) songItems);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return (songItems.size());
    }
    public void filter(List<songs> newList) {
        songItems = new ArrayList<>();
        songItems.addAll(newList);
        notifyDataSetChanged();

    }
    class recyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView im;
        private TextView tv;
        private View container;
        public recyclerViewHolder(View itemView) {
            super(itemView);
            im = (ImageView) itemView.findViewById(R.id.image);
            tv= (TextView) itemView.findViewById(R.id.textView2);
            container = itemView;
        }
    }
}
