package com.example.android.musicbash;

import android.content.ContentUris;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Srikant on 3/12/2017.
 */

public class Songs implements Serializable{
    private String ID;
    private String Title;
    private String Album;
    private String Data;
    private String uri;

    public String getAlbumId() {
        return AlbumId;
    }

    private String AlbumId;

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public String getAlbum() {
        return Album;
    }

    public String getData() {
        return Data;
    }

    public String getDuration() {
        return Duration;
    }


    private String Duration;

    public Songs(String ID, String title, String album, String data, String duration, String AlbumId) {
        this.ID = ID;
        Title = title;
        Album = album;
        Data = data;
        Duration = duration;
        this.AlbumId = AlbumId;
        long albumID = Integer.parseInt(this.AlbumId);
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        uri = ContentUris.withAppendedId(sArtworkUri, albumID).toString();
    }
    public static Comparator<Songs> SongNameComparator
            = new Comparator<Songs>() {

        public int compare(Songs song1, Songs song2) {

            String songName1 = song1.getTitle().toUpperCase();
            String songName2 = song2.getTitle().toUpperCase();

            //ascending order
            return songName1.compareTo(songName2);

            //descending order
            //return songName2.compareTo(songName1);
        }

    };

    public String getUri(){
        return uri;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.music)
                .error(R.mipmap.music)
                .into(view);
    }


}
