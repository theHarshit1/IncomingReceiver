package com.example.incomingreceiver;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class AudioPlaying extends AppCompatActivity {

    TextView playing;
    static MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_playing);
        playing=(TextView) findViewById(R.id.audioplaying);
        playing.setText("stopped");
        mp = MediaPlayer.create(AudioPlaying.this,R.raw.allfallsdown);

        String uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("audio/"+uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().toString().equals("start")) {
                        mp.seekTo(0);
                        mp.start();
                        playing.setText("audio playing");
                    } else {
                        playing.setText("audio stopped");
                        if (mp.isPlaying()) {
                            mp.pause();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
