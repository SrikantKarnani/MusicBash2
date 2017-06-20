package com.example.android.musicbash;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Srikant on 3/12/2017.
 */

public class JSONHelper {
    private static final String file_name = "list.json";
    private static final String TAG = "JSONHelper";

    public static boolean exportToJSON(Context context, List<songs> songObjects){
        Songs songsees = new Songs();
        songsees.setSong(songObjects);
        Gson gson = new Gson();
        String jsonString = gson.toJson(songsees);
        Log.i(TAG,"exportToJSON"+jsonString);

    return false;
    }

    static class Songs{
        List<com.example.android.musicbash.songs> song;

        public List<com.example.android.musicbash.songs> getSong() {
            return song;
        }

        public void setSong(List<com.example.android.musicbash.songs> song) {
            this.song = song;
        }
    }


}
