package com.julianhatzky.goodnightbaby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class StartRecord extends AppCompatActivity {

    Button btnRecord, btnStopRecord, btnPlay, btnStopPlaying;
    public static String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //Request runtime permission

        if(!checkPermissionFromDevice())
            requestPermission();

        //init View
        btnPlay = (Button) findViewById(R.id.button_play);
        btnRecord = (Button) findViewById(R.id.button_record);
        btnStopPlaying = (Button) findViewById(R.id.button_stop_play);
        btnStopRecord = (Button) findViewById(R.id.button_stop);


        //Request runtime permission

            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (checkPermissionFromDevice())
                    {

                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                            + UUID.randomUUID().toString() + "_audio_record.3gp";


                    pathSave = MainActivity.pathSave;

                    setupMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnPlay.setEnabled(false);
                    btnStopPlaying.setEnabled(false);

                    Toast.makeText(StartRecord.this, "Recording...", Toast.LENGTH_SHORT).show();

                    }


                    else {
                        requestPermission();
                    }
                }
            });


            btnStopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStopPlaying.setEnabled(false);
                }
            });


            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStopPlaying.setEnabled(true);
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(false);

                    mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    mediaPlayer.start();
                    Toast.makeText(StartRecord.this, "Playing..", Toast.LENGTH_SHORT).show();

                }
            });


            btnStopPlaying.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(true);
                    btnStopPlaying.setEnabled(false);
                    btnPlay.setEnabled(true);


                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });



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


        private void setupMediaRecorder () {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(pathSave);
        }
}

