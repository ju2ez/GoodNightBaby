package com.julianhatzky.goodnightbaby;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class SleepMode extends Activity {

    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner, player;
    private static double mEMA = 0.0;
    private static double mTreshold=10000;
    static final private double EMA_FILTER = 0.6;
    private static MediaPlayer mediaPlayer;
    String pathSave = "";
    static int mediaPlayerInstance=0;

    final int REQUEST_PERMISSION_CODE = 1000;


    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };


    final Handler mHandler = new Handler();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_mode);
        mStatusView = (TextView) findViewById(R.id.showDecibel);


        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);

                    }
                }

            };
            runner.start();
            Log.d("Noise", "start runner()");
        }



        if(player==null) {
            player=new Thread() {
                public void run() {
                    Looper.prepare();
                    while (player!=null) {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Player", "Clap");
                        } catch (InterruptedException e) { };

                       checkTreshold();
                    }
                }
            };

            player.start();
            Log.d("Noise", "start player()");
        }



        if (checkPermissionFromDevice())
        {
            pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + UUID.randomUUID().toString() + "_audio_record.3gp";
        }

        else {
            requestPermission();
        }

    }

    public void onResume()
    {
        super.onResume();
        startRecorder();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        mStatusView.setText(Double.toString((getAmplitudeEMA())) + " dB");
    }
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }



    public void checkTreshold() {
         TextInputLayout input = (TextInputLayout) findViewById(R.id.input_treshold);
         String inputStr=input.getEditText().getText().toString();
         Log.i("inputStr", inputStr);
         double buf = 10000;
        try {
            if (!inputStr.isEmpty())
              buf=Double.parseDouble(inputStr);
        }  catch (NumberFormatException e) {
            buf=10000;
        }
        mTreshold = buf;
        if  (getAmplitudeEMA()>mTreshold) {
            playRecord();
        }
    }

    public void playRecord() {
        Log.i("AmplitudeEma", Double.toString(getAmplitudeEMA()));
        Log.i("mThreshold", Double.toString(mTreshold));
        if (mediaPlayerInstance == 0 && mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(StartRecord.pathSave);
                mediaPlayer.prepare();

                mediaPlayerInstance=1;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            Toast.makeText(SleepMode.this, "Playing..", Toast.LENGTH_SHORT).show();

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

