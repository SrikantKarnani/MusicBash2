package com.example.android.musicbash;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.prefs.PreferenceChangeEvent;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.cursorVisible;
import static android.R.attr.displayOptions;
import static android.R.attr.dividerHeight;
import static android.R.attr.name;
import static android.R.attr.permission;
import static android.R.attr.theme;
import static android.media.CamcorderProfile.get;
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READABLE_CODE = 144 ;
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    RecyclerView lv;
    Boolean shuf = false;
    List<songs> songsObject;
    Intent play;
    recylcerAdapter ar;
    Cursor cursor;
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        while (!checkPermissions()){};
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                int position = r.nextInt(songsObject.size()) + 1;
                play = new Intent(getApplicationContext(), Player.class);
                shuf = true;
                startActivity(play.putExtra("pos", position).putExtra("songList", (Serializable) songsObject).putExtra("shuffle", shuf));
            }

        });
        Toolbar tb1 = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbarT);
        getSupportActionBar().setSubtitle(R.string.toolbatST);
        lv = (RecyclerView) findViewById(R.id.lvPlaylist);
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        String orderBy = MediaStore.Audio.Media.TITLE + " ASC";
                cursor = this.managedQuery(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        null,
                        orderBy
                );

        songsObject = new ArrayList<>();
        addSongs(cursor);
//        Comparator comp = new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.toLowerCase().compareTo(o2.toLowerCase());
//            }
//        };
//        Collections.sort(songsName, comp);

        ar = new recylcerAdapter(this, songsObject);
        lv.setAdapter(ar);
        lv.setNestedScrollingEnabled(false);
    }
        /* Checks if external storage is available for read and write */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state);
        }

    /* Checks if external storage is available to at least read */
        public boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            return (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
        }

        // Initiate request for permissions.
        private boolean checkPermissions() {

            if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
                Toast.makeText(this, "This app only works on devices with usable external storage",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequest();
            }
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_WRITE);
                return false;
            } else {
                return true;
            }
        }

        // Handle permissions result
        @Override
        public void onRequestPermissionsResult(int requestCode,
        @NonNull String permissions[],
        @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQUEST_PERMISSION_WRITE:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionGranted = true;
                        Toast.makeText(this, "External storage permission granted",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_READABLE_CODE:
                    if (grantResults.length > 0
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        permissionGranted = true;
                        Toast.makeText(this, "External storage permission granted",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READABLE_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchView searchView1 = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView1.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<songs> newList = new ArrayList<>();
                for(songs name : songsObject){
                    if((name.getTitle().toLowerCase()).contains(newText)){
                        newList.add(name);
                    }
                }
                ar.filter(newList);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    void addSongs(Cursor cur){
        cur.moveToFirst();
        do {
            songsObject.add(new songs(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            ));
        }while (cur.moveToNext());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(MainActivity.this, "Created By Srikant", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(MainActivity.this, "Settings Under Construction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.export:
                boolean isExported = JSONHelper.exportToJSON(this,songsObject);
                break;
            case R.id.importJSON:
                break;
            case R.id.sortDate:

        }
        return super.onOptionsItemSelected(item);
    }
}
