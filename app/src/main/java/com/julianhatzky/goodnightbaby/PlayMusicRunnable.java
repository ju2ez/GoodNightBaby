package com.julianhatzky.goodnightbaby;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class PlayMusicRunnable extends SleepMode implements Runnable {

    private static MediaPlayer mediaPlayer;
    double AmplitudeEMA;
    double mTreshold;
    final int REQUEST_PERMISSION_CODE = 1000;

    public PlayMusicRunnable(double amplitudeEMA, double mTreshold) {
        this.AmplitudeEMA = amplitudeEMA;
        this.mTreshold = mTreshold;
    }

    public void run() {
            playRecord();
        };




    public void playRecord() {
        Log.i("AmplitudeEma", Double.toString(AmplitudeEMA));
        Log.i("mThreshold", Double.toString(mTreshold));
        if  (AmplitudeEMA>mTreshold) {
            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(StartRecord.pathSave);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }


            mediaPlayer.start();
            Toast.makeText(PlayMusicRunnable.this, "Playing..", Toast.LENGTH_SHORT).show();

        }
    }



    private void requestPermission () {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                   Toast.makeText(this, "Permission Granted ", Toast.LENGTH_SHORT).show();
                else
                   Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();

                break;
            }


        }
    }


    private boolean checkPermissionFromDevice () {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;

    }







}
