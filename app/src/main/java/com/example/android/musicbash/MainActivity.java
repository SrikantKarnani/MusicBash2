package com.example.android.musicbash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private static final int REQUEST_PERMISSION_READ = 1002;
    private static final int REQUEST_PERMISSION_WRITEREAD = 1003 ;
    Intent play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tb1 = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbarT);
        getSupportActionBar().setSubtitle(R.string.toolbatST);
        initViews();
    }

    private void initViews(){
        if(checkPermissions()){
            Fragment frag = BlankFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frag_container,frag).commit();
        }
        else{
            askPermission();
        }
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
            int readExternalPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readExternalPermission == PackageManager.PERMISSION_GRANTED && writeExternalPermission == PackageManager.PERMISSION_GRANTED;
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
                        initViews();
                    } else {
                        Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_PERMISSION_WRITEREAD:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initViews();
                    } else {
                        Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    protected void askPermission() {
        int readExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(readExternalPermission !=PackageManager.PERMISSION_GRANTED && writeExternalPermission!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITEREAD);
        }else if (readExternalPermission !=PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ);
        }
        else if( writeExternalPermission !=PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
        }
    }
}
