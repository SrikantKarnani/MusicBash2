package com.example.android.musicbash;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Srikant on 3/12/2017.
 */

public class songs implements Serializable{
    private String ID;
    private String Title;
    private String Album;
    private String Data;

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

    public songs(String ID, String title, String album, String data, String duration,String AlbumId) {
        this.ID = ID;
        Title = title;
        Album = album;
        Data = data;
        Duration = duration;
        this.AlbumId = AlbumId;

    }
    public static Comparator<songs> SongNameComparator
            = new Comparator<songs>() {

        public int compare(songs fruit1, songs fruit2) {

            String fruitName1 = fruit1.getTitle().toUpperCase();
            String fruitName2 = fruit2.getTitle().toUpperCase();

            //ascending order
            return fruitName1.compareTo(fruitName2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };


}
