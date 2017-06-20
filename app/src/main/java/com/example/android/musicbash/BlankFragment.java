package com.example.android.musicbash;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SEARCH_SERVICE;
/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    RecyclerView lv;
    List<songs> songsObject;
    recylcerAdapter ar;
    static Cursor cursor;
    public BlankFragment() {
        // Required empty public constructor
    }
    public static BlankFragment newInstance(){
        return new BlankFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        setHasOptionsMenu(true);
        lv = (RecyclerView) view.findViewById(R.id.lvPlaylist);
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
        cursor = getActivity().managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                orderBy
        );
        songsObject = new ArrayList<>();
        addSongs(cursor);
        ar = new recylcerAdapter(getActivity().getApplicationContext(), songsObject,cursor);
        lv.setAdapter(ar);
        lv.setNestedScrollingEnabled(false);
        return view;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView1 = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView1.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
    }
    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }
}
