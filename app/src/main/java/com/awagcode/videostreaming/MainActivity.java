package com.awagcode.videostreaming;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private VideoView video_view;
    private Button play_pause_button;
    private TextView current_time;
    private TextView stop_time;
    private ProgressBar progress_bar;
    private Uri videoUri;
    private ProgressBar circle_progress_bar;
    private Boolean isPlaying = false;
    private int current = 0;
    int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setOnClick();
        video_view.setVideoURI(videoUri);
        video_view.requestFocus();
        video_view.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if(what == mp.MEDIA_INFO_BUFFERING_START){
                    circle_progress_bar.setVisibility(View.VISIBLE);
                }else if(what == mp.MEDIA_INFO_BUFFERING_END){
                    circle_progress_bar.setVisibility(View.GONE);
                }
                return false;
            }
        });
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //divide by 1000 to get the second value
                duration = mp.getDuration()/1000;
                circle_progress_bar.setVisibility(View.GONE);
                String durationString = String.format("%02d:%02d",duration/60,duration%60);
                stop_time.setText(durationString);
                new videoProgress().execute();
            }
        });

        video_view.start();


        isPlaying = true;

    }

    private void setOnClick() {
        play_pause_button.setOnClickListener(this);
    }

    private void init() {
        video_view = findViewById(R.id.video_view);
        play_pause_button = findViewById(R.id.play_pause_button);
        current_time = findViewById(R.id.start_time);
        stop_time = findViewById(R.id.stop_time);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setMax(100);
        circle_progress_bar = findViewById(R.id.circle_progress_bar);
        videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/videostreaming-3c158.appspot.com/o/Twinkle%20Twinkle%20Little%20Star.mp4?alt=media&token=5df78d9d-992b-41d9-8c30-194bb316e9a8");

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.play_pause_button){
            if(isPlaying){
                video_view.pause();
                isPlaying = false;
                //circle_progress_bar.setVisibility(View.VISIBLE);
            }else{
                video_view.start();
                isPlaying = true;
                //circle_progress_bar.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;

    }

    public class videoProgress extends AsyncTask<Void,Integer,Void>{

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            do {

                if(isPlaying) {
                    current = video_view.getCurrentPosition() / 1000;
                    publishProgress(current);
                }

            }while (progress_bar.getProgress() <= 100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try{
            int currentPercent =  values[0] * 100 / duration;
            progress_bar.setProgress(currentPercent);
            String currentString = String.format("%02d:%02d",values[0]/60,values[0]%60);
            current_time.setText(currentString);}
            catch (Exception e){

            }
        }
    }

}
